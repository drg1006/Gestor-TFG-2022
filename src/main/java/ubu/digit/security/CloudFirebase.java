package ubu.digit.security;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase donde se realiza la conexión con Firestore.
 * 
 * @author Diana Bringas Ochoa
 */
public class CloudFirebase {
	
	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CloudFirebase.class.getName());
	
	/**
	 * Base de Datos de Firestore
	 * Se almacena en colecciones.
	 */
	private Firestore db;
	
	  /**
	   * Se inicializa Firestore usando el proyecto ID por defecto.
	   */
	  public CloudFirebase() {
	    Firestore db = FirestoreOptions.getDefaultInstance().getService();
	    this.db = db;
	  }

	  /**
	   * Se inicializa Firestore usando un proyecto ID determinado
	   * @param projectId
	   * @throws Exception
	   */
	  public CloudFirebase(String projectId) {
 		try {
 			FirestoreOptions firestoreOptions =
 			        FirestoreOptions.getDefaultInstance().toBuilder()
 			            .setProjectId(projectId)
 			            .setCredentials(GoogleCredentials.getApplicationDefault())
 			            .build();
 			    Firestore db = firestoreOptions.getService();
 			    this.db = db;
 		} catch (IOException e) {
 			LOGGER.error("Error al inicializar Google Cloud Platform "+e);
 		}
	  }

	  Firestore getDb() {
	    return db;
	  }

	  /**
	   * Add named test documents with fields first, last, middle (optional), born.
	   *
	   * @param docName document name
	   */
	  void addDocument(String docName) throws Exception {
	    switch (docName) {
	      case "alovelace": {
	        DocumentReference docRef = db.collection("users").document("alovelace");
	        // Add document data  with id "alovelace" using a hashmap
	        Map<String, Object> data = new HashMap<>();
	        data.put("first", "Ada");
	        data.put("last", "Lovelace");
	        data.put("born", 1815);
	        //asynchronously write data
	        ApiFuture<WriteResult> result = docRef.set(data);
	        // ...
	        // result.get() blocks on response
	        System.out.println("Update time : " + result.get().getUpdateTime());
	        break;
	      }
	      case "aturing": {
	        DocumentReference docRef = db.collection("users").document("aturing");
	        // Add document data with an additional field ("middle")
	        Map<String, Object> data = new HashMap<>();
	        data.put("first", "Alan");
	        data.put("middle", "Mathison");
	        data.put("last", "Turing");
	        data.put("born", 1912);

	        ApiFuture<WriteResult> result = docRef.set(data);
	        System.out.println("Update time : " + result.get().getUpdateTime());
	        break;
	      }
	      case "cbabbage": {
	        DocumentReference docRef = db.collection("users").document("cbabbage");
	        Map<String, Object> data =
	            new ImmutableMap.Builder<String, Object>()
	                .put("first", "Charles")
	                .put("last", "Babbage")
	                .put("born", 1791)
	                .build();
	        ApiFuture<WriteResult> result = docRef.set(data);
	        System.out.println("Update time : " + result.get().getUpdateTime());
	        break;
	      }
	      default:
	    }
	  }

	  void runQuery() throws Exception {
	    // asynchronously query for all users born before 1900
	    ApiFuture<QuerySnapshot> query =
	        db.collection("users").whereLessThan("born", 1900).get();
	    // ...
	    // query.get() blocks on response
	    QuerySnapshot querySnapshot = query.get();
	    List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
	    for (QueryDocumentSnapshot document : documents) {
	      System.out.println("User: " + document.getId());
	      System.out.println("First: " + document.getString("first"));
	      if (document.contains("middle")) {
	        System.out.println("Middle: " + document.getString("middle"));
	      }
	      System.out.println("Last: " + document.getString("last"));
	      System.out.println("Born: " + document.getLong("born"));
	    }
	  }

	  /**
	   * Leer datos de Cloud Firestore 
	   * Ver en Firebase console (https://console.firebase.google.com/project/_/firestore/data?hl=es)
	   *  Se puede recuperar toda la colección con get.
	   * @throws Exception
	   */
	  void retrieveAllDocuments() throws Exception {
	    // asynchronously retrieve all users
	    ApiFuture<QuerySnapshot> query = db.collection("users").get();
	    // ...
	    // query.get() blocks on response
	    QuerySnapshot querySnapshot = query.get();
	    List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
	    for (QueryDocumentSnapshot document : documents) {
	      System.out.println("User: " + document.getId());
	      System.out.println("First: " + document.getString("first"));
	      if (document.contains("middle")) {
	        System.out.println("Middle: " + document.getString("middle"));
	      }
	      System.out.println("Last: " + document.getString("last"));
	      System.out.println("Born: " + document.getLong("born"));
	    }
	  }

	  void run() throws Exception {
	    String[] docNames = {"alovelace", "aturing", "cbabbage"};

	    // Adding document 1
	    System.out.println("########## Adding document 1 ##########");
	    addDocument(docNames[0]);

	    // Adding document 2
	    System.out.println("########## Adding document 2 ##########");
	    addDocument(docNames[1]);

	    // Adding document 3
	    System.out.println("########## Adding document 3 ##########");
	    addDocument(docNames[2]);

	    // retrieve all users born before 1900
	    System.out.println("########## users born before 1900 ##########");
	    runQuery();

	    // retrieve all users
	    System.out.println("########## All users ##########");
	    retrieveAllDocuments();
	    System.out.println("###################################");
	  }

	  /**
	   * Un ejemplo de aplicación de Firestore
	   *
	   * @param args firestore-project-id (optional)
	   */
	  public static void main(String[] args) throws Exception {
	    // default project is will be used if project-id argument is not available
	    String projectId = (args.length == 0) ? null : args[0];
	    CloudFirebase quickStart = (projectId != null) ? new CloudFirebase(projectId) : new CloudFirebase();
	    quickStart.run();
	    quickStart.close();
	  }

	  /** Closes the gRPC channels associated with this instance and frees up their resources. */
	  void close() throws Exception {
	    db.close();
	  }
}