package de.dbon.java.vlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.swing.JFrame;

public class MediaLibrary {

  public static String vlibHashes = "";
  public static String diskHashes = "";
  public static long vLibCount = 0;
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
   * @throws NoSuchAlgorithmException
   */
  public static void main(String[] args) throws IOException, SQLException, NoSuchAlgorithmException {

    Logger.log("-------------------------------");
    Logger.log("App started....");

    // TODO: make use of java properties
    Configuration.init();

    JFrame frame = Interface.getInstance().run();

    // when configuration file not exists
    if (!new File(Configuration.configurationFileName).exists()) {
      Logger.log("configuration file " + Configuration.configurationFileName
          + " not found: User has to select workspace path");
      Interface.getInstance().showSelectWorkspaceDialog(frame);
    } else {
      // read database file name and path from mlib.properties
      switch (readPropertyFile()) {
        case 0:
          DatabaseWorker.getInstance().openDatabase();
          DatabaseWorker.getInstance().readLibraryIntoObjects();
          Interface.getInstance().reloadFileTable();
          Interface.databaseLocation.setText(Configuration.databasePathAndFile);
          break;
        case -1:
          Interface.getInstance().showSelectWorkspaceDialog(frame);
          break;
        case -2:
          Interface.getInstance().showSelectWorkspaceDialog(frame);
          break;
        case -3:
          Interface.getInstance().showSelectScanDirDialog();
          break;
        default:
          break;
      }

      // if (line.contains(Configuration.configurationKeyDatabaseLocation)) {
      // String[] databaseLocationKeyValue = line.split("=");
      // check if configuration.ini contains value for database location
      // if (databaseLocationKeyValue.length > 1) {
      // Configuration.databasePathAndFile = databaseLocationKeyValue[1];

      // } else {
      // Logger.log("configuration.ini does not contain a workspace location... select it!");
      // Interface.getInstance().showSelectWorkspaceDialog(frame);
      // }
      // } else {
      // Logger.log("Configuration.ini does not contain a key for workspace location.\n"
      // + "Add the following line to your configuration.ini: \n"
      // + "'database.location=<location_to_your_workspace>' \n"
      // + "or use Workspace Launcher Dialog");
      // Interface.getInstance().showSelectWorkspaceDialog(frame);
      // }
      // }


    }
    // drop table for clean start (during development)
    // Connection con = DriverManager.getConnection("jdbc:sqlite:" + VLib.databaseFile);
    // if (SqliteConnector.tableExists(con, "vlib")) {
    // SqliteConnector.dropTable(databaseFile);
    // }
  }


  private static int readPropertyFile() throws FileNotFoundException, IOException {
    int errorCode = 0;

    File configFile = new File(Configuration.configurationFileName);
    if (configFile.length() > 0) {
      BufferedReader br = new BufferedReader(new FileReader(configFile));
      String line;
      while ((line = br.readLine()) != null) {
        String[] property = line.split("=");
        if (Configuration.configurationKeyDatabaseLocation.equals(property[0])) {
          Configuration.databasePathAndFile = property[1];
        } else if (Configuration.configurationKeyScanDir.equals(property[0])) {
          Configuration.scanDir = property[1];
        } else {
          Logger.log("unknown property key: " + property[1]);
        }
      }
      if ("".equals(Configuration.databasePathAndFile)) {
        errorCode = -2;
      }
      if ("".equals(Configuration.scanDir)) {
        errorCode = -3;
      }
    } else {
      Logger.log("Configuration.ini does not containt a single line.");
      errorCode = -1;
    }
    return errorCode;
  }

  private void removeDBFile() throws IOException {
    File dbfile = new File(Configuration.defaultDatabaseFile);
    Path dbpath = dbfile.toPath();
    Files.deleteIfExists(dbpath);
  }

  public static String generateFileHash(String name, String extension, String filesize) {
    return name + "." + extension + "-" + filesize;
  }
}
