package de.dbon.java.vlib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Logger {

  private static final String logFileName = "vlib.log";
  private static File logFile = null;

  private static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");

  /**
   * logs the passed message to the configured log file.
   * 
   * @param msg the message to be logged
   */
  public static void log(String msg) {
    try {
      logFile = new File(logFileName);

      if (!logFile.exists()) {
        logFile.createNewFile();
      }

      PrintWriter out = new PrintWriter(new FileWriter(logFile, true));
      out.println(dateFormat.format(new Date()) + " " + msg);
      out.close();

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }


  }
}
