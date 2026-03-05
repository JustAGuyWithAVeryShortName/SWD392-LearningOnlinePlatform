import { Clock, Calendar } from "lucide-react";
import "./CourseCard.css";
import { useTranslation } from "react-i18next";

const CourseCard = ({
  course,
  onEnrollClick,
  onDetailsClick,
  onContinueClick,
  status,
}) => {
  const { t } = useTranslation("courseCard");

  const getAgeGroupColor = (ageGroup) => {
    const colors = {
      ADOLESCENT: "ageGroup-adolescent",
      ADULT: "ageGroup-adult",
      SENIOR: "ageGroup-senior",
      EVERYONE: "ageGroup-everyone",
    };
    return colors[ageGroup] || "ageGroup-default";
  };

  return (
    <div
      className="course-card-2"
      style={{
        backgroundImage: `url(${course.image})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <div className="course-card-overlay-2">
        <div className="course-card-content-2">

          <div className="course-header-2">
            <span className={`ageGroup-tag-2 ${getAgeGroupColor(course.ageGroup)}`}>
              {t(`ageGroup.${course.ageGroup}`)}
            </span>

            <div className="course-meta-info-2">
              <Clock size={16} className="meta-icon-2" />
              <span className="meta-text-2">
                {course.duration} {t("durationSuffix")}
              </span>
            </div>
          </div>

          <h3 className="course-title-2">{course.courseName}</h3>

          <p className="course-description-2">
            {course.description}
          </p>

          <div className="course-details-2">
            <div className="detail-item-2">
              <Calendar size={14} className="detail-icon-2" />
              <span className="detail-text-2">
                {new Date(course.createdAt).toLocaleDateString()}
              </span>
            </div>
          </div>

          <div className="course-actions-2">
            {status === "learning" ? (
              <button
                className="details-button-2"
                onClick={() => onContinueClick(course.courseID)}
              >
                {t("continueButton")}
              </button>
            ) : (
              <>
                <button
                  className="enroll-button-2"
                  onClick={() => onEnrollClick(course.courseID)}
                >
                  {t("enrollButton")}
                </button>

                <button
                  className="details-button-2"
                  onClick={() => onDetailsClick(course.courseID)}
                >
                  {t("detailsButton")}
                </button>
              </>
            )}
          </div>

        </div>
      </div>
    </div>
  );
};

export default CourseCard;