import { Card } from "react-bootstrap"
import { Calendar, Clock, User } from "lucide-react"
import "./BlogCard.css"
import { useTranslation } from "react-i18next"

const BlogCard = ({ blog, status, onReadClick }) => {
  const { t } = useTranslation("blogCard")

  return (
    <Card
      className="blog-card-2 mb-4"
      style={{
        backgroundImage: `url(${blog.image})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <div className="blog-card-overlay-2">

        <div className="blog-card-content-2 p-4">

          {/* META */}
          <div className="blog-meta-row-2">

            <span className="category-badge-2">
              {blog.blogType}
            </span>

            <div className="meta-group-2">

              <span className="meta-info-2">
                <Clock size={16} className="meta-icon-2"/>
                {blog.readingTime} {t("readingTimeSuffix")}
              </span>

              <span className="meta-info-2">
                <Calendar size={16} className="meta-icon-2"/>
                {new Date(blog.createdAt).toLocaleDateString()}
              </span>

              <span className="meta-info-2">
                <User size={16} className="meta-icon-2"/>
                {blog.member.username}
              </span>

            </div>

          </div>

          {/* TITLE */}
          <h2 className="blog-title-2">
            {blog.blogName}
          </h2>

          {/* DESCRIPTION */}
          <p className="blog-excerpt-2">
            {blog.description}
          </p>

          {/* READ MORE */}
          {status !== "draft" && (
            <div className="blog-readmore-wrapper-2">

              <span
                className="read-more-2"
                onClick={() => onReadClick(blog.blogID)}
              >
                {t("readButton")} →
              </span>

            </div>
          )}

        </div>
      </div>
    </Card>
  )
}

export default BlogCard