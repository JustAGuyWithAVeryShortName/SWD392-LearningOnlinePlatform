import { useEffect, useRef, useState, useCallback } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import API from "../api";

const SOCKET_URL = "/ws"; // Use proxied endpoint

export default function useChatSocket({ userId, role, onMessage, onTyping, onRead }) {
  const stompClient = useRef(null);
  const [connected, setConnected] = useState(false);
  const [messages, setMessages] = useState([]);
  const [typingUsers, setTypingUsers] = useState([]);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("No token found, cannot connect to WebSocket");
      return;
    }

    let reconnectAttempts = 0;
    const maxReconnectAttempts = 3;

    const connectWebSocket = () => {
      try {
        // Pass token as query param for SockJS handshake
        const socket = new SockJS(`${SOCKET_URL}?token=${token}`);

        stompClient.current = new Client({
          webSocketFactory: () => socket,
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
          // No need for connectHeaders for JWT
          onConnect: () => {
            console.log("WebSocket connected successfully");
            setConnected(true);
            reconnectAttempts = 0;

            // Subscribe to user-specific queues for private messages
            stompClient.current.subscribe("/user/queue/private", msg => {
              console.log("Received private message:", msg.body);
              const message = JSON.parse(msg.body);
              console.log("Parsed message:", message);
              setMessages(prev => {
                // Avoid duplicates and sort by sentAt timestamp
                const messageExists = prev.some(m => m.messageId === message.messageId);
                if (messageExists) return prev;
                
                const newMessages = [...prev, message];
                return newMessages.sort((a, b) => new Date(a.sentAt) - new Date(b.sentAt));
              });
              onMessage && onMessage(message);
            });

            stompClient.current.subscribe("/user/queue/typing", msg => {
              console.log("Received typing indicator:", msg.body);
              const typing = JSON.parse(msg.body);
              setTypingUsers(typing.users || []);
              onTyping && onTyping(typing);
            });
            stompClient.current.subscribe("/user/queue/read-receipt", msg => {
              console.log("Received read receipt:", msg.body);
              const read = JSON.parse(msg.body);
              onRead && onRead(read);
            });
          },
          onDisconnect: () => {
            setConnected(false);
            console.log("WebSocket disconnected");
          },
          onStompError: err => {
            console.error("WebSocket STOMP error:", err);
            setConnected(false);
            reconnectAttempts++;
            if (reconnectAttempts < maxReconnectAttempts) {
              console.log(`Attempting to reconnect... (${reconnectAttempts}/${maxReconnectAttempts})`);
              setTimeout(connectWebSocket, 2000);
            } else {
              console.error("Max reconnection attempts reached. WebSocket connection failed.");
            }
          },
          onWebSocketError: err => {
            console.error("WebSocket connection error:", err);
            setConnected(false);
          }
        });

        // Activate the client
        stompClient.current.activate();
      } catch (error) {
        console.error("Failed to create WebSocket connection:", error);
        setConnected(false);
      }
    };

    // Initial connection attempt
    connectWebSocket();

    return () => {
      if (stompClient.current) {
        stompClient.current.deactivate();
      }
    };
  }, [userId, role]); // Removed onMessage, onTyping, onRead from dependencies

  const sendMessage = async message => {
    console.log("Sending message:", message);
    console.log("WebSocket connected:", stompClient.current?.connected);
    
    if (!stompClient.current || !stompClient.current.connected) {
      console.error("Cannot send message: WebSocket not connected");
      return;
    }
    
    try {
      const payload = {
        content: message.content,
        recipient: message.recipientId,
        roomId: message.chatId
      };
      console.log("WebSocket payload:", payload);
      
      stompClient.current.publish({
        destination: "/app/private",
        body: JSON.stringify(payload)
      });
      console.log("Message sent via WebSocket");
      
      // Add message to local state immediately for better UX
      const localMessage = {
        messageId: `local-${Date.now()}`, // temporary ID with prefix
        roomId: message.chatId,
        senderUsername: message.senderId,
        recipientUsername: message.recipientId,
        content: message.content,
        messageType: "TEXT",
        sentAt: new Date().toISOString(),
        deliveredAt: null,
        readAt: null,
        isDelivered: false,
        isRead: false
      };
      
      setMessages(prev => {
        // Avoid duplicates and sort by sentAt timestamp
        const messageExists = prev.some(m => m.messageId === localMessage.messageId);
        if (messageExists) return prev;
        
        const newMessages = [...prev, localMessage];
        return newMessages.sort((a, b) => new Date(a.sentAt) - new Date(b.sentAt));
      });
      
    } catch (error) {
      console.error("Error sending message via WebSocket:", error);
      throw error;
    }
  };

  const sendTyping = typingInfo => {
    if (!stompClient.current || !stompClient.current.connected) {
      console.error("Cannot send typing: WebSocket not connected");
      return;
    }
    
    try {
      stompClient.current.publish({
        destination: "/app/typing",
        body: JSON.stringify(typingInfo)
      });
    } catch (error) {
      console.error("useChatSocket - Error sending typing:", error);
    }
  };

  const sendRead = readInfo => {
    if (!stompClient.current || !stompClient.current.connected) {
      console.error("Cannot send read receipt: WebSocket not connected");
      return;
    }
    
    try {
      stompClient.current.publish({
        destination: "/app/read",
        body: JSON.stringify(readInfo)
      });
    } catch (error) {
      console.error("Error sending read receipt:", error);
    }
  };

  const fetchHistory = useCallback(async (chatId) => {
    try {
      const res = await API.get(`/api/chat/history/${chatId}`);
      // Sort messages by sentAt timestamp to ensure correct order
      const sortedMessages = res.data.sort((a, b) => new Date(a.sentAt) - new Date(b.sentAt));
      setMessages(sortedMessages);
      return sortedMessages;
    } catch (error) {
      console.error("Error fetching chat history:", error);
      return [];
    }
  }, []); // No dependencies needed since it only uses the chatId parameter

  const createChatRoom = async (memberUsername, consultantUsername) => {
    try {
      const res = await API.post("/api/chat/room", {
        memberUsername,
        consultantUsername
      });
      return res.data;
    } catch (error) {
      console.error("Error creating chat room:", error);
      throw error;
    }
  };

  const getActiveRooms = async () => {
    try {
      const res = await API.get("/api/chat/rooms");
      return res.data;
    } catch (error) {
      console.error("Error fetching active rooms:", error);
      return [];
    }
  };

  const getUnreadCount = async () => {
    try {
      const res = await API.get("/api/chat/unread-count");
      return res.data.unreadCount || 0;
    } catch (error) {
      console.error("Error fetching unread count:", error);
      return 0;
    }
  };

  return {
    connected,
    messages,
    typingUsers,
    sendMessage,
    sendTyping,
    sendRead,
    fetchHistory,
    createChatRoom,
    getActiveRooms,
    getUnreadCount
  };
}
