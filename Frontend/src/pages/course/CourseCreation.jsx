import { useEffect, useState, useRef } from "react";
import { Container, Row, Col, Form, Button, Card, Alert } from "react-bootstrap";
import { Upload, Plus, Save, Edit3, ImageIcon } from "lucide-react";
import "./CourseCreation.css";
import { useNavigate, useParams } from "react-router-dom";
import useFetch from "../../hooks/useFetch";
import useUpload from "../../hooks/useUpload";
import { toast } from "react-toastify";
import BackButton from "../../components/BackButton";
import { useTranslation } from "react-i18next";

const CourseCreation = () => {
  const { t } = useTranslation("courseCreation");

  const navigate = useNavigate();
  const { courseID: paramCourseID } = useParams();

  const [courseID, setCourseID] = useState(paramCourseID || "");
  const [modules, setModules] = useState([]);
  const [selectedModuleIds, setSelectedModuleIds] = useState([]);
  const [ageGroups, setAgeGroups] = useState([]);

  const fileInputRef = useRef(null);

  const {
    imageUrl: uploadedImageUrl,
    uploading: isUploadingImage,
    uploadError: imageUploadError,
    uploadImage,
    setImageUrl: setUploadedImageUrl,
  } = useUpload();

  const { get: getAgeGroups } = useFetch();
  const { get: getCourse } = useFetch();
  const { get: getModules } = useFetch();
  const { post: postCourse } = useFetch();
  const { put: putCourse } = useFetch();
  const { put: putModulesStatus } = useFetch();

  const [formData, setFormData] = useState({
    courseName: "",
    description: "",
    ageGroup: "",
    image: "",
    price: 0,
  });

  const [imagePreview, setImagePreview] = useState(null);

  /*
  =========================
  FETCH DATA
  =========================
  */

  useEffect(() => {
    const fetchData = async () => {
      try {
        const ageData = await getAgeGroups(
          "http://localhost:8080/api/user/age-group"
        );
        setAgeGroups(ageData || []);

        if (courseID) {
          const courseData = await getCourse(
            `http://localhost:8080/api/course/${courseID}`
          );

          setFormData({
            courseName: courseData.courseName || "",
            description: courseData.description || "",
            ageGroup: courseData.ageGroup || "",
            image: courseData.image || "",
            price: courseData.price ?? 0,
          });

          if (courseData.image) {
            setImagePreview(courseData.image);
            setUploadedImageUrl(courseData.image);
          }

          const moduleData = await getModules(
            `http://localhost:8080/api/course/${courseID}/modules`
          );

          setModules(moduleData || []);
        }
      } catch (error) {
        console.error("Fetch error:", error);
        toast.error("Failed to load course data");
      }
    };

    fetchData();
  }, [courseID]);

  /*
  =========================
  HANDLE INPUT
  =========================
  */

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  /*
  =========================
  IMAGE UPLOAD
  =========================
  */

  const handleImageSelect = (e) => {
    const file = e.target.files[0];

    if (!file) return;

    const reader = new FileReader();

    reader.onload = (event) => {
      setImagePreview(event.target.result);
    };

    reader.readAsDataURL(file);

    uploadImage(file);
  };

  /*
  =========================
  VALIDATE
  =========================
  */

  const validateCourse = () => {
    if (!formData.courseName.trim()) {
      toast.error("Course name required");
      return false;
    }

    if (!formData.description.trim()) {
      toast.error("Description required");
      return false;
    }

    if (!formData.ageGroup) {
      toast.error("Age group required");
      return false;
    }

    return true;
  };

  /*
  =========================
  CREATE COURSE
  =========================
  */

  const handleCreateCourse = async () => {
    if (!validateCourse()) return;

    try {
      const body = {
        courseName: formData.courseName,
        description: formData.description,
        ageGroup: formData.ageGroup,
        image: uploadedImageUrl,
        price: Number(formData.price) || 0,
      };

      // Pass Authorization header so backend can identify the actor
      const token = localStorage.getItem("token");
      const headers = { Authorization: `Bearer ${token}` };

      const res = await postCourse(
        body,
        headers,                              // ← was {} before
        "http://localhost:8080/api/course"
      );

      setCourseID(res.courseID);
      toast.success("Course created successfully");
    } catch (err) {
      toast.error("Create course failed");
    }
  };

  /*
  =========================
  SAVE COURSE
  =========================
  */

  const handleSaveCourse = async () => {
    if (!validateCourse()) return;

    try {
      const body = {
        courseName: formData.courseName,
        description: formData.description,
        ageGroup: formData.ageGroup,
        image: uploadedImageUrl,
        price: Number(formData.price) || 0,
      };

      await putCourse(
        body,
        {},
        `http://localhost:8080/api/course/${courseID}`
      );

      toast.success("Course updated");

      navigate(`/courses/${courseID}`);
    } catch (err) {
      toast.error("Update course failed");
    }
  };

  /*
  =========================
  MODULE
  =========================
  */

  const handleAddModule = () => {
    if (!courseID) {
      toast.error("Create course first");
      return;
    }

    navigate(`/courses/${courseID}/module/create`);
  };

  const handleEditModule = (moduleID) => {
    navigate(`/courses/${courseID}/module/${moduleID}/update`);
  };

  const toggleModuleSelect = (moduleID) => {
    setSelectedModuleIds((prev) =>
      prev.includes(moduleID)
        ? prev.filter((id) => id !== moduleID)
        : [...prev, moduleID]
    );
  };

  /*
  =========================
  RENDER
  =========================
  */

  return (
    <Container className="course-creation-container py-5">
      <Row className="justify-content-center">

        <Col lg={10}>
          <BackButton label="Back" />

          {/* FORM */}

          <Row className="align-items-center">

            {/* LEFT */}

            <Col md={6}>

              <Form.Group className="mb-3">
                <Form.Control
                  type="text"
                  placeholder="Course name"
                  value={formData.courseName}
                  onChange={(e) =>
                    handleInputChange("courseName", e.target.value)
                  }
                  className="course-name-input"
                />
              </Form.Group>

              <Form.Group className="mb-4">
                <Form.Control
                  as="textarea"
                  rows={4}
                  placeholder="Description"
                  value={formData.description}
                  onChange={(e) =>
                    handleInputChange("description", e.target.value)
                  }
                  className="course-description-input"
                />
              </Form.Group>

              <Form.Select
                value={formData.ageGroup}
                onChange={(e) =>
                  handleInputChange("ageGroup", e.target.value)
                }
                className="filter-select-new"
              >
                <option value="">Select age group</option>

                {ageGroups.map((age) => (
                  <option key={age} value={age}>
                    {age}
                  </option>
                ))}
              </Form.Select>

              <Form.Group className="mb-3 mt-3">
                <Form.Label className="fw-semibold">Giá khóa học (VNĐ)</Form.Label>
                <Form.Control
                  type="number"
                  min="0"
                  step="1000"
                  placeholder="0 = Miễn phí"
                  value={formData.price}
                  onChange={(e) => handleInputChange("price", e.target.value)}
                  className="course-name-input"
                />
                <Form.Text className="text-muted">
                  Để trống hoặc nhập 0 nếu khóa học miễn phí.
                </Form.Text>
              </Form.Group>

            </Col>

            {/* RIGHT */}

            <Col md={6}>

              <div
                className="image-upload-area-new"
                onClick={() => fileInputRef.current.click()}
              >

                {imagePreview || uploadedImageUrl ? (
                  <div className="image-preview-new">

                    <img
                      src={imagePreview || uploadedImageUrl}
                      alt="preview"
                      className="preview-image-new"
                    />

                    <div className="image-overlay-new">
                      <Upload size={22} />
                      <span>Change image</span>
                    </div>

                  </div>
                ) : (
                  <div className="upload-placeholder-new">

                    <ImageIcon
                      size={48}
                      className="upload-icon-new"
                    />

                    <span className="upload-text-new">
                      Click to upload
                    </span>

                  </div>
                )}

              </div>

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                className="d-none"
                onChange={handleImageSelect}
              />

              {isUploadingImage && (
                <Alert className="mt-2">Uploading image...</Alert>
              )}

              {imageUploadError && (
                <Alert variant="danger">{imageUploadError}</Alert>
              )}

              {/* BUTTON */}

              <div className="mt-4">

                {courseID ? (
                  <Button
                    className="create-button"
                    onClick={handleSaveCourse}
                  >
                    <Save size={16} className="me-2" />
                    Save course
                  </Button>
                ) : (
                  <Button
                    className="create-button"
                    onClick={handleCreateCourse}
                  >
                    <Plus size={16} className="me-2" />
                    Create course
                  </Button>
                )}

              </div>

            </Col>

          </Row>

          {/* MODULES */}

          <div className="module-section mt-5">

            <div className="d-flex justify-content-between mb-4">

              <Button
                className="add-module-btn"
                onClick={handleAddModule}
              >
                <Plus size={16} className="me-1" />
                Add Module
              </Button>

            </div>

            {modules.length === 0 ? (
              <p className="text-center text-muted">
                No modules yet
              </p>
            ) : (
              modules.map((module) => (

                <Card
                  key={module.moduleID}
                  className="module-card mb-3"
                >

                  <Card.Body className="d-flex align-items-center">

                    <Form.Check
                      type="checkbox"
                      className="me-3"
                      checked={selectedModuleIds.includes(module.moduleID)}
                      onChange={() =>
                        toggleModuleSelect(module.moduleID)
                      }
                    />

                    <h5 className="module-title flex-grow-1 mb-0">
                      {module.moduleName}
                    </h5>

                    <Button
                      variant="link"
                      onClick={() =>
                        handleEditModule(module.moduleID)
                      }
                    >
                      <Edit3 size={16} />
                    </Button>

                  </Card.Body>

                </Card>

              ))
            )}

          </div>

        </Col>
      </Row>
    </Container>
  );
};

export default CourseCreation;