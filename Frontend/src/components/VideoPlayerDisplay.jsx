import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import useFetch from '../hooks/useFetch';
import LoadingSpinner from './LoadingSpinner';
import ErrorMessage from './ErrorMessage';
import { Play, Download, Calendar } from 'lucide-react';
import './VideoPlayerDisplay.css';

export default function VideoPlayerDisplay({ lessonID }) {
  const { t } = useTranslation("courseLesson");
  const [videos, setVideos] = useState([]);
  const [selectedVideo, setSelectedVideo] = useState(null);
  const [currentTime, setCurrentTime] = useState(0);

  // Fetch videos for the lesson
  const { data: videosData, loading, error, get: getVideos } = useFetch();

  // Fetch videos when lessonID changes
  useEffect(() => {
    if (lessonID) {
      getVideos(`/api/videos/lesson/${lessonID}`);
    }
  }, [lessonID, getVideos]);

  // Update videos when data is fetched
  useEffect(() => {
    if (videosData && Array.isArray(videosData)) {
      setVideos(videosData);
      if (videosData.length > 0 && !selectedVideo) {
        setSelectedVideo(videosData[0]);
      }
    }
  }, [videosData]);

  // Handle video selection
  const handleVideoSelect = (video) => {
    setSelectedVideo(video);
    setCurrentTime(0);
  };

  // Format duration in seconds to MM:SS
  const formatDuration = (seconds) => {
    if (!seconds) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  // Format date
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  if (loading) {
    return <LoadingSpinner loading={true} />;
  }

  if (error) {
    return <ErrorMessage error={error} />;
  }

  // If no videos available
  if (!videos || videos.length === 0) {
    return (
      <div className="video-player-display empty-state">
        <div className="empty-message">
          <p>{t('videoPlayer.noVideosAvailable')}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="video-player-display">
      <div className="video-player-section">
        {selectedVideo && (
          <div className="player-wrapper">
            <video
              key={selectedVideo.videoId}
              className="video-player"
              controls
              width="100%"
              controlsList="nodownload"
              onTimeUpdate={(e) => setCurrentTime(e.target.currentTime)}
            >
              <source src={selectedVideo.videoUrl} type="video/mp4" />
              {t('videoPlayer.browserNotSupport')}
            </video>
            <div className="player-info">
              <h3 className="video-title">{selectedVideo.title}</h3>
              <div className="video-meta">
                <span className="duration">
                  {formatDuration(selectedVideo.duration)}
                </span>
                <span className="upload-date">
                  <Calendar size={14} />
                  {formatDate(selectedVideo.createdAt)}
                </span>
              </div>
            </div>
          </div>
        )}
      </div>

      {videos.length > 1 && (
        <div className="video-playlist">
          <h4 className="playlist-title">
            {t('videoPlayer.playlist')} ({videos.length})
          </h4>
          <div className="playlist-items">
            {videos.map((video) => (
              <div
                key={video.videoId}
                className={`playlist-item ${selectedVideo?.videoId === video.videoId ? 'active' : ''}`}
                onClick={() => handleVideoSelect(video)}
              >
                <div className="item-thumbnail">
                  <Play size={24} className="play-icon" />
                </div>
                <div className="item-info">
                  <h5 className="item-title">{video.title}</h5>
                  <p className="item-duration">
                    {formatDuration(video.duration)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
