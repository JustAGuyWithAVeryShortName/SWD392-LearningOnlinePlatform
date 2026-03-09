import React, { useEffect, useState } from "react";
import { Container, Row, Col } from "react-bootstrap";
import {
  Users,
  Calendar,
  BookOpen,
  FileText,
  MessageSquare,
  UserCheck,
  Award,
  Target,
  ChevronRight,
} from "lucide-react";
import "./HomeManager.css";
import StatusCard from "../../components/dashboard/StatusCard";
import PendingCard from "../../components/dashboard/PendingCard";
import LineChart from "../../components/dashboard/LineChart";
import AnalyticsPreview from "../../components/dashboard/AnalyticsPreview";
import useFetch from "../../hooks/useFetch";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { toast } from "react-toastify";
import { useTranslation } from "react-i18next"; // Import useTranslation
import StaffRequestManagement from "../home/Staffrequestmanagement"; // Import StaffRequestManagement component

function HomeManager() {
  const [pendingStaffRequests, setPendingStaffRequests] = useState([]);
  const { get: getPendingStaffRequests } = useFetch();
  const { t } = useTranslation("homeManager"); // Initialize useTranslation with the 'homeManager' namespace
  const [activeTab, setActiveTab] = useState("dashboard");
  const { user } = useAuth();
  const navigate = useNavigate();
  const [itemsPerPage] = useState(3);

  // --- State phân trang riêng biệt cho từng khu vực ---
  const [blogCurrentPage, setBlogCurrentPage] = useState(1);
  const [courseCurrentPage, setCourseCurrentPage] = useState(1);
  const [eventCurrentPage, setEventCurrentPage] = useState(1);

  // --- State lưu trữ dữ liệu ---
  const [staffPendingBlogs, setStaffPendingBlogs] = useState([]);
  const {
    get: getStaffPendingBlogs,
    put: putApproveStaffBlog,
    put: putRejectStaffBlog,
  } = useFetch();

  const [pendingCourses, setPendingCourses] = useState([]);
  const {
    get: getPendingCourses,
    put: putApproveCourse,
    put: putRejectCourse,
  } = useFetch();

  const [pendingEvents, setPendingEvents] = useState([]);
  const {
    get: getPendingEvents,
    put: putApproveEvent,
    put: putRejectEvent,
  } = useFetch();

  const [stat, setStat] = useState({});
  const { get: getStat } = useFetch();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const statData = await getStat("http://localhost:8080/api/report");
        setStat(statData);

        const pendingCoursesData = await getPendingCourses(
          "http://localhost:8080/api/course/status/PENDING",
        );
        setPendingCourses(pendingCoursesData || []);

        if (user) {
          const staffPendingBlogsData = await getStaffPendingBlogs(
            `http://localhost:8080/api/blog/status/PENDING/role/STAFF`,
          );
          setStaffPendingBlogs(staffPendingBlogsData || []);
        }

        try {
          const pendingEventsData = await getPendingEvents(
            "http://localhost:8080/api/event/status/PENDING_APPROVAL",
          );
          setPendingEvents(pendingEventsData || []);
        } catch (e) {
          console.log("Event API lỗi nhưng bỏ qua");
        }
        //staff pending requests
        const staffRequests = await getPendingStaffRequests(
          "/api/staff-requests/pending",
        );

        if (Array.isArray(staffRequests)) {
          setPendingStaffRequests(staffRequests);
        } else {
          setPendingStaffRequests([]);
        }
      } catch (error) {
        console.error("Fetch error in HomeManager:", error);
      }
    };

    fetchData();
  }, [
    getStat,
    getPendingCourses,
    getStaffPendingBlogs,
    getPendingEvents,
    getPendingStaffRequests,
    user,
  ]);
  console.log(stat);
  console.log(staffPendingBlogs);
  console.log(pendingCourses);

  // --- Logic phân trang cho Blog ---
  const totalBlogPages = Math.ceil(staffPendingBlogs.length / itemsPerPage);
  const currentBlogItems = staffPendingBlogs.slice(
    (blogCurrentPage - 1) * itemsPerPage,
    blogCurrentPage * itemsPerPage,
  );

  // --- Logic phân trang cho Course ---
  const totalCoursePages = Math.ceil(pendingCourses.length / itemsPerPage);
  const currentCourseItems = pendingCourses.slice(
    (courseCurrentPage - 1) * itemsPerPage,
    courseCurrentPage * itemsPerPage,
  );

  const handleView = (id, type) => {
    if (type === "blog") navigate(`/blogs/${id}`);
    if (type === "course") navigate(`/courses/${id}`);
  };

  const handleApprove = async (id, type) => {
    try {
      if (type === "blog") {
        await putApproveStaffBlog(
          {},
          {},
          `http://localhost:8080/api/blog/${id}/PUBLISHED`,
        );
        setStaffPendingBlogs((prevBlogs) =>
          prevBlogs.filter((blog) => blog.blogID !== id),
        );
      } else if (type === "course") {
        await putApproveCourse(
          {},
          {},
          `http://localhost:8080/api/course/${id}/AVAILABLE`,
        );
        setPendingCourses((prevCourses) =>
          prevCourses.filter((course) => course.courseID !== id),
        );
      }

      toast.success(t("successfullyApproved", { type: type, id: id }));
    } catch (error) {
      console.error(`Error approving ${type} with ID ${id}:`, error);
      toast.error(t("failedToApprove", { type: type, id: id }));
    }
  };

  const handleReject = async (id, type) => {
    try {
      if (type === "blog") {
        await putRejectStaffBlog(
          {},
          {},
          `http://localhost:8080/api/blog/${id}/REJECTED`,
        );
        setStaffPendingBlogs((prevBlogs) =>
          prevBlogs.filter((blog) => blog.blogID !== id),
        );
      } else if (type === "course") {
        await putRejectCourse(
          {},
          {},
          `http://localhost:8080/api/course/${id}/REJECTED`,
        );
        setPendingCourses((prevCourses) =>
          prevCourses.filter((course) => course.courseID !== id),
        );
      }

      toast.success(t("successfullyRejected", { type: type, id: id }));
    } catch (error) {
      console.error(`Error rejecting ${type} with ID ${id}:`, error);
      toast.error(t("failedToReject", { type: type, id: id }));
    }
  };

  return (
    <div style={{ minHeight: "100vh" }}>
      <Container fluid className="px-4 py-4">
        {/* Top Section: Chart on left, Stats on right */}
        <Row className="g-4 mb-4">
          {/* Chart takes 2/3 of the width */}
          <Col lg={8}>
            <LineChart />
          </Col>

          {/* Stats cards take 1/3 of the width, arranged vertically */}
          <Col lg={4}>
            <Row className="g-3">
              <Col xs={3} lg={6}>
                <div
                  role="button"
                  tabIndex={0}
                  onClick={() => navigate("/user-management")}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") navigate("/user-management");
                  }}
                  className="status-card-clickable"
                >
                  <StatusCard
                    title={t("totalMembers")}
                    value={stat.totalMembers}
                    change={12}
                    icon={Users}
                    gradientClass="icon-gradient-primary"
                  />
                </div>
              </Col>
              <Col xs={3} lg={6}>
                <StatusCard
                  title={t("staffMembers")}
                  value={stat.staffMembers}
                  change={8}
                  icon={UserCheck}
                  gradientClass="icon-gradient-success"
                />
              </Col>
              <Col xs={3} lg={6}>
                <StatusCard
                  title={t("consultants")}
                  value={stat.consultants}
                  change={5}
                  icon={Award}
                  gradientClass="icon-gradient-secondary"
                />
              </Col>
              <Col xs={3} lg={6}>
                <StatusCard
                  title={t("monthlyConsultations")}
                  value={stat.monthlyConsultations}
                  change={15}
                  icon={MessageSquare}
                  gradientClass="icon-gradient-warning"
                />
              </Col>
              <Col xs={3} lg={6}>
                <StatusCard
                  title={t("activeCourses")}
                  value={stat.activeCourses}
                  change={-2}
                  icon={BookOpen}
                  gradientClass="icon-gradient-info"
                />
              </Col>
              <Col xs={3} lg={6}>
                <StatusCard
                  title={t("blogs")}
                  value={stat.blogs}
                  change={18}
                  icon={FileText}
                  gradientClass="icon-gradient-dark"
                />
              </Col>

              <Col xs={3} lg={6}>
                <StatusCard
                  title={t("courses")}
                  value={stat.courses}
                  change={10}
                  icon={Target}
                  gradientClass="icon-gradient-warning"
                />
              </Col>
              <Col xs={3} lg={6}>
                <div
                  role="button"
                  tabIndex={0}
                  onClick={() => navigate("/staffrequest-management")}
                  b
                  onKeyDown={(e) => {
                    if (e.key === "Enter") navigate("/staffrequest-management");
                  }}
                  className="status-card-clickable"
                >
                  <StatusCard
                    title={t("staffPending")}
                    value={pendingStaffRequests?.length || 0}
                    change={10}
                    icon={UserCheck}
                    gradientClass="icon-gradient-warning"
                  />
                </div>
              </Col>
            </Row>
          </Col>
        </Row>

        {/* Pending Content Cards */}
        <Row className="g-4 mb-4">
          <Col lg={6} className="d-flex flex-column">
            <PendingCard
              title={t("pendingBlogs")}
              count={staffPendingBlogs.length}
              items={currentBlogItems}
              onView={(id) => handleView(id, "blog")}
              onApprove={(id) => handleApprove(id, "blog")}
              onReject={(id) => handleReject(id, "blog")}
            />
            <Link to="/staff/blogs">
              <button className="btn btn-primary mt-3">
                {t("viewAllBlogs")}
              </button>
            </Link>
          </Col>

          <Col lg={6} className="d-flex flex-column">
            <PendingCard
              title={t("pendingCourses")}
              count={pendingCourses.length}
              items={currentCourseItems}
              onView={(id) => handleView(id, "course")}
              onApprove={(id) => handleApprove(id, "course")}
              onReject={(id) => handleReject(id, "course")}
            />
            <Link to="/staff/courses">
              <button className="btn btn-primary mt-3">
                {t("viewAllCourses")}
              </button>
            </Link>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default HomeManager;
