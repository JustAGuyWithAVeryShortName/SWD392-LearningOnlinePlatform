import { useEffect, useState } from "react";
import { Container, Row, Col, Form, Button, Card } from "react-bootstrap";
import { Plus, Edit3, Save } from "lucide-react";
import "./ModuleCreation.css";
import useFetch from "../../hooks/useFetch";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import BackButton from "../../components/BackButton";
import { useTranslation } from "react-i18next";

const ModuleCreation = () => {
  const { t } = useTranslation("moduleCreation");

  const { courseID, moduleID: paramModuleID } = useParams();

  const navigate = useNavigate();

  const [moduleID, setModuleID] = useState(paramModuleID || "");
  const [lessons, setLessons] = useState([]);
  const [course, setCourse] = useState({});
  const [selectedLessonIds, setSelectedLessonIds] = useState([]);

  const [formData, setFormData] = useState({
    moduleName: "",
  });

  const { loading: loadingPostModule, post: postNewModule } = useFetch();
  const { loading: loadingPutModule, put: putModule } = useFetch();
  const { get: getLessons } = useFetch();
  const { get: getModule } = useFetch();
  const { get: getCourse } = useFetch();
  const { loading: loadingPutLessonsStatus, put: putLessonsStatus } = useFetch();

  /* ---------------- FETCH DATA ---------------- */

  useEffect(() => {
    const fetchData = async () => {
      try {
        const courseData = await getCourse(
          `http://localhost:8080/api/course/${courseID}`
        );
        setCourse(courseData);

        if (moduleID) {
          const lessonsData = await getLessons(
            `http://localhost:8080/api/module/${moduleID}/lessons`
          );
          setLessons(lessonsData);

          const moduleData = await getModule(
            `http://localhost:8080/api/module/${moduleID}`
          );
          setFormData(moduleData);
        }
      } catch (error) {
        console.error(error);
        toast.error(t("lessonsSection.toastMessages.fetchError"));
      }
    };

    fetchData();
  }, [courseID, moduleID]);

  /* ---------------- LESSON ACTIONS ---------------- */

  const handleAddLesson = () => {
    if (!moduleID) {
      toast.error(t("lessonsSection.toastMessages.addLessonError"));
      return;
    }

    navigate(`/courses/${courseID}/module/${moduleID}/lesson/create`);
  };

  const handleEditLesson = (lessonID) => {
    navigate(`/courses/${courseID}/module/${moduleID}/lesson/${lessonID}/update`);
  };

  const handleToggleLessonSelection = (lessonID) => {
    setSelectedLessonIds((prev) =>
      prev.includes(lessonID)
        ? prev.filter((id) => id !== lessonID)
        : [...prev, lessonID]
    );
  };

  const handleMarkSelectedUnavailableLessons = async () => {
    if (selectedLessonIds.length === 0) {
      toast.info(t("lessonsSection.toastMessages.markSelectedInfo"));
      return;
    }

    if (
      !window.confirm(
        t("lessonsSection.confirmUnavailable", {
          count: selectedLessonIds.length,
        })
      )
    ) {
      return;
    }

    const originalLessons = [...lessons];

    setLessons((prev) =>
      prev.map((lesson) =>
        selectedLessonIds.includes(lesson.lessonID)
          ? { ...lesson, status: "UNAVAILABLE" }
          : lesson
      )
    );

    try {
      const requestBody = {
        lessonIds: selectedLessonIds,
        status: "UNAVAILABLE",
      };

      await putLessonsStatus(
        requestBody,
        {},
        `http://localhost:8080/api/lesson/${moduleID}/unavailable`
      );

      toast.success(t("lessonsSection.toastMessages.updateStatusSuccess"));

      setSelectedLessonIds([]);
    } catch (error) {
      console.error(error);

      setLessons(originalLessons);

      toast.error(t("lessonsSection.toastMessages.updateStatusError"));
    }
  };

  /* ---------------- MODULE ACTIONS ---------------- */

  const handleCreateModule = async () => {
    try {
      const moduleData = {
        moduleName: formData.moduleName,
        courseID,
      };

      const response = await postNewModule(
        moduleData,
        {},
        "http://localhost:8080/api/module"
      );

      const newModuleID = response.moduleID;

      setModuleID(newModuleID);

      toast.success(t("lessonsSection.toastMessages.createSuccess"));

      navigate(`/courses/${courseID}/module/${newModuleID}/update`, {
        replace: true,
      });
    } catch (error) {
      console.error(error);
      toast.error(t("lessonsSection.toastMessages.createError"));
    }
  };

  const handleSaveModule = async () => {
    try {
      const moduleData = {
        moduleName: formData.moduleName,
        courseID,
      };

      await putModule(
        moduleData,
        {},
        `http://localhost:8080/api/module/${moduleID}`
      );

      toast.success(t("lessonsSection.toastMessages.saveSuccess"));

      navigate(`/courses/${courseID}/update`);
    } catch (error) {
      console.error(error);
      toast.error(t("lessonsSection.toastMessages.saveError"));
    }
  };

  /* ---------------- FORM ---------------- */

  const handleInputChange = (value) => {
    setFormData((prev) => ({
      ...prev,
      moduleName: value,
    }));
  };

  /* ---------------- UI ---------------- */

  return (
    <Container className="module-creation-container">
      <Row className="justify-content-center">
        <Col lg={10} md={12}>

          <BackButton label={t("backButton")} />

          {/* COURSE HEADER */}

          <div className="course-header-section mb-4">
            <h1 className="course-title">
              {t("courseHeader", {
                courseName: course.courseName || "Loading...",
              })}
            </h1>
          </div>

          {/* MODULE FORM */}

          <div className="module-form-content">

            <Form.Group className="mb-4">
              <Form.Control
                type="text"
                placeholder={t("form.moduleNamePlaceholder")}
                value={formData.moduleName}
                onChange={(e) => handleInputChange(e.target.value)}
                className="module-name-input"
              />
            </Form.Group>

            {/* CREATE / SAVE */}

            <div className="save-section d-flex justify-content-end mb-4">
              {moduleID ? (
                <Button
                  className="save-module-btn"
                  onClick={handleSaveModule}
                  disabled={loadingPutModule}
                >
                  <Save size={16} className="me-2" />
                  {t("form.saveModuleButton")}
                </Button>
              ) : (
                <Button
                  className="create-module-btn"
                  onClick={handleCreateModule}
                  disabled={loadingPostModule}
                >
                  <Plus size={16} className="me-2" />
                  {t("form.createModuleButton")}
                </Button>
              )}
            </div>

            {/* LESSON HEADER */}

            <div className="lessons-list-header d-flex justify-content-between mb-3">

              <Button
                className="add-lesson-btn"
                onClick={handleAddLesson}
                disabled={!moduleID}
              >
                <Plus size={16} className="me-1" />
                {t("lessonsSection.addLessonButton")}
              </Button>

              <Button
                className="mark-unavailable-btn"
                onClick={handleMarkSelectedUnavailableLessons}
                disabled={
                  !moduleID ||
                  selectedLessonIds.length === 0 ||
                  loadingPutLessonsStatus
                }
              >
                {t("lessonsSection.markSelectedUnavailableButton")} (
                {selectedLessonIds.length})
              </Button>

            </div>

            {/* LESSON LIST */}

            <div className="lessons-list">

              {lessons.length === 0 ? (
                <p className="text-center text-muted">
                  {t("lessonsSection.noLessonsAdded")}
                </p>
              ) : (
                lessons.map((lesson) => (
                  <Card
                    key={lesson.lessonID}
                    className={`lesson-card d-flex flex-row align-items-center ${
                      lesson.status === "UNAVAILABLE"
                        ? "lesson-unavailable"
                        : ""
                    }`}
                  >
                    <Card.Body className="d-flex align-items-center w-100">

                      <Form.Check
                        type="checkbox"
                        className="me-3"
                        checked={selectedLessonIds.includes(lesson.lessonID)}
                        onChange={() =>
                          handleToggleLessonSelection(lesson.lessonID)
                        }
                        disabled={lesson.status === "UNAVAILABLE"}
                      />

                      <div className="lesson-content flex-grow-1">
                        <h5 className="lesson-title mb-0">

                          {lesson.lessonName}

                          {lesson.status === "UNAVAILABLE" && (
                            <span className="badge bg-warning text-dark ms-2">
                              {t("lessonsSection.unavailableBadge")}
                            </span>
                          )}

                        </h5>
                      </div>

                      <Button
                        variant="link"
                        className="edit-lesson-btn"
                        onClick={() => handleEditLesson(lesson.lessonID)}
                        disabled={lesson.status === "UNAVAILABLE"}
                      >
                        <Edit3 size={16} />
                      </Button>

                    </Card.Body>
                  </Card>
                ))
              )}

            </div>

          </div>

        </Col>
      </Row>
    </Container>
  );
};

export default ModuleCreation;