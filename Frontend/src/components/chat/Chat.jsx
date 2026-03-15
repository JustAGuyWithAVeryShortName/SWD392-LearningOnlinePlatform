import React, { useEffect, useState } from "react";
import useChatSocket from "../../hooks/useChatSocket";
import API from "../../api";
import UserList from "./UserList";
import ChatBox from "./ChatBox";

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

export default function Chat({ userId, role, onClose }) {
  const [input, setInput] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [userList, setUserList] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [chatId, setChatId] = useState(null);

  console.log("Original Chat - Props:", { userId, role });

  const {
    connected,
    messages,
    typingUsers,
    sendMessage,
    sendTyping,
    sendRead,
    fetchHistory
  } = useChatSocket({ userId, role });

  // Fetch user list (consultants for members, members for consultants)
  useEffect(() => {
    const fetchUsers = async () => {
      const targetRole = role === "consultant" ? "MEMBER" : "CONSULTANT";
      const endpoint = `/api/user/role/${targetRole}`;

      try {
        console.log("Original Chat - Fetching users from endpoint:", endpoint);
        console.log("Original Chat - Current user role:", role);
        const res = await API.get(endpoint);
        console.log("Original Chat - API response status:", res.status);
        console.log("Original Chat - Full API response:", res);
        console.log("Original Chat - Response data:", res.data);

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

        console.log("Original Chat - Final processed user data:", userData);

        if (Array.isArray(userData)) {
          const normalizedUsers = userData
            .map((item) => ({
              ...item,
              username: resolveChatIdentity(item),
            }))
            .filter((item) => item.username);

          setUserList(normalizedUsers);
          console.log("Original Chat - Successfully set user list with", userData.length, "users");
        } else {
          console.log("Original Chat - Data is not an array, setting empty list");
          setUserList([]);
        }

      } catch (e) {
        console.error("Original Chat - Failed to fetch users:", e);
        setUserList([]);
      }
    };
    fetchUsers();
  }, [role]);

  // When a user is selected, set chatId and fetch history
  useEffect(() => {
    if (selectedUser) {
      const selectedUserId = resolveChatIdentity(selectedUser);
      const currentUserId = resolveChatIdentity(userId);
      if (!selectedUserId || !currentUserId) return;

      const newChatId =
        currentUserId < selectedUserId
          ? `${currentUserId}-${selectedUserId}`
          : `${selectedUserId}-${currentUserId}`;
      setChatId(newChatId);
      fetchHistory(newChatId);
    }
  }, [selectedUser, userId, fetchHistory]);

  useEffect(() => {
    if (isTyping && chatId) {
      sendTyping({ chatId, userId });
    }
  }, [isTyping, chatId, sendTyping, userId]);

  const handleSend = () => {
    if (!input.trim() || !selectedUser) return;

    const selectedUserId = resolveChatIdentity(selectedUser);
    const currentUserId = resolveChatIdentity(userId);
    if (!selectedUserId || !currentUserId) return;

    sendMessage({
      chatId,
      senderId: currentUserId,
      recipientId: selectedUserId,
      content: input,
      role
    });
    setInput("");
    setIsTyping(false);
  };

  const handleInputChange = e => {
    setInput(e.target.value);
    setIsTyping(true);
  };

  const handleRead = () => {
    if (chatId) sendRead({ chatId, userId });
  };

  if (!['consultant', 'member'].includes(role)) {
    return <div>Chat is only available for consultant and member roles.</div>;
  }

  return (
    <div className="messenger-container" style={{ display: "flex", height: 400, width: 600, background: "#fff", borderRadius: 8, boxShadow: "0 2px 8px rgba(0,0,0,0.15)" }}>
      <UserList
        users={userList}
        selectedUser={selectedUser}
        onSelect={setSelectedUser}
        role={role}
      />
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
        onClose={onClose}
      />
    </div>
  );
}
