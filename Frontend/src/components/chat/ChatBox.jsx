import React, { useEffect, useRef } from "react";
// import { useTranslation } from "react-i18next";

export default function ChatBox({
  userId,
  selectedUser,
  messages,
  typingUsers,
  input,
  onInputChange,
  onSend,
  onRead,
  connected,
  onClose
}) {
  // const { t } = useTranslation("chatPage");
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (selectedUser) {
      scrollToBottom();
    }
  }, [selectedUser]);

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      onSend();
    }
  };

  const handleInputChangeLocal = (e) => {
    onInputChange(e);
  };

  const handleInputFocus = (e) => {
    onRead && onRead();
  };

  const handleSendClick = () => {
    onSend();
  };

  const isMyMessage = (msg) => {
    return msg.senderUsername === userId || msg.senderId === userId;
  };

  return (
    <div className="chat-box-panel">
      {/* Header */}
      <div className="chat-header">
        <h5 className="chat-header-title">
          {selectedUser
            ? (selectedUser.name || selectedUser.username || selectedUser.email)
            : "Select a user"
          }
          <span className={`connection-dot ${connected ? "online" : "offline"}`} title={connected ? "Connected" : "Offline mode"}></span>
        </h5>
        {onClose && (
          <button
            onClick={onClose}
            className="chat-close-btn"
            aria-label="Close chat"
          >
            x
          </button>
        )}
      </div>

      {/* Messages Area */}
      <div
        className="chat-history"
        onClick={onRead}
      >
        {selectedUser ? (
          messages.length > 0 ? (
            messages.map((msg, idx) => (
              (() => {
                const mine = isMyMessage(msg);
                return (
                  <div
                    key={msg.messageId || idx}
                    className={`message ${mine ? "my-message" : "other-message"}`}
                  >
                    <div className="message-sender">
                      {mine ? "You" : msg.senderUsername || "Other"}
                    </div>
                    <div className="message-content">
                      {msg.content}
                    </div>
                    <div className="message-time">
                      <span>{new Date(msg.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                      {mine && (
                        <span className="message-status">
                          {msg.isRead ? "✓✓" : msg.isDelivered ? "✓" : "⏳"}
                        </span>
                      )}
                    </div>
                  </div>
                );
              })()
            ))
          ) : (
            <div className="no-messages">
              No messages yet.
            </div>
          )
        ) : (
          <div className="select-user">
            Select a user
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Typing Indicator */}
      <div className="chat-typing">
        {typingUsers.length > 0 && (
          <span>{typingUsers.join(", ")} typing...</span>
        )}
      </div>

      {/* Input Area */}
      <div className="chat-input-area">
        <input
          className="chat-input"
          type="text"
          value={input}
          onChange={handleInputChangeLocal}
          onKeyPress={handleKeyPress}
          onFocus={handleInputFocus}
          placeholder={selectedUser ? "Type a message..." : "Select a user"}
          disabled={!selectedUser}
        />
        <button
          className="chat-send-btn"
          onClick={handleSendClick}
          disabled={!selectedUser || !input.trim()}
        >
          {connected ? "Send" : "Send (Offline)"}
        </button>
      </div>
    </div>
  );
}