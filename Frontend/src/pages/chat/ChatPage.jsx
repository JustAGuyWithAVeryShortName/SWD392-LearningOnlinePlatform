import React, { useEffect, useState, useRef } from "react";
import { Container, Row, Col, Card } from "react-bootstrap";
import { useAuth } from "../../hooks/useAuth";
import useChatSocket from "../../hooks/useChatSocket";
import UserList from "../../components/chat/UserList";
import ChatBox from "../../components/chat/ChatBox";
import Navbar from "../../components/home/Navbar";
import API from "../../api";
import "./ChatPage.css";

const resolveChatIdentity = (value) => {
  if (!value) return "";
  if (typeof value === "string" || typeof value === "number") {
    return String(value).trim();
  }

  return String(
    value.username
    || value.userName
    || value.email
    || value.id
    || ""
  ).trim();
};

export default function ChatPage() {
  const { user } = useAuth();
  const [input, setInput] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [userList, setUserList] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [chatId, setChatId] = useState(null);
  const lastFetchedChatId = useRef(null);

  const userId = resolveChatIdentity(user);
  const role = user?.role?.toLowerCase();

  const {
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
  } = useChatSocket({ userId, role });

  // Fetch user list (consultants for members, members for consultants)
  useEffect(() => {
    const fetchUsers = async () => {
      if (!role || !['consultant', 'member'].includes(role)) {
        return;
      }

      const targetRole = role === "consultant" ? "MEMBER" : "CONSULTANT";
      const endpoint = `/api/user/role/${targetRole}`;

      try {
        const res = await API.get(endpoint);

        // Try multiple possible response structures
        let userData = null;

        if (Array.isArray(res.data)) {
          userData = res.data;
        } else if (res.data?.data && Array.isArray(res.data.data)) {
          userData = res.data.data;
        } else if (res.data?.users && Array.isArray(res.data.users)) {
          userData = res.data.users;
        } else if (res.data?.content && Array.isArray(res.data.content)) {
          userData = res.data.content;
        } else if (res.data?.result && Array.isArray(res.data.result)) {
          userData = res.data.result;
        } else {
          userData = res.data;
        }

        if (Array.isArray(userData)) {
          const normalizedUsers = userData
            .map((item) => ({
              ...item,
              username: resolveChatIdentity(item),
            }))
            .filter((item) => item.username);

          setUserList(normalizedUsers);
        } else {
          setUserList([]);
        }

      } catch (e) {
        console.error("Failed to fetch users:", e);
        setUserList([]);
      }
    };
    fetchUsers();
  }, [role]);

  // When a user is selected, set chatId and fetch history
  useEffect(() => {
    if (selectedUser && userId) {
      const selectedUserId = resolveChatIdentity(selectedUser);
      const currentUserId = userId;

      if (!selectedUserId) {
        console.error("Selected user does not have a valid chat identity", selectedUser);
        return;
      }

      // Use role-based room ID format: memberUsername-consultantUsername
      let newChatId;
      if (role === 'member') {
        newChatId = `${currentUserId}-${selectedUserId}`;
      } else if (role === 'consultant') {
        newChatId = `${selectedUserId}-${currentUserId}`;
      } else {
        // Fallback to alphabetical sorting
        newChatId = currentUserId < selectedUserId
          ? `${currentUserId}-${selectedUserId}`
          : `${selectedUserId}-${currentUserId}`;
      }

      setChatId(newChatId);

      // Only fetch history if chatId has changed
      if (newChatId !== lastFetchedChatId.current) {
        console.log("Fetching history for new chatId:", newChatId);
        lastFetchedChatId.current = newChatId;
        fetchHistory(newChatId);
      }
    } else {
      setChatId(null);
      lastFetchedChatId.current = null;
    }
  }, [selectedUser, userId, role]); // Removed fetchHistory from dependencies

  useEffect(() => {
    if (!chatId) return;

    // Fallback sync: keep current room fresh if backend websocket routing is inconsistent.
    const intervalId = setInterval(() => {
      fetchHistory(chatId);
    }, 2000);

    return () => clearInterval(intervalId);
  }, [chatId, fetchHistory]);

  useEffect(() => {
    if (isTyping && chatId) {
      sendTyping({ chatId, userId });
    }
  }, [isTyping, chatId, sendTyping, userId]);

  const handleSend = () => {
    console.log("handleSend called");
    console.log("Input:", input);
    console.log("Selected user:", selectedUser);
    console.log("Chat ID:", chatId);

    if (!input.trim() || !selectedUser) {
      console.log("Send blocked: empty input or no selected user");
      return;
    }

    const selectedUserId = resolveChatIdentity(selectedUser);
    if (!selectedUserId) {
      console.error("Cannot send message: selected user identity is missing", selectedUser);
      return;
    }

    const messageData = {
      chatId,
      senderId: userId,
      recipientId: selectedUserId,
      content: input,
      role
    };

    console.log("Sending message with data:", messageData);
    sendMessage(messageData);
    setInput("");
    setIsTyping(false);
  };

  const handleInputChange = (e) => {
    setInput(e.target.value);
    setIsTyping(true);
  };

  const handleRead = () => {
    if (chatId) sendRead({ chatId, userId });
  };

  // Unauthorized access check
  if (!user || !['consultant', 'member'].includes(role)) {
    return (
      <Container className="mt-5">
        <Row className="justify-content-center">
          <Col md={6}>
            <Card className="text-center">
              <Card.Body>
                <Card.Title>Access Denied</Card.Title>
                <Card.Text>
                  Chat is only available for members and consultants.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <div className="chat-page-shell">
      <Navbar />
      <div className="chat-page">
        <Row className="chat-page-header">
          <Col>
            <h2 className="chat-page-title">Chat</h2>
            <p className="chat-page-subtitle">
              {connected ? "Connected" : "Connecting..."}
            </p>
            {/* Temporary debug info */}
            {/* <div style={{ fontSize: '12px', color: '#666', marginTop: '10px' }}>
            DEBUG: UserList length: {userList?.length || 0} | Role: {role} | User ID: {userId}
            {userList?.length > 0 && <span> | First user: {JSON.stringify(userList[0])}</span>}
            <br />
            Selected User: {selectedUser ? `${selectedUser.name || selectedUser.username} (ID: ${selectedUser.id || selectedUser.username})` : 'None'} | 
            Chat ID: {chatId || 'None'} | 
            Connected: {connected ? 'Yes' : 'No'} | 
            Input: "{input}"
          </div> */}
          </Col>
        </Row>

        <Row className="chat-page-content h-100 flex-fill mx-0">
          <Col xs={12} className="chat-container h-100 px-0">
            <Card className="chat-card h-100">
              <Card.Body className="p-0 h-100 d-flex flex-column">
                <div className="chat-layout h-100">
                  <div className="chat-sidebar">
                    <UserList
                      users={userList}
                      selectedUser={selectedUser}
                      onSelect={(user) => {
                        setSelectedUser(user);
                      }}
                      role={role}
                    />
                  </div>

                  <div className="chat-main">
                    <ChatBox
                      userId={userId}
                      selectedUser={selectedUser}
                      messages={messages}
                      typingUsers={typingUsers}
                      input={input}
                      onInputChange={handleInputChange}
                      onSend={handleSend}
                      onRead={handleRead}
                      connected={connected}
                    />
                  </div>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  );
}
