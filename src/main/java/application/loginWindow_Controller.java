package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
    private Button btnEliminarCurs;

    @FXML
    private ListView<String> listCursos;

    @FXML
    private Button btnCrearCurs;
    
    private final List<String> listIds = FXCollections.observableArrayList();
	
		
		@Override
		public void start(Stage primaryStage) throws IOException {
			
			//URI text file
			File uriFile = new File("uri");
			BufferedReader br = new BufferedReader(new FileReader(uriFile));
			
			while ((uriString = br.readLine()) != null) {
				System.out.println(uriString);
				uri = uriString;
			}

			//Connection to database
			try (MongoClient mongoClient = MongoClients.create(uri)) {
	             database = mongoClient.getDatabase("ClassVRroom");
	            try {
	                Bson command = new BsonDocument("ping", new BsonInt64(1));
	                Document commandResult = database.runCommand(command);
	                System.out.println("Connected successfully to server.");
	                
	                MongoCollection<Document> collection = database.getCollection("courses");
				    //Retrieving the documents
				    FindIterable<Document> iterDoc = collection.find();
//				    Iterator it = iterDoc.iterator();
				    MongoCursor<Document> cursor = collection.find().iterator();

				    while (cursor.hasNext()) {
				         System.out.println(cursor.next());
				    }
				    
//				    ObservableList<String> listOfIDS = new ObservableList();
				    while (cursor.hasNext()) {
				        listIds.add(cursor.next().getObjectId("_id").toString());
				        listCursos.setItems((ObservableList<String>) listIds);
				        listCursos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				    }
				    
				    

	            } catch (MongoException me) {
	                System.err.println("An error occurred while attempting to run a command: " + me);
	            }
	        }
	
			
			//Window creation			
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
	
	public static void createWindow() {
		
	}
			
	
}


