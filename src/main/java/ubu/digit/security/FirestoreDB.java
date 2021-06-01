package ubu.digit.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import java.io.IOException;

/**
 * Clase de conexión y administración de Firestore 
 * 
 * @author Diana Bringas Ochoa
 */
public class FirestoreDB {

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FirestoreDB.class.getName());
	
	/**
	 * Conexión con la base de datos de Firestore 
	 */
	private Firestore connectionDB = null;
	
	/**
	 * Instancia de la clase FirestoreDB
	 */
	private static FirestoreDB instance = null;
	
	/**
	 * Usuario loggeado
	 */
	private static String user = "";
    
    /**
	 * Constructor que inicia la conexión con la Firestore.
	 */
	private FirestoreDB() {
		this.connectionDB = this.getConectionFirectore();
	}

	/**
	 * Método singleton para obtener la instancia de FirestoreDB
	 */
	public static FirestoreDB getInstance(){
		if (instance == null) {
			instance = new FirestoreDB();
		}
		return instance;
	}
	
    /**
	 * Comprueba si el usuario y la contraseña introducidos son correctos.
	 * @param email
	 * @param password
	 * @return bool
	 */
	public Boolean authenticate(String email, String password){
		try {
			DocumentReference docRef = connectionDB.collection("Users").document("Data_Users");
			ApiFuture<DocumentSnapshot> users = docRef.get();

			DocumentSnapshot document = users.get();
			if (document.exists()) {
				if(document.getData().get(email).toString().equals(password)) {
					LOGGER.info("Usuario " + document.getData().get(email) + " autentificado.");
					user = email;
					return true;
				}
			} else {
				LOGGER.error("No existe ese documento en la base de datos");
			}
		}catch(Exception e) {
			LOGGER.error("Error al extraer la información de la base de datos : ", e);
		}
		return false;
	}
	
	/**
	 * Verifica si se ha iniciado sesión
	 * @return bool
	 */
	public static boolean isUserLogin() {
		if(user != "") {
			return true;
		}
		return false;
	}
	
	/**
	 * Cerrar sesión del usuario actual
	 */
	public static void logout() {
		if(user != "") {
			user = "";
		}
	}
	
	/**
	 * Se establece la conexión con la base de datos de Firebase, Firestore.
	 * A través de la cual se obtendrán los datos de los usuarios.
	 * 
	 * @return base de datos Firestore
	 */
	private Firestore getConectionFirectore() {
		try {
			/*String path = this.getClass().getClassLoader().getResource("").getPath();
			String serverPath = path.substring(0, path.length()-17);
			
			InputStream serviceAccount = new FileInputStream(serverPath + "/service-account-file.json");
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);*/
			
			FirestoreOptions firestoreOptions =
				    FirestoreOptions.getDefaultInstance().toBuilder()
				        .setProjectId("gestor-tfg-2021")
				        .setCredentials(GoogleCredentials.getApplicationDefault())
				        .build();
			
			LOGGER.info("Realizada la conexión con Firestore ");
			return firestoreOptions.getService();
				
		}catch(IOException e) {
			LOGGER.error("Opciones de configuración de Firestore invalidas : ", e);
		}
		return null;
	}
}

