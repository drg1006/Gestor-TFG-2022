package ubu.digit.ui.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.AbstractValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import ubu.digit.ui.MainLayout;
import ubu.digit.ui.components.NavigationBar;
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
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7873783760565727604L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "login";

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Campo de texto para el nombre de usuario.
	 */
	private TextField userField;

	/**
	 * Campo para la contraseña.
	 */
	private PasswordField passwordField;

	/**
	 * Botón para iniciar sesión.
	 */
	private Button loginButton;
	
	/**
	 * Base de datos de Firestore
	 */
	private Firestore db;

	/**
	 * Constructor.
	 */
	public LoginView() {
		config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);
		setMargin(true);
		setSpacing(true);

		userField = new TextField("Nombre de usuario:");
		userField.setWidth("300px");
		userField.setRequired(true);

		passwordField = new PasswordField("Contraseña:");
		passwordField.setWidth("300px");
		passwordField.setRequired(true);
		passwordField.setValue("");
		
		loginButton = new Button("Login");
		loginButton.addClickListener(e -> new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        	 String username = userField.getValue();
             String password = passwordField.getValue();
             if (username.isEmpty()) {
                 JOptionPane.showMessageDialog(null, this, "Inserte un usuario valido", 0);
             } else if (password.isEmpty()) {
                 JOptionPane.showMessageDialog(null, this, "Inserte una contraseña valida", 0);
             } else if (!username.equals(getDataLogin("email"))) {
                 JOptionPane.showMessageDialog(null, this, "Email incorrecto", 0);
             } else if (!password.equals(getDataLogin("password"))) {
                 JOptionPane.showMessageDialog(null, this, "Contraseña incorrecta", 0);
             } else {
                 try {
                     getUserByEmail(username, password);
                 } catch (InterruptedException | ExecutionException ex) {
                	 LOGGER.error(LoginView.class.getName()+ " "+ ex);
                 }
             }
            }
        });
		
		//UI.getCurrent().navigate(UploadView.class));
		
		VerticalLayout fields = new VerticalLayout();
		fields.add(userField, passwordField, loginButton);
		fields.add("Inicie sesión para continuar.");
		fields.setSpacing(true);
		fields.setMargin(true);
		fields.setSizeUndefined();
		fields.setAlignSelf(Alignment.CENTER);
		add(fields);
	}
	
	public void getUserByEmail(String email, String password) throws InterruptedException, ExecutionException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
        // See the UserRecord reference doc for the contents of userRecord.
        System.out.println("Successfully fetched user data: " + userRecord.getEmail());
        JOptionPane.showMessageDialog(null, this, "Anda masuk sebagai " + userRecord.getDisplayName(), 0);
        simpanData(userRecord.getEmail(), password);
        setVisible(false);
    }
	
	private void simpanData(String usuario, String password) {
        try {
            FileOutputStream output = new FileOutputStream("datalogin.ini");
            new PrintStream(output).print("email = " + usuario + "\n");
            new PrintStream(output).print("password = " + password);
            try {
                output.close();
            } catch (IOException ex) {
            	LOGGER.error(LoginView.class.getName()+ " "+ ex);
            }
        } catch (FileNotFoundException ex) {
        	LOGGER.error(LoginView.class.getName()+ " "+ ex);
        }
    }

    private String getDataLogin(String saldo1) {
        String value = null;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("datalogin.ini"));
            value = properties.getProperty(saldo1);
        } catch (IOException ex) {
        	LOGGER.error(LoginView.class.getName()+ " "+ ex);
        }
        return value;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        	LOGGER.error(LoginView.class.getName()+ " "+ ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new LoginView().setVisible(true);
        });

        // Initialize Firebase
        try {
            FileInputStream serviceAccount = new FileInputStream("touri-dinacom-firebase-adminsdk-aozcy-920c20d745.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            System.out.println("ERROR: invalid service account credentials. See README.");
            System.out.println(e.getMessage());

            System.exit(1);
        }

    }
}
