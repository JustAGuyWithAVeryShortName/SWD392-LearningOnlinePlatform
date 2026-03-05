import { useState, useRef, useEffect } from "react";
import { Save, Eye, Upload, ImageIcon } from "lucide-react";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import "./BlogCreation.css";
import useFetch from "../../hooks/useFetch";
import useUpload from "../../hooks/useUpload";
import { useAuth } from "../../hooks/useAuth";
import { toast } from "react-toastify";
import { useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import BackButton from "../../components/BackButton";

const BlogCreation = () => {
  const { t } = useTranslation("blogCreation");
  const { user } = useAuth();
  const username = user?.username;
  const { id: blogID } = useParams();
  const navigate = useNavigate();

  const [imagePreview, setImagePreview] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const fileInputRef = useRef(null);

  const [types, setTypes] = useState([]);
  const [ageGroups, setAgeGroups] = useState([]);

  const {
    imageUrl: uploadedImageUrl,
    uploading: isUploadingImage,
    uploadError: imageUploadError,
    uploadImage,
    setImageUrl: setUploadedImageUrl,
  } = useUpload();

  const { get: getBlogTypes } = useFetch();
  const { get: getAgeGroups } = useFetch();
  const { post: postNewBlog } = useFetch();
  const { put: putExistingBlog } = useFetch();
  const { get: getDraft } = useFetch();

  const [formData, setFormData] = useState({
    blogName: "",
    blogType: "",
    author: username,
    ageGroup: "",
    description: "",
    content: "",
    image: null,
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const typesData = await getBlogTypes(
          "http://localhost:8080/api/blog/type"
        );
        setTypes(typesData);

        const ageGroupsData = await getAgeGroups(
          "http://localhost:8080/api/user/age-group"
        );
        setAgeGroups(ageGroupsData);

        if (blogID) {
          const blogData = await getDraft(
            `http://localhost:8080/api/blog/${blogID}`
          );

          setFormData(blogData);

          if (blogData.image) {
            setImagePreview(blogData.image);
            setUploadedImageUrl(blogData.image);
          }
        }
      } catch (error) {
        console.error(error);
      }
    };

    fetchData();
  }, [blogID]);

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleImageSelect = (event) => {
    const file = event.target.files[0];

    if (file) {
      const reader = new FileReader();

      reader.onload = (e) => {
        setImagePreview(e.target.result);
      };

      reader.readAsDataURL(file);
      uploadImage(file);
    }
  };

  const validateForm = () => {
    if (!formData.blogName.trim()) {
      toast.error("Blog name required");
      return false;
    }

    if (!formData.blogType) {
      toast.error("Select blog type");
      return false;
    }

    if (!formData.description.trim()) {
      toast.error("Description required");
      return false;
    }

    if (!formData.content.trim() || formData.content === "<p><br></p>") {
      toast.error("Content required");
      return false;
    }

    if (!blogID && !uploadedImageUrl) {
      toast.error("Image required");
      return false;
    }

    return true;
  };

  const handleSave = async () => {
    if (!validateForm()) return;

    if (isUploadingImage) {
      toast.info("Uploading image...");
      return;
    }

    setIsSubmitting(true);

    try {
      const blogData = {
        ...formData,
        image: uploadedImageUrl,
        blogStatus: "DRAFT",
      };

      if (blogID) {
        await putExistingBlog(
          blogData,
          {},
          `http://localhost:8080/api/blog/${blogID}`
        );
      } else {
        await postNewBlog(blogData, {}, "http://localhost:8080/api/blog");
      }

      toast.success("Saved successfully");
      navigate("/blogs");
    } catch {
      toast.error("Save failed");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePublish = async () => {
    if (!validateForm()) return;

    if (isUploadingImage) {
      toast.info("Uploading image...");
      return;
    }

    setIsSubmitting(true);

    try {
      const blogData = {
        ...formData,
        image: uploadedImageUrl,
        blogStatus: "PUBLISHED",
      };

      if (blogID) {
        await putExistingBlog(
          blogData,
          {},
          `http://localhost:8080/api/blog/${blogID}`
        );
      } else {
        await postNewBlog(blogData, {}, "http://localhost:8080/api/blog");
      }

      toast.success("Published!");
      navigate("/blogs");
    } catch {
      toast.error("Publish failed");
    } finally {
      setIsSubmitting(false);
    }
  };

  const quillModules = {
    toolbar: [
      [{ header: [1, 2, 3, false] }],
      ["bold", "italic", "underline"],
      [{ list: "ordered" }, { list: "bullet" }],
      ["link", "image"],
    ],
  };

  return (
    <div className="blog-creation-page-new">
      <div className="creation-header">
        <h1>{t("header.title")}</h1>
        <p>{t("header.subtitle")}</p>
      </div>

      <div className="container-new">
        <BackButton label="Back" />

        <div className="creation-form-container-new">

          <input
            type="text"
            placeholder="Blog title"
            value={formData.blogName}
            onChange={(e) =>
              handleInputChange("blogName", e.target.value)
            }
            className="blog-name-input"
          />

          <div className="form-row">
            <select
              value={formData.blogType}
              onChange={(e) =>
                handleInputChange("blogType", e.target.value)
              }
              className="filter-select-new"
            >
              <option value="">Topic</option>

              {types.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>

            <select
              value={formData.ageGroup}
              onChange={(e) =>
                handleInputChange("ageGroup", e.target.value)
              }
              className="filter-select-new"
            >
              <option value="">Age group</option>

              {ageGroups.map((age) => (
                <option key={age} value={age}>
                  {age}
                </option>
              ))}
            </select>

            <div className="author-text">
              By {formData.author}
            </div>
          </div>

          <div
            className="image-upload-area-new"
            onClick={() => fileInputRef.current.click()}
          >
            {imagePreview || uploadedImageUrl ? (
              <div className="image-preview-new">
                <img
                  src={imagePreview || uploadedImageUrl}
                  className="preview-image-new"
                />

                <div className="image-overlay-new">
                  <Upload size={24} />
                  <span>Change image</span>
                </div>
              </div>
            ) : (
              <div className="upload-placeholder-new">
                <ImageIcon size={40} />
                <span>Upload image</span>
              </div>
            )}
          </div>

          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            hidden
            onChange={handleImageSelect}
          />

          <textarea
            placeholder="Short description"
            value={formData.description}
            onChange={(e) =>
              handleInputChange("description", e.target.value)
            }
            className="form-textarea-new"
          />

          <div className="quill-container-new">
            <ReactQuill
              value={formData.content}
              onChange={(value) =>
                handleInputChange("content", value)
              }
              modules={quillModules}
            />
          </div>

          <div className="action-buttons-new">
            <button
              className="action-btn-new save-btn-new"
              onClick={handleSave}
              disabled={isSubmitting}
            >
              <Save size={16} />
              Save Draft
            </button>

            <button
              className="action-btn-new publish-btn-new"
              onClick={handlePublish}
              disabled={isSubmitting}
            >
              <Eye size={16} />
              Publish
            </button>
          </div>

        </div>
      </div>
    </div>
  );
};

export default BlogCreation;