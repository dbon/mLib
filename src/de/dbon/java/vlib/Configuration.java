package de.dbon.java.vlib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

public class Configuration {
  public static final String propertyKeyWorkspaceDir = "databaseDir";
  public static final String propertyKeyScanDir = "scanDir";
  public static final String propertyKeyExtensions = "allowedExtensions";

  public static final String propertiesFileName = "mlib.properties";

  public static final String databaseFileName = "mlib.sqlite";
  public static final String databaseTableName = "mlib";

  public static String databaseDir = "";
  public static String scanDir = "";
  public static String allowedExtensions = "";

  public static String unsupportedExtensions = "";
  public static String suspiciousFiles = "";

  public static String vlcLocation = "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe";

  public static HashMap<String, Integer> ratings = new HashMap<String, Integer>();
  static boolean fileNotFound;

  public static void init() {
    ratings.put("AAA", 1);
    ratings.put("AA", 2);
    ratings.put("A", 3);
    ratings.put("B", 4);
    ratings.put("C", 5);
  }

  /**
   * Reads property file and loads its values to class Configuration. If necessary properties are
   * missing a wizard dialog will be opened to select those values.
   */
  public static void initialize() {
    Properties prop = new Properties();
    InputStream input = null;
    try {
      input = new FileInputStream(Configuration.propertiesFileName);
      prop.load(input);

      Configuration.databaseDir = prop.getProperty(Configuration.propertyKeyWorkspaceDir);
      Configuration.scanDir = prop.getProperty(Configuration.propertyKeyScanDir);
      Configuration.allowedExtensions = prop.getProperty(Configuration.propertyKeyExtensions);

      if (Configuration.databaseDir != null && !"".equals(Configuration.databaseDir)) {
        Interface.databaseDir.setText(Configuration.databaseDir);
        Interface.getInstance().reloadFileTable();
        checkForScanDir();
      } else {
        Interface.getInstance().showSelectWorkspaceDialog();
      }

    } catch (FileNotFoundException e) {
      Logger.log("configuration file " + Configuration.propertiesFileName
          + " not found: User has to select workspace path");
      Configuration.fileNotFound = true;
      Interface.getInstance().showSelectWorkspaceDialog();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void checkForScanDir() {
    if (Configuration.scanDir != null && !"".equals(Configuration.scanDir)) {
      Interface.scanDir.setText(Configuration.scanDir);
      checkForAllowedExtensions();
    } else {
      Interface.getInstance().showSelectScanDirDialog();
    }
  }

  public static void checkForAllowedExtensions() {
    Logger.log("selected extensions=" + Configuration.allowedExtensions);
    if (Configuration.allowedExtensions != null && !"".equals(Configuration.allowedExtensions)) {
      // Interface.fileExtensionList.setText(Configuration.allowedExtensions);
    } else {
      allowedExtensions = "mp4,mkv,mov,mpg,wmv,flv,avi";
      Interface.getInstance().showFileExtensionDialog();
    }
  }


  public static void setConfigurationProperty(String key, String value) {
    Properties prop = new Properties();
    OutputStream output = null;
    try {
      output = new FileOutputStream(Configuration.propertiesFileName, true);

      // String newVal = value.replace("\\", "/");

      prop.setProperty(key, value);
      prop.store(output, null);

      Field fld = Configuration.class.getDeclaredField(key);
      fld.set(null, value);

    } catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
