package de.dbon.java.vlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class VLib {

  private final static String databaseFile = "data/vlib.sqlite";
  private final static String rootDir = "N:\\Video";

  /**
   * Main method, not much to say.
   * 
   * @param args command line arguments
   * @throws SQLException sqlexceptions
   */
  public static void main(String[] args) throws IOException, SQLException {

    Logger.log("-------------------------------");
    Logger.log("App started....");

    // drop table for clean start (during development)
    // Connection con = DriverManager.getConnection("jdbc:sqlite:" + VLib.databaseFile);
    // if (SqliteConnector.tableExists(con, "vlib")) {
    // SqliteConnector.dropTable(databaseFile);
    // }

    new SqliteConnector().init(databaseFile);
    new SqliteConnector().openLibrary(databaseFile);
    new FileProcessor(rootDir, databaseFile);

  }


  private void removeDBFile() throws IOException {
    File dbfile = new File(databaseFile);
    Path dbpath = dbfile.toPath();
    Files.deleteIfExists(dbpath);
  }

}
