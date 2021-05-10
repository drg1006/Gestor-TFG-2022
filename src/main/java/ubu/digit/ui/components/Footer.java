package ubu.digit.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import  com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ubu.digit.ui.views.LoginView;
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
	
	HorizontalLayout conten;
	
	VerticalLayout license;
	
	VerticalLayout information;

	/**
	 * Constructor.
	 * 
	 */
	public Footer() {
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

		Button login = new Button("Actualizar");
		login.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
		license.add(login);
	}
}
