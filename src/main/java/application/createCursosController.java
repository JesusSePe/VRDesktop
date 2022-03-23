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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class createCursosController {
	
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

	    
		public void start(Stage primaryStage) throws IOException, ParseException {
			
			
		}
	    
//	    json element json object
	    @FXML
	    void initialize() {
	    	

	    }

	public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
		
		readUri();
		
		try (MongoClient mongoClient = MongoClients.create(uri)) {
            database = mongoClient.getDatabase("ClassVRroom");
           try {
               Bson command = new BsonDocument("ping", new BsonInt64(1));
               Document commandResult = database.runCommand(command);		
               System.out.println("Connected successfully to server.");
//			   JSONParser parser = new JSONParser();
			   
			   Gson gson = new Gson();
//			   Object object = gson.fromJson(new FileReader("classVRroom_OneCourse.json"), Object.class);
//			  
//			   JsonElement json = gson.toJsonTree(object);
//			   JsonObject jsonObj = (JsonObject) json;
//			   
//			   JsonObject jsonTitle = (JsonObject) jsonObj.get("title");
//			   System.out.println(jsonTitle);
			   
//			   JsonPrimitive prim = (JsonPrimitive) jsonObj.get("title");
//			   JsonPrimitive prim2 = (JsonPrimitive) jsonObj.get("description");
			   
			   JsonReader reader = new JsonReader(new FileReader("classVRroom_OneCourse.json"));
//			   JsonObject data = gson.fromJson(reader, JsonObject.class);
			   
			   JSONParser jsonParser = new JSONParser();
			   File file = new File("classVRroom_OneCourse.json");
			   Object object = jsonParser.parse(new FileReader(file));
			   jsonObject = (JSONObject) object;
			   parseJson(jsonObject);
			   
//			   JsonElement element = gson.fromJson (jsonStr, JsonElement.class);
//			   JsonObject jsonObj = element.getAsJsonObject();
			   
			   //STRING A JSON OBJECT Y LUEGO MODIFICAR EL NODE EN EL JSON Y SUBIR EL JSON 

			   
//			   JsonObject titleOb = jsonObj.getAsJsonObject("title");
//			   JsonObject descrOb = jsonObj.getAsJsonObject("description");
//			   JsonPrimitive titlePrim = contentRatingsList.get(i).getAsJsonObject().getAsJsonPrimitive("r$scheme");

//			   System.out.println(prim);
//			   System.out.println(prim2);
//			   
//			   collectionCourses = database.getCollection("courses");

			   
           } catch (MongoException me) {
               System.err.println("An error occurred while attempting to run a command: " + me);
           }
       }

	}
	
	public static void readUri() {
		try {
			File uriFile = new File("uri");
		BufferedReader br = new BufferedReader(new FileReader(uriFile));
			
		while ((uriString = br.readLine()) != null) {
			System.out.println(uriString);
			uri = uriString;
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public static void getArray(Object object2) throws ParseException {

	    JSONArray jsonArr = (JSONArray) object2;

	    for (int k = 0; k < jsonArr.size(); k++) {

	        if (jsonArr.get(k) instanceof JSONObject) {
	            parseJson((JSONObject) jsonArr.get(k));
	        } else {
	            System.out.println(jsonArr.get(k));
	        }

	    }
	}

	public static void parseJson(JSONObject jsonObject) throws ParseException {

	    Set<Object> set = jsonObject.keySet();
	    Iterator<Object> iterator = set.iterator();
	    while (iterator.hasNext()) {
	        Object obj = iterator.next();
	        if (jsonObject.get(obj) instanceof JSONArray) {
	            System.out.println(obj.toString());
	            getArray(jsonObject.get(obj));
	        } else {
	            if (jsonObject.get(obj) instanceof JSONObject) {
	                parseJson((JSONObject) jsonObject.get(obj));
	            } else {
	                System.out.println(obj.toString() + "\t"
	                        + jsonObject.get(obj));
	            }
	        }
	    }
	
	}

}
