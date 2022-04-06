package application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class createCursosController extends Application {

	public Stage primaryStage;
	public Stage principal;
	AnchorPane ap;

	@FXML
	private TextArea taDescripcio;

	@FXML
	private TextField inputTitol;

	@FXML
	private Button btnCrearCurs;

	static MongoDatabase database;
	static String uriString;
	static String uri;
	static MongoCollection<Document> collectionCourses;
	public static JSONObject jsonObject = null;
	loginWindow_Controller logController;

	private Object obj;

	// public static void setStage(Stage primaryStage) {
	// primaryStage = primaryStage;
	// }

	// public void start(Stage primaryStage) throws IOException, ParseException {
	//
	// Parent root = FXMLLoader.load(getClass().getResource("createCursos.fxml"));
	// Scene scene = new Scene(root);
	// primaryStage.setScene(scene);
	// principal = primaryStage;
	// primaryStage.show();
	//
	// }

	@FXML
	void initialize() throws Exception {

		try (MongoClient mongoClient = MongoClients.create(
				"mongodb+srv://m001-student:m001-student@sandbox.qczb2.mongodb.net/ClassVRroom?authSource=admin&replicaSet=Sandbox-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true")) {

			database = mongoClient.getDatabase("ClassVRroom");
			collectionCourses = database.getCollection("courses");

			JSONParser jsonParser = new JSONParser();
			try (FileReader reader = new FileReader("classVRroom_OneCourse.json")) {
				// Read JSON file
				obj = jsonParser.parse(reader);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (MongoException me) {
			System.err.println("An error occurred while attempting to run a command: " + me);
		}

		btnCrearCurs.addEventHandler(ActionEvent.ACTION, new EventHandler<Event>() {
			public void handle(Event arg0) {
				((Node) (arg0.getSource())).getScene().getWindow().hide();
			}
		});

	}

	public void clicked() {
		try (MongoClient mongoClient = MongoClients.create(
				"mongodb+srv://m001-student:m001-student@sandbox.qczb2.mongodb.net/ClassVRroom?authSource=admin&replicaSet=Sandbox-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true")) {

			database = mongoClient.getDatabase("ClassVRroom");
			collectionCourses = database.getCollection("courses");

			JSONParser jsonParser = new JSONParser();
			try (FileReader reader = new FileReader("classVRroom_OneCourse.json")) {
				// Read JSON file
				obj = jsonParser.parse(reader);

				if (!inputTitol.getText().isEmpty() && !taDescripcio.getText().isEmpty()) {
					// JSON parser object to parse read file

					System.out.println("entra");

					try {
						Document test = Document.parse(String.valueOf(obj));
						test.put("title", inputTitol.getText());
						test.put("description", taDescripcio.getText());

						System.out.println(test);
						collectionCourses.insertOne(test);
						System.out.println("Creado");

						// ((Node)(arg0.getSource())).getScene().getWindow().hide();

						FXMLLoader fxmlLoader = new FXMLLoader();
						Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));

						Scene scene = new Scene(root);
						Stage stage = new Stage();
						stage.setScene(scene);

						loginWindow_Controller rc = fxmlLoader.getController();
						stage.show();

					} catch (MongoException | IOException me) {
					}

				} else {
					Alert a = new Alert(AlertType.WARNING, "Nothing can be left blank");
					a.show();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (MongoException me) {
			System.err.println("An error occurred while attempting to run a command: " + me);
		}

//		btnCrearCurs.addEventHandler(ActionEvent.ACTION, new EventHandler<Event>() {
//			public void handle(Event arg0) {

//			}
//		});
	}

	@Override
	public void start(Stage arg0) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
		Scene scene = new Scene(root);

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);

		principal = primaryStage;

		primaryStage.show();

		System.out.println("hola");

	}

}
