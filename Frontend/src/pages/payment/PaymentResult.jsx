import { useSearchParams, useNavigate } from "react-router-dom";
import { Container, Button } from "react-bootstrap";
import { CheckCircle, XCircle } from "lucide-react";
import { useTranslation } from "react-i18next";
import "./PaymentResult.css";

const MOMO_CODE_KEYS = {
    1003: "1003",
    1006: "1006",
    1007: "1007",
};

const PaymentResult = () => {
    const { t } = useTranslation("paymentResult");
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    const orderId = searchParams.get("orderId") || "";
    const resultCode = parseInt(searchParams.get("resultCode") ?? "-1", 10);
    const rawMessage = searchParams.get("message") || "";

    const isSuccess = resultCode === 0;

    const friendlyMessage = (() => {
        const codeKey = MOMO_CODE_KEYS[resultCode];
        if (codeKey) return t(`failure.codes.${codeKey}`);
        if (rawMessage) return rawMessage;
        return t("failure.defaultMessage");
    })();

    return (
        <div className="payment-result-page">
            <Container className="payment-result-container">
                {isSuccess ? (
                    <div className="payment-result-card payment-result-success">
                        <CheckCircle size={72} className="payment-result-icon text-success" />
                        <h2 className="payment-result-title">{t("success.title")}</h2>
                        <p className="payment-result-message">{t("success.message")}</p>
                        {orderId && (
                            <p className="payment-result-orderid">{t("success.orderId", { orderId })}</p>
                        )}
                        <Button
                            variant="success"
                            size="lg"
                            className="payment-result-btn"
                            onClick={() => navigate("/courses")}
                        >
                            {t("success.button")}
                        </Button>
                    </div>
                ) : (
                    <div className="payment-result-card payment-result-failure">
                        <XCircle size={72} className="payment-result-icon text-danger" />
                        <h2 className="payment-result-title">{t("failure.title")}</h2>
                        <p className="payment-result-message">{friendlyMessage}</p>
                        {orderId && (
                            <p className="payment-result-orderid text-muted" style={{ fontSize: "0.85rem" }}>
                                Mã đơn hàng: {orderId}
                            </p>
                        )}
                        <Button
                            variant="danger"
                            size="lg"
                            className="payment-result-btn"
                            onClick={() => navigate(-1)}
                        >
                            {t("failure.button")}
                        </Button>
                    </div>
                )}
            </Container>
        </div>
    );
};

export default PaymentResult;
