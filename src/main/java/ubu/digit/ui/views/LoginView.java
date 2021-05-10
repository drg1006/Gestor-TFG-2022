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
	 * Constructor.
	 */
	public LoginView() {
		config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);
		setMargin(true);
		setSpacing(true);

		userField = new TextField("Nombre de usuario:");
		userField.setWidth("300px");
		userField.setRequired(true);
		//userField.addValidator(new StringLengthValidator("El nombre de usuario introducido no es válido", 10, 20, false));

		passwordField = new PasswordField("Contraseña:");
		passwordField.setWidth("300px");
		passwordField.setRequired(true);
		//passwordField.addValidator(new AbstractValidator<String>("La contraseña introducida no es válida") {

			//private static final long serialVersionUID = 5378729929183088531L;

			/*@Override
			protected boolean isValidValue(String value) {
				// At least 8 characters long and contains one digit
				if (value != null && (value.length() < 8 || !value.matches(".*\\d.*"))) {
					return false;
				}
				return true;
			}*/

			/*@Override
			public Class<String> getType() {
				return String.class;
			}*/
		//});

		passwordField.setValue("");
		//passwordField.setNullRepresentation("");

		loginButton = new Button("Login");
		loginButton.addClickListener(e -> UI.getCurrent().navigate(UploadView.class));
		//loginButton.setClickShortcut(KeyCode.ENTER);
		
		VerticalLayout fields = new VerticalLayout();
		fields.add(userField, passwordField, loginButton);
		fields.setClassName("Inicie sesión para continuar.");
		//fields.setCaption("Inicie sesión para continuar.");
		fields.setSpacing(true);
		fields.setMargin(true);
		fields.setSizeUndefined();

		add(fields);
		//setComponentAlignment(fields, Alignment.CENTER); //MIDDLE_CENTER
	}

	/**
	 * Listener para el botón de inicio de sesión.
	 * 
	 * Compara el valor de los campos con los del fichero de configuración. 
	 * Si coinciden, crea una sesión y cambia a la vista de administración. 
	 * Sino, muestra un mensaje de error.
	 * 
	 * @author Javier de la Fuente Barrios
	 */
	/*private class LoginClickListener implements Button.ClickListener {

		private static final long serialVersionUID = 4149064591058379344L;

		@Override
		public void buttonClick(ClickEvent event) {
			if (!userField.isValid() || !passwordField.isValid()) {
				Notification.show("Error",
						"El nombre de usuario y/o la contraseña no son válidos. Reviselos e inténtelo de nuevo.",
						Notification.Type.WARNING_MESSAGE);
			} else {
				String username = userField.getValue();
				String password = passwordField.getValue();

				boolean isValid = username.equals(config.getSetting("username"))
						&& password.equals(config.getSetting("password"));

				if (isValid) {
					getSession().setAttribute("user", username);
					getUI().getNavigator().navigateTo(UploadView.VIEW_NAME);
					Notification.show("Has iniciado sesión satisfactoriamente.");

				} else {
					Notification.show("Error",
							"El nombre de usuario y/o la contraseña no son correctos. Reviselos e inténtelo de nuevo.",
							Notification.Type.WARNING_MESSAGE);
					passwordField.setValue(null);
					passwordField.focus();
				}
			}
		}
	}*/

}
