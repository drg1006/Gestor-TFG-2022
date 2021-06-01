package ubu.digit.ui.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ubu.digit.security.FirestoreDB;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;

/**
 * Vista de inicio de sesión.
 * 
 * @author Diana Bringas Ochoa
 */
@Route(value = "Login")
@PageTitle("Login ")
public class LoginView extends VerticalLayout{

	private static final long serialVersionUID = 1L;

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
	 * Instancia de la Base de datos de Firestore 
	 */
	private static FirestoreDB firestore = null;
	
	/**
	 * Constructor donde se crea el login
	 */
	public LoginView() {

		addClassName("login-view");
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		NavigationBar bat = new NavigationBar();
		add(bat);
		
		/*setJustifyContentMode(JustifyContentMode.CENTER);
		setAlignItems(Alignment.CENTER);*/

		firestore = FirestoreDB.getInstance();
		
		login = new LoginForm();
		login.setI18n(createSpanishI18n());
		login.setForgotPasswordButtonVisible(false);
		login.addLoginListener(e -> {
			login.setEnabled(false);
			LOGGER.info("Comprobando autentificación del usuario... ");
		    boolean isAuthenticated = firestore.authenticate(e.getUsername(), e.getPassword());
		    if (isAuthenticated) {
		    	UI.getCurrent().navigate(UploadView.class);
		    } else {
		    	login.setEnabled(true);
		    	login.setError(true);
		    }
		});
		add(login);
		Footer footer = new Footer(null);
		add(footer);
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

}
