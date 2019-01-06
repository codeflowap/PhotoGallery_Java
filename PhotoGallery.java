
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

//https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm

public class PhotoGallery extends Application {
	static String selectedDir;
	static VBox thumbContainer;
	static ImageView mainViewer;
	static TextField tag1, tag2, tag3, filterText;
	static Button saveTags, filter, clearFilter;
	String[] acceptedExtensions = {".jpg", ".png"}; 
	static Label currPath;
	static ArrayList<String> paths = new ArrayList<>();
	
	static String[] readTags(String path) {
		String tagPath = path + ".txt";
		String[] tags = {"", "", ""};
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(tagPath));
			String line = br.readLine();
			String[] split = line.split(",");
			tags[0] = split[0];
			tags[1] = split[1];
			tags[2] = split[2];
			br.close();
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		} catch (ArrayIndexOutOfBoundsException e) {
			
		}
		return tags;
	}
	
	static void loadTags(String path) {
		String[] tags = readTags(path);
		tag1.setText(tags[0]);
		tag2.setText(tags[1]);
		tag3.setText(tags[2]);
	}
		
	static void loadImage(String path) {
		System.out.println("Setting text " + path);
		currPath.setText("Image path: " + path);
		Image image = new Image("file:"+path);
		mainViewer.setImage(image);
		tag1.setText("");
		tag2.setText("");
		tag3.setText("");
		saveTags.setOnAction(e -> {
			System.out.println("Saving tags");
			String tagPath = path + ".txt";
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter(tagPath));
				br.write(tag1.getText() + "," + tag2.getText() + "," + tag3.getText());
				br.close();
			}
			catch (IOException ioe) {
				
			}
		});		
		loadTags(path);
	}
	
	static boolean tagMatched(String[] tags, String tag) {
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equals(tag)) {
				return true;
			}
		}
		return false;
	}
	
	static void loadOneThumbnail(String p) {
		Image image = new Image("file:"+p);
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(150);
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		imageView.setOnMouseClicked(e-> {
			System.out.println("Clicked " + p);
			loadImage(p);
		});
		thumbContainer.getChildren().add(imageView);
	}
	
	static void loadThumbnails() {
		thumbContainer.getChildren().clear();
		for (int i = 0; i < paths.size(); i++) {
			String p = paths.get(i);
			System.out.println("Loading " + p);
			loadOneThumbnail(p);
		}
	}
	
	static void filterThumbnails() {
		thumbContainer.getChildren().clear();
		for (int i = 0; i < paths.size(); i++) {
			String p = paths.get(i);
			System.out.println("Checking " + p);
			String[] tags = readTags(p);
			if (tagMatched(tags, filterText.getText())) {
				loadOneThumbnail(p);
			}
		}
	}
	
	public void start(Stage primaryStage) {
		
		GridPane gridPane = new GridPane();
		gridPane.setOnMouseClicked(e -> {
			System.out.println("Grid Pane clicked");
		});
		MenuBar menuBar = new MenuBar();
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		
		Menu menuFile = new Menu("File");
		MenuItem openDir = new MenuItem("Open directory");
		MenuItem exit = new MenuItem("Exit");
		menuFile.getItems().addAll(openDir, exit);
		menuBar.getMenus().addAll(menuFile);
		exit.setOnAction(e -> System.exit(0));
		openDir.setOnAction(e-> {
			File selectedDirectory = dirChooser.showDialog(primaryStage);
			if (selectedDirectory != null) {
				String path = selectedDirectory.getAbsolutePath();
				System.out.println(path);
				FileDiscovery fd = new FileDiscovery(path, this.acceptedExtensions);
				paths = fd.discover();
				loadThumbnails();
				selectedDir = path;
				/*for (int i = 0; i < paths.size(); i++) {
					System.out.println(paths.get(i));
				}*/
				
			}
		});
		
		
		// Create a scene and place a button in the scene
		HBox hbox = new HBox(10);
		hbox.setFillHeight(true);
		hbox.setPadding(new Insets(10, 10, 10, 10)); // top right bottom left
		
		GridPane sideBar = new GridPane();
		//VBox sideBar2 = new VBox(10);
		HBox filterContainer = new HBox(10);
		filterText = new TextField();
		filterText.setPrefWidth(60);
		filter = new Button("Filter");
		filter.setOnAction(e -> {
			filterThumbnails();
		});
		clearFilter = new Button("Clear");
		clearFilter.setOnAction(e -> {
			loadThumbnails();
		});
		filterContainer.getChildren().addAll(filterText, filter, clearFilter);
		ScrollPane s1 = new ScrollPane();
		thumbContainer = new VBox();
		/*thumbContainer.getChildren().addAll(
				new Label("First Name:"),
				new TextField(), 
				new Label("MI:"));*/
		/*for (int i = 0; i < 100; i++) {
			thumbContainer.getChildren().addAll(new TextField());
		}*/
		s1.setContent(thumbContainer);
		s1.setPrefWidth(180);
		s1.setMinWidth(180);
		
		sideBar.setVgap(10);
		sideBar.add(filterContainer, 0, 0);
		sideBar.add(s1, 0, 1);
		
		RowConstraints rowSidebar1 = new RowConstraints();
		RowConstraints rowSidebar2 = new RowConstraints();
		rowSidebar2.setVgrow(Priority.ALWAYS);
		sideBar.getRowConstraints().addAll(rowSidebar1, rowSidebar2);
		ColumnConstraints colSidebar = new ColumnConstraints();
		//colSidebar.setPrefWidth(200);
		sideBar.getColumnConstraints().addAll(colSidebar);
		//sideBar2.getChildren().addAll(filterContainer, s1);
		mainViewer = new ImageView();
		mainViewer.setFitWidth(300);
		mainViewer.setPreserveRatio(true);
		
		VBox mainContainer = new VBox();
		mainContainer.setSpacing(10);
		HBox tagContainer = new HBox();
		tagContainer.setSpacing(10);
		tag1 = new TextField();
		tag1.setPrefWidth(60);
		tag2 = new TextField();
		tag2.setPrefWidth(60);
		tag3 = new TextField();
		tag3.setPrefWidth(60);
		saveTags = new Button("Save");
		tagContainer.getChildren().addAll(
			new Label("Tags: "), 
			tag1,
			tag2,
			tag3,
			saveTags
		);
		HBox pathContainer = new HBox(10);
		currPath = new Label("No image selected");
		//currPath.setWrapText(true);
		//currPath.setMaxWidth(500);
		mainContainer.getChildren().addAll(currPath, tagContainer, mainViewer);
		
		hbox.getChildren().addAll(sideBar, mainContainer);
		
		gridPane.add(menuBar, 0, 0);
		gridPane.add(hbox, 0, 1);
		
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(100);
		gridPane.getColumnConstraints().addAll(col1);
		
		RowConstraints row1 = new RowConstraints();
		RowConstraints row2 = new RowConstraints();
		row2.setVgrow(Priority.ALWAYS);
		gridPane.getRowConstraints().addAll(row1, row2);
		
		Scene scene = new Scene(gridPane, 600, 500);
		primaryStage.setTitle("Photo Gallery"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(args);
	}
}
