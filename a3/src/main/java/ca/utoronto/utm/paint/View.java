package ca.utoronto.utm.paint;

import java.io.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class View implements EventHandler<ActionEvent> {

    private PaintModel paintModel;
    private PaintPanel paintPanel;
    private ShapeChooserPanel shapeChooserPanel;
    private Stage stage;

    public View(PaintModel model, Stage stage) {
        this.stage = stage;
        this.paintModel = model;
        initUI(stage);
    }

    public PaintModel getPaintModel() {
        return this.paintModel;
    }

    public void setPaintModel(PaintModel paintModel) {
        this.paintModel = paintModel;
        this.paintPanel.setPaintModel(paintModel);
    }

    private void initUI(Stage stage) {

        this.paintPanel = new PaintPanel(this.paintModel);
        this.shapeChooserPanel = new ShapeChooserPanel(this);

        BorderPane root = new BorderPane();
        root.setTop(createMenuBar());
        root.setCenter(this.paintPanel);
        root.setLeft(this.shapeChooserPanel);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Paint");
        stage.show();
    }

    public PaintPanel getPaintPanel() {
        return paintPanel;
    }

    public ShapeChooserPanel getShapeChooserPanel() {
        return shapeChooserPanel;
    }

    private MenuBar createMenuBar() {

        MenuBar menuBar = new MenuBar();
        Menu menu;
        MenuItem menuItem;

        // A menu for File

        menu = new Menu("File");

        menuItem = new MenuItem("New");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Open");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Save");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menu.getItems().add(new SeparatorMenuItem());

        menuItem = new MenuItem("Exit");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);

        // Another menu for Edit

        menu = new Menu("Edit");

        menuItem = new MenuItem("Cut");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Copy");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Paste");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menu.getItems().add(new SeparatorMenuItem());

        menuItem = new MenuItem("Undo");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Redo");
        menuItem.setOnAction(this);
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);

        return menuBar;
    }

    public void setPaintPanelShapeManipulatorStrategy(ShapeManipulatorStrategy strategy) {
        this.paintPanel.setShapeManipulatorStrategy(strategy);
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println(((MenuItem) event.getSource()).getText());
        String command = ((MenuItem) event.getSource()).getText();

        // Set the default initial directory to the user's home folder:
        File homeFolder;
        // For some reason if <user.home> is not correctly set or misconfigured, or if the OS has no home directory:
        try {
            homeFolder = new File(System.getProperty("user.home"));
            if (!homeFolder.exists() || !homeFolder.isDirectory()) {
                throw new Exception("Home directory not valid.");
            }
        } catch (Exception e) {
            // Fallback to the root directory as a last resort:
            homeFolder = new File(File.separator);
            System.out.println("Failed to find home directory, falling back to root directory.");
        }

        if (command.equals("Open")) {
            FileChooser fc = new FileChooser();

            // Set the title and file extension type:
            fc.setTitle("Open Paint File");
            fc.setInitialDirectory(homeFolder);
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Documents (*.txt)", "*.txt"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Paint Save File Format Files (*.pssf)", "*.pssf"));

            File file = fc.showOpenDialog(this.stage);

            if (file != null) {
                try {
                    System.out.println("Opening: " + file.getName() + "." + "\n");
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    PaintModel newModel = new PaintModel();
                    PaintFileParser parser = new PaintFileParser();
                    if (parser.parse(bufferedReader, newModel)) {
                        this.setPaintModel(newModel);
                        System.out.println("File loaded successfully.");
                    } else {
                        System.out.println("Error while loading file: " + parser.getErrorMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Parsing issue.");
                }
            } else {
                System.out.println("Open command cancelled by user." + "\n");
            }
        } else if (command.equals("Save")) {
            FileChooser fc = new FileChooser();

            // Set the title and file extension type:
            fc.setTitle("Save Paint File");
            fc.setInitialDirectory(homeFolder);
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Documents (*.txt)", "*.txt"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Paint Save File Format Files (*.pssf)", "*.pssf"));

            File file = fc.showSaveDialog(this.stage);

            if (file != null) {
                // This is where a real application would open the file.

                PrintWriter writer = null;
                try {
                    System.out.println("Saving: " + file.getName() + "." + "\n");
                    writer = new PrintWriter(file);
                    View.save(writer, this.paintModel);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            } else {
                System.out.println("Save command cancelled by user." + "\n");
            }
        } else if (command.equals("New")) {
            // this.paintModel.reset();
            this.setPaintModel(new PaintModel());
        } else if (command.equals("Exit")) {
            Platform.exit();
        }
    }

    /**
     * Save the given paintModel to the open file
     *
     * @param writer
     * @param paintModel
     */
    public static void save(PrintWriter writer, PaintModel paintModel) {
        writer.println("Paint Save File Version 1.0");
        paintModel.save(writer);
        writer.println("End Paint Save File");
        writer.close();
    }
}
