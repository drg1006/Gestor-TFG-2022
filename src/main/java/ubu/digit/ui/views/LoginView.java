package ubu.digit.ui.views;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;
/**
 * Vista de inicio de sesión.
 * 
 * @author Javier de la Fuente Barrios
 */
public class LoginView extends VerticalLayout implements View {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7873783760565727604L;

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

		NavigationBar navBar = new NavigationBar();
		addComponent(navBar);

		userField = new TextField("Nombre de usuario:");
		userField.setWidth("300px");
		userField.setRequired(true);
		userField.addValidator(new StringLengthValidator("El nombre de usuario introducido no es válido", 10, 20, false));

		passwordField = new PasswordField("Contraseña:");
		passwordField.setWidth("300px");
		passwordField.setRequired(true);
		passwordField.addValidator(new AbstractValidator<String>("La contraseña introducida no es válida") {

			private static final long serialVersionUID = 5378729929183088531L;

			@Override
			protected boolean isValidValue(String value) {
				// At least 8 characters long and contains one digit
				if (value != null && (value.length() < 8 || !value.matches(".*\\d.*"))) {
					return false;
				}
				return true;
			}

			@Override
			public Class<String> getType() {
				return String.class;
			}
		});

		passwordField.setValue("");
		passwordField.setNullRepresentation("");

		loginButton = new Button("Login");
		loginButton.addClickListener(new LoginClickListener());
		loginButton.setClickShortcut(KeyCode.ENTER);
		
		VerticalLayout fields = new VerticalLayout();
		fields.addComponents(userField, passwordField, loginButton);
		fields.setCaption("Inicie sesión para continuar.");
		fields.setSpacing(true);
		fields.setMargin(true);
		fields.setSizeUndefined();

		addComponent(fields);
		setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
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
	private class LoginClickListener implements Button.ClickListener {

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
					getUI().getNavigator().navigateTo(UploadCsvView.VIEW_NAME);
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
	}

	/**
	 * La vista se inicializa en el constructor.
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		userField.focus();
	}

}
