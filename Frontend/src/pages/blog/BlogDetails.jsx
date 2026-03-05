import { Calendar, User, Clock, PencilLine, Trash } from "lucide-react"
import "./BlogDetails.css"
import useFetch from "../../hooks/useFetch"
import { useNavigate, useParams } from "react-router-dom"
import { useEffect, useState } from "react"
import Recommendation from "../../components/others/Recommendation"
import ErrorMessage from "../../components/ErrorMessage"
import BackButton from "../../components/BackButton"
import NotFound from "../not-found/NotFound"
import LoadingSpinner from "../../components/LoadingSpinner"
import { toast } from "react-toastify"
import { useTranslation } from "react-i18next"
import { useAuth } from "../../hooks/useAuth"

const BlogDetails = () => {
    const { user } = useAuth()
    const { t } = useTranslation("blogDetails")

    const { id } = useParams()
    const [blogDetails, setBlogDetails] = useState(null)

    const { loading: loadingBlogDetails, error: errorBlogDetails, get: getBlogDetails } = useFetch()
    const { put: putBlogStatus } = useFetch()

    const navigate = useNavigate()

    useEffect(() => {
        const fetchBlogs = async () => {
            try {
                if (id) {
                    const data = await getBlogDetails(`http://localhost:8080/api/blog/${id}`)
                    setBlogDetails(data)
                }
            } catch (error) {
                console.error("Fetch error:", error)
            }
        }

        fetchBlogs()
    }, [id, getBlogDetails])

    const handleEditBlog = () => {
        navigate(`/blogs/create/${id}`)
    }

    const handleDeleteBlog = async () => {
        try {
            const blogID = blogDetails?.blogID

            if (blogID) {
                await putBlogStatus({}, {}, `http://localhost:8080/api/blog/${blogID}/UNAVAILABLE`)
                toast.success(t("toastMessages.deleteSuccess"))
                navigate("/blogs")
            } else {
                toast.error(t("toastMessages.blogIdNotFound"))
            }

        } catch (error) {
            toast.error(t("toastMessages.deleteError"))
        }
    }

    if (loadingBlogDetails) {
        return (
            <div className="container-new">
                <LoadingSpinner loading={loadingBlogDetails} />
            </div>
        )
    }

    if (errorBlogDetails) {
        return (
            <div className="container-new">
                <ErrorMessage error={errorBlogDetails} />
            </div>
        )
    }

    if (!blogDetails) {
        return (
            <NotFound
                code={t("blogNotFound.code")}
                title={t("blogNotFound.title")}
                message={t("blogNotFound.message")}
                backLink="/blogs"
                backText={t("blogNotFound.backLinkText")}
            />
        )
    }

    return (
        <div className="blog-details-page">

            <div className="container-new">

                {/* Top bar */}
                <div className="blog-topbar">

                    <BackButton label={t("backButton")} />

                    {user?.username === blogDetails?.member?.username && (
                        <div className="action-buttons">

                            <button
                                className="action-btn edit-btn"
                                onClick={handleEditBlog}
                            >
                                <PencilLine size={18} />
                                {t("editButton")}
                            </button>

                            <button
                                className="action-btn delete-btn"
                                onClick={handleDeleteBlog}
                            >
                                <Trash size={18} />
                                {t("deleteButton")}
                            </button>

                        </div>
                    )}

                </div>

                {/* Header */}
                <div className="blog-header">

                    <h1 className="blog-detail-title">
                        {blogDetails.blogName}
                    </h1>

                    <p className="blog-author">
                        {t("byPrefix")} {blogDetails.member.username}
                    </p>

                    <div className="blog-meta">

                        <span className="category-badge">
                            {blogDetails.blogType}
                        </span>

                        <span className="meta-info">
                            <Clock size={16} />
                            {blogDetails.readingTime} {t("minsReading")}
                        </span>

                        <span className="meta-info">
                            <Calendar size={16} />
                            {blogDetails.createdAt}
                        </span>

                        <span className="meta-info">
                            <User size={16} />
                            {blogDetails.member.username}
                        </span>

                    </div>

                </div>

                {/* hashtags */}
                <div className="hashtags-container">
                    <span className="hashtag">{t("hashtags.recovery")}</span>
                    <span className="hashtag">{t("hashtags.trueStory")}</span>
                    <span className="hashtag">{t("hashtags.detox")}</span>
                    <span className="hashtag">{t("hashtags.hope")}</span>
                    <span className="hashtag">{t("hashtags.lifeJourney")}</span>
                </div>

                {/* Image */}
                <div className="featured-image-container">

                    <img
                        src={blogDetails.image}
                        alt={blogDetails.blogName}
                        className="featured-image"
                    />

                </div>

                {/* Content */}
                <div className="blog-content">

                    <section className="content-section">

                        <h2 className="section-title">
                            {t("sections.introductionTitle")}
                        </h2>

                        <p className="section-text">
                            {blogDetails.description}
                        </p>

                    </section>

                    <section className="content-section">

                        <h2 className="section-title">
                            {t("sections.mainContentTitle")}
                        </h2>

                        <div
                            className="section-text quill-content"
                            dangerouslySetInnerHTML={{
                                __html: blogDetails.content
                            }}
                        />

                    </section>

                </div>

            </div>

            <Recommendation type="blog" />

        </div>
    )
}

export default BlogDetails