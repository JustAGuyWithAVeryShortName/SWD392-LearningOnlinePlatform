import React, { useEffect, useState } from "react";
import { Container, Row, Col, Table, Button, Form, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import useFetch from "../../hooks/useFetch";
import { toast } from "react-toastify";

function UserManagement() {
  const navigate = useNavigate();
  const { get } = useFetch();

  const [users, setUsers] = useState([]);
  const [roleFilter, setRoleFilter] = useState("ALL");

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
  try {
    const res = await get("http://localhost:8080/api/user/no-admin");
    console.log("RAW RESPONSE:", res);
    setUsers(res || []);
  } catch (err) {
    toast.error("Failed to load users");
  }
};

  const filteredUsers =
    roleFilter === "ALL"
      ? users
      : users.filter((u) => u.role === roleFilter);

  return (
    <Container fluid className="px-4 py-4">
      {/* Header */}
      <Row className="mb-3">
        <Col>
          <h3>User Management</h3>
        </Col>
        <Col md="auto">
          <Form.Select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
          >
            <option value="ALL">All Roles</option>
            <option value="MEMBER">Member</option>
            <option value="STAFF">Staff</option>
            <option value="CONSULTANT">Consultant</option>
            <option value="MANAGER">Manager</option>
          </Form.Select>
        </Col>
      </Row>

      {/* Table */}
      <Row>
        <Col>
          <Table striped bordered hover responsive>
            <thead>
              <tr>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.length === 0 && (
                <tr>
                  <td colSpan={6} className="text-center">
                    No users found
                  </td>
                </tr>
              )}

              {filteredUsers.map((user) => (
                <tr key={user.username}>
                  <td>{user.username}</td>
                  <td>{user.fullName}</td>
                  <td>{user.email}</td>
                  <td>
                    <Badge bg="info">{user.role}</Badge>
                  </td>
                  <td>
                    <Badge bg={user.status === "ACTIVE" ? "success" : "secondary"}>
                      {user.status}
                    </Badge>
                  </td>
                  <td>
                    <Button
                      size="sm"
                      variant="primary"
                      onClick={() => navigate(`/user/${user.username}`)}
                    >
                      View
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Col>
      </Row>
    </Container>
  );
}

export default UserManagement;