# Video Management API Documentation

## Overview
Video Management feature provides comprehensive functionality for uploading, streaming, and managing course videos in the E-Learning platform.

## Features Implemented

### Phase 1: Core Features ✅
- ✅ Video file validation (size, type, extension)
- ✅ Secure upload with ROLE-based authorization
- ✅ Structured error handling with custom exceptions
- ✅ Response DTOs for API contracts
- ✅ Configuration management for video parameters

### Phase 2: Enhanced Features ✅
- ✅ Video streaming endpoint (inline playback)
- ✅ Video download endpoint (attachment)
- ✅ Video duration metadata extraction
- ✅ Delete video functionality
- ✅ List videos by lesson

### Phase 3: Production Ready ✅
- ✅ Cloudinary integration (cloud-based storage)
- ✅ Swagger/OpenAPI documentation
- ✅ Unit tests

## API Endpoints

### 1. Upload Video
**Endpoint:** `POST /api/videos/upload`

**Request Parameters:**
- `title` (String, required): Video title (1-255 characters)
- `file` (MultipartFile, required): Video file
- `lessonId` (UUID, required): Associated lesson ID

**Authorization:** ADMIN, INSTRUCTOR

**Response:**
```json
{
  "status": 201,
  "message": "Video uploaded successfully",
  "data": {
    "videoId": "uuid",
    "title": "Introduction to English",
    "videoUrl": "http://localhost:8080/api/videos/stream/filename.mp4",
    "duration": 300,
    "lessonId": "uuid",
    "lessonName": "Lesson Name"
  }
}
```

**Error Responses:**
- `400`: Invalid video file (unsupported format, oversized, etc.)
- `404`: Lesson not found
- `401`: Unauthorized
- `403`: Forbidden (insufficient role)
- `500`: Internal server error

### 2. Get Videos by Lesson
**Endpoint:** `GET /api/videos/lesson/{lessonId}`

**Path Parameters:**
- `lessonId` (UUID, required): Lesson ID

**Authorization:** ADMIN, INSTRUCTOR, STUDENT

**Response:**
```json
{
  "status": 200,
  "message": "Videos retrieved successfully",
  "data": [
    {
      "videoId": "uuid",
      "title": "Video Title",
      "videoUrl": "...",
      "duration": 300,
      "lessonId": "uuid",
      "lessonName": "Lesson Name"
    }
  ]
}
```

### 3. Stream Video
**Endpoint:** `GET /api/videos/stream/{filename}`

**Path Parameters:**
- `filename` (String, required): Video filename

**Returns:** Raw video stream for browser playback

**Supported Formats:** mp4, avi, mov, mkv, flv, webm

### 4. Download Video
**Endpoint:** `GET /api/videos/download/{filename}`

**Path Parameters:**
- `filename` (String, required): Video filename

**Returns:** Video file as attachment (Content-Type: application/octet-stream)

### 5. Delete Video
**Endpoint:** `DELETE /api/videos/{videoId}`

**Path Parameters:**
- `videoId` (UUID, required): Video ID

**Authorization:** ADMIN, INSTRUCTOR

**Response:**
```json
{
  "status": 200,
  "message": "Video deleted successfully"
}
```

## Configuration

Add to `application.properties`:

```properties
# Video Upload Configuration
app.video.max-file-size=104857600          # 100MB
app.video.allowed-extensions=mp4,avi,mov,mkv,flv,webm
app.video.base-url=http://localhost:8080

# Cloudinary Configuration (for cloud storage)
cloudinary.cloud_name=YOUR_CLOUD_NAME
cloudinary.api_key=YOUR_API_KEY
cloudinary.api_secret=YOUR_API_SECRET
```

## Storage Options

### Option 1: Local Storage (Default)
Videos are stored in `uploads/videos/` directory

**Pros:**
- Simple setup
- No external dependencies

**Cons:**
- Limited scalability
- Not ideal for production

**Usage:**
Use `VideoService` for local file uploads

### Option 2: Cloudinary (Recommended for Production)
Videos hosted on Cloudinary CDN

**Pros:**
- Scalable, cloud-based
- Automatic transcoding
- Global CDN delivery
- Easy maintenance

**Cons:**
- Requires Cloudinary account
- Additional costs

**Usage:**
```java
@Autowired
private CloudinaryVideoService cloudinaryVideoService;

// Upload to Cloudinary
VideoResponse response = cloudinaryVideoService.uploadVideoToCloudinary(
    title, file, lesson
);
```

## File Validation

### Constraints
- **Max File Size:** 100MB (configurable)
- **Allowed Extensions:** mp4, avi, mov, mkv, flv, webm
- **Supported MIME Types:** video/*

### Validation Process
1. Check if file is not empty
2. Verify file size doesn't exceed limit
3. Validate file extension against whitelist
4. Check title is not empty

## Error Handling

### Custom Exceptions
- `InvalidVideoException`: Thrown for validation failures
- `ResourceNotFoundException`: Thrown when video/lesson not found

### Global Exception Handler
All exceptions are caught by `GlobalExceptionHandler` and return standardized `ApiResponse`:

```json
{
  "status": 400,
  "message": "Error message",
  "errors": null
}
```

## Security

### Authorization & Authentication
- All upload/delete operations require JWT token
- Role-based access control:
  - ADMIN: Full access (upload, delete, view)
  - INSTRUCTOR: Can upload and delete own course videos
  - STUDENT: Can only view/stream videos

### Password Security
- Credentials stored via environment variables
- Cloudinary credentials in application.properties

## Video Metadata

### Duration Extraction
Duration is automatically extracted during upload:
- **Local Upload:** Uses `VideoMetadataUtil` (requires FFProbe)
- **Cloudinary Upload:** Cloudinary extracts automatically

### To Enable Real Duration Extraction:
1. Install FFProbe on server
2. Update `VideoMetadataUtil.extractDurationWithFFProbe()`

## Testing

### Unit Tests
Run VideoService tests:
```bash
mvn test -Dtest=VideoServiceTest
```

### Test Coverage
- File validation
- Video retrieval
- Delete operations
- Error handling

### Manual Testing with cURL

**Upload:**
```bash
curl -X POST "http://localhost:8080/api/videos/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "title=Test Video" \
  -F "file=@video.mp4" \
  -F "lessonId=YOUR_LESSON_ID"
```

**Get Videos:**
```bash
curl -X GET "http://localhost:8080/api/videos/lesson/{lessonId}" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Stream:**
```bash
curl "http://localhost:8080/api/videos/stream/filename.mp4"
```

**Delete:**
```bash
curl -X DELETE "http://localhost:8080/api/videos/{videoId}" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Performance Considerations

1. **Streaming:** Videos served directly from storage
2. **Caching:** Implement CDN caching for public streams
3. **Database:** Index on `lesson_id` for fast queries
4. **File System:** Regular cleanup of deleted video files

## Future Enhancements

1. **Video Transcoding:** Automatic format conversion (Cloudinary)
2. **Thumbnail Generation:** Extract first frame as thumbnail
3. **Subtitles Support:** Upload and serve subtitle files
4. **Adaptive Bitrate:** Multiple quality options (HLS/DASH)
5. **Viewing Analytics:** Track student video watch time
6. **Progressive Download:** Support resume on pause
7. **Plagiarism Detection:** Prevent unauthorized hosting

## Troubleshooting

### Issue: "File size exceeds maximum limit"
**Solution:** Increase `app.video.max-file-size` in properties

### Issue: Video upload fails with "resource type is invalid"
**Solution:** Check file extension is in `app.video.allowed-extensions`

### Issue: Duration always 0
**Solution:** Install FFProbe for duration extraction:
```bash
# Linux
sudo apt-get install ffmpeg

# macOS
brew install ffmpeg

# Windows
choco install ffmpeg
```

### Issue: Cloudinary upload fails
**Solution:** Verify Cloudinary credentials in environment variables

## References
- Spring Boot Multipart File Upload
- Cloudinary API Documentation
- OpenAPI 3.0 Specification
- RFC 7233 (HTTP Range Requests)
