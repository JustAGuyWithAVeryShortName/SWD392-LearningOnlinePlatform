import React, { useState } from "react";
import {
  Navbar as BootstrapNavbar,
  Button,
  Container,
  Dropdown,
  Nav,
} from "react-bootstrap";
import { Link, useNavigate, useLocation } from "react-router-dom";
import "./Navbar.css";
import { useAuth } from "../../hooks/useAuth";
import { LogOut, User, BookOpen } from "lucide-react";
import { useTranslation } from "react-i18next";
import LanguageSwitcher from "./LanguageSwitcher";

export default function Navbar() {
  const { t, i18n } = useTranslation("navbar");
  const location = useLocation();

  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const role = user?.role;
  let homePath = "/"; // default
  if (role === "STAFF") homePath = "/staff";
  else if (role === "MANAGER") homePath = "/manager";
  else if (role === "CONSULTANT") homePath = "/consultant";

  let coursePath = "/courses";
  if (role === "STAFF") coursePath = "/staff/courses";
  else if (role === "MANAGER") coursePath = "/manager/courses";
  else if (role === "CONSULTANT") coursePath = "/consultant/courses";

  let blogPath = "/blogs";
  if (role === "STAFF") blogPath = "/staff/blogs";
  else if (role === "MANAGER") blogPath = "/manager/blogs";
  else if (role === "CONSULTANT") blogPath = "/consultant/blogs";

  let navItems = [
    { name: t("home"), path: homePath },
    { name: t("blogs"), path: blogPath },
    { name: t("courses"), path: coursePath },
  ];

  // Add chat to navigation for consultants and members
  if (role === "CONSULTANT" || role === "MEMBER") {
    navItems.push({ name: t("chat"), path: "/chat" });
  }

  const handleLanguageChange = (lng) => {
    i18n.changeLanguage(lng);
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const UserAvatar = () => (
    <div className="user-avatar">
      <svg
        width="32"
        height="32"
        viewBox="0 0 32 32"
        fill="none"
        className="avatar-icon"
      >
        <circle cx="16" cy="16" r="16" fill="#6c757d" />
        <circle cx="16" cy="12" r="5" fill="white" />
        <path d="M6 26c0-5.5 4.5-10 10-10s10 4.5 10 10" fill="white" />
      </svg>
    </div>
  );

  return (
    <BootstrapNavbar expand="lg" className="navbar-custom py-3">
      <Container>
        <BootstrapNavbar.Brand
          as={Link}
          to="/"
          className="d-flex align-items-center"
        >
          <div className="logo-icon me-2">
            <img
              src="/src/images/6.png"
              alt="EnglishPath"
              className="logo-img"
            />
          </div>
          <span className="fw-bold fs-4 text-dark">{t("projectName")}</span>
        </BootstrapNavbar.Brand>

        <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav" />
        <BootstrapNavbar.Collapse id="basic-navbar-nav">
          <Nav className="mx-auto align-items-center">
            {navItems.map((item) => (
              <Nav.Link
                key={item.name}
                as={Link}
                to={item.path}
                className={`nav-item-custom mx-2 ${location.pathname === item.path ? "active" : ""
                  }`}
              >
                {item.name}
              </Nav.Link>
            ))}

            <LanguageSwitcher
              currentLanguage={i18n.language}
              onChangeLanguage={handleLanguageChange}
            />
          </Nav>

          <div className="d-flex align-items-center">
            {isAuthenticated ? (
              <Dropdown align="end">
                <Dropdown.Toggle
                  variant="link"
                  id="user-dropdown"
                  className="user-dropdown-toggle p-0 border-0 shadow-none"
                >
                  <UserAvatar />
                </Dropdown.Toggle>

                <Dropdown.Menu className="user-dropdown-menu">
                  <div className="dropdown-header">
                    <div className="user-info">
                      <div className="user-name">
                        {user?.username || t("userPlaceholder")}
                      </div>
                    </div>
                  </div>
                  <Dropdown.Divider />
                  <Dropdown.Item
                    as={Link}
                    to="/profile"
                    className="dropdown-item-custom"
                  >
                    <User size={16} className="me-2" />
                    {t("myProfile")}
                  </Dropdown.Item>
                  {role === "MEMBER" && (
                    <Dropdown.Item
                      as={Link}
                      to="/payment/history"
                      className="dropdown-item-custom"
                    >
                      <BookOpen size={16} className="me-2" />
                      {t("myCourses")}
                    </Dropdown.Item>
                  )}
                  <Dropdown.Item
                    onClick={handleLogout}
                    className="dropdown-item-custom"
                  >
                    <LogOut size={16} className="me-2" />
                    {t("logout")}
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            ) : (
              <div className="d-flex gap-2">
                <Link
                  to="/login"
                  className="btn-outline-primary-custom"
                >
                  {t("login")}
                </Link>
                <Link
                  to="/login"
                  className="btn-primary-custom"
                >
                  {t("register")}
                </Link>
              </div>
            )}
          </div>
        </BootstrapNavbar.Collapse>
      </Container>
    </BootstrapNavbar>
  );
}
