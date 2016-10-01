package de.dbon.java.vlib;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.dbon.java.vlib.object.MediaFile;

/**
 * creates sqlite database if not exists.
 * 
 * @author Daniel
 *
 */
public class DatabaseWorker {

  private static DatabaseWorker instance = null;
  public static String updateFieldRating = "rating";
  public static String updateFieldToBeDeleted = "toBeDeleted";
  public static String updateFieldReviewed = "reviewed";

  private DatabaseWorker() {
    // prevents this class from beeing instantiated
  }

  public static DatabaseWorker getInstance() {
    if (instance == null) {
      instance = new DatabaseWorker();
    }
    return instance;
  }

  /**
   * connects to sqlite databes.
   * 
   */
  public boolean openDatabase() {
    Connection con = null;
    try {
      Class.forName("org.sqlite.JDBC");
      Logger.log("creating database " + Configuration.databaseDir);
      File dbFile = new File(Configuration.databaseDir);
      Logger.log("open database " + Configuration.databaseDir);
      con = DriverManager.getConnection("jdbc:sqlite:" + Configuration.databaseDir);
    } catch (Exception ex) {
      Logger.log(ex.getClass().getName() + ": " + ex.getMessage());
      ex.printStackTrace();
      System.exit(0);
      return false;
    }
    Logger.log("database successfully opened");
    if (!tableExists(con, Configuration.databaseTableName)) {
      Logger.log("table " + Configuration.databaseTableName + " not existing.. creating it.");
      createTable(con);
      Logger.log("scan directory not found: user has to select it.");
      Interface.getInstance().showSelectScanDirDialog();
    }
    return true;
  }

  public boolean tableExists(Connection con, String table) {
    try {
      Statement stmt = con.createStatement();
      stmt.executeQuery("SELECT * from " + table);
      stmt.close();
      return true;
    } catch (SQLException ex) {
      return false;
    }
  }

  private void createTable(Connection con) {
    try {
      String createTableQuery =
          "CREATE TABLE "
              + Configuration.databaseTableName
              + " (name VARCHAR2(500) NOT NULL,"
              + " extension TEXT NOT NULL, path VARCHAR2(500) NOT NULL, filesize INT, lastviewed INT,"
              + " viewcount INT, tags VARCHAR2(500), hash VARCHAR2(250), toBeDeleted INT, rating INT, reviewed INT)";
      Statement stmt;
      stmt = con.createStatement();
      Logger.log("creating table " + Configuration.databaseTableName);
      Logger.log("sql:" + createTableQuery);
      stmt.executeUpdate(createTableQuery);
      stmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void insertMediaFile(MediaFile mFile, String databaseFile) {
    try {
      String insertQuery =
          "INSERT INTO " + Configuration.databaseTableName + " values (? ,'" + mFile.getExtension()
              + "',? ," + mFile.getFilesize() + ",'" + mFile.getLastviewed() + "',"
              + mFile.getViewcount() + ",'" + mFile.getTags() + "','" + mFile.getHash() + "','"
              + mFile.getRating() + "','" + mFile.getReviewed() + "','" + mFile.getToBeDeleted()
              + "')";
      Connection con;

      con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
      PreparedStatement stmt = con.prepareStatement(insertQuery);
      stmt.setString(1, mFile.getName());
      stmt.setString(2, mFile.getPath());
      stmt.executeUpdate();
      stmt.close();
      con.close();

      // Interface.getInstance().reloadFileTable();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void deleteMediaFile(String fileHash) {
    try {
      String insertQuery = "DELETE FROM " + Configuration.databaseTableName + " WHERE hash=? ";
      Connection con;
      con = DriverManager.getConnection("jdbc:sqlite:" + Configuration.databaseDir);
      PreparedStatement stmt = con.prepareStatement(insertQuery);
      stmt.setString(1, fileHash);
      stmt.executeUpdate();
      stmt.close();
      con.close();
      Interface.getInstance().reloadFileTable();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void dropTable(String databaseFile) {
    try {
      String dropTableQuery = "DROP TABLE " + Configuration.databaseTableName;
      Logger.log("sql:" + dropTableQuery);
      Connection con;
      con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
      PreparedStatement stmt = con.prepareStatement(dropTableQuery);
      stmt.executeUpdate();
      stmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void readLibraryIntoObjects() {
    MediaLibrary.mLibCount = 0;
    try {
      FileProcessor.sqliteFiles = new ArrayList<MediaFile>();
      Connection con;
      con = DriverManager.getConnection("jdbc:sqlite:" + Configuration.databaseDir);
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + Configuration.databaseTableName);
      while (rs.next()) {
        MediaFile vid = new MediaFile();
        vid.setName(rs.getString("name"));
        vid.setExtension(rs.getString("extension"));
        vid.setPath(rs.getString("path"));
        vid.setFilesize(rs.getInt("filesize"));
        vid.setLastviewed(rs.getString("lastviewed"));
        vid.setViewcount(rs.getInt("viewcount"));
        vid.setTags(rs.getString("tags"));
        vid.setHash(rs.getString("hash"));
        vid.setToBeDeleted(rs.getInt("toBeDeleted"));
        vid.setRating(rs.getInt("rating"));
        vid.setReviewed(rs.getInt("reviewed"));

        MediaLibrary.mLibHashes =
            MediaLibrary.mLibHashes
                + MediaLibrary.generateFileHash(rs.getString("name"), rs.getString("extension"),
                    rs.getString("filesize")) + ",";

        FileProcessor.sqliteFiles.add(vid);
        MediaLibrary.mLibCount++;
      }
      rs.close();
      stmt.close();
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateMediaFile(String hash, String updateFieldName, int newValue) {
    try {
      String insertQuery =
          "UPDATE " + Configuration.databaseTableName + " SET " + updateFieldName + "='" + newValue
              + "' WHERE hash=? ";
      Connection con;

      con = DriverManager.getConnection("jdbc:sqlite:" + Configuration.databaseDir);

      PreparedStatement stmt = con.prepareStatement(insertQuery);
      stmt.setString(1, hash);
      stmt.executeUpdate();
      stmt.close();
      con.close();
      // Interface.getInstance().reloadFileTable();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
