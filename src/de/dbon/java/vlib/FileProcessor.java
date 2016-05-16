package de.dbon.java.vlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import de.dbon.java.vlib.object.Video;

public class FileProcessor {

  private File directory = null;
  private ArrayList<File> filesInDirectory = null;
  private static ArrayList<Video> scannedVideos = null;
  public static ArrayList<Video> videoLibrary = new ArrayList<Video>();
  private String allowedExtensions = ".mp4,.mpg,.avi,.flv,.wmv,.mov,.mpeg,.mpg,.divx,.mkv";
  private String unsupportedExtensions = "";
  private String suspiciousFiles;
  public static long addedCount = 0;
  public static long skippedVideos = 0;

  /**
   * scans file system recursively for videos and writes them to database.
   * 
   * @param dir the root folder in wich the scan starts
   * @throws IOException
   * @throws SQLException
   */
  public FileProcessor(String dir, String databaseFile) throws IOException, SQLException {
    directory = new File(dir);
    scannedVideos = new ArrayList<Video>();
    fetchVideos(new ArrayList<File>(Arrays.asList(directory.listFiles())));
    calculateVideoDelta(scannedVideos, databaseFile, videoLibrary);

    Logger.log("unsupportedExtension: " + unsupportedExtensions);
    Logger.log("suspeciousFiles: " + suspiciousFiles);
    Logger.log("added videos: " + addedCount);
    Logger.log("skipped videos: " + skippedVideos);

  }

  /**
   * searches for the delta between loaded vids from sqlite db and fetched videos from file system
   * and writes delta to database.
   * 
   * @param scannedVideos videos scanned from filesystem
   * @param databaseFile the database file
   * @param vlib videos which were already stored in database
   * @throws SQLException
   */
  public static void calculateVideoDelta(ArrayList<Video> scannedVideos, String databaseFile,
      ArrayList<Video> vlib) throws SQLException {

    // TODO: add logic to calculate delta between scannedVideos and videoLibrary of databaseFile

    for (Video vid : scannedVideos) {

      if (!vlib.contains(vid)) {
        // SqliteConnector.insertVideo(vid, databaseFile);
        Logger.log("writing video to db: " + vid.toString());
        addedCount++;
      } else {
        Logger.log("video already inserted, skipping.");
        skippedVideos++;
      }


    }
  }

  private void fetchVideos(ArrayList<File> files) throws IOException {
    for (File file : files) {

      // recursive call to deep fetch all videos
      if (file.isDirectory()) {
        fetchVideos(new ArrayList<File>(Arrays.asList(file.listFiles())));
      } else {

        Video video = new Video();

        if (file.getName().contains(".")) {
          video.setExtension(file.getName().substring(file.getName().lastIndexOf(".")));
        } else {
          suspiciousFiles += file.getPath() + ",";
          continue;
        }

        if (allowedExtensions.toLowerCase().contains(video.getExtension().toLowerCase())) {
          video.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));

          video.setPath(file.getAbsolutePath().replace("?", ""));
          video.setFilesize(file.length() / 1024 / 1024);
          video.setViewcount(0);
          video.setTags("");

          // get last access time of file
          Path f2 = file.toPath();
          BasicFileAttributes attr = Files.readAttributes(f2, BasicFileAttributes.class);
          FileTime lastviewed = attr.lastAccessTime();
          SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
          String lastmodified = df.format(lastviewed.toMillis());
          video.setLastviewed(lastmodified);

          scannedVideos.add(video);

        } else {
          Logger.log("extension " + video.getExtension() + " not allowed, skipping file");
          if (!unsupportedExtensions.toLowerCase().contains(video.getExtension().toLowerCase())) {
            unsupportedExtensions += video.getExtension().toLowerCase() + ",";
          }
        }
        Logger.log(video.toString());
      }
    }
  }

  public String getAllFiles() {
    String allFiles = "";
    for (File video : filesInDirectory) {
      allFiles = allFiles + "\r\n" + video.getName();
    }
    return allFiles;
  }


}
