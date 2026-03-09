import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import useFetch from '../hooks/useFetch';
import LoadingSpinner from './LoadingSpinner';
import ErrorMessage from './ErrorMessage';
import { Play, Download } from 'lucide-react';
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
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
