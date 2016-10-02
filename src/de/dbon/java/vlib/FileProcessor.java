package de.dbon.java.vlib;

import java.io.File;
import java.io.FileNotFoundException;
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
  public static ArrayList<MediaFile> mLibObjects = null;

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
      MediaLibrary.diskHashes = "";
      MediaLibrary.diskCount = 0;
      Configuration.unsupportedExtensions = "";
      // reads media files from hard drive and adds them to scannedMediaFiles
      scanFilesOnDisk(new ArrayList<File>(Arrays.asList(searchDir.listFiles())));
      // delta logic
      importNewFiles(harddriveFiles, Configuration.databaseDir, mLibObjects);
      // check if there are files in library which doesn't exist on file system anymore
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
    Logger.log("skipped relevant files: " + skippedMediaFileCount);
    Logger.log("ignored files by extension: " + ignoredFilesByExtension);
    Logger.log("files in library: " + MediaLibrary.mLibCount);
    Logger.log("files found on disk: " + MediaLibrary.diskCount);
    Logger.log("--------------------------------------------------");
  }

  private void fileIntegrityCheck() throws SQLException {
    Logger.log("### File Integrety Check Start", Logger.LOG_LEVEL_MUTE);
    vLibStatus();

    Logger.log("mLibCount: " + MediaLibrary.mLibCount, Logger.LOG_LEVEL_MUTE);
    Logger.log("diskCount: " + MediaLibrary.diskCount, Logger.LOG_LEVEL_MUTE);

    if (MediaLibrary.mLibCount > MediaLibrary.diskCount) {
      Logger.log("integrity check failed - scanning for deleted files");
      Logger.log("Reason: media files in library > media files on disk");
      removeOutdatedFilesFromDB(harddriveFiles, Configuration.databaseDir, mLibObjects);
      DatabaseWorker.getInstance().readMLibIntoObjects();
      fileIntegrityCheck();
    } else {
      Logger.log("integrity check successfull - all files up to date.");
    }
    Logger.log("### File Integrety Check End", Logger.LOG_LEVEL_MUTE);
  }

  private void removeOutdatedFilesFromDB(ArrayList<MediaFile> scannedMediaFiles,
      String databaseFile, ArrayList<MediaFile> mediaFileLibrary) throws SQLException {
    Logger.log("searching for outdated files...");
    int count = 0;
    for (MediaFile mLibFile : mediaFileLibrary) {
      // is there a media file which is not on hard disc anymore?

      Logger.log("diskHashes:" + MediaLibrary.diskHashes, Logger.LOG_LEVEL_MUTE);
      Logger.log("mLibFile:" + mLibFile.getHash(), Logger.LOG_LEVEL_MUTE);

      if (!MediaLibrary.diskHashes.contains(mLibFile.getHash())) {
        // if yes delete it from db file
        Logger.log("file " + mLibFile.getName() + " not found on disk, removing from db",
            Logger.LOG_LEVEL_FILE);

        // remove delete file from mLibHases to prevent errors in later workflow
        Logger.log("removing deleted file hash " + mLibFile.getHash() + " from mLibHashes",
            Logger.LOG_LEVEL_FILE);
        String searchHash = "," + mLibFile.getHash() + ",";
        MediaLibrary.mLibHashes = MediaLibrary.mLibHashes.replace(searchHash, ",");

        DatabaseWorker.getInstance().deleteMediaFile(mLibFile.getHash());

        count++;
      }
    }

    if (count == 0) {
      Logger.log("no outdated files found.");
    }
    Interface.getInstance().reloadFileTable();
  }

  /**
   * searches for the delta between loaded vids from sqlite db and fetched media files from file
   * system and writes delta to database.
   * 
   * @param harddriveFiles files scanned from filesystem
   * @param databaseFile the database file
   * @param mediaLibrary files which were already stored in database
   * @throws SQLException
   */
  public static void importNewFiles(ArrayList<MediaFile> harddriveFiles, String databaseFile,
      ArrayList<MediaFile> mediaLibrary) throws SQLException {
    Logger.log("### Delta Logic Start (Add or Skip)", Logger.LOG_LEVEL_FILE);

    // for each file from harddrive
    for (MediaFile diskFile : harddriveFiles) {

      // when file from disk is not yes in db: add it!
      if (!MediaLibrary.mLibHashes.contains(diskFile.getHash())) {
        Logger.log("processing scanned file with hash: " + diskFile.getHash(),
            Logger.LOG_LEVEL_FILE);
        DatabaseWorker.getInstance().insertMediaFile(diskFile, databaseFile);
        Logger.log("file not found in db, adding: " + diskFile.toString(), Logger.LOG_LEVEL_FILE);
        MediaLibrary.mLibCount++;
      } else {
        // Logger.log("media File already inserted, skipping.");
        skippedMediaFileCount++;
        skippedMediaFiles.add(diskFile);
      }
    }
    Logger.log("### Delta Logic END", Logger.LOG_LEVEL_FILE);
  }

  private void scanFilesOnDisk(ArrayList<File> files) throws IOException, NoSuchAlgorithmException {
    Logger.log("### Scanning files on disk START", Logger.LOG_LEVEL_FILE);
    for (File file : files) {
      Logger.log("scanning file " + file.getAbsolutePath(), Logger.LOG_LEVEL_FILE);

      ShellFolder folder = null;
      try {
        folder = ShellFolder.getShellFolder(file);
      } catch (FileNotFoundException e) {
        Logger.log("file not found: " + file.getAbsolutePath());
        continue;
      }

      // recursive call to deep fetch all media files
      if (file.isDirectory() && !folder.isLink()) {
        scanFilesOnDisk(new ArrayList<File>(Arrays.asList(file.listFiles())));
      } else {
        MediaFile mFile = new MediaFile();

        if (file.getName().contains(".")) {
          mFile.setExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));
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
    Logger.log("### Scanning files on disk END", Logger.LOG_LEVEL_FILE);
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
