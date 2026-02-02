import "./HomeCourseCard.css";
const HomeCourseCard = () => {
  return (
    <div className="home-course-card">
      <img
        src="src/images/1.jpg"
        alt="course"
        className="course-img"
      />

      <div className="course-body">
        <span className="course-tag">Development</span>
        <h5 className="course-title">
          Full-stack Web Development
        </h5>
        <p className="course-desc">
          Learn HTML, CSS, React and Spring Boot from scratch.
        </p>

        <div className="course-footer">
          <span className="course-price">$49.99</span>
          <button className="course-btn">+</button>
        </div>
      </div>
    </div>
  );
};

/*const HomeCourseCard  = ({ course, onClick }) => {
  
  return (
    <div className="course-card" onClick={onClick}>
      <div
        className="course-image"
        style={{ backgroundImage: `url(${course.thumbnailUrl})` }}
      />

      <div className="course-body">
        <div className="course-meta">
          <span className={`course-tag ${course.category}`}>
            {course.category}
          </span>
          <span className="course-rating">⭐ {course.rating}</span>
        </div>

        <h5 className="course-title">{course.title}</h5>
        <p className="course-desc">{course.shortDescription}</p>

        <div className="course-footer">
          <span className="course-price">${course.price}</span>
          <button className="cart-btn">🛒</button>
        </div>
      </div>
    </div>
  );
};*/

export default HomeCourseCard;
