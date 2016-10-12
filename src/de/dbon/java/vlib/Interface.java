package de.dbon.java.vlib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.dbon.java.vlib.object.MediaFile;

public class Interface implements ActionListener, MouseListener, DocumentListener {

  private static Interface instance = null;

  private JFrame frame = new JFrame();

  private final String appTitle = "File Analyzer";
  private final String appVersion = "v0.1 alpha";
  public JDialog selectDatabaseDialog;
  public JDialog selectScanDirDialog;
  public JDialog selectFileExtenstionDialog;
  public JDialog selectMaintainScanDirDialog;
  public JTextField workspacePath;
  public JTextField scandirPath = new JTextField();
  public static JTextField fileExtensionList;
  public static JTextField databaseDir;
  public static JTextField scanDir;
  public static MLibTableModel tableModel;
  public static JTextField searchBar;
  public static JLabel filterCount;

  public static JTextArea log = new JTextArea(10, 100);
  public static JTable fileTable;

  public static JButton ratingAButton;
  public static JButton ratingBButton;
  public static JButton ratingCButton;
  public static JButton ratingDButton;
  public static JButton ratingEButton;
  public static JButton ratingFButton;

  public static LinkedHashMap<String, Integer> columnNames = new LinkedHashMap<String, Integer>();

  public static final String COLUMN_NAME_NUMBER = "No";
  public static final String COLUMN_NAME_FILENAME = "Filename";
  public static final String COLUMN_NAME_FILEEXTENSION = "Extension";
  public static final String COLUMN_NAME_FILESIZE = "Filesize";
  public static final String COLUMN_NAME_FILEPATH = "Filepath";
  public static final String COLUMN_NAME_FILERATING = "Rating";
  public static final String COLUMN_NAME_REVIEWED = "Reviewed";
  public static final String COLUMN_NAME_TOBEDELTED = "to be deleted";
  public static final String COLUMN_NAME_FILERATINGINT = "RatingINT";

  public static final String BUTTON_ACTION_COMMAND_MENUITEM = "menuitem";
  public static final String MENU_ITEM_QUIT = "Beenden";
  public static final String MENU_ITEM_FILE_EXTENSION = "Dateiendungen anpassen";
  public static final String MENU_ITEM_SCAN_DIRECTORIES = "Scan Directories";

  public static final String BUTTON_ACTION_COMMAND_WORKSPACE_BROWSE = "browse workspace";
  public static final String BUTTON_ACTION_COMMAND_WORKSPACE_OK = "save workspace";

  public static final String BUTTON_ACTION_COMMAND_SCANDIR_BROWSE = "browse scan dir";
  public static final String BUTTON_ACTION_COMMAND_SCANDIR_OK = "save scan dir";
//  public static final String BUTTON_ACTION_COMMAND_SCANDIR_CHANGE = "change scan dir";
  public static final String BUTTON_ACTION_COMMAND_SCANDIR_MAINTAIN = "maintain scan dir";

  public static final String BUTTON_ACTION_COMMAND_SYNCHRONIZATION = "sync library";
  public static final String BUTTON_ACTION_COMMAND_REFRESH_TABLE = "refresh table";
  public static final String BUTTON_ACTION_COMMAND_DELETE_FILE = "delete file";
  public static final String BUTTON_ACTION_COMMAND_OPEN_IN_EXPLORER = "open in explorer";

  public static final String BUTTON_ACTION_COMMAND_RATING_A = "ratingA";
  public static final String BUTTON_ACTION_COMMAND_RATING_B = "ratingB";
  public static final String BUTTON_ACTION_COMMAND_RATING_C = "ratingC";
  public static final String BUTTON_ACTION_COMMAND_RATING_D = "ratingD";
  public static final String BUTTON_ACTION_COMMAND_RATING_E = "ratingE";
  public static final String BUTTON_ACTION_COMMAND_RATING_F = "ratingF";

  public static final String BUTTON_ACTION_COMMAND_SET_TOBEDELETED_ = "set to be delted";
  public static final String BUTTON_ACTION_COMMAND_SET_REVIEWD = "set reviewed";
  private static final String BUTTON_ACTION_COMMAND_RESET_ROW = "reset row";

  public static final String BUTTON_ACTION_COMMAND_FILEEXTENSIONS_OK = "save file extensions";

  boolean isRowSelected = false;

  JMenuBar menuBar;
  JMenu menu, submenu;
  JMenuItem menuItem;

  public static ArrayList<String> iconNames = new ArrayList<String>();
  HashMap<Integer, ImageIcon> icons = new HashMap<Integer, ImageIcon>();

  private Interface() {
    // prevents this class from being instantiated
  }

  public static Interface getInstance() {
    if (instance == null) {
      instance = new Interface();
    }
    return instance;
  }

  public JFrame run() throws IOException {
    initTableColumnNames();
    loadAndScaleImages();
    frame = new JFrame();
    // frame.setSize(1650, 800);
    frame.setTitle(appTitle + " " + appVersion);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setJMenuBar(createMenu());

    // NORTH
    JPanel north = new JPanel();
    north.setLayout(new GridBagLayout());

    JPanel databaseDirPanel = new JPanel();
    databaseDirPanel.add(new JLabel("Database Location: "));

    databaseDir = new JTextField(30);
    databaseDir.setEditable(false);
    databaseDirPanel.add(databaseDir);

    GridBagConstraints cNorth = new GridBagConstraints();
    cNorth.anchor = GridBagConstraints.FIRST_LINE_START;
    cNorth.gridx = 0;
    cNorth.gridy = 0;
    cNorth.weightx = 1;
    cNorth.gridwidth = 1;
    north.add(databaseDirPanel, cNorth);

//    JPanel scanDirPanel = new JPanel();
//    // scanDirPanel.setBackground(Color.green);
//    scanDirPanel.add(new JLabel("Scan Directory: "));
//
//    scanDir = new JTextField(30);
//    scanDir.setEditable(false);
//    scanDirPanel.add(scanDir);
//
//    cNorth.gridx = 1;
//    north.add(scanDirPanel, cNorth);
//
//    cNorth.gridx = 2;
//    cNorth.insets = new Insets(2, -70, 0, 0);
//    JButton buttonBrowseScanDir = new JButton("Change");
//    buttonBrowseScanDir.addActionListener(this);
//    buttonBrowseScanDir.setActionCommand(BUTTON_ACTION_COMMAND_SCANDIR_CHANGE);
//    north.add(buttonBrowseScanDir, cNorth);


    cNorth.gridx = 0;
    cNorth.gridy = 1;
    cNorth.gridwidth = 2;
    cNorth.insets = new Insets(0, 0, 0, 0);
    JPanel searchBarPanel = new JPanel();

    searchBarPanel.add(new JLabel("Filter: "));
    searchBar = new JTextField(85);
    searchBar.setEditable(true);
    searchBar.getDocument().addDocumentListener(this);
    searchBarPanel.add(searchBar);

    filterCount = new JLabel("Rows: ");
    searchBarPanel.add(filterCount);
    north.add(searchBarPanel, cNorth);

    // CENTER
    // create table model with columns and 0 rows
    tableModel = new MLibTableModel(columnNames.keySet().toArray(), 0);
    fileTable = new JTable(tableModel);
    // remove rating int column
    fileTable.removeColumn(fileTable.getColumnModel().getColumn(
        columnNames.get(COLUMN_NAME_FILERATINGINT)));

    fileTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
    fileTable.setFillsViewportHeight(true);
    fileTable.setAutoCreateRowSorter(true);
    fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    fileTable.addMouseListener(this);
    fileTable.setAutoscrolls(false);
    setColumnSizes();
    setColumnsHeaderAlignLeft();
    setColumnsAlignLeft();

    // EAST
    JPanel east = new JPanel();
    east.setLayout(new GridBagLayout());

    GridBagConstraints cEast = new GridBagConstraints();
    cEast.fill = GridBagConstraints.HORIZONTAL;
    cEast.gridx = 0;
    cEast.gridy = 0;
    cEast.weightx = 0;
    cEast.gridwidth = 0;

    // JPanel optionPanel = new JPanel();
    JButton syncButton = new JButton("Synchronize");
    syncButton.setActionCommand(BUTTON_ACTION_COMMAND_SYNCHRONIZATION);
    syncButton.addActionListener(this);
    east.add(syncButton, cEast);

    cEast.gridy = 1;
    JButton refreshTableButton = new JButton("RefreshTable");
    refreshTableButton.setActionCommand(BUTTON_ACTION_COMMAND_REFRESH_TABLE);
    refreshTableButton.addActionListener(this);
    east.add(refreshTableButton, cEast);

    cEast.gridy = 2;
    JButton openInExplorerTableButton = new JButton("Open in Explorer");
    openInExplorerTableButton.setActionCommand(BUTTON_ACTION_COMMAND_OPEN_IN_EXPLORER);
    openInExplorerTableButton.addActionListener(this);
    east.add(openInExplorerTableButton, cEast);

    cEast.gridy = 3;
    JButton deleteButton = new JButton("Delete File");
    deleteButton.setActionCommand(BUTTON_ACTION_COMMAND_DELETE_FILE);
    deleteButton.addActionListener(this);
    east.add(deleteButton, cEast);

    cEast.gridy = 4;
    east.add(new JLabel("Rating:"), cEast);

    cEast.gridy = 5;
    ratingAButton = new JButton();
    ratingAButton.addActionListener(this);
    ratingAButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING_A);
    ratingAButton.setIcon(icons.get(6));
    east.add(ratingAButton, cEast);

    cEast.gridy = 6;
    ratingBButton = new JButton();
    ratingBButton.addActionListener(this);
    ratingBButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING_B);
    ratingBButton.setIcon(icons.get(5));
    east.add(ratingBButton, cEast);

    cEast.gridy = 7;
    ratingCButton = new JButton();
    ratingCButton.addActionListener(this);
    ratingCButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING_C);
    ratingCButton.setIcon(icons.get(4));
    east.add(ratingCButton, cEast);

    cEast.gridy = 8;
    ratingDButton = new JButton();
    ratingDButton.addActionListener(this);
    ratingDButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING_D);
    ratingDButton.setIcon(icons.get(3));
    east.add(ratingDButton, cEast);

    cEast.gridy = 9;
    ratingEButton = new JButton();
    ratingEButton.addActionListener(this);
    ratingEButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING_E);
    ratingEButton.setIcon(icons.get(2));
    east.add(ratingEButton, cEast);

    cEast.gridy = 10;
    ratingFButton = new JButton();
    ratingFButton.addActionListener(this);
    ratingFButton.setActionCommand(BUTTON_ACTION_COMMAND_RATING_F);
    ratingFButton.setIcon(icons.get(1));
    east.add(ratingFButton, cEast);

    cEast.gridy = 11;
    JButton toBeDeletedButton = new JButton("Set toBeDeleted");
    toBeDeletedButton.addActionListener(this);
    toBeDeletedButton.setActionCommand(BUTTON_ACTION_COMMAND_SET_TOBEDELETED_);
    east.add(toBeDeletedButton, cEast);

    cEast.gridy = 12;
    JButton setReviewedButton = new JButton("Set reviewed");
    setReviewedButton.addActionListener(this);
    setReviewedButton.setActionCommand(BUTTON_ACTION_COMMAND_SET_REVIEWD);
    east.add(setReviewedButton, cEast);

    cEast.gridy = 13;
    JButton resetRowButton = new JButton("Reset Row");
    resetRowButton.addActionListener(this);
    resetRowButton.setActionCommand(BUTTON_ACTION_COMMAND_RESET_ROW);
    east.add(resetRowButton, cEast);

    // SOUTH
    log.setEditable(false);
    log.setLineWrap(false);
    log.setAutoscrolls(true);
    JScrollPane scrollPane = new JScrollPane(log);
    scrollPane.setAutoscrolls(true);
    Rectangle rectangle = new Rectangle();
    scrollPane.scrollRectToVisible(rectangle);

    frame.add(north, BorderLayout.NORTH);
    frame.add(new JScrollPane(fileTable), BorderLayout.CENTER);
    frame.add(east, BorderLayout.EAST);
    frame.add(scrollPane, BorderLayout.SOUTH);
    frame.setVisible(true);
    frame.pack();


    return frame;
  }



  private void setColumnsHeaderAlignLeft() {
    DefaultTableCellRenderer renderer =
        (DefaultTableCellRenderer) fileTable.getTableHeader().getDefaultRenderer();
    renderer.setHorizontalAlignment(SwingConstants.LEFT);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_NUMBER))
        .setHeaderRenderer(renderer);
  }

  private JMenuBar createMenu() {
    menuBar = new JMenuBar();
    menu = new JMenu("Einstellungen");
    menu.setMnemonic(KeyEvent.VK_E);

    menuItem = new JMenuItem(MENU_ITEM_FILE_EXTENSION, KeyEvent.VK_D);
    menu.add(menuItem);
    menuItem.addActionListener(this);
    menuItem.setActionCommand(BUTTON_ACTION_COMMAND_MENUITEM);
    
    menuItem = new JMenuItem(MENU_ITEM_SCAN_DIRECTORIES, KeyEvent.VK_S);
    menu.add(menuItem);
    menuItem.addActionListener(this);
    menuItem.setActionCommand(BUTTON_ACTION_COMMAND_MENUITEM);

    menu.addSeparator();
    menuItem = new JMenuItem(MENU_ITEM_QUIT, KeyEvent.VK_Q);
    menuItem.setMnemonic(KeyEvent.VK_B);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    menu.add(menuItem);

    menuItem.addActionListener(this);
    menuItem.setActionCommand(BUTTON_ACTION_COMMAND_MENUITEM);

    menuBar.add(menu);
    return menuBar;
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
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_REVIEWED))
        .setCellRenderer(leftRenderer);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_TOBEDELTED))
        .setCellRenderer(leftRenderer);
  }


  private void setColumnSizes() {
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_NUMBER)).setPreferredWidth(35);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILENAME))
        .setPreferredWidth(400);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILEEXTENSION))
        .setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILESIZE))
        .setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILEPATH))
        .setPreferredWidth(800);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_FILERATING))
        .setPreferredWidth(60);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_REVIEWED))
        .setPreferredWidth(50);
    fileTable.getColumnModel().getColumn(columnNames.get(COLUMN_NAME_TOBEDELTED))
        .setPreferredWidth(50);
  }

  private void initTableColumnNames() {
    columnNames.put(Interface.COLUMN_NAME_NUMBER, 0);
    columnNames.put(Interface.COLUMN_NAME_FILERATING, 1);
    columnNames.put(Interface.COLUMN_NAME_REVIEWED, 2);
    columnNames.put(Interface.COLUMN_NAME_TOBEDELTED, 3);
    columnNames.put(Interface.COLUMN_NAME_FILENAME, 4);
    columnNames.put(Interface.COLUMN_NAME_FILEEXTENSION, 5);
    columnNames.put(Interface.COLUMN_NAME_FILESIZE, 6);
    columnNames.put(Interface.COLUMN_NAME_FILEPATH, 7);
    columnNames.put(Interface.COLUMN_NAME_FILERATINGINT, 8);
  }

  public void showSelectWorkspaceDialog() {
    selectDatabaseDialog = new JDialog();
    selectDatabaseDialog.setSize(600, 200);
    selectDatabaseDialog.setLocationRelativeTo(frame);
    selectDatabaseDialog.setTitle("Workspace Launcher");

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
    workspacePath = new JTextField();
    workspacePath.setText("C:\\Users\\Daniel\\workspace\\mLib");
    panel.add(workspacePath, c);

    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 1;
    c.weighty = 0;
    c.ipadx = 0;
    c.weightx = 0.1;
    c.fill = GridBagConstraints.VERTICAL;
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

    selectDatabaseDialog.add(panel);
    selectDatabaseDialog.setVisible(true);
  }

  public void showSelectScanDirDialog() {
    selectScanDirDialog = new JDialog();
    selectScanDirDialog.setSize(600, 200);
    selectScanDirDialog.setLocationRelativeTo(Interface.getInstance().frame);
    selectScanDirDialog.setTitle("Scan Diretory");

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(0, 10, 0, 0);
    panel.add(new JLabel("Select the directory which has to be sanned"), c);

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
    if(Configuration.scanDir.isEmpty()){
      scandirPath.setText("C:\\Users\\Daniel\\Downloads");
    }
    else{
      scandirPath.setText(Configuration.scanDir);
    }
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
  
  public void showMaintainScanDirDialog() {
    selectMaintainScanDirDialog = new JDialog();
    selectMaintainScanDirDialog.setSize(600, 200);
    selectMaintainScanDirDialog.setLocationRelativeTo(Interface.getInstance().frame);
    selectMaintainScanDirDialog.setTitle("Scan Diretory");

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(0, 10, 0, 0);
    panel.add(new JLabel("Use a comma separated list of Directories which will be scanned."), c);

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
    scandirPath.setText(Configuration.scanDir);
    panel.add(scandirPath, c);

//    c.gridx = 1;
//    c.gridy = 2;
//    c.gridwidth = 1;
//    c.weighty = 0;
//    c.ipadx = 0;
//    c.weightx = 0.1;
//    c.fill = GridBagConstraints.VERTICAL;
//    // c.anchor = GridBagConstraints.CENTER;
//    JButton browseButton = new JButton("Browse...");
//    browseButton.setActionCommand(BUTTON_ACTION_COMMAND_SCANDIR_BROWSE);
//    browseButton.addActionListener(this);
//    panel.add(browseButton, c);

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
    okButton.setActionCommand(BUTTON_ACTION_COMMAND_SCANDIR_MAINTAIN);
    okButton.addActionListener(this);
    panel.add(okButton, c);

    selectMaintainScanDirDialog.add(panel);
    selectMaintainScanDirDialog.setVisible(true);
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
      case BUTTON_ACTION_COMMAND_MENUITEM:
        String menuItemText = ((JMenuItem) event.getSource()).getText();
        switch (menuItemText) {
          case MENU_ITEM_QUIT:
            System.exit(0);
            break;
          case MENU_ITEM_FILE_EXTENSION:
            showFileExtensionDialog();
            break;
          case MENU_ITEM_SCAN_DIRECTORIES:
            showMaintainScanDirDialog();
            break;
          default:
            break;
        }
        break;

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
        Configuration.databaseDir = workspacePath.getText() + "\\" + Configuration.databaseFileName;
        databaseDir.setText(Configuration.databaseDir);
        selectDatabaseDialog.dispose();
        Configuration.setConfigurationProperty(Configuration.propertyKeyWorkspaceDir,
            Configuration.databaseDir);

        DatabaseWorker.getInstance().openDatabase();
        // DatabaseWorker.getInstance().readLibraryIntoObjects();
        Interface.getInstance().reloadFileTable();

        // prevents the dialog from being opened when properties file not existed
        if (!Configuration.fileNotFound) {
          Configuration.checkForScanDir();
        }
        break;
      case BUTTON_ACTION_COMMAND_SCANDIR_BROWSE:

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File selectedDir = fc.getSelectedFile();
          scandirPath.setText(selectedDir.getAbsolutePath());
        }
        break;
      case BUTTON_ACTION_COMMAND_SCANDIR_OK:
        Configuration.scanDir = scandirPath.getText();
        scanDir.setText(scandirPath.getText());
        selectScanDirDialog.dispose();
        Logger.log("set scandir to: " + scanDir.getText());
        Configuration.setConfigurationProperty(Configuration.propertyKeyScanDir,
            Configuration.scanDir);


        Interface.getInstance().reloadFileTable();
        Configuration.checkForAllowedExtensions();
        break;
//      case BUTTON_ACTION_COMMAND_SCANDIR_CHANGE:
//        showSelectScanDirDialog();
//        break;
      case BUTTON_ACTION_COMMAND_SYNCHRONIZATION:
        if (DatabaseWorker.getInstance().openDatabase()) {
          Thread thread = null;
          try {
            Logger.log("Synchronization...");
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
      case BUTTON_ACTION_COMMAND_RATING_A:
        try {
          if (isRowSelected) {
            int ratingNumber = Configuration.ratings.get("A");
            rateFile(fileName, fileExtension, fileSize, selectedRow, ratingNumber);
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_RATING_B:
        try {
          if (isRowSelected) {
            int ratingNumber = Configuration.ratings.get("B");
            rateFile(fileName, fileExtension, fileSize, selectedRow, ratingNumber);
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_RATING_C:
        try {
          if (isRowSelected) {
            int ratingNumber = Configuration.ratings.get("C");
            rateFile(fileName, fileExtension, fileSize, selectedRow, ratingNumber);
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_RATING_D:
        try {
          if (isRowSelected) {
            int ratingNumber = Configuration.ratings.get("D");
            rateFile(fileName, fileExtension, fileSize, selectedRow, ratingNumber);
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_RATING_E:
        try {
          if (isRowSelected) {
            int ratingNumber = Configuration.ratings.get("E");
            rateFile(fileName, fileExtension, fileSize, selectedRow, ratingNumber);
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        break;
      case BUTTON_ACTION_COMMAND_RATING_F:
        try {
          if (isRowSelected) {
            int ratingNumber = Configuration.ratings.get("F");
            rateFile(fileName, fileExtension, fileSize, selectedRow, ratingNumber);
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

          reloadFileTable();
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

          reloadFileTable();
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
          reloadFileTable();
        }
        break;
      case BUTTON_ACTION_COMMAND_FILEEXTENSIONS_OK:
        Configuration.allowedExtensions = fileExtensionList.getText();
        selectFileExtenstionDialog.dispose();
        Logger.log("set allowed extensions to: " + fileExtensionList.getText());
        Configuration.setConfigurationProperty(Configuration.propertyKeyExtensions,
            Configuration.allowedExtensions);
        break;
      case BUTTON_ACTION_COMMAND_SCANDIR_MAINTAIN:
        selectMaintainScanDirDialog.dispose();
        Configuration.scanDir = scandirPath.getText();        
        Logger.log("set scandir to " + Configuration.scanDir);
        Configuration.setConfigurationProperty(Configuration.propertyKeyScanDir,
            Configuration.scanDir);
      default:
        break;
    }
  }

  private void rateFile(String fileName, String fileExtension, String fileSize, int selectedRow,
      int ratingNumber) throws IOException, InterruptedException {

    DatabaseWorker.getInstance().updateMediaFile(
        MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
        DatabaseWorker.updateFieldRating, ratingNumber);

    DatabaseWorker.getInstance().updateMediaFile(
        MediaLibrary.generateFileHash(fileName, fileExtension, fileSize),
        DatabaseWorker.updateFieldReviewed, 1);

    Interface.getInstance().reloadFileTable();

    Runtime.getRuntime().exec("taskkill /F /IM vlc.exe");
    Thread.sleep(500);

    int nextRowNumber = selectNextUnratedRow(selectedRow);

    if (nextRowNumber != -1) {
      String nextFilePath =
          (String) fileTable.getValueAt(nextRowNumber,
              Interface.columnNames.get(COLUMN_NAME_FILEPATH));

      Logger.log("opening file " + nextFilePath);
      ProcessBuilder pb = new ProcessBuilder(Configuration.vlcLocation, nextFilePath);
      pb.start();
    }
  }

  private boolean isNextUnratedRowSelectable(int nextRowNumber) {
    boolean isNextRowSelectable = true;
    try {
      fileTable.setRowSelectionInterval(nextRowNumber, nextRowNumber);
    } catch (IllegalArgumentException e) {
      // thrown when table has no row with nextRowNumber
      isNextRowSelectable = false;
    }
    return isNextRowSelectable;
  }

  private int selectNextUnratedRow(int selectedRow) {
    int nextRowNumber = 0;
    int nextRowRating = 0;
    int countToNextRow = 1;

    do {
      // if next row is selectable
      if (isNextUnratedRowSelectable(selectedRow + countToNextRow)) {
        nextRowRating =
            (int) fileTable.getModel().getValueAt(selectedRow + countToNextRow,
                Interface.columnNames.get(COLUMN_NAME_FILERATINGINT));
      } else {
        return -1;
      }

      if (nextRowRating == 0) {
        nextRowNumber = selectedRow + countToNextRow;
      } else {
        nextRowNumber = selectedRow;
      }
      countToNextRow++;

    } while (nextRowRating != 0);

    return nextRowNumber;
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
    return (int) fileTable.getModel().getValueAt(fileTable.getSelectedRow(),
        columnNames.get(Interface.COLUMN_NAME_FILERATINGINT));
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

  private void loadAndScaleImages() {
    iconNames.add("images/0-rating.png");
    iconNames.add("images/1-rating.png");
    iconNames.add("images/2-rating.png");
    iconNames.add("images/3-rating.png");
    iconNames.add("images/4-rating.png");
    iconNames.add("images/5-rating.png");

    for (int i = 5; i >= 0; i--) {
      icons.put(i + 1, scaleIcon(iconNames.get(i)));
    }
  }

  private ImageIcon scaleIcon(String iconName) {
    ImageIcon ii = new ImageIcon(iconName);
    Image img = ii.getImage();
    Image scaledIcon = img.getScaledInstance(50, 10, java.awt.Image.SCALE_SMOOTH);
    return new ImageIcon(scaledIcon);
  }

  public void reloadFileTable() {
    Logger.log("reloading file table", Logger.LOG_LEVEL_APP);
    // remove all rows from table model
    for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
      tableModel.removeRow(i);
    }

    // scan mLib (sqlite) and save into mLibObjects
    DatabaseWorker.getInstance().readMLibIntoObjects();

    int count = 0;
    for (MediaFile v : FileProcessor.mLibObjects) {

      Object[] tableColumns =
          {count, icons.get(v.getRating()), v.getReviewed(), v.getToBeDeleted(), v.getName(),
              v.getExtension(), v.getFilesize(), v.getPath(), v.getRating()};
      tableModel.addRow(tableColumns);



      count++;
    }
    filterCount.setText("Rows: " + fileTable.getRowCount());
  }

  /*
   * filters the jTable to the values entered in searchBar
   */
  public void filterFileTable() {
    TableRowSorter<TableModel> sorter =
        new TableRowSorter<TableModel>(((DefaultTableModel) fileTable.getModel()));
    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchBar.getText()));
    fileTable.setRowSorter(sorter);
    filterCount.setText("Rows: " + fileTable.getRowCount());
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

      String filePath =
          fileTable.getValueAt(fileTable.getSelectedRow(),
              Interface.columnNames.get(COLUMN_NAME_FILEPATH)).toString();
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

  public void showFileExtensionDialog() {
    selectFileExtenstionDialog = new JDialog();
    selectFileExtenstionDialog.setSize(600, 200);
    selectFileExtenstionDialog.setLocationRelativeTo(frame);
    selectFileExtenstionDialog.setTitle("Scanned File Extensions");

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(0, 10, 0, 0);
    panel.add(new JLabel("Enter file extensinos "), c);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.weighty = 0;
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(10, 10, 0, 0);

    JTextArea textArea =
        new JTextArea(
            "Add a comma seperated list of file extensions like: \"mp3,mp4,mov,jpg,png\".\nAll listed file extensions will be scanned.");
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
    c.insets = new Insets(15, 10, 0, 15);
    // c.anchor = GridBagConstraints.CENTER;
    fileExtensionList = new JTextField();
    fileExtensionList.setText(Configuration.allowedExtensions);
    fileExtensionList.grabFocus();
    panel.add(fileExtensionList, c);

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
    okButton.setActionCommand(BUTTON_ACTION_COMMAND_FILEEXTENSIONS_OK);
    okButton.addActionListener(this);
    panel.add(okButton, c);

    selectFileExtenstionDialog.getRootPane().setDefaultButton(okButton);
    selectFileExtenstionDialog.add(panel);
    selectFileExtenstionDialog.setVisible(true);

    // set focus to text field
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        fileExtensionList.requestFocusInWindow();
      }
    });
  }
  
  @Override
  public void changedUpdate(DocumentEvent arg0) {}

  @Override
  public void insertUpdate(DocumentEvent arg0) {
    filterFileTable();
  }

  @Override
  public void removeUpdate(DocumentEvent arg0) {
    filterFileTable();
  }


}
