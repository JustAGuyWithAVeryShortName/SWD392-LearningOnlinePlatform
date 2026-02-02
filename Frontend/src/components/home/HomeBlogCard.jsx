import "./HomeBlogCard.css";

const HomeBlogCard = ({ blog, onReadMore }) => {
  const data = blog || {
    thumbnailUrl: "/images/blog-demo.jpg", // nhớ để ảnh trong public/images
    category: "Technology",
    title: "How to Learn Programming Effectively",
    excerpt: "Tips and strategies to improve your coding skills faster.",
    createdDate: "Sep 12, 2025",
  };

  return (
    <div className="blog-card" onClick={onReadMore}>
      <div
        className="blog-thumb"
        style={{
          backgroundImage: `url(${data.thumbnailUrl})`,
        }}
      />
      <div className="blog-content">
        <span className="blog-tag">{data.category}</span>
        <h5>{data.title}</h5>
        <p>{data.excerpt}</p>
        <small>{data.createdDate}</small>
      </div>
    </div>
  );
};
/*const HomeBlogCard  = ({ blog, onReadMore }) => {
  return (
    <div className="blog-card" onClick={onReadMore}>
      <div
        className="blog-thumb"
        style={{ backgroundImage: `url(${blog.thumbnailUrl})` }}
      />
      <div className="blog-content">
        <span className="blog-tag">{blog.category}</span>
        <h5>{blog.title}</h5>
        <p>{blog.excerpt}</p>
        <small>{blog.createdDate}</small>
      </div>
    </div>
  );
};*/

export default HomeBlogCard;
