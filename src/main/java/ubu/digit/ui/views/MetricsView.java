package ubu.digit.ui.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubu.digit.ui.MainLayout;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

/**
 * Vista de métricas.
 * 
 * @author Javier de la Fuente Barrios
 */
@Route(value = "Metrics", layout = MainLayout.class)
@PageTitle("Métricas")
public class MetricsView extends VerticalLayout{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1110300353177565418L;

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "metrics";

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Constructor.
	 */
	public MetricsView() {
		config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);
		setMargin(true);
		setSpacing(true);
		
		NavigationBar navBar = new NavigationBar();
		add(navBar);
		
		H1 metricsTitle = new H1(METRICAS);
		metricsTitle.addClassName(TITLE_STYLE);
		add(metricsTitle);

		//addSonarImageLink();
		
		Footer footer = new Footer();
		add(footer);
	}

	/**
	 * Añade la imagen y el link de SonarQube.
	 */
	private void addSonarImageLink(){
		
		//String urlSonar = config.getSetting("urlSonar");
		//Anchor sonarLink = new ThemeResource("img/tfgsonar.png"); //(null, new ExternalResource("https://" + urlSonar));
		//sonarLink.setHref("https://" + urlSonar);
		
		//Image sonarLink = new Image("./styles/img/tfgsonar.png", "https://" + urlSonar);
		//add(sonarLink);
	}
}
