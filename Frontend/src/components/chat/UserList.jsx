import React from "react";
// import { useTranslation } from "react-i18next";

export default function UserList({ users = [], selectedUser, onSelect, role }) {
  // const { t } = useTranslation("chatPage");

  const getInitials = (user) => {
    const name = user.name || user.username || user.email || "U";
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  };

  const getIdentity = (user) => user?.username || user?.email || user?.id;

  return (
    <div className="chat-user-list">
      <div className="user-list-header">
        <div className="user-list-header-title">
          {role === "consultant" ? "Members" : "Consultants"}
        </div>
        <div className="user-list-header-count">{users.length}</div>
      </div>

      <div className="user-list-scroll">
        {users.length === 0 ? (
          <div className="no-users">
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
                key={u.id || u.username || u.email}
                className={`user-item${isSelected ? ' selected' : ''}`}
                onClick={() => onSelect(u)}
              >
                <div className="user-avatar">
                  {getInitials(u)}
                </div>
                <div className="user-text">
                  <div className="user-name">{u.name || u.username || u.email}</div>
                  <div className="user-handle">@{getIdentity(u)}</div>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}