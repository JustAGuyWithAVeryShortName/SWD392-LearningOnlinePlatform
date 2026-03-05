import { Container, Row, Col } from "react-bootstrap";
import { Globe, ArrowUp, Mail, Github, Facebook } from "lucide-react";
import { Link } from "react-router-dom";
import "./Footer.css";
import { useTranslation } from "react-i18next";

const Footer = () => {
  const { t } = useTranslation("footer");

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  return (
    <footer className="site-footer">
      <div className="footer-content">
        <Container>

          {/* Brand */}
          <div className="footer-brand">
            <Link to="/" className="footer-logo">
              <span className="brand-text">{t("projectName")}</span>
            </Link>
            <p className="footer-description">
              A modern e-learning platform helping people learn and share
              knowledge anywhere.
            </p>
          </div>

          {/* Links */}
          <Row className="footer-links-grid">

            <Col md={3} sm={6}>
              <h6 className="footer-title">Product</h6>
              <Link to="/" className="footer-link">Features</Link>
              <Link to="/" className="footer-link">Pricing</Link>
              <Link to="/" className="footer-link">API</Link>
            </Col>

            <Col md={3} sm={6}>
              <h6 className="footer-title">Company</h6>
              <Link to="/" className="footer-link">About</Link>
              <Link to="/" className="footer-link">Careers</Link>
              <Link to="/" className="footer-link">Blog</Link>
            </Col>

            <Col md={3} sm={6}>
              <h6 className="footer-title">Resources</h6>
              <Link to="/terms" className="footer-link">{t("termsOfService")}</Link>
              <Link to="/privacy" className="footer-link">{t("privacyPolicy")}</Link>
              <Link to="/contact" className="footer-link">{t("contactUs")}</Link>
            </Col>

            <Col md={3} sm={6}>
              <h6 className="footer-title">Contact</h6>

              <div className="footer-contact">
                <Mail size={16} />
                support@email.com
              </div>

              <div className="footer-social">
                <a href="#">
                  <Github size={18} />
                </a>

                <a href="#">
                  <Facebook size={18} />
                </a>

                <button onClick={scrollToTop}>
                  <ArrowUp size={18} />
                </button>
              </div>

            </Col>

          </Row>

          {/* Bottom */}
          <div className="footer-bottom">
            <p>{t("copyright")}</p>

            <button className="footer-language">
              <Globe size={16} /> EN
            </button>
          </div>

        </Container>
      </div>
    </footer>
  );
};

export default Footer;