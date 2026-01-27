import React from "react";
// import { useTranslation } from "react-i18next";

export default function UserList({ users = [], selectedUser, onSelect, role }) {
  // const { t } = useTranslation("chatPage");
  
  console.log("UserList - selectedUser:", selectedUser);
  console.log("UserList - users:", users);
  
  const getInitials = (user) => {
    const name = user.name || user.username || user.email || "U";
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  };

  return (
    <div style={{ height: "100%", display: "flex", flexDirection: "column" }}>
      <div className="user-list-header" style={{ 
        padding: 16, 
        background: "#e9ecef", 
        borderBottom: "1px solid #dee2e6",
        fontWeight: "bold",
        fontSize: "1.1rem",
        color: "#495057"
      }}>
        {role === "consultant" ? "Members" : "Consultants"}
      </div>
      
      <div style={{ flex: 1, overflowY: "auto" }}>
        {users.length === 0 ? (
          <div className="no-users" style={{ 
            padding: 16, 
            textAlign: "center", 
            color: "#6c757d",
            fontStyle: "italic"
          }}>
            No users found.
          </div>
        ) : (
          users.map(u => {
            const isSelected = selectedUser && (
              (selectedUser.id && u.id && selectedUser.id === u.id) ||
              (selectedUser.username && u.username && selectedUser.username === u.username) ||
              (selectedUser === u)
            );
            return (
              <div
                key={u.id || u.username}
                className={`user-item${isSelected ? ' selected' : ''}`}
                onClick={() => onSelect(u)}
                style={{
                  padding: "12px 16px",
                  cursor: "pointer",
                  borderBottom: "1px solid #e9ecef",
                  backgroundColor: isSelected ? "#007bff" : "transparent",
                  color: isSelected ? "white" : "#212529",
                  display: "flex",
                  alignItems: "center",
                  transition: "all 0.2s ease"
                }}
              >
                <div className="user-avatar" style={{
                  width: 40,
                  height: 40,
                  borderRadius: "50%",
                  backgroundColor: isSelected ? "#0056b3" : "#6c757d",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  color: "white",
                  fontWeight: "bold",
                  marginRight: 12,
                  fontSize: "0.9rem"
                }}>
                  {getInitials(u)}
                </div>
                <div className="user-name" style={{ fontWeight: 500 }}>
                  {u.name || u.username || u.email}
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}