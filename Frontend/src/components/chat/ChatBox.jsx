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

  return (
    <div style={{ height: "100%", display: "flex", flexDirection: "column" }}>
      {/* Header */}
      <div className="chat-header" style={{ 
        padding: "16px 20px", 
        background: "#f8f9fa", 
        borderBottom: "2px solid #e9ecef",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between"
      }}>
        <h5 className="chat-header-title" style={{ 
          fontWeight: 600, 
          fontSize: "1.2rem", 
          color: "#495057",
          margin: 0,
          display: "flex",
          alignItems: "center",
          gap: "8px"
        }}>
          {selectedUser 
            ? (selectedUser.name || selectedUser.username || selectedUser.email)
            : "Select a user"
          }
          <span style={{
            width: 8,
            height: 8,
            borderRadius: "50%",
            backgroundColor: connected ? "#28a745" : "#dc3545",
            display: "inline-block"
          }} title={connected ? "Connected" : "Offline mode"}></span>
        </h5>
        {onClose && (
          <button
            onClick={onClose}
            style={{
              border: "none",
              background: "transparent",
              fontSize: 20,
              cursor: "pointer",
              color: "#6c757d"
            }}
            aria-label="Close chat"
          >
            ×
          </button>
        )}
      </div>

      {/* Messages Area */}
      <div 
        className="chat-history" 
        onClick={onRead} 
        style={{ 
          flex: 1, 
          overflowY: "auto", 
          padding: "20px 16px",
          backgroundColor: "#ffffff",
          scrollBehavior: "smooth"
        }}
      >
        {selectedUser ? (
          messages.length > 0 ? (
            messages.map((msg, idx) => (
              <div 
                key={msg.messageId || idx} 
                className={`message ${msg.senderUsername === userId ? "my-message" : "other-message"}`}
                style={{
                  marginBottom: 12,
                  padding: "10px 14px",
                  borderRadius: msg.senderUsername === userId ? "18px 18px 4px 18px" : "18px 18px 18px 4px",
                  minWidth: "80px",
                  maxWidth: window.innerWidth > 768 ? "65%" : "85%",
                  width: "fit-content",
                  wordWrap: "break-word",
                  wordBreak: "break-word",
                  backgroundColor: msg.senderUsername === userId ? "#007bff" : "#f1f3f5",
                  color: msg.senderUsername === userId ? "white" : "#495057",
                  marginLeft: msg.senderUsername === userId ? "auto" : "0",
                  marginRight: msg.senderUsername === userId ? "0" : "auto",
                  textAlign: msg.senderUsername === userId ? "right" : "left",
                  boxShadow: "0 1px 2px rgba(0,0,0,0.1)",
                  position: "relative"
                }}
              >
                <div className="message-sender" style={{
                  fontWeight: 600,
                  fontSize: "0.8rem",
                  marginBottom: 2,
                  opacity: 0.8
                }}>
                  {msg.senderUsername === userId ? "You" : msg.senderUsername || "Other"}
                </div>
                <div className="message-content" style={{ 
                  fontSize: "0.95rem",
                  lineHeight: 1.4,
                  marginBottom: 4
                }}>
                  {msg.content}
                </div>
                <div className="message-time" style={{ 
                  fontSize: "0.7rem", 
                  opacity: 0.6, 
                  marginTop: 2,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: msg.senderUsername === userId ? "flex-end" : "flex-start",
                  gap: "4px"
                }}>
                  <span>{new Date(msg.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                  {msg.senderUsername === userId && (
                    <span style={{ 
                      fontSize: "0.65rem",
                      color: msg.isRead ? "#00ff88" : msg.isDelivered ? "#88ccff" : "#ffcc88"
                    }}>
                      {msg.isRead ? "✓✓" : msg.isDelivered ? "✓" : "⏳"}
                    </span>
                  )}
                </div>
              </div>
            ))
          ) : (
            <div className="no-messages" style={{ 
              textAlign: "center", 
              color: "#6c757d",
              fontStyle: "italic",
              padding: "40px 20px"
            }}>
              No messages yet.
            </div>
          )
        ) : (
          <div className="select-user" style={{ 
            textAlign: "center", 
            color: "#6c757d",
            fontStyle: "italic",
            padding: "40px 20px"
          }}>
            Select a user
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Typing Indicator */}
      <div className="chat-typing" style={{ 
        padding: "8px 20px", 
        minHeight: 24,
        color: "#6c757d",
        fontStyle: "italic",
        fontSize: "0.85rem",
        backgroundColor: "#f8f9fa",
        borderTop: "1px solid #e9ecef"
      }}>
        {typingUsers.length > 0 && (
          <span>{typingUsers.join(", ")} typing...</span>
        )}
      </div>

      {/* Input Area */}
      <div className="chat-input-area" style={{ 
        padding: "16px 20px", 
        backgroundColor: "#f8f9fa",
        borderTop: "2px solid #e9ecef",
        display: "flex",
        gap: 12,
        alignItems: "center",
        position: "relative",
        zIndex: 1000,
        flexShrink: 0,
        minHeight: "80px"
      }}>
        <input
          className="chat-input"
          type="text"
          value={input}
          onChange={handleInputChangeLocal}
          onKeyPress={handleKeyPress}
          onFocus={handleInputFocus}
          placeholder={selectedUser ? "Type a message..." : "Select a user"}
          disabled={!selectedUser}
          style={{
            flex: 1,
            padding: "10px 16px",
            border: "1px solid #ced4da",
            borderRadius: 20,
            fontSize: "0.95rem",
            outline: "none",
            transition: "border-color 0.2s ease",
            backgroundColor: !selectedUser ? "#f8f9fa" : "white"
          }}
        />
        <button 
          className="chat-send-btn"
          onClick={handleSendClick} 
          disabled={!selectedUser || !input.trim()}
          style={{
            padding: "10px 20px",
            backgroundColor: (!selectedUser || !input.trim()) ? "#6c757d" : connected ? "#007bff" : "#28a745",
            color: "white",
            border: "none",
            borderRadius: 20,
            fontWeight: 500,
            cursor: (!selectedUser || !input.trim()) ? "not-allowed" : "pointer",
            transition: "background-color 0.2s ease"
          }}
        >
          Send {!connected && "📱"}
        </button>
      </div>
    </div>
  );
}