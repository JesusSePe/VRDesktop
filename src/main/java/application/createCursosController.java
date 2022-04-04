package application;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.print.DocFlavor.URL;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import application.loginWindow_Controller;

public class createCursosController {
	
		public Stage primaryStage;
		public static Stage principal;
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
		public void start(Stage primaryStage) throws IOException, ParseException {
			
			
		}
		public void isClicked() throws Exception {
			if(!inputTitol.getText().isBlank() && !taDescripcio.getText().isBlank()) {
				//JSON parser object to parse read file
		        JSONParser jsonParser = new JSONParser();
				try (FileReader reader = new FileReader("classVRroom_OneCourse.json"))
		        {
		            //Read JSON file
		            obj = jsonParser.parse(reader);
		 
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
				
				System.out.println("entra");
				try (MongoClient mongoClient = MongoClients.create("mongodb+srv://m001-student:m001-student@sandbox.qczb2.mongodb.net/ClassVRroom?authSource=admin&replicaSet=Sandbox-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true")) {
		            database = mongoClient.getDatabase("ClassVRroom");
		            collectionCourses = database.getCollection("courses");
		           try {
		        	   Document test = Document.parse(String.valueOf(obj));
		        	   test.put("title",inputTitol.getText());
		        	   test.put("description",taDescripcio.getText() );

		        	   System.out.println(test);
		        	   collectionCourses.insertOne(test);
		        	   System.out.println("Creado");
		        	   
		        	   
		        	   FXMLLoader fxmlLoader = new FXMLLoader();
		        	   Parent root = FXMLLoader.load(getClass().getResource("loginWindow.fxml"));

		  	           Scene scene = new Scene(root);
		  	           Stage stage = new Stage();
		  	           stage.setScene(scene);
		  	           
		  	           
		  	         
		  	           loginWindow_Controller rc = fxmlLoader.getController();
		  	           stage.show();
		        	   
		        	   
		           } catch (MongoException me) {
		               System.err.println("An error occurred while attempting to run a command: " + me);
		           }
		       }
				
			}
			
		
			else {
				Alert a = new Alert(AlertType.WARNING,"Nothing can be left blank");
				a.show();
				
			}
	        
	    }
		

}
