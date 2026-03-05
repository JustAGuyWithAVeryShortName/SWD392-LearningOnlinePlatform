import "./HomeBlogCard.css";

const HomeBlogCard = ({ blog, onReadMore }) => {

  if (!blog) return null;

  return (
    <div className="blog-card" onClick={onReadMore}>
      <div
        className="blog-thumb"
        style={{ backgroundImage: `url(${blog.image})` }}
      />

      <div className="blog-content">
        <span className="blog-tag">{blog.blogType}</span>

        <h5>{blog.blogName}</h5>

        <p>
          {blog.blogDescription?.slice(0, 150)}...
        </p>

        <div className="blog-footer">
          <small>{blog.createdDate}</small>
          <span className="read-more">Read more →</span>
        </div>
      </div>
    </div>
  );
};

export default HomeBlogCard;