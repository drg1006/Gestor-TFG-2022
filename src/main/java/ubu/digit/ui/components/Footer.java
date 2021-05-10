package ubu.digit.ui.components;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import  com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.server.FontAwesome;

import ubu.digit.ui.views.InformationView;
import ubu.digit.ui.views.LoginView;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;


/**
 * Pié de página común a todas las vistas.
 * 
 * @author Javier de la Fuente Barrios
 *
 */
public class Footer extends HorizontalLayout {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1285443082746553886L;

	/**
	 * Contenedor del pié de página.
	 */
	private HorizontalLayout content;
	
	VerticalLayout license;
	
	VerticalLayout information;

	/**
	 * Nombre del fichero .csv relacionado con la vista
	 */
	private String fileName;

	/**
	 * Constructor.
	 * 
	 */
	public Footer() {
		content = new HorizontalLayout();
		content.addClassName("Footer-grid");
		//content.setSizeFull();
		//content.setWidth("100%");

		addInformation();
		addLicense();
		add(content);
		content.add(information, license);
		
	}

	/**
	 * Añade la información del proyecto.
	 */
	private void addInformation() {
		information = new VerticalLayout();
		information.setMargin(false);
		information.setSpacing(true);

		H1 subtitle = new H1(INFORMACION);
		subtitle.addClassName(SUBTITLE_STYLE);
		information.add(subtitle);
		
		Text version2 = new Text("Versión 2.0 creada por Javier de la Fuente Barrios");
		Anchor link2 = new Anchor("mailto:jfb0019@alu.ubu.es","jfb0019@alu.ubu.es");
		information.add(version2);
		information.add(link2);
		
		Text version1 = new Text("Versión 1.0 creada por Beatriz Zurera Martínez-Acitores");
		Anchor link1 = new Anchor("bzm0001@alu.ubu.es","mailto:bzm0001@alu.ubu.es");
		information.add(version1);
		information.add(link1);
		
		Text tutor = new Text("Tutorizado por Carlos López Nozal");
		Anchor linkT = new Anchor("clopezno@ubu.es", "mailto:clopezno@alu.ubu.es");
		information.add(tutor);
		information.add(linkT);
		
		Text copyright = new Text("Copyright @ LSI");
		information.add(copyright);
	}

	/**
	 * Obtiene la fecha de última modificación del fichero asociado a la vista.
	 * 
	 * @param fileName
	 *            nombre del fichero
	 * @return fecha de última modificación del fichero
	 */
	private String getLastModified(String fileName) {
		//String serverPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		String serverPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
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
		license = new VerticalLayout();
		license.setMargin(false);
		license.setSpacing(true);

		Image ccImage= new Image("frontend/img/cc.png", "https://creativecommons.org/licenses/by/4.0/");
		
		Text licenseText = new Text("This work is licensed under a: ");
		Anchor ccLink = new Anchor("https://creativecommons.org/licenses/by/4.0/","Creative Commons Attribution 4.0 International License.");

		license.add(ccImage);
		license.add(licenseText);
		license.add(ccLink);

		if (fileName != null) {
			//String lastModified = getLastModified(fileName);
			license.add(new Text("Ultima actualización: " + "null")); //lastModified //TODO:
		}

		Button login = new Button("Actualizar");
		login.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
		license.add(login);
	}
}
