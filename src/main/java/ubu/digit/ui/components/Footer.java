package ubu.digit.ui.components;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import ubu.digit.ui.views.UploadCsvView;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

/**
 * Pié de página común a todas las vistas.
 * 
 * @author Javier de la Fuente Barrios
 *
 */
public class Footer extends CustomComponent {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1285443082746553886L;

	/**
	 * Contenedor del pié de página.
	 */
	private GridLayout content;

	/**
	 * Nombre del fichero .csv relacionado con la vista
	 */
	private String fileName;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            nombre del fichero .csv relacionado con la vista.
	 */
	public Footer(String fileName) {
		this.fileName = fileName;
		content = new GridLayout(2, 1);
		content.setMargin(false);
		content.setSpacing(true);
		content.setWidth("100%");

		addInformation();
		addLicense();

		content.setColumnExpandRatio(0, 4);
		content.setColumnExpandRatio(1, 4);

		setCompositionRoot(content);
	}

	/**
	 * Añade la información del proyecto.
	 */
	private void addInformation() {
		VerticalLayout information = new VerticalLayout();
		information.setMargin(false);
		information.setSpacing(true);

		Label subtitle = new Label(INFORMACION);
		subtitle.setStyleName(SUBTITLE_STYLE);

		Label version2 = new Label("Versión 2.0 creada por Javier de la Fuente Barrios");
		Link link2 = new Link("jfb0019@alu.ubu.es", new ExternalResource("mailto:jfb0019@alu.ubu.es"));
		link2.setIcon(FontAwesome.ENVELOPE);

		Label version1 = new Label("Versión 1.0 creada por Beatriz Zurera Martínez-Acitores");
		Link link1 = new Link("bzm0001@alu.ubu.es", new ExternalResource("mailto:bzm0001@alu.ubu.es"));
		link1.setIcon(FontAwesome.ENVELOPE);

		Label tutor = new Label("Tutorizado por Carlos López Nozal");
		Link linkT = new Link("clopezno@ubu.es", new ExternalResource("mailto:clopezno@alu.ubu.es"));
		linkT.setIcon(FontAwesome.ENVELOPE);

		Label copyright = new Label("Copyright @ LSI");

		information.addComponents(subtitle, version2, link2, version1, link1, tutor, linkT, copyright);
		content.addComponent(information);
	}

	/**
	 * Obtiene la fecha de última modificación del fichero asociado a la vista.
	 * 
	 * @param fileName
	 *            nombre del fichero
	 * @return fecha de última modificación del fichero
	 */
	private String getLastModified(String fileName) {
		String serverPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		ExternalProperties config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);
		String dirCsv = config.getSetting("dataIn");
		String dir = serverPath + dirCsv + "/";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String lastModified = null;
		File file = new File(dir + fileName);
		if (file.exists()) {
			Date date = new Date(file.lastModified());
			lastModified = sdf.format(date);
		}
		return lastModified;
	}

	/**
	 * Añade la información de la licencia del proyecto.
	 */
	private void addLicense() {
		VerticalLayout license = new VerticalLayout();
		license.setMargin(false);
		license.setSpacing(true);

		Link ccImage = new Link(null, new ExternalResource("https://creativecommons.org/licenses/by/4.0/"));
		ccImage.setIcon(new ThemeResource("img/cc.png"));

		Label licenseText = new Label("This work is licensed under a: ");
		Link ccLink = new Link("Creative Commons Attribution 4.0 International License.",
				new ExternalResource("https://creativecommons.org/licenses/by/4.0/"));

		license.addComponents(ccImage, licenseText, ccLink);

		if (fileName != null) {
			String lastModified = getLastModified(fileName);
			license.addComponent(new Label("Ultima actualización: " + lastModified));
		}

		Button login = new Button("Actualizar");
		login.addClickListener(new LoginClickListener());
		license.addComponent(login);
		content.addComponent(license);
	}

	/**
	 * Listener para el botón que accede a la pantalla de administración.
	 * 
	 * @author Javier de la Fuente Barrios
	 */
	private final class LoginClickListener implements Button.ClickListener {
		private static final long serialVersionUID = -861788921350517735L;
	
		/**
		 * Acción a realizar al recibir el evento de un click.
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			 UI.getCurrent().getNavigator().navigateTo(UploadCsvView.VIEW_NAME);				
		}
	}
}
