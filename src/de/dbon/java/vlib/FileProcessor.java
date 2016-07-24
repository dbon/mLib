package de.dbon.java.vlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import sun.awt.shell.ShellFolder;
import de.dbon.java.vlib.object.MediaFile;

public class FileProcessor implements Runnable {

  private File searchDir = null;
  private ArrayList<File> filesInDirectory = null;
  private static ArrayList<MediaFile> harddriveFiles = null;
  private static ArrayList<MediaFile> skippedMediaFiles = null;
  public static ArrayList<MediaFile> sqliteFiles = null;

  public static long addedCount = 0;
  public static long skippedMediaFileCount = 0;
  public static long ignoredFilesByExtension = 0;

  /**
   * scans file system recursively for media files and writes them to database.
   * 
   * @param dir the root folder in wich the scan starts
   * @throws IOException
   * @throws SQLException
   * @throws NoSuchAlgorithmException
   */
  public FileProcessor(String dir) throws IOException, SQLException, NoSuchAlgorithmException {
    searchDir = new File(dir);
    harddriveFiles = new ArrayList<MediaFile>();
    skippedMediaFiles = new ArrayList<MediaFile>();
    skippedMediaFileCount = 0;
    ignoredFilesByExtension = 0;
    MediaLibrary.diskCount = 0;
  }

  @Override
  public void run() {
    try {
      // reads media files from hard drive and adds them to scannedMediaFiles
      scanFilesOnDisk(new ArrayList<File>(Arrays.asList(searchDir.listFiles())));
      // delta logic
      importNewFiles(harddriveFiles, Configuration.databasePathAndFile, sqliteFiles);
      fileIntegrityCheck();
      Interface.getInstance().reloadFileTable();
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void vLibStatus() {
    Logger.log("--------------------------------------------------");
    Logger.log("unsupportedExtension: " + Configuration.unsupportedExtensions);
    Logger.log("suspeciousFiles: " + Configuration.suspiciousFiles);
    Logger.log("added files: " + addedCount);
    Logger.log("skipped relevant files: " + skippedMediaFileCount);
    Logger.log("ignored files by extension: " + ignoredFilesByExtension);
    Logger.log("files in library: " + MediaLibrary.vLibCount);
    Logger.log("files found on disk: " + MediaLibrary.diskCount);
    Logger.log("--------------------------------------------------");
  }

  private void fileIntegrityCheck() throws SQLException {

    vLibStatus();

    if (MediaLibrary.vLibCount > MediaLibrary.diskCount) {
      Logger.log("integrity check failed - scanning for deleted files");
      Logger.log("Reason: media files in library > media files on disk");
      removeOutdatedFilesFromDB(harddriveFiles, Configuration.databasePathAndFile, sqliteFiles);
      DatabaseWorker.getInstance().readLibraryIntoObjects();
      fileIntegrityCheck();
    } else {
      Logger.log("integrity check successfull - all files up to date.");
    }
  }

  private void removeOutdatedFilesFromDB(ArrayList<MediaFile> scannedMediaFiles,
      String databaseFile, ArrayList<MediaFile> mediaFileLibrary) throws SQLException {
    Logger.log("searching for outdated files...");
    int count = 0;
    for (MediaFile vid : mediaFileLibrary) {
      // is there a media file which is not on hard disc anymore?
      if (!MediaLibrary.diskHashes.contains(vid.getHash())) {
        // if yes delete it from db file
        DatabaseWorker.getInstance().deleteMediaFile(vid.getHash());
        Logger.log("file " + vid.getName() + " not found on disk, removing from db");
        count++;
      } else {
        Logger
            .log("file found in db... that means that we have duplicated files in db.. delete DB file and reInitialize!!!");
        Logger.log("duplicat file: " + vid.toString());
      }
    }

    if (count == 0) {
      Logger.log("no outdated files found.");
    }

  }

  /**
   * searches for the delta between loaded vids from sqlite db and fetched media files from file
   * system and writes delta to database.
   * 
   * @param scannedMediaFiles files scanned from filesystem
   * @param databaseFile the database file
   * @param mediaLibrary files which were already stored in database
   * @throws SQLException
   */
  public static void importNewFiles(ArrayList<MediaFile> scannedMediaFiles, String databaseFile,
      ArrayList<MediaFile> mediaLibrary) throws SQLException {

    for (MediaFile vid : scannedMediaFiles) {

      // Logger.log("processing scanned file with hash: " + vid.getHash());

      if (!MediaLibrary.vlibHashes.contains(vid.getHash())) {
        Logger.log("processing scanned file with hash: " + vid.getHash());
        DatabaseWorker.getInstance().insertMediaFile(vid, databaseFile);
        Logger.log("file not found in db, adding: " + vid.toString());
        addedCount++;
        MediaLibrary.vLibCount++;
      } else {
        // Logger.log("media File already inserted, skipping.");
        skippedMediaFileCount++;
        skippedMediaFiles.add(vid);
      }
    }
  }

  private void scanFilesOnDisk(ArrayList<File> files) throws IOException, NoSuchAlgorithmException {

    for (File file : files) {
      ShellFolder folder = ShellFolder.getShellFolder(file);
      // recursive call to deep fetch all media files
      if (file.isDirectory() && !folder.isLink()) {
        scanFilesOnDisk(new ArrayList<File>(Arrays.asList(file.listFiles())));
      } else {
        MediaFile mFile = new MediaFile();

        if (file.getName().contains(".")) {
          mFile.setExtension(file.getName().substring(file.getName().lastIndexOf(".")));
        } else {
          Configuration.suspiciousFiles += file.getPath() + ",";
          continue;
        }

        if (Configuration.allowedExtensions.toLowerCase().contains(
            mFile.getExtension().toLowerCase())) {
          mFile.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));

          mFile.setPath(file.getAbsolutePath().replace("?", ""));
          mFile.setFilesize(file.length() / 1024 / 1024);
          mFile.setViewcount(0);
          mFile.setTags("");

          // get last access time of file
          Path f2 = file.toPath();
          BasicFileAttributes attr = Files.readAttributes(f2, BasicFileAttributes.class);
          FileTime lastviewed = attr.lastAccessTime();
          SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
          String lastmodified = df.format(lastviewed.toMillis());
          mFile.setLastviewed(lastmodified);

          mFile.setHash(MediaLibrary.generateFileHash(mFile.getName(), mFile.getExtension(),
              String.valueOf(mFile.getFilesize())));

          MediaLibrary.diskCount++;
          MediaLibrary.diskHashes += mFile.getHash();
          harddriveFiles.add(mFile);

        } else {
          if (!Configuration.unsupportedExtensions.toLowerCase().contains(
              mFile.getExtension().toLowerCase())) {
            Configuration.unsupportedExtensions += mFile.getExtension().toLowerCase() + ",";
            ignoredFilesByExtension++;
          }
        }
      }
    }
  }

  public String getAllFiles() {
    String allFiles = "";
    for (File mediaFile : filesInDirectory) {
      allFiles = allFiles + "\r\n" + mediaFile.getName();
    }
    return allFiles;
  }

  void listSkippedMediaFiles() {
    Logger.log("skipped files:");
    for (MediaFile vid : skippedMediaFiles) {
      Logger.log(vid.getName());
    }
  }
}
