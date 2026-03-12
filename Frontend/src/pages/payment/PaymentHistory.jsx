import { useState, useEffect } from "react";
import { Container, Table, Badge, Spinner } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import useFetch from "../../hooks/useFetch";
import BackButton from "../../components/BackButton";
import "./PaymentHistory.css";

const formatVND = (amount) =>
    new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);

const formatDate = (dateStr) =>
    dateStr
        ? new Date(dateStr).toLocaleDateString("vi-VN", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        })
        : "—";

const STATUS_VARIANT = {
    SUCCESS: "success",
    FAILED: "danger",
    PENDING: "warning",
};

const PaymentHistory = () => {
    const { t } = useTranslation("paymentHistory");
    const { loading, get: getHistory } = useFetch();
    const [payments, setPayments] = useState([]);

    useEffect(() => {
        const fetch = async () => {
            try {
                const data = await getHistory("http://localhost:8080/api/payment/my-history");
                setPayments(Array.isArray(data) ? data : []);
            } catch (error) {
                console.error("Failed to fetch payment history:", error);
                setPayments([]);
            }
        };
        fetch();
    }, [getHistory]);

    return (
        <Container className="payment-history-container py-5">
            <BackButton label="Quay lại" />
            <h2 className="payment-history-title mb-4">{t("title")}</h2>

            {loading ? (
                <div className="text-center py-5">
                    <Spinner animation="border" variant="primary" />
                    <p className="mt-3 text-muted">{t("loading")}</p>
                </div>
            ) : payments.length === 0 ? (
                <p className="text-center text-muted py-5">{t("empty")}</p>
            ) : (
                <div className="table-responsive payment-history-table-wrapper">
                    <Table hover className="payment-history-table align-middle">
                        <thead>
                            <tr>
                                <th>{t("table.orderId")}</th>
                                <th>{t("table.courseName")}</th>
                                <th>{t("table.amount")}</th>
                                <th>{t("table.status")}</th>
                                <th>{t("table.date")}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {payments.map((p) => (
                                <tr key={p.paymentId}>
                                    <td className="text-muted" style={{ fontSize: "0.82rem", maxWidth: 180, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                                        {p.orderId}
                                    </td>
                                    <td className="fw-semibold">{p.course?.courseName || "—"}</td>
                                    <td className="fw-bold text-primary">{formatVND(p.amount)}</td>
                                    <td>
                                        <Badge bg={STATUS_VARIANT[p.status] || "secondary"}>
                                            {t(`table.statuses.${p.status}`, { defaultValue: p.status })}
                                        </Badge>
                                    </td>
                                    <td>{formatDate(p.createdAt)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </div>
            )}
        </Container>
    );
};

export default PaymentHistory;
