import { useState } from "react";
import { useAuth } from "../../hooks/useAuth";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";
import { Eye, EyeOff } from "lucide-react";
import "./Login.css";

export default function Login() {
  const { t } = useTranslation("loginPage");
  const { login, register, authLoading } = useAuth();

  const [activeTab, setActiveTab] = useState("login");

  const [showLoginPassword, setShowLoginPassword] = useState(false);
  const [showRegisterPassword, setShowRegisterPassword] = useState(false);
  const [showRegisterConfirmPassword, setShowRegisterConfirmPassword] =
    useState(false);

  const [loginData, setLoginData] = useState({
    username: "",
    password: "",
    rememberMe: false,
  });

  const [registerData, setRegisterData] = useState({
    username: "",
    password: "",
    confirmPassword: "",
  });

  const handleLoginChange = (e) => {
    const { name, value, type, checked } = e.target;

    setLoginData({
      ...loginData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const handleRegisterChange = (e) => {
    const { name, value } = e.target;

    setRegisterData({
      ...registerData,
      [name]: value,
    });
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    await login(loginData.username, loginData.password);
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();

    if (registerData.password !== registerData.confirmPassword) {
      toast.error(t("registerForm.passwordMismatch"));
      return;
    }

    await register(
      registerData.username,
      registerData.password,
      registerData.confirmPassword
    );
  };

  const handleGoogleLogin = () => {
    window.location.href =
      "http://localhost:8080/oauth2/authorization/google";
  };

  if (authLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="login-page">

      <div className="login-card">

        {/* LEFT BRAND */}
        <div className="login-brand">

          <h1 className="brand-title">{t("brand.title")}</h1>

          <p className="brand-subtitle">{t("brand.subtitle")}</p>

        </div>

        {/* RIGHT FORM */}
        <div className="login-form">

          <img
            src="/src/images/7.png"
            alt="logo"
            className="login-logo"
            onClick={() => window.history.back()}
          />

          {/* TABS */}
          <div className="tabs">

            <button
              className={
                activeTab === "login" ? "tab active-tab" : "tab"
              }
              onClick={() => setActiveTab("login")}
            >
              {t("tabs.login")}
            </button>

            <button
              className={
                activeTab === "register" ? "tab active-tab" : "tab"
              }
              onClick={() => setActiveTab("register")}
            >
              {t("tabs.register")}
            </button>

          </div>

          {/* LOGIN FORM */}
          {activeTab === "login" && (
            <form onSubmit={handleLoginSubmit} className="form">

              <label>{t("loginForm.usernameLabel")}</label>

              <input
                type="text"
                name="username"
                placeholder={t("loginForm.usernamePlaceholder")}
                value={loginData.username}
                onChange={handleLoginChange}
                required
              />

              <label>{t("loginForm.passwordLabel")}</label>

              <div className="password-input">

                <input
                  type={showLoginPassword ? "text" : "password"}
                  name="password"
                  placeholder={t("loginForm.passwordPlaceholder")}
                  value={loginData.password}
                  onChange={handleLoginChange}
                  required
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowLoginPassword(!showLoginPassword)
                  }
                >
                  {showLoginPassword ? <EyeOff size={18}/> : <Eye size={18}/>}
                </button>

              </div>

              <label className="remember">

                <input
                  type="checkbox"
                  name="rememberMe"
                  checked={loginData.rememberMe}
                  onChange={handleLoginChange}
                />

                {t("loginForm.rememberMe")}

              </label>

              <button className="submit-btn">
                {t("loginForm.submitButton")}
              </button>

              <div className="divider">
                <span>{t("dividerText")}</span>
              </div>

              <button
                type="button"
                className="google-btn"
                onClick={handleGoogleLogin}
              >
                {t("googleLoginButton")}
              </button>

            </form>
          )}

          {/* REGISTER */}
          {activeTab === "register" && (
            <form onSubmit={handleRegisterSubmit} className="form">

              <label>{t("registerForm.usernameLabel")}</label>

              <input
                type="text"
                name="username"
                value={registerData.username}
                onChange={handleRegisterChange}
                required
              />

              <label>{t("registerForm.passwordLabel")}</label>

              <div className="password-input">

                <input
                  type={showRegisterPassword ? "text" : "password"}
                  name="password"
                  value={registerData.password}
                  onChange={handleRegisterChange}
                  required
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowRegisterPassword(!showRegisterPassword)
                  }
                >
                  {showRegisterPassword ? <EyeOff size={18}/> : <Eye size={18}/>}
                </button>

              </div>

              <label>{t("registerForm.confirmPasswordLabel")}</label>

              <div className="password-input">

                <input
                  type={
                    showRegisterConfirmPassword
                      ? "text"
                      : "password"
                  }
                  name="confirmPassword"
                  value={registerData.confirmPassword}
                  onChange={handleRegisterChange}
                  required
                />

                <button
                  type="button"
                  onClick={() =>
                    setShowRegisterConfirmPassword(
                      !showRegisterConfirmPassword
                    )
                  }
                >
                  {showRegisterConfirmPassword ? <EyeOff size={18}/> : <Eye size={18}/>}
                </button>

              </div>

              <button className="submit-btn">
                {t("registerForm.submitButton")}
              </button>

            </form>
          )}

        </div>

      </div>

    </div>
  );
}