package ubu.digit.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import  com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ubu.digit.security.Controller;
import ubu.digit.ui.views.LoginView;
import ubu.digit.ui.views.UploadView;
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
 * @author Diana Bringas Ochoa
 */
public class Footer extends VerticalLayout {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1285443082746553886L;
	
	/**
	 * Layout que contiene los layout verticales (columnas)
	 * de information y license
	 */
	HorizontalLayout conten;
	
	/**
	 * Layout correspondiente a la columna con la licencia, 
	 * fecha de actualización de los ficheros y acceso al login.
	 */
	VerticalLayout license;
	
	/**
	 * Layout con la información sobre los autores y tutores de la app
	 */
	VerticalLayout information;
	
	/**
	 * Nombre del fichero .csv  o .xls relacionado con la vista
	 */
	private String fileName;
	
	/**
	 * Controlador del acceso al moodle de UbuVirtual
	 */
	private static Controller CONTROLLER;
	
	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Footer.class.getName());

	/**
	 * Constructor.
	 */
	public Footer(String fileName) {
		this.fileName = fileName;
		conten = new HorizontalLayout();
		conten.addClassName("Footer-grid");
		
		H2 subtitle = new H2(INFORMACION);
		subtitle.addClassName(SUBTITLE_STYLE);
		
		//Se crea la instancia del controlador de acceso al moodle de UbuVirtual
		CONTROLLER = Controller.getInstance();
		
		addInformation();
		addLicense();
		conten.add(information, license);
		
		add(subtitle, conten);
	}

	/**
	 * Añade la información de los autores y tutores del proyecto.
	 */
	private void addInformation() {
		information = new VerticalLayout();
		information.setMargin(false);
		information.setSpacing(true);

		Text version1 = new Text("Versión 1.0 creada por Beatriz Zurera Martínez-Acitores");
		Anchor link1 = new Anchor("mailto:bzm0001@alu.ubu.es","bzm0001@alu.ubu.es");
		information.add(new HorizontalLayout(new Icon(VaadinIcon.ENVELOPE),link1));
		information.add(version1);
		
		Text version2 = new Text("Versión 2.0 creada por Javier de la Fuente Barrios");
		Anchor link2 = new Anchor("mailto:jfb0019@alu.ubu.es","jfb0019@alu.ubu.es");
		information.add(new HorizontalLayout(new Icon(VaadinIcon.ENVELOPE),link2));
		information.add(version2);
		
		Text version3 = new Text("Versión 3.0 creada por Diana Bringas Ochoa");
		Anchor link3 = new Anchor("mailto:dbo1001@alu.ubu.es","dbo1001@alu.ubu.es");
		information.add(new HorizontalLayout(new Icon(VaadinIcon.ENVELOPE),link3));
		information.add(version3);
		
		Text tutor1 = new Text("Tutorizado por Carlos López Nozal");
		Anchor linkT1 = new Anchor("mailto:clopezno@alu.ubu.es","clopezno@ubu.es");
		information.add(new HorizontalLayout(new Icon(VaadinIcon.ENVELOPE),linkT1));
		information.add(tutor1);
		
		Text tutor2 = new Text("Tutorizado por Alvar Arnaiz Gonzalez");
		Anchor linkT2 = new Anchor("mailto:alvarag@alu.ubu.es", "alvarag@ubu.es");
		information.add(new HorizontalLayout(new Icon(VaadinIcon.ENVELOPE),linkT2));
		information.add(tutor2);
		
		Text copyright = new Text("Copyright @ LSI");
		information.add(new HorizontalLayout(copyright));
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String lastModified = null;
		File file = new File(dir + fileName);
		if (file.exists()) {
			Date date = new Date(file.lastModified());
			lastModified = sdf.format(date);
		}
		return lastModified;
	}

	/**
	 * Añade la información de la licencia del proyecto, la fecha de actualización 
	 * de los ficheros de datos y el butón del acceso al Login.
	 */
	private void addLicense() {
		license = new VerticalLayout();
		license.setMargin(false);
		license.setSpacing(true);

		Text licenseText = new Text("This work is licensed under: ");
		Anchor ccLink = new Anchor("https://creativecommons.org/licenses/by/4.0/", "Creative Commons Attribution 4.0 International License.");
		
		license.add(licenseText, ccLink);
		
		if (fileName != null) {
			String lastModifiedCsv = getLastModified(fileName);
			String lastModifiedXls = getLastModified("BaseDeDatosTFGTFM.xls");
			license.add(new Label("Ultima actualización de " + fileName + " : " + lastModifiedCsv));
			license.add(new Label("Ultima actualización de BaseDeDatosTFGTFM.xls : " + lastModifiedXls));
		}

		Button login = new Button("Actualizar");
		login.addClickListener(e -> {	
			if(CONTROLLER.getLogin() != null) {
				UI.getCurrent().navigate(UploadView.class);
			}else {	
				UI.getCurrent().navigate(LoginView.class);
			}
		});
		license.add(login);
	}
}
