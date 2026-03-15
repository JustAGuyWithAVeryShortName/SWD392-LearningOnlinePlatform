import { useEffect, useState } from "react";
import "./CourseCard.css";
import { useTranslation } from "react-i18next";
import { Clock, Calendar, User } from "lucide-react";
import useFetch from "../../hooks/useFetch";

const formatVND = (amount) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount || 0);

const CourseCard = ({
  course,
  onEnrollClick,
  onDetailsClick,
  onContinueClick,
  isPurchased = false,
  status,
}) => {
  const { t } = useTranslation("courseCard");
  const isPaidCourse = Number(course?.price || 0) > 0;
  const [calculatedDuration, setCalculatedDuration] = useState(Number(course?.duration) || 0);
  const { get: getModules } = useFetch();
  const { get: getLessons } = useFetch();

  useEffect(() => {
    let mounted = true;

    const fetchAccurateDuration = async () => {
      if (!course?.courseID) {
        if (mounted) setCalculatedDuration(0);
        return;
      }

      try {
        const modules = await getModules(`http://localhost:8080/api/course/${course.courseID}/modules`);
        const safeModules = Array.isArray(modules) ? modules : [];

        if (safeModules.length === 0) {
          if (mounted) setCalculatedDuration(Number(course?.duration) || 0);
          return;
        }

        const lessonsByModule = await Promise.all(
          safeModules.map((module) =>
            getLessons(`http://localhost:8080/api/module/${module.moduleID}/lessons`)
          )
        );

        const allLessons = lessonsByModule.flatMap((item) => (Array.isArray(item) ? item : []));
        const totalDuration = allLessons.reduce(
          (sum, lesson) => sum + Number(lesson.duration ?? lesson.lessonDuration ?? 0),
          0
        );

        if (mounted) {
          setCalculatedDuration(totalDuration > 0 ? Math.round(totalDuration) : Number(course?.duration) || 0);
        }
      } catch (error) {
        console.error("Failed to calculate course duration:", error);
        if (mounted) setCalculatedDuration(Number(course?.duration) || 0);
      }
    };

    fetchAccurateDuration();

    return () => {
      mounted = false;
    };
  }, [course?.courseID, course?.duration, getLessons, getModules]);

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
                {calculatedDuration} {t("durationSuffix")}
              </span>
            </div>

            {course.staffFullName && (
              <div className="course-meta-info-2">
                <User size={16} className="meta-icon-2" />
                <span className="meta-text-2">{course.staffFullName}</span>
              </div>
            )}
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
            <div className="course-price-2">
              {isPaidCourse
                ? t("pricePaid", { price: formatVND(course.price) })
                : t("priceFree")}
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
                  onClick={() => {
                    if (isPurchased) {
                      if (onContinueClick) onContinueClick(course.courseID)
                      else onDetailsClick(course.courseID)
                      return
                    }
                    onEnrollClick(course.courseID)
                  }}
                >
                  {isPurchased
                    ? t("continueButton")
                    : isPaidCourse
                      ? t("buyButton")
                      : t("enrollButton")}
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