package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import application.model.Course;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class loginWindow_Controller extends Application {

	public Stage primaryStage;

	public static Stage principal;
	static String uriString;
	static String uri;

	static MongoDatabase database;

	@FXML
	private Button btnCrearCurs;

	@FXML
	public GridPane gridCursos;

	@FXML
	public ListView<String> listCursos;

	@FXML
	private Label lblDescr;

	@FXML
	private ListView<String> listProfessorsCurs;

	@FXML
	private ListView<String> listUsuaris;

	@FXML
	private ListView<String> listAlumnesCurs;

	@FXML
	private Button btnEliminarAlumne;

	@FXML
	private Button btnEliminarProf;

	@FXML
	private Button btnAfegirAlumne;

	@FXML
	private Button btnAfegirProf;

	Label nDescripcion;
	Label nTitulo;
	static Label lTitulo;
	AnchorPane ap;
	// static Button btnElimi;
	String currentC;
	static MongoCollection<Document> collectionCourses;
	static MongoCollection<Document> collectionUsers;
	FindIterable<Document> iterDoc;
	MongoCursor<Document> cursor;

	private final List<String> listIds = FXCollections.observableArrayList();
	private HashMap<String, Object> userId = new HashMap<>();

	private ObservableList<Course> courseData = FXCollections.observableArrayList();
	ArrayList<String> arrayTitulo = new ArrayList<String>();
	static ArrayList<ObjectId> arrayId = new ArrayList<ObjectId>();
	ArrayList<String> arrayDescripcion = new ArrayList<String>();
	ArrayList<JsonObject> array = new ArrayList<JsonObject>();
	ArrayList<String> intTeac = new ArrayList<String>();

	@FXML
	public void initialize() throws ParseException, IOException {

		try (MongoClient mongoClient = MongoClients.create(uri)) {
			database = mongoClient.getDatabase("ClassVRroom");
			try {
				Bson command = new BsonDocument("ping", new BsonInt64(1));
				Document commandResult = database.runCommand(command);
				System.out.println("Connected successfully to server.");

				JSONParser parser = new JSONParser();

				// COLLECTION COURSES//
				collectionCourses = database.getCollection("courses");
				iterDoc = collectionCourses.find();
				cursor = collectionCourses.find().iterator();
				while (cursor.hasNext()) {
					Document stringCursor = cursor.next();
				}
				FindIterable<Document> f = collectionCourses.find();
				int x = 0;
				for (Document doc : f) {
					String title = (String) doc.get("title");
					ObjectId _id = (ObjectId) doc.get("_id");
					String descrp = (String) doc.get("description");
					Document subscribers = (Document) doc.get("subscribers");
					Object subTeachers = subscribers.get("teachers");
					ArrayList<String> subStudents = (ArrayList<String>) subscribers.get("students");
					ObservableList<String> subS = FXCollections.observableArrayList();
					ObservableList<String> subT = FXCollections.observableArrayList();
					ObservableList<String> users = FXCollections.observableArrayList();

//					subT.addAll(subTeachers);
//					subS.addAll(subStudents);
					// COLLECTION USERS//
					collectionUsers = database.getCollection("users");
					FindIterable<Document> fu = collectionUsers.find();
					MongoCursor<Document> cursorUsers = collectionUsers.find().iterator();

					Gson gson = new Gson();

					for (Document docU : fu) {
						String usersName = (String) docU.get("first_name");
						Integer idUser = (Integer) docU.get("ID");
						userId.put(usersName, idUser);
						users.add(usersName);
						((Collection) subTeachers).forEach(id -> {
							if (id == docU.get("ID")) {
								subT.add(usersName);
								users.remove(usersName);

							}
						});
						((Collection) subStudents).forEach(id -> {
							if (id == docU.get("ID")) {
								subS.add(usersName);
								users.remove(usersName);

							}
						});

						String docJ = docU.toJson();
						array.add((JsonObject) new JsonParser().parse(docJ));

						// Afegir profesor llista
						btnAfegirProf.setOnAction(new EventHandler<ActionEvent>() {
							public void handle(ActionEvent arg0) {
								String teach = listUsuaris.getSelectionModel().getSelectedItem();
								try (MongoClient mongoClient = MongoClients.create(uri)) {

									database = mongoClient.getDatabase("ClassVRroom");
									collectionCourses = database.getCollection("courses");
									try {

										List<Integer> teachers = new ArrayList<>();
										userId.forEach((k, v) -> {
											if (k.equals(teach)) {
												teachers.add((Integer) v);
												collectionCourses.updateOne(Filters.eq("title", currentC),
														Updates.push("subscribers.teachers", v));

											}
										});

									} catch (MongoException me) {
										System.err
												.println("An error occurred while attempting to run a command: " + me);
									}
								}

								listProfessorsCurs.getItems().addAll(teach);
								listUsuaris.getItems().remove(teach);
								userId.forEach((k, v) -> {
									if (k.equals(teach)) {
										System.out.println(currentC + " dentro " + k + " " + v);

									}
								});

							}
						});
						// eliminar profesor llista
						btnEliminarProf.setOnAction(new EventHandler<ActionEvent>() {
							public void handle(ActionEvent arg0) {
								String teach = listProfessorsCurs.getSelectionModel().getSelectedItem();
								try (MongoClient mongoClient = MongoClients.create(uri)) {

									database = mongoClient.getDatabase("ClassVRroom");
									collectionCourses = database.getCollection("courses");
									try {

										List<Integer> teachers = new ArrayList<>();
										userId.forEach((k, v) -> {
											if (k.equals(teach)) {
												teachers.add((Integer) v);
												collectionCourses.updateOne(Filters.eq("title", currentC),
														Updates.pull("subscribers.teachers", v));

											}
										});

									} catch (MongoException me) {
										System.err
												.println("An error occurred while attempting to run a command: " + me);
									}
								}
								listUsuaris.getItems().addAll(teach);
								listProfessorsCurs.getItems().remove(teach);
							}
						});
						// Afegir alumne llista
						btnAfegirAlumne.setOnAction(new EventHandler<ActionEvent>() {
							public void handle(ActionEvent arg0) {
								String student = listUsuaris.getSelectionModel().getSelectedItem();
								try (MongoClient mongoClient = MongoClients.create(uri)) {

									database = mongoClient.getDatabase("ClassVRroom");
									collectionCourses = database.getCollection("courses");
									try {

										List<Integer> students = new ArrayList<>();
										userId.forEach((k, v) -> {
											if (k.equals(student)) {
												collectionCourses.updateOne(Filters.eq("title", currentC),
														Updates.push("subscribers.students", v));

											}
										});

									} catch (MongoException me) {
										System.err
												.println("An error occurred while attempting to run a command: " + me);
									}
								}
								listAlumnesCurs.getItems().addAll(student);
								listUsuaris.getItems().remove(student);

							}
						});
						// Eliminar alumne llista
						btnEliminarAlumne.setOnAction(new EventHandler<ActionEvent>() {
							public void handle(ActionEvent arg0) {
								String student = listAlumnesCurs.getSelectionModel().getSelectedItem();
								try (MongoClient mongoClient = MongoClients.create(uri)) {

									database = mongoClient.getDatabase("ClassVRroom");
									collectionCourses = database.getCollection("courses");
									try {

										List<Integer> students = new ArrayList<>();
										userId.forEach((k, v) -> {
											if (k.equals(student)) {
												students.add((Integer) v);
												collectionCourses.updateOne(Filters.eq("title", currentC),
														Updates.pull("subscribers.students", v));

											}
										});

									} catch (MongoException me) {
										System.err
												.println("An error occurred while attempting to run a command: " + me);
									}
								}
								listUsuaris.getItems().addAll(student);
								listAlumnesCurs.getItems().remove(student);

							}
						});

					}
					lTitulo = new Label();
					lTitulo.setText(title);

					gridCursos.add(lTitulo, 0, x);
					Button btnElimi = new Button("X");
					gridCursos.add(btnElimi, 2, x);

					btnElimi.setId(_id.toString());
					lTitulo.setId(_id.toString());

					btnElimi.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							deleteCourse(btnElimi.getId());
							if (btnElimi.getId().equals(lTitulo.getId())) {
								btnElimi.setVisible(false);
								lTitulo.setVisible(false);
							}
						}
					});

					lTitulo.setOnMouseClicked(event -> {
						lblDescr.setText(doc.get("description").toString());
						currentC = doc.get("title").toString();
						listProfessorsCurs.setItems(subT);
						listAlumnesCurs.setItems(subS);
						listUsuaris.setItems(users);

					});

					x++;
				}

			} catch (MongoException me) {
				System.err.println("An error occurred while attempting to run a command: " + me);
			}
		}

		btnCrearCurs.addEventHandler(ActionEvent.ACTION, new EventHandler<Event>() {
			public void handle(Event arg0) {
				try {
					Parent root = FXMLLoader.load(getClass().getResource("createCursos.fxml"));
					Stage st = new Stage();
					st.setScene(new Scene(root));
					st.setResizable(false);
					st.show();

					((Node) (arg0.getSource())).getScene().getWindow().hide();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	public static void main(String[] args) throws IOException {
		launch(args);

	}

	public static void addItemsToListView(ListView<JsonObject> list, ArrayList<JsonObject> array) {
		list.getItems().clear();
		list.getItems().addAll(array);
		list.setCellFactory(lv -> new ListCell<JsonObject>() {
			@Override
			public void updateItem(JsonObject array, boolean empty) {
				super.updateItem(array, empty);
				setText(empty ? null : array.get("first_name").toString());
			}
		});
	}

	public static void deleteCourse(String id) {
		Alert a = new Alert(AlertType.CONFIRMATION, "S'eliminara el curs, estas segur?");
		Optional<ButtonType> result = a.showAndWait();

		if (result.get() == ButtonType.OK) {
			try (MongoClient mongoClient = MongoClients.create(uri)) {
				database = mongoClient.getDatabase("ClassVRroom");
				try {
					Bson command = new BsonDocument("ping", new BsonInt64(1));
					Document commandResult = database.runCommand(command);
					JSONParser parser = new JSONParser();

					collectionCourses = database.getCollection("courses");
					collectionCourses.deleteOne(new Document("_id", new ObjectId(id)));
					// collectionCourses.deleteOne(Filters.eq("_id", new ObjectId(id)));
					Alert b = new Alert(AlertType.INFORMATION, "El curs s'ha eliminat correctament!");
					b.showAndWait();

				} catch (MongoException me) {
					System.err.println("Hi ha hagut un error" + me);
				}
			}
		} else {
			Alert b = new Alert(AlertType.INFORMATION, "Res s'ha eliminat");
			b.showAndWait();
		}

	}

	public void deleteUser(String course, int teacher) {
		try (MongoClient mongoClient = MongoClients.create(uri)) {
			database = mongoClient.getDatabase("ClassVRroom");
			try {
				Bson command = new BsonDocument("ping", new BsonInt64(1));
				Document commandResult = database.runCommand(command);
				JSONParser parser = new JSONParser();
				collectionCourses = database.getCollection("courses");

				Document query = new Document().append("title", course);
				Bson updates = Updates.pull("subscribers.teachers", teacher);
				UpdateOptions options = new UpdateOptions().upsert(true);
				UpdateResult result = collectionCourses.updateOne(query, updates, options);

			} catch (MongoException me) {
				System.err.println("An error occurred while attempting to run a command: " + me);
			}
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		////////// URI text file//////////
		File uriFile = new File("uri");
		BufferedReader br = new BufferedReader(new FileReader(uriFile));

		while ((uriString = br.readLine()) != null) {
			System.out.println(uriString);
			uri = uriString;
		}

		////////// Window creation//////////
		try {
			Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);

			principal = primaryStage;

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
