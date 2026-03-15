import { useState, useEffect } from "react"
import { Form, Button, Alert, ProgressBar } from "react-bootstrap"
import { Upload, Trash2, Play } from "lucide-react"
import { useTranslation } from "react-i18next"
import useFetch from "../../hooks/useFetch"
import { toast } from "react-toastify"
import "./VideoUploadForm.css"

const VideoUploadForm = ({ lessonID, onVideoAdded, onVideoCountChange }) => {
  const { t } = useTranslation("lessonCreation")
  const [videoFile, setVideoFile] = useState(null)
  const [videoTitle, setVideoTitle] = useState("")
  const [videos, setVideos] = useState([])
  const [uploading, setUploading] = useState(false)
  const [uploadProgress, setUploadProgress] = useState(0)
  const [loadingVideos, setLoadingVideos] = useState(false)
  const { post: uploadVideo } = useFetch()
  const { get: fetchVideos } = useFetch()
  const { delete: deleteVideo } = useFetch()

  // Fetch videos when lesson is loaded
  const loadVideos = async () => {
    if (!lessonID) {
      setVideos([])
      onVideoCountChange?.(0)
      return
    }
    setLoadingVideos(true)
    try {
      // useFetch already extracts .data from ApiResponse
      const response = await fetchVideos(`http://localhost:8080/api/videos/lesson/${lessonID}`)
      const normalizedVideos = Array.isArray(response) ? response : []
      setVideos(normalizedVideos)
      onVideoCountChange?.(normalizedVideos.length)
    } catch (error) {
      console.error("Failed to fetch videos:", error)
      setVideos([])
      onVideoCountChange?.(0)
    } finally {
      setLoadingVideos(false)
    }
  }

  // Load videos when component mounts or lessonID changes
  useEffect(() => {
    loadVideos()
  }, [lessonID])

  // Handle file selection
  const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (file) {
      // Validate file type
      const allowedTypes = ["video/mp4", "video/x-msvideo", "video/quicktime", "video/x-matroska", "video/x-flv", "video/webm"]
      const allowedExtensions = ["mp4", "avi", "mov", "mkv", "flv", "webm"]

      const fileExtension = file.name.split(".").pop().toLowerCase()
      const isValidType = allowedTypes.includes(file.type) || allowedExtensions.includes(fileExtension)

      if (!isValidType) {
        toast.error(t("video.invalidType"))
        return
      }

      // Validate file size (100MB)
      const maxSize = 100 * 1024 * 1024
      if (file.size > maxSize) {
        toast.error(t("video.fileTooLarge"))
        return
      }

      setVideoFile(file)
    }
  }

  // Handle video upload
  const handleUploadVideo = async () => {
    if (!videoFile) {
      toast.error(t("video.selectFile"))
      return
    }

    if (!videoTitle.trim()) {
      toast.error(t("video.enterTitle"))
      return
    }

    if (!lessonID) {
      toast.error(t("video.lessonRequired"))
      return
    }

    setUploading(true)
    setUploadProgress(0)

    try {
      const formData = new FormData()
      formData.append("file", videoFile)
      formData.append("title", videoTitle)
      formData.append("lessonId", lessonID)

      // Simulate progress
      const progressInterval = setInterval(() => {
        setUploadProgress((prev) => {
          if (prev < 90) return prev + 10
          return prev
        })
      }, 200)

      const response = await uploadVideo(formData, {}, "http://localhost:8080/api/videos/upload")

      clearInterval(progressInterval)
      setUploadProgress(100)

      // Reset form
      setVideoFile(null)
      setVideoTitle("")
      setUploadProgress(0)

      // Reload videos from server to ensure consistency
      await loadVideos()

      toast.success(t("video.uploadSuccess"))
      onVideoAdded?.()

    } catch (error) {
      toast.error(error.response?.data?.message || t("video.uploadError"))
    } finally {
      setUploading(false)
      setUploadProgress(0)
    }
  }

  // Handle video delete
  const handleDeleteVideo = async (videoId) => {
    if (window.confirm(t("video.confirmDelete"))) {
      try {
        await deleteVideo({}, "DELETE", `http://localhost:8080/api/videos/${videoId}`)
        // Reload videos from server after deletion
        await loadVideos()
        toast.success(t("video.deleteSuccess"))
      } catch (error) {
        toast.error(error.response?.data?.message || t("video.deleteError"))
      }
    }
  }

  return (
    <div className="video-upload-section mt-5">
      <h4 className="section-title">{t("video.title")}</h4>

      {/* Upload Form */}
      <div className="upload-form-container p-4 border rounded bg-light">
        <Form.Group className="mb-3">
          <Form.Label className="fw-bold">{t("video.videoTitle")}</Form.Label>
          <Form.Control
            type="text"
            placeholder={t("video.titlePlaceholder")}
            value={videoTitle}
            onChange={(e) => setVideoTitle(e.target.value)}
            disabled={uploading}
            className="video-title-input"
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label className="fw-bold">{t("video.selectVideo")}</Form.Label>
          <Form.Control
            type="file"
            accept="video/*"
            onChange={handleFileChange}
            disabled={uploading}
            className="video-file-input"
          />
          <Form.Text className="text-muted d-block mt-2">
            {t("video.supportedFormats")}: MP4, AVI, MOV, MKV, FLV, WebM
          </Form.Text>
          <Form.Text className="text-muted d-block">
            {t("video.maxSize")}: 100MB
          </Form.Text>
        </Form.Group>

        {videoFile && (
          <Alert variant="info" className="mt-3">
            <strong>{t("video.selectedFile")}:</strong> {videoFile.name} ({(videoFile.size / (1024 * 1024)).toFixed(2)} MB)
          </Alert>
        )}

        {uploading && (
          <div className="mb-3">
            <p className="text-muted">{t("video.uploading")}</p>
            <ProgressBar now={uploadProgress} label={`${uploadProgress}%`} animated />
          </div>
        )}

        <Button
          className="upload-btn"
          onClick={handleUploadVideo}
          disabled={!videoFile || !videoTitle.trim() || uploading}
        >
          <Upload size={16} className="me-2" />
          {uploading ? t("video.uploadingBtn") : t("video.uploadBtn")}
        </Button>
      </div>

      {/* Videos List */}
      {videos.length > 0 && (
        <div className="videos-list mt-4">
          <h5>{t("video.uploadedVideos")} ({videos.length})</h5>
          <div className="videos-grid">
            {videos.map((video) => (
              <div key={video.videoId} className="video-card border rounded p-3 mb-3">
                <div className="video-header d-flex justify-content-between align-items-start">
                  <div className="flex-grow-1">
                    <h6 className="video-title-display mb-2">{video.title}</h6>
                    {video.duration > 0 && (
                      <p className="text-muted small mb-0">
                        {t("video.duration")}: {Math.floor(video.duration / 60)}:{String(video.duration % 60).padStart(2, "0")}
                      </p>
                    )}
                  </div>
                  <div className="video-actions">
                    <a
                      href={video.videoUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="btn btn-sm btn-outline-primary me-2"
                      title={t("video.watchVideo")}
                    >
                      <Play size={14} />
                    </a>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => handleDeleteVideo(video.videoId)}
                      title={t("video.delete")}
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {videos.length === 0 && !uploading && (
        <Alert variant="info" className="mt-4">
          {t("video.noVideos")}
        </Alert>
      )}
    </div>
  )
}

export default VideoUploadForm
