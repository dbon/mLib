package de.dbon.java.vlib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import de.dbon.java.vlib.object.MediaFile;

public class Interface implements ActionListener, MouseListener {

  private static Interface instance = null;

  private JFrame frame;

  private final String appTitle = "File Analyzer";
  private final String appVersion = "v0.1 alpha";
  public JDialog selectDBDialog;
  public JDialog selectScanDirDialog;
  public JTextField workspacePath;
  public JTextField scandirPath;
  public static JTextField databaseLocation;
  public static A_DefaultTableModel tableModel;

  public static JTextArea log = new JTextArea(10, 100);
  public static JTable fileTable;

  public static JButton ratingAAAButton;
  public static JButton ratingAAButton;
  public static JButton ratingAButton;
  public static JButton ratingBButton;
  public static JButton ratingCButton;

  public static LinkedHashMap<String, Integer> columnNames = new LinkedHashMap<String, Integer>();

  public static final String COLUMN_NAME_NUMBER = "No";
  public static final String COLUMN_NAME_FILENAME = "Filename";
  public static final String COLUMN_NAME_FILEEXTENSION = "Extension";
  public static final String COLUMN_NAME_FILESIZE = "Filesize";
  public static final String COLUMN_NAME_FILEPATH = "Filepath";
  public static final String COLUMN_NAME_FILERATING = "Rating";
  public static final String COLUMN_NAME_REVIEWED = "Reviewed";
  public static final String COLUMN_NAME_TOBEDELTED = "to be deleted";

  public static final String BUTTON_ACTION_COMMAND_WORKSPACE_BROWSE = "browse workspace";
  public static final String BUTTON_ACTION_COMMAND_WORKSPACE_OK = "save workspace";

  public static final String BUTTON_ACTION_COMMAND_SCANDIR_BROWSE = "browse scan dir";
  public static final String BUTTON_ACTION_COMMAND_SCANDIR_OK = "save scan dir";

  public static final String BUTTON_ACTION_COMMAND_SYNCHRONIZATION = "sync library";
  public static final String BUTTON_ACTION_COMMAND_REFRESH_TABLE = "refresh table";
  public static final String BUTTON_ACTION_COMMAND_DELETE_FILE = "delete file";
  public static final String BUTTON_ACTION_COMMAND_OPEN_IN_EXPLORER = "open in explorer";

  public static final String BUTTON_ACTION_COMMAND_RATING = "rating";
  public static final String BUTTON_ACTION_COMMAND_SET_TOBEDELETED_ = "set to be delted";
  public static final String BUTTON_ACTION_COMMAND_SET_REVIEWD = "set reviewed";
  private static final String BUTTON_ACTION_COMMAND_RESET_ROW = "reset row";

  boolean isRowSelected = false;

  private Interface() {
    // prevents this class from being instantiated
  }

  public static Interface getInstance() {
    if (instance == null) {
      instance = new Interface();
    }
    return instance;
  }

  public JFrame run() {
    initTableColumnNames();
    frame = new JFrame();
    frame.setSize(1650, 800);
    frame.setTitle(appTitle + " " + appVersion);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // NORTH
    JPanel main = new JPanel();
    main.add(new JLabel("Database Location: "));
    databaseLocation = new JTextField(20);
    databaseLocation.setEditable(false);
    main.add(databaseLocation);

    // CENTER
    tableModel = new A_DefaultTableModel(columnNames.keySet().toArray(), 0);
    fileTable = new JTable(tableModel);
    fileTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
    fileTable.setFillsViewportHeight(true);
    fileTable.setAutoCreateRowSorter(true);
    fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    fileTable.addMouseListener(this);
    setColumnSizes();
    setColumnsAlignLeft();

    // EAST
    JPanel optionPanel = new JPanel();
    optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
    JButton syncButton = new JButton("Synchronize");
    syncButton.setActionCommand(BUTTON_ACTION_COMMAND_SYNCHRONIZATION);
    syncButton.addActionListener(this);
    optionPanel.add(syncButton);

    JButton refreshTableButton = new JButton("RefreshTable");
    refreshTableButton.setActionCommand(BUTTON_ACTION_COMMAND_REFRESH_TABLE);
    refreshTableButton.addActionListener(this);
    optionPanel.add(refreshTableButton);

    JButton openInExplorerTableButton = new JButton("Open in Explorer");
    openInExplorerTableButton.setActionCommand(BUTTON_ACTION_COMMAND_OPEN_IN_EXPLORER);
    openInExplorerTableButton.addActionListener(this);
    optionPanel.add(openInExplorerTableButton);

    JButton deleteButton = new JButton("Delete File");
    deleteButton.setActionCommand(BUTTON_ACTION_COMMAND_DELETE_FILE);
    deleteButton.addActionListener(this);
    optionPanel.add(deleteButton);

    ratingAAAButton = new JButton("AAA");
    ratingAAButton = new JButton("AA");
    ratingAButton = new JButton("A");
    ratingBButton = new JButton("B");
    ratingCButton = new JButton("C");

    ratingAAAButton.addActionListener(this);
    ratingAAAButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING);

    ratingAAButton.addActionListener(this);
    ratingAAButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING);

    ratingAButton.addActionListener(this);
    ratingAButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING);

    ratingBButton.addActionListener(this);
    ratingBButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING);

    ratingCButton.addActionListener(this);
    ratingCButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING);

    optionPanel.add(ratingAAAButton);
    optionPanel.add(ratingAAButton);
    optionPanel.add(ratingAButton);
    optionPanel.add(ratingBButton);
    optionPanel.add(ratingCButton);

    JButton toBeDeletedButton = new JButton("Set toBeDeleted");
    toBeDeletedButton.addActionListener(this);
    toBeDeletedButton.setActionCommand(BUTTON_ACTION_COMMAND_SET_TOBEDELETED_);
    optionPanel.add(toBeDeletedButton);

    JButton setReviewedButton = new JButton("Set reviewed");
    setReviewedButton.addActionListener(this);
    setReviewedButton.setActionCommand(BUTTON_ACTION_COMMAND_SET_REVIEWD);
    optionPanel.add(setReviewedButton);

    JButton resetRowButton = new JButton("Reset Row");
    resetRowButton.addActionListener(this);
    resetRowButton.setActionCommand(BUTTON_ACTION_COMMAND_RESET_ROW);
    optionPanel.add(resetRowButton);

    // SOUTH
    log.setEditable(false);
    log.setLineWrap(false);
    log.setAutoscrolls(true);
    JScrollPane scrollPane = new JScrollPane(log);
    scrollPane.setAutoscrolls(true);
    Rectangle rectangle = new Rectangle();
    scrollPane.scrollRectToVisible(rectangle);

    frame.add(main, BorderLayout.NORTH);
    frame.add(new JScrollPane(fileTable), BorderLayout.CENTER);
    frame.add(optionPanel, BorderLayout.EAST);
    frame.add(scrollPane, BorderLayout.SOUTH);
    frame.setVisible(true);
    return frame;
  }

  private void setColumnsAlignLeft() {
    DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
    leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_NUMBER))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILENAME))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILEEXTENSION))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILESIZE))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILEPATH))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILERATING))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_REVIEWED))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_TOBEDELTED))
        .setCellRenderer(leftRenderer);
  }

  private void setColumnSizes() {
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_NUMBER)).setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILENAME))
        .setPreferredWidth(400);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILEEXTENSION))
        .setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILESIZE))
        .setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILEPATH))
        .setPreferredWidth(800);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILERATING))
        .setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_REVIEWED))
        .setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_TOBEDELTED))
        .setPreferredWidth(50);
  }

  private void initTableColumnNames() {
    columnNames.put(Interface.COLUMN_NAME_NUMBER, 0);
    columnNames.put(Interface.COLUMN_NAME_FILENAME, 1);
    columnNames.put(Interface.COLUMN_NAME_FILEEXTENSION, 2);
    columnNames.put(Interface.COLUMN_NAME_FILESIZE, 3);
    columnNames.put(Interface.COLUMN_NAME_FILEPATH, 4);
    columnNames.put(Interface.COLUMN_NAME_FILERATING, 5);
    columnNames.put(Interface.COLUMN_NAME_REVIEWED, 6);
    columnNames.put(Interface.COLUMN_NAME_TOBEDELTED, 7);
  }

  public void showSelectWorkspaceDialog(JFrame frame) {
    selectDBDialog = new JDialog();
    selectDBDialog.setSize(600, 200);
    selectDBDialog.setLocationRelativeTo(frame);
    selectDBDialog.setTitle("Workspace Launcher");

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(0, 10, 0, 0);
    panel.add(new JLabel("Select a workspace"), c);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(10, 10, 0, 0);

    JTextArea textArea =
        new JTextArea(
            "Select a location where the database file will be stored.\nAll important configurations are saved inside this file.");
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setSize(600, 200);
    Color color = new Color(UIManager.getColor("control").getRGB());
    textArea.setBackground(color);
    panel.add(textArea, c);

    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.weighty = 0;
    c.weightx = 1;
    c.ipadx = 300;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(15, 10, 0, 0);
    // c.anchor = GridBagConstraints.CENTER;
    workspacePath = new JTextField();
    panel.add(workspacePath, c);

    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 1;
    c.weighty = 0;
    c.ipadx = 0;
    c.weightx = 0.1;
    c.fill = GridBagConstraints.VERTICAL;
    // c.anchor = GridBagConstraints.CENTER;
    JButton browseButton = new JButton("Browse...");
    browseButton.setActionCommand(BUTTON_ACTION_COMMAND_WORKSPACE_BROWSE);
    browseButton.addActionListener(this);
    panel.add(browseButton, c);

    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 2;
    c.ipadx = 50;
    c.insets = new Insets(15, 0, 15, 17);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    JButton okButton = new JButton("OK");
    okButton.setActionCommand(BUTTON_ACTION_COMMAND_WORKSPACE_OK);
    okButton.addActionListener(this);
    panel.add(okButton, c);

    selectDBDialog.add(panel);
    selectDBDialog.setVisible(true);
  }

  public void showSelectScanDirDialog() {
    selectScanDirDialog = new JDialog();
    selectScanDirDialog.setSize(600, 200);
    selectScanDirDialog.setLocationRelativeTo(Interface.getInstance().frame);
    selectScanDirDialog.setTitle("Observation Directory");

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(0, 10, 0, 0);
    panel.add(new JLabel("Select the directory which has to be observed"), c);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(10, 10, 0, 0);

    JTextArea textArea =
        new JTextArea(
            "Select a location where the App should search for files matching the allowed extension patterns. ");
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setSize(600, 200);
    Color color = new Color(UIManager.getColor("control").getRGB());
    textArea.setBackground(color);
    panel.add(textArea, c);

    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.weighty = 0;
    c.weightx = 1;
    c.ipadx = 300;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(15, 10, 0, 0);
    // c.anchor = GridBagConstraints.CENTER;
    scandirPath = new JTextField();
    panel.add(scandirPath, c);

    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 1;
    c.weighty = 0;
    c.ipadx = 0;
    c.weightx = 0.1;
    c.fill = GridBagConstraints.VERTICAL;
    // c.anchor = GridBagConstraints.CENTER;
    JButton browseButton = new JButton("Browse...");
    browseButton.setActionCommand(BUTTON_ACTION_COMMAND_SCANDIR_BROWSE);
    browseButton.addActionListener(this);
    panel.add(browseButton, c);

    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 2;
    c.ipadx = 50;
    c.insets = new Insets(15, 0, 15, 17);
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    JButton okButton = new JButton("OK");
    okButton.setActionCommand(BUTTON_ACTION_COMMAND_SCANDIR_OK);
    okButton.addActionListener(this);
    panel.add(okButton, c);

    selectScanDirDialog.add(panel);
    selectScanDirDialog.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent event) {

    int rowNumber = 0;
    String fileName = null;
    String fileExtension = null;
    String fileSize = null;
    String filePath = null;
    int fileRating = 0;
    int fileReviewd = 0;
    int fileToBeDeleted = 0;

    int selectedRow = 0;

    if (fileTable.getSelectedRow() != -1) {
      isRowSelected = true;
    }

    // do not execute when table does not exist (on select workspace)
    if (isRowSelected && fileTable.getRowCount() > 0) {
      rowNumber = getNumberOfSelectedRow();
      fileName = getFilenameOfSelectedRow();
      fileExtension = getFileExtensionOfSelectedRow();
      fileSize = getFilesizeOfSelectedRow();
      filePath = getFilepathOfSelectedRow();
      fileRating = getRatingOfSelectedRow();
      fileReviewd = getReviewedOfSelectedRow();
      fileToBeDeleted = getToBeDeletedOfSelectedRow();

      selectedRow = fileTable.getSelectedRow();
    }

    JFileChooser fc = null;
    int returnVal = -1;

    switch (event.getActionCommand()) {
      case BUTTON_ACTION_COMMAND_WORKSPACE_BROWSE:
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File selectedDir = fc.getSelectedFile();
          workspacePath.setText(selectedDir.getAbsolutePath());
        }
        break;
      case BUTTON_ACTION_COMMAND_WORKSPACE_OK:
        try {
          Configuration.databasePath = workspacePath.getText();
          Configuration.databasePathAndFile =
              Configuration.databasePath + "\\" + Configuration.defaultDatabaseFile;
          selectDBDialog.dispose();
          databaseLocation.setText(Configuration.databasePath);

          File configFile = new File(Configuration.configurationFileName);
          configFile.createNewFile();

          FileWriter writer;
          writer = new FileWriter(configFile);

          writer.write(Configuration.configurationKeyDatabaseLocation + "="
              + Configuration.databasePathAndFile + "\n");
          writer.close();

          DatabaseWorker.getInstance().openDatabase();
          DatabaseWorker.getInstance().readLibraryIntoObjects();
          Interface.getInstance().reloadFileTable();
        } catch (IOException e2) {
          e2.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_SCANDIR_BROWSE:

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File selectedDir = fc.getSelectedFile();
          Configuration.scanDir = selectedDir.getAbsolutePath();
          scandirPath.setText(Configuration.scanDir);
        }
        break;
      case BUTTON_ACTION_COMMAND_SCANDIR_OK:
        try {
          selectScanDirDialog.dispose();

          File configFile = new File(Configuration.configurationFileName);
          FileWriter writer = new FileWriter(configFile, true);
          writer.append(Configuration.configurationKeyScanDir + "=" + Configuration.scanDir);
          writer.close();

          DatabaseWorker.getInstance().readLibraryIntoObjects();
          Interface.getInstance().reloadFileTable();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_SYNCHRONIZATION:
        if (DatabaseWorker.getInstance().openDatabase()) {
          Thread thread = null;
          try {
            thread = new Thread(new FileProcessor(Configuration.scanDir));
          } catch (NoSuchAlgorithmException | IOException | SQLException e1) {
            e1.printStackTrace();
          }
          thread.start();
        }
        break;
      case BUTTON_ACTION_COMMAND_DELETE_FILE:
        try {
          if (isRowSelected) {
            Runtime.getRuntime().exec("taskkill /F /IM vlc.exe");

            Thread.sleep(500);
            File selectedFileToBeDeleted = new File(filePath);
            if (selectedFileToBeDeleted.exists()) {
              Path path = Paths.get(selectedFileToBeDeleted.toURI());
              Files.delete(path);

              DatabaseWorker.getInstance().deleteMediaFile(
                  MediaLibrary.generateFileHash(fileName, fileExtension, fileSize));
              Logger.log("delete media file " + filePath);

            } else {
              Logger.log("Datei nicht gefunden: " + filePath);
            }
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_REFRESH_TABLE:
        Interface.getInstance().reloadFileTable();
        break;
      case BUTTON_ACTION_COMMAND_OPEN_IN_EXPLORER:
        try {
          if (isRowSelected) {
            Runtime.getRuntime().exec("explorer.exe /select," + filePath);
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_RATING:
        try {
          if (isRowSelected) {
            Runtime.getRuntime().exec("taskkill /F /IM vlc.exe");
            Thread.sleep(500);

            int ratingNumber = Configuration.ratings.get(((JButton) event.getSource()).getText());
            int nextRow = 0;

            int rating = 0;
            int countToNextRow = 1;
            do {
              rating = (int) fileTable.getValueAt(fileTable.getSelectedRow() + countToNextRow, 5);
              if (rating == 0) {
                nextRow = selectedRow + countToNextRow;
              } else {
                nextRow = selectedRow;
              }
              countToNextRow++;
            } while (rating != 0);

            DatabaseWorker.getInstance().updateMediaFile(
                MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
                DatabaseWorker.updateFieldRating, ratingNumber);

            DatabaseWorker.getInstance().updateMediaFile(
                MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
                DatabaseWorker.updateFieldReviewed, 1);

            fileTable.setRowSelectionInterval(nextRow, nextRow);

            Logger.log("opening file " + filePath);
            ProcessBuilder pb = new ProcessBuilder(Configuration.vlcLocation, filePath);
            pb.start();
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_SET_TOBEDELETED_:
        if (isRowSelected) {
          if (fileToBeDeleted == 0) {
            fileToBeDeleted = 1;
          } else {
            fileToBeDeleted = 0;
          }

          DatabaseWorker.getInstance().updateMediaFile(
              MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
              DatabaseWorker.updateFieldToBeDeleted, fileToBeDeleted);
          DatabaseWorker.getInstance().updateMediaFile(
              MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
              DatabaseWorker.updateFieldReviewed, 1);
        }
        break;
      case BUTTON_ACTION_COMMAND_SET_REVIEWD:
        if (isRowSelected) {

          if (fileReviewd == 0) {
            fileReviewd = 1;
          } else {
            fileReviewd = 0;
          }
          DatabaseWorker.getInstance().updateMediaFile(
              MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
              DatabaseWorker.updateFieldReviewed, fileReviewd);
        }
        break;
      case BUTTON_ACTION_COMMAND_RESET_ROW:
        if (isRowSelected) {
          DatabaseWorker.getInstance().updateMediaFile(
              MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
              DatabaseWorker.updateFieldRating, 0);
          DatabaseWorker.getInstance().updateMediaFile(
              MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
              DatabaseWorker.updateFieldToBeDeleted, 0);
          DatabaseWorker.getInstance().updateMediaFile(
              MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
              DatabaseWorker.updateFieldReviewed, 0);

          fileTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
        }
        break;
      default:
        break;
    }
  }

  private int getNumberOfSelectedRow() {
    return (int) fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_NUMBER));
  }

  private String getFilenameOfSelectedRow() {
    return fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_FILENAME)).toString();
  }

  private String getFileExtensionOfSelectedRow() {
    return fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_FILEEXTENSION)).toString();
  }

  private String getFilesizeOfSelectedRow() {
    return fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_FILESIZE)).toString();
  }

  private String getFilepathOfSelectedRow() {
    return fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_FILEPATH)).toString();
  }

  private int getToBeDeletedOfSelectedRow() {
    return (int) fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_TOBEDELTED));
  }

  private int getRatingOfSelectedRow() {
    return (int) fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_FILERATING));
  }

  private int getReviewedOfSelectedRow() {
    return (int) fileTable.getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_REVIEWED));
  }

  public static void refreshLog() throws IOException {
    File logFile = new File(Logger.logFileName);
    BufferedReader br = new BufferedReader(new FileReader(logFile));
    String line;
    String output = "";
    while ((line = br.readLine()) != null) {
      // output = output + "\n" + line;
      Interface.log.append(line + "\n");
    }
    Interface.log.setCaretPosition(Interface.log.getDocument().getLength());
  }

  public void reloadFileTable() {

    for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
      tableModel.removeRow(i);
    }
    DatabaseWorker.getInstance().readLibraryIntoObjects();

    int count = 1;
    for (MediaFile v : FileProcessor.sqliteFiles) {
      Object[] o =
          {count, v.getName(), v.getExtension(), v.getFilesize(), v.getPath(), v.getRating(),
              v.getReviewed(), v.getToBeDeleted()};
      tableModel.addRow(o);
      count++;
    }

  }

  @Override
  public void mouseClicked(MouseEvent e) {

    // double click
    if (e.getClickCount() == 2) {

      // try {
      // Runtime.getRuntime().exec("taskkill /F /IM vlc.exe");
      // Thread.sleep(50);
      // } catch (IOException e2) {
      // e2.printStackTrace();
      // } catch (InterruptedException e1) {
      // e1.printStackTrace();
      // }

      String filePath = fileTable.getValueAt(fileTable.getSelectedRow(), 4).toString();
      Logger.log("opening file " + filePath);

      // use -f as second parameter for fullscreen
      ProcessBuilder pb = new ProcessBuilder(Configuration.vlcLocation, filePath);
      try {
        pb.start();
      } catch (IOException e1) {
        e1.printStackTrace();
      }


    }
  }

  @Override
  public void mousePressed(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}


}
