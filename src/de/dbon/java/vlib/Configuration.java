package de.dbon.java.vlib;

import java.util.HashMap;

public class Configuration {
  public static final String configurationFileName = "mlib.properties";
  public static final String configurationKeyDatabaseLocation = "database.location";
  public static final String defaultDatabaseFile = "mlib.sqlite";
  public static final String configurationKeyScanDir = "scan.dir";
  public static final String sqliteTable = "mlib";
  public static final String allowedExtensions =
      ".mp4,.mpg,.avi,.flv,.wmv,.mov,.mpeg,.mpg,.divx,.mkv";

  public static String vlcLocation = "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe";
  public static String databasePathAndFile = "";
  public static String databasePath = "";
  public static String scanDir = "";
  public static String unsupportedExtensions = "";
  public static String suspiciousFiles = "";

  public static HashMap<String, Integer> ratings = new HashMap<String, Integer>();

  public static void init() {
    ratings.put("AAA", 1);
    ratings.put("AA", 2);
    ratings.put("A", 3);
    ratings.put("B", 4);
    ratings.put("C", 5);
  }
}
