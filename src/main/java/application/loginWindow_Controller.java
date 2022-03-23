package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.sun.glass.events.MouseEvent;

import application.model.Course;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class loginWindow_Controller extends Application {

	public static Stage primaryStage;
	private Pane rootLayout;

	public static Stage principal;
	static String uriString;
	static String uri;
	
	static MongoDatabase database;

    @FXML
    private Button btnCrearCurs;
    
    @FXML
    private GridPane gridCursos;
    
    @FXML
    private ListView<String> listCursos;
    
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
	Label lTitulo;	
	AnchorPane ap;
	static Button btnElimi;
	

	static MongoCollection<Document> collectionCourses;
	static MongoCollection<Document> collectionUsers;
	FindIterable<Document> iterDoc;
	MongoCursor<Document> cursor;
    
    private final List<String> listIds = FXCollections.observableArrayList();
    
	private ObservableList<Course> courseData = FXCollections.observableArrayList();
	ArrayList<String> arrayTitulo = new ArrayList<String>();
	ArrayList<ObjectId> arrayId = new ArrayList<ObjectId>();
	ArrayList<String> arrayDescripcion = new ArrayList<String>();


	@FXML
    public void initialize() throws ParseException {
		
		try (MongoClient mongoClient = MongoClients.create(uri)) {
            database = mongoClient.getDatabase("ClassVRroom");
           try {
               Bson command = new BsonDocument("ping", new BsonInt64(1));
               Document commandResult = database.runCommand(command);
               System.out.println("Connected successfully to server.");
			    
			    JSONParser parser = new JSONParser();
			    
			    
			    //COLLECTION COURSES//
	            collectionCourses = database.getCollection("courses");
	            iterDoc = collectionCourses.find();
				cursor = collectionCourses.find().iterator();
				while (cursor.hasNext()) {
			         Document stringCursor = cursor.next();
			    }
				Label lTitulo = null;
			    FindIterable<Document> f = collectionCourses.find();
			    for(Document doc : f) {
			    	String title = (String) doc.get("title");
			    	ObjectId _id = (ObjectId) doc.get("_id");
			    	String descrp = (String) doc.get("description");
			    	System.out.println(_id);
				    arrayTitulo.add(title);
				    arrayId.add(_id);
				    
				    for(int x = 0; x < arrayTitulo.size(); x++) {
		            	lTitulo = new Label();
		            	lTitulo.setText(arrayTitulo.get(x));
		            	
		            	gridCursos.add(lTitulo, 0, x);
		        		btnElimi = new Button("X");
		            	gridCursos.add(btnElimi, 2, x);
		            	
		            	btnElimi.setOnAction(new EventHandler<ActionEvent>() {
		            		 public void handle(ActionEvent event) {
		            			 deleteCourse();
		            		 }
						});
		            	
		            	for (int i = 0; i < arrayId.size(); i++) {
							btnElimi.setId(_id.toString());
							System.out.println(btnElimi);
						}
			           
		            	lTitulo.setOnMouseClicked(event -> {
			           });
	            	}
		    		
			    }

			    //COLLECTION USERS//
			    collectionUsers = database.getCollection("users");
			    FindIterable<Document> fu = collectionUsers.find();
				MongoCursor<Document> cursorUsers = collectionUsers.find().iterator();
			    for(Document docU : fu) {
			    	String usersName = (String) docU.get("first_name");
			    	
	            	listUsuaris.getItems().addAll(usersName);
	            	listUsuaris.getItems().addAll(usersName);

			    }


           } catch (MongoException me) {
               System.err.println("An error occurred while attempting to run a command: " + me);
           }
       }
		
		btnCrearCurs.addEventHandler(ActionEvent.ACTION, new EventHandler<Event>() {
			public void handle(Event arg0) {	
				try {
					
					FXMLLoader fxmlLoader = new FXMLLoader();
			         fxmlLoader.setLocation(loginWindow_Controller.class.getResource("createCursos.fxml"));
			         ap = fxmlLoader.load();
			         Scene scene = new Scene(ap, 600, 400);
			         Stage stage = new Stage();
			         stage.setScene(scene);
			         
			         createCursosController cc = fxmlLoader.getController();
			         stage.show();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	
	}
		
	public void start(Stage primaryStage) throws IOException, ParseException {
			
			//////////URI text file//////////
		File uriFile = new File("uri");
		BufferedReader br = new BufferedReader(new FileReader(uriFile));
			
		while ((uriString = br.readLine()) != null) {
			System.out.println(uriString);
			uri = uriString;
		}
				
			
		//////////Window creation//////////	
		try {
			Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));
			Scene scene = new Scene(root);
				
				
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
				
				
			principal = primaryStage;
				
			primaryStage.show();
				
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
		
	public static void setStage(Stage primaryStage) {
	   	primaryStage = primaryStage;
	 }

	public static void main(String[] args) throws IOException {
		launch(args);
		

	}
	
	public static void deleteCourse() {
		try (MongoClient mongoClient = MongoClients.create(uri)) {
            database = mongoClient.getDatabase("ClassVRroom");
           try {
               Bson command = new BsonDocument("ping", new BsonInt64(1));
               Document commandResult = database.runCommand(command);			    
			   JSONParser parser = new JSONParser();
			    
			    collectionCourses = database.getCollection("courses");
				collectionCourses.deleteOne(Filters.eq("_id", new ObjectId(btnElimi.getId())));

           } catch (MongoException me) {
               System.err.println("An error occurred while attempting to run a command: " + me);
           }
       }
	}
	
	
	
	
}


