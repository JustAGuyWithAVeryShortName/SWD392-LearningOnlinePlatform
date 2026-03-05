import { useEffect, useState } from "react";
import { Container, Row, Col, Spinner, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import useFetch from "../../hooks/useFetch";
import AppointmentCard from "../../components/card/AppointmentCard";
import "./Home.css";
import { useTranslation } from "react-i18next";
import { useAuth } from "../../hooks/useAuth";
import { jwtDecode } from "jwt-decode";

import HomeBlogCard from "../../components/home/HomeBlogCard";
import CourseCard from "../../components/card/CourseCard";
// Helper function moved from HomeExplore
const getRandomItems = (array, count) => {
  const shuffled = [...array].sort(() => 0.5 - Math.random());
  return shuffled.slice(0, count);
};

const Home = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { t } = useTranslation("home"); // Assuming 'home' namespace for translations

  // State and fetch hooks from HomeExplore
  const [randomBlogs, setRandomBlogs] = useState([]);
  const [randomCourses, setRandomCourses] = useState([]);
  const [todayAppointments, setUpcomingAppointments] = useState([]);

  const { loading, get } = useFetch();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const blogsData = await get("http://localhost:8080/api/blog/status/PUBLISHED");

        const latestBlogs = blogsData
          .sort((a, b) => new Date(b.createdDate) - new Date(a.createdDate))
          .slice(0, 2);

        setRandomBlogs(latestBlogs);
        const coursesData = await get("http://localhost:8080/api/course/status/AVAILABLE");
        setRandomCourses(getRandomItems(coursesData, 3));

        // Only fetch appointments if user is logged in
        if (user?.username) {
          const todayAppointmentsData = await get(`http://localhost:8080/api/appointment/today/member/${user?.username}`);
          setUpcomingAppointments(todayAppointmentsData);
        }
      } catch (error) {
        console.error("Fetch error in Home:", error);
      }
    }
    fetchData()
  }, [user, get]);


  // Redirect logic from Home
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/");
      return;
    }
    try {
      const { scope } = jwtDecode(token);
      console.log(scope);
      // Redirect based on role if not MEMBER or empty scope (which defaults to MEMBER for this context)
      if (scope === "STAFF") navigate("/staff");
      if (scope === "MANAGER") navigate("/manager");
      if (scope === "CONSULTANT") navigate("/consultant");
      // If MEMBER or an unrecognized scope, stay on the current home page
    } catch (error) {
      console.error("Invalid token:", error);
      navigate("/"); // Redirect to home if token is invalid
    }
  }, [navigate]);

  const handleReadMore = (blogId) => {
    navigate(`/blogs/${blogId}`);
  };

  const handleCoursesClick = (courseID) => {
    navigate(`/courses/${courseID}`);
  };

  const handleBookAppointmentClick = () => {
    navigate('/appointment');
  };

  if (loading) {
    return (
      <Container className="my-5 text-center">
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  return (
    <div className="home-page">
      {/* Hero Section */}
      <Container className="my-4">
        <div className="hero-section rounded-4 p-5 text-center text-white position-relative overflow-hidden">
          <div className="hero-content">
            <h1 className="display-4 fw-bold mb-3">{t("heroSection.title")}</h1>
            <p className="lead mb-4">{t("heroSection.subtitle")}</p>
          </div>
        </div>
      </Container>

      {/* Today Appointment Card - Conditionally rendered */}
      {user && (
        <Container className="mb-5">
          <AppointmentCard appointments={todayAppointments} />
          <div className="d-flex justify-content-end mt-3">
            <Button variant="info" size="sm" className="rounded-pill shadow-sm custom-button"
              onClick={handleBookAppointmentClick}>
              {t("bookAppointmentButton")}
            </Button>
          </div>
        </Container>
      )}

      {/* Featured Courses */}
      {/* Courses Section */}
      <Container className="mb-5">
        <div className="bg-light rounded-4 p-4">
          <div className="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h2 className="fw-bold">{t("featuredCourses.title")}</h2>
              <p className="text-muted">{t("featuredCourses.subtitle")}</p>
            </div>
            <Button
              variant="link"
              className="fw-semibold"
              onClick={() => navigate("/courses")}
            >
              {t("viewAllCourses")}
            </Button>
          </div>

          <Row>
            {randomCourses.map((course) => (
              <Col md={4} key={course.courseID} className="mb-4">
                <CourseCard
                  course={course}
                  onEnrollClick={() => handleCoursesClick(course.courseID)}
                  onDetailsClick={() => handleCoursesClick(course.courseID)}
                />
              </Col>
            ))}
          </Row>
        </div>
      </Container>

      {/* Instructors */}
      <section className="bg-light py-5 mb-5">
        <Container>
          <div className="text-center mb-5">
            <h2 className="fw-bold">{t("instructors.title")}</h2>
            <p className="text-muted">{t("instructors.subtitle")}</p>
          </div>

          <Row className="g-4 justify-content-center">
            {[
              { name: "Dr. Alex Rivers", role: "Senior Web Architect", avatar: "src/images/4.jpg" },
              { name: "Sarah Jenkins", role: "Lead UI Designer", avatar: "src/images/5.jpg" },
              { name: "Michael Chen", role: "Data Scientist", avatar: "src/images/6.webp" },
              { name: "Elena Rodriguez", role: "Business Strategist", avatar: "src/images/7.jpg" },
            ].map((ins, index) => (
              <Col key={index} xs={6} md={3} className="text-center">
                <img
                  src={ins.avatar}
                  alt={ins.name}
                  className="rounded-circle mb-3 instructor-avatar"
                />
                <h6 className="fw-bold mb-0">{ins.name}</h6>
                <small className="text-muted">{ins.role}</small>
              </Col>
            ))}
          </Row>
        </Container>
      </section>
      {/* Latest Blogs */}
      <Container className="mb-5">
        <h2 className="fw-bold mb-4">{t("latestBlogs.title")}</h2>

        <Row className="g-4">
          {/*{randomBlogs.map((blog) => (
      <Col key={blog.blogID} xs={12} md={6}>
        <HomeBlogCard
          blog={blog}
          onReadMore={() => handleReadMore(blog.blogID)}
        />
      </Col>
    ))}*/}
          {randomBlogs.map((blog) => (
            <Col key={blog.blogID} xs={12} md={6}>
              <HomeBlogCard
                blog={blog}
                onReadMore={() => handleReadMore(blog.blogID)}
              />
            </Col>
          ))}
        </Row>
      </Container>

    </div>
  );
};

export default Home;