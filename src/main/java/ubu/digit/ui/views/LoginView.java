package ubu.digit.ui.views;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.common.io.BaseEncoding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.FirebaseService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import ubu.digit.ui.MainLayout;
import ubu.digit.util.ExternalProperties;
/**
 * Vista de inicio de sesión.
 * 
 * @author Javier de la Fuente Barrios
 */

@Route(value = "Login", layout = MainLayout.class)
@PageTitle("Login ")
public class LoginView extends VerticalLayout{

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "login";
	
	/**
	 * Login 
	 */
	private LoginForm login;
	
	/**
	 * Punto de entrada del SDKs de Firebase
	 */
	private FirebaseApp firebaseApp = null;
	
	/**
	 * Base de datos de Firestore 
	 */
	private Firestore db = null;
	
	/**
	 * Usuario loggeado
	 */
	private String user = "";
	
	/**
	 * Constructor donde se crea el login
	 */
	public LoginView() {
		getInstanceDatabase();

		addClassName("login-view");
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		setJustifyContentMode(JustifyContentMode.CENTER);
		setAlignItems(Alignment.CENTER);

		login = new LoginForm();
		login.setAction("Login");
		login.setI18n(createSpanishI18n());
		login.setForgotPasswordButtonVisible(false);
		login.addLoginListener(e -> {
			login.setEnabled(false);
		    boolean isAuthenticated = authenticate(e.getUsername(), e.getPassword());
		    if (isAuthenticated) {
		    	UI.getCurrent().navigate(UploadView.class);
		    } else {
		    	login.setEnabled(true);
		    	 login.setError(true);
		    }
		});
			
		add(login);
	}
	
	/**
	 * Cambiar el idioma de los mensajes que se muestran en el Login
	 * a Español
	 * @return LoginI18n
	 */
	private LoginI18n createSpanishI18n() {
	    final LoginI18n i18n = LoginI18n.createDefault();

	    i18n.setHeader(new LoginI18n.Header());
	    i18n.getHeader().setTitle("Gestor-TFG-2021");
	    i18n.getHeader().setDescription("Gestor TFG");
	    i18n.getForm().setUsername("Usuario");
	    i18n.getForm().setTitle("Iniciar Sesión");
	    i18n.getForm().setSubmit("Iniciar Sesión");
	    i18n.getForm().setPassword("Contraseña");
	    i18n.getForm().setForgotPassword("¿Olvidaste la contraseña?");
	    i18n.getErrorMessage().setTitle("Usuario/contraseña inválidos");
	    i18n.getErrorMessage()
	        .setMessage("Verifique su usuario y contraseña y vuelva a intentarlo.");
	    return i18n;
	}
	        
	/**
	 * Comprueba si el usuario y la contraseña introducidos son correctos.
	 * @param email
	 * @param password
	 * @return bool
	 */
	private Boolean authenticate(String email, String password){
		try {
			DocumentReference docRef = db.collection("Users").document("Data_Users");
			ApiFuture<DocumentSnapshot> users = docRef.get();

			DocumentSnapshot document = users.get();
			if (document.exists()) {
				if(document.getData().get(email).toString().equals(password)) {
					user = email;
					return true;
				}
				LOGGER.info("PASSWORD: " + document.getData().get(email));
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
	public boolean isUserLogin() {
		if(user != "") {
			return true;
		}
		return false;
	}
	
	public boolean logout() {
		return false;
	}
	
	/**
	 * Método singleton para obtener la conexión con la base de datos de Firebase
	 */
	private void getInstanceDatabase() {
		if (db == null) {
			db = getConectionFirectore();
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
			FirestoreOptions firestoreOptions =
				    FirestoreOptions.getDefaultInstance().toBuilder()
				        .setProjectId("gestor-tfg-2021")
				        .setCredentials(GoogleCredentials.getApplicationDefault())
				        .build();
			
				return firestoreOptions.getService();
				
		}catch(IOException e) {
			LOGGER.error("Opciones de configuración de Firestore invalidas : ", e);
		}
		return null;
	}
}
