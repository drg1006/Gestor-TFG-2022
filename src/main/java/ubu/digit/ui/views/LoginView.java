package ubu.digit.ui.views;

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
	 * Constructor.
	 */
	public LoginView() {
		config = ExternalProperties.getInstance("/config.properties", false);
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
		loginButton.addClickListener(e -> UI.getCurrent().navigate(UploadView.class));
		
		VerticalLayout fields = new VerticalLayout();
		fields.add(userField, passwordField, loginButton);
		fields.add("Inicie sesión para continuar.");
		fields.setSpacing(true);
		fields.setMargin(true);
		fields.setSizeUndefined();
		add(fields);
	}
}
