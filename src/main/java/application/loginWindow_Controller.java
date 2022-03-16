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
	
	MongoDatabase database;

    @FXML
    private Button btnCrearCurs;
    
    @FXML
    private GridPane gridCursos;
    
    Label nDescripcion;
	Label nTitulo;
	static String idPrueba = "6230ca858e7ea74e00e08c43";
	
	AnchorPane ap;
	

	static MongoCollection<Document> collection;
    
    private final List<String> listIds = FXCollections.observableArrayList();
    
	private ObservableList<Course> courseData = FXCollections.observableArrayList();
	ArrayList<String> arrayTitulo = new ArrayList<String>();
	ArrayList<String> arrayDescripcion = new ArrayList<String>();


	@FXML
    public void initialize() throws ParseException {
		
		try (MongoClient mongoClient = MongoClients.create(uri)) {
            database = mongoClient.getDatabase("ClassVRroom");
           try {
               Bson command = new BsonDocument("ping", new BsonInt64(1));
               Document commandResult = database.runCommand(command);
               System.out.println("Connected successfully to server.");
               
               collection = database.getCollection("courses");
			   FindIterable<Document> iterDoc = collection.find();
			   MongoCursor<Document> cursor = collection.find().iterator();

			   while (cursor.hasNext()) {
			         Document stringCursor = cursor.next();
//			         System.out.println(stringCursor);
			    }
			    
			    JSONParser parser = new JSONParser();
			    			    
			    FindIterable<Document> f = collection.find();
			    ArrayList<String> colString = new ArrayList<>();
			    for (Document doc : f) {
	                colString.add(doc.toJson());
//	                System.out.println(doc);
	            };
	            
	            for (String finString : colString) {
	            	Object obj = parser.parse(finString);
	            	JSONObject jObject = (JSONObject) obj;
	            	String titulo = (String) jObject.get("title");
	            	String descripcion = (String) jObject.get("description");
	            	String id_Doc = (String) jObject.get("id");
//	            	System.out.println(titulo);
//	            	System.out.println(descripcion);
	            		            	
	            	arrayTitulo.add(titulo);
	            	arrayDescripcion.add(descripcion);

	            };
//            	System.out.println(arrayTitulo);

            	//Adding data to grid
	            for(int x = 0; x < arrayTitulo.size(); x++) {
	            	Label lTitulo = new Label();
	            	lTitulo.setText(arrayTitulo.get(x));
	            	gridCursos.add(lTitulo, 0, x);
	        		Button btnElimi = new Button("Eliminar");
	            	gridCursos.add(btnElimi, 2, x);
	            	
//	            	btnElimi.addEventHandler(ActionEvent.ACTION, new EventHandler<Event>() {
//						public void handle(Event arg0) {	
//							try {
//								collection.deleteOne(new Document("_id", new ObjectId(idPrueba)));
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					});
//	            	     	
            	}
//	            System.out.println(arrayDescripcion);
	            for(int x = 0; x < arrayDescripcion.size(); x++) {
	            	Label lDescripcion = new Label();
	            	lDescripcion.setText(arrayDescripcion.get(x));
	            	gridCursos.add(lDescripcion, 1, x);
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
	
	
}


