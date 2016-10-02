package de.dbon.java.vlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class MediaLibrary {

  public static String mLibHashes = ",";
  public static String diskHashes = "";
  public static long mLibCount = 0;
  public static long diskCount = 0;

  /**
   * Checks if file configuration.ini exists in app directory. If not exists or file doesn't contain
   * the key 'database.location' or key doesn't have a value the user has do select workspace via
   * dialog. After selecting workspace path a sqlite database file will be created at this location.
   * Contents of database will be displayed in table. If database doesn't contain any files a dialog
   * will be fired up to select a 'observationDirectory'.
   * 
   * @param args command line arguments
   * @throws SQLException sqlexceptions
   */
  public static void main(String[] args) throws IOException, SQLException {

    Logger.log("-------------------------------");
    Logger.log("App started....");

    Interface.getInstance().run();
    Configuration.initialize();
  }

  @SuppressWarnings("unused")
  private void removeDatabaseFile() throws IOException {
    File dbfile = new File(Configuration.databaseFileName);
    Path dbpath = dbfile.toPath();
    Files.deleteIfExists(dbpath);
  }

  public static String generateFileHash(String name, String extension, String filesize) {
    return name + extension + "-" + filesize;
  }
}
