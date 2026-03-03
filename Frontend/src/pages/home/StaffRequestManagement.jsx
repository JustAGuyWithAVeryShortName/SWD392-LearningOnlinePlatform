import React, { useEffect, useMemo, useState } from "react";
import { Container, Card, Table, Button, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import useFetch from "../../hooks/useFetch";
import Pagination from "../../components/others/Pagination";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next";
import { useAuth } from "../../hooks/useAuth";

function StaffRequestManagement() {
  const { t } = useTranslation("staffRequest");
  const { user } = useAuth();
  const navigate = useNavigate();
  const { get, put } = useFetch();

  const [requests, setRequests] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    try {
      const data = await get(
        "http://localhost:8080/api/staff-requests/pending"
      );
      setRequests(data || []);
    } catch (err) {
      toast.error("Không thể tải danh sách yêu cầu");
    }
  };

  // Pagination
  const totalPages = Math.ceil(requests.length / itemsPerPage);
  const currentRequests = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return requests.slice(start, start + itemsPerPage);
  }, [requests, currentPage]);

  const handleApprove = async (id) => {
    if (!window.confirm("Duyệt user này thành STAFF?")) return;

    try {
      await put({}, {}, `http://localhost:8080/api/staff-requests/${id}/approve`);
      setRequests(prev => prev.filter(r => r.id !== id));
      toast.success("Đã duyệt thành STAFF");
    } catch {
      toast.error("Duyệt thất bại");
    }
  };

  const handleReject = async (id) => {
    if (!window.confirm("Từ chối yêu cầu này?")) return;

    try {
      await put({}, {}, `http://localhost:8080/api/staff-requests/${id}/reject`);
      setRequests(prev => prev.filter(r => r.id !== id));
      toast.success("Đã từ chối yêu cầu");
    } catch {
      toast.error("Từ chối thất bại");
    }
  };

  return (
    <Container className="mb-5">
      <h1 className="mb-4">Staff Request Management</h1>

      <Card>
        <Card.Header>
          Pending Requests <Badge bg="warning">{requests.length}</Badge>
        </Card.Header>

        <Card.Body style={{ padding: 0 }}>
          <Table bordered hover responsive className="mb-0">
            <thead>
              <tr>
                <th>#</th>
                <th>Username</th>
                <th>Email</th>
                <th>Requested At</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {currentRequests.map((req, index) => (
                <tr key={req.id}>
                  <td>{(currentPage - 1) * itemsPerPage + index + 1}</td>
                  <td>{req.username}</td>
                  <td>{req.email}</td>
                  <td>{new Date(req.createdAt).toLocaleString()}</td>
                  <td>
                    <Badge bg="warning">PENDING</Badge>
                  </td>
                  <td>
                    <div className="d-flex gap-2">
                      <Button
                        size="sm"
                        variant="success"
                        onClick={() => handleApprove(req.id)}
                      >
                        Approve
                      </Button>
                      <Button
                        size="sm"
                        variant="danger"
                        onClick={() => handleReject(req.id)}
                      >
                        Reject
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}

              {currentRequests.length === 0 && (
                <tr>
                  <td colSpan={6} className="text-center py-4 text-muted">
                    Không có yêu cầu nào
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
        </Card.Body>
      </Card>

      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        itemsPerPage={itemsPerPage}
      />
    </Container>
  );
}

export default StaffRequestManagement;