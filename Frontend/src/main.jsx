import { Buffer } from 'buffer';
window.global = window;
window.process = { env: {} };
window.Buffer = Buffer;

import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import "./index.css"
import App from "./App.jsx"
import { AuthProvider } from "./context/AuthContext";
import { BrowserRouter } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import './i18n.jsx'
import "./styles/root.css";
import "./styles/bootstrap-theme.css";
createRoot(document.getElementById("root")).render(
  // <StrictMode>
  <BrowserRouter>
    <AuthProvider>
      <App />
      <ToastContainer />
    </AuthProvider>
  </BrowserRouter>
  // </StrictMode>,
)
