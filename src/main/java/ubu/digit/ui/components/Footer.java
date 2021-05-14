package ubu.digit.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import  com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ubu.digit.pesistence.SistInfDataXls;
import ubu.digit.ui.views.LoginView;
import ubu.digit.util.ExternalProperties;

import static ubu.digit.util.Constants.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	
	HorizontalLayout conten;
	
	VerticalLayout license;
	
	VerticalLayout information;
	
	/**
	 * Nombre del fichero .csv  o .xls relacionado con la vista
	 */
	private String fileName;
	
	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Footer.class.getName());


	/**
	 * Constructor.
	 * 
	 */
	public Footer(String fileName) {
		this.fileName = fileName;
		conten = new HorizontalLayout();
		conten.addClassName("Footer-grid");
		
		addInformation();
		addLicense();
		conten.add(information, license);
		
		add(conten);
		
	}

	/**
	 * Añade la información del proyecto.
	 */
	private void addInformation() {
		information = new VerticalLayout();
		information.setMargin(false);
		information.setSpacing(true);
		
		Label subtitle = new Label(INFORMACION);
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
		String path = this.getClass().getClassLoader().getResource("").getPath();
		String serverPath = path.substring(1, path.length()-17);
		LOGGER.info("Ruta Footer " + serverPath);
		
		ExternalProperties config = ExternalProperties.getInstance("/config.properties", false);
		String dirCsv = config.getSetting("dataIn");
		String dir = serverPath + dirCsv + "/";
		
		LOGGER.info("Ruta config.properties Footer " + dir);
		
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

		Image ccImage= new Image("./styles/img/cc.png","https://creativecommons.org/licenses/by/4.0/");
	
		Text licenseText = new Text("This work is licensed under a: ");
		Anchor ccLink = new Anchor("https://creativecommons.org/licenses/by/4.0/","Creative Commons Attribution 4.0 International License.");

		license.add(ccImage);
		license.add(licenseText);
		license.add(ccLink);
		
		if (fileName != null) {
			String lastModified = getLastModified(fileName);
			license.add(new Label("Ultima actualización: " + lastModified));
		}

		Button login = new Button("Actualizar");
		login.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
		license.add(login);
	}
}
