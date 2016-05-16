package de.dbon.java.vlib;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dbon.java.vlib.object.Video;

/**
 * creates sqlite database if not exists.
 * 
 * @author Daniel
 *
 */
public class SqliteConnector {

  private final static String videoLibraryTable = "vlib";

  /**
   * connects to sqlite databes.
   * 
   */
  public boolean init(String databaseFile) {
    Connection con = null;
    try {

      Class.forName("org.sqlite.JDBC");

      File dbFile = new File(databaseFile);

      if (!dbFile.exists()) {
        Logger.log("database not existing");
      }

      Logger.log("open database " + databaseFile);
      con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

      if (!tableExists(con, videoLibraryTable)) {
        Logger.log("table not existing");
        createTable(con);
      }

    } catch (Exception ex) {
      Logger.log(ex.getClass().getName() + ": " + ex.getMessage());
      ex.printStackTrace();
      System.exit(0);
      return false;
    }
    Logger.log("database successfully opened");
    return true;
  }

  public static boolean tableExists(Connection con, String table) {
    try {
      Statement stmt = con.createStatement();
      stmt.executeQuery("SELECT * from " + table);
      stmt.close();
      return true;
    } catch (SQLException ex) {
      return false;
    }
  }

  private void createTable(Connection con) throws SQLException {
    String createTableQuery =
        "CREATE TABLE "
            + videoLibraryTable
            + " (name VARCHAR2(500) NOT NULL,"
            + " extension TEXT NOT NULL, path VARCHAR2(500) NOT NULL, filesize INT, lastviewed INT,"
            + " viewcount INT, tags VARCHAR2(500))";

    Logger.log("sql:" + createTableQuery);

    Statement stmt = con.createStatement();
    Logger.log("creating table " + videoLibraryTable);
    stmt.executeUpdate(createTableQuery);
    stmt.close();
    con.close();
  }

  public static void insertVideo(Video video, String databaseFile) throws SQLException {
    String insertQuery =
        "INSERT INTO " + videoLibraryTable + " values (? ,'" + video.getExtension() + "',? ,"
            + video.getFilesize() + ",'" + video.getLastviewed() + "'," + video.getViewcount()
            + ",'" + video.getTags() + "')";
    Connection con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
    PreparedStatement stmt = con.prepareStatement(insertQuery);
    stmt.setString(1, video.getName());
    stmt.setString(2, video.getPath());
    stmt.executeUpdate();
    stmt.close();
    con.close();
  }

  public static void dropTable(String databaseFile) throws SQLException {
    String dropTableQuery = "DROP TABLE " + videoLibraryTable;
    Logger.log("sql:" + dropTableQuery);
    Connection con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
    PreparedStatement stmt = con.prepareStatement(dropTableQuery);
    stmt.executeUpdate();
    stmt.close();
    con.close();
  }

  public void openLibrary(String libFile) throws SQLException {
    Connection con = DriverManager.getConnection("jdbc:sqlite:" + libFile);
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM " + videoLibraryTable);
    while (rs.next()) {
      Video vid = new Video();
      vid.setName(rs.getString("name"));
      vid.setExtension(rs.getString("extension"));
      vid.setPath(rs.getString("path"));
      vid.setFilesize(rs.getInt("filesize"));
      vid.setLastviewed(rs.getString("lastviewed"));
      vid.setViewcount(rs.getInt("viewcount"));
      vid.setTags(rs.getString("tags"));

      FileProcessor.videoLibrary.add(vid);
    }
    rs.close();
    stmt.close();
    con.close();
  }
}
