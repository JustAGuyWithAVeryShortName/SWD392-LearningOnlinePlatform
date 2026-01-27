import { useEffect, useState } from "react";
import { Container, Row, Col, Spinner, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import useFetch from "../../hooks/useFetch";
import BlogCard from "../../components/card/BlogCard";
import CourseCard from "../../components/card/CourseCard";
import AppointmentCard from "../../components/card/AppointmentCard";
import "./Home.css";
import { useTranslation } from "react-i18next";
import { useAuth } from "../../hooks/useAuth";
import { jwtDecode } from "jwt-decode";


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
        setRandomBlogs(getRandomItems(blogsData, 2));
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

      {/* Blogs Section (from HomeExplore) */}
      <Container className="mb-5">
        <div className="bg-light rounded-4 p-4">
          <h3 className="fw-bold text-dark mb-4">{t("newBlogsTitle")}</h3>
          <Row>
            {randomBlogs.map((blog) => (
              <Col md={6} key={blog.blogID} className="mb-4">
                <BlogCard
                  blog={blog}
                  onReadClick={() => handleReadMore(blog.blogID)}
                />
              </Col>
            ))}
          </Row>
        </div>
      </Container>

      {/* Courses Section (from HomeExplore) */}
      <Container className="mb-5">
        <div className="bg-light rounded-4 p-4">
          <h3 className="fw-bold text-dark mb-4">{t("popularCoursesTitle")}</h3>
          <Row>
            {randomCourses.map((course) => (
              <Col md={4} key={course.courseID} className="mb-4">
                <CourseCard
                  course={course}
                  onEnrollClick={handleCoursesClick}
                  onDetailsClick={handleCoursesClick}
                />
              </Col>
            ))}
          </Row>
        </div>
      </Container>
    </div>
  );
};

export default Home;