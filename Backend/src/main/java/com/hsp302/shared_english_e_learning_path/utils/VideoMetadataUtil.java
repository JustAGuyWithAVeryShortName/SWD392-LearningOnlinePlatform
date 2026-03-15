package com.hsp302.shared_english_e_learning_path.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for extracting video metadata
 * Currently supports basic duration calculation
 * Can be extended with FFProbe integration for advanced metadata extraction
 */
public class VideoMetadataUtil {

    /**
     * Extract duration from video file in seconds
     * 
     * @param videoFile the video file
     * @return duration in seconds (0 if unable to extract)
     * 
     *         Note: Currently returns 0 as default.
     *         To implement real duration extraction, integrate with:
     *         1. FFProbe (execute system command)
     *         2. JAVE library (ffmpeg-jave dependency)
     *         3. Xuggler library for Java-based extraction
     */
    public static int extractDuration(File videoFile) {
        if (videoFile == null || !videoFile.exists()) {
            return 0;
        }

        try {
            return extractDurationWithFFProbe(videoFile);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Extract duration using FFProbe command
     * Requires FFProbe to be installed on the system
     * 
     * @param videoFile the video file
     * @return duration in seconds
     */
    public static int extractDurationWithFFProbe(File videoFile) {
        if (videoFile == null || !videoFile.exists()) {
            return 0;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1:nokey_wrappers=1",
                    videoFile.getAbsolutePath());

            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished || process.exitValue() != 0 || output.isBlank()) {
                process.destroyForcibly();
                return 0;
            }

            double duration = Double.parseDouble(output);
            return (int) Math.ceil(duration);

        } catch (IOException | InterruptedException | NumberFormatException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return 0;
        }
    }
}
