package ubu.digit.ui.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Link;

import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

/**
 * Vista de métricas.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 * 
 */
@Route(value = "Metrics")
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
		config = ExternalProperties.getInstance("/config.properties", false);
		setMargin(true);
		setSpacing(true);
		
		NavigationBar bat = new NavigationBar();
		add(bat);
		
		H1 metricsTitle = new H1(METRICAS);
		metricsTitle.addClassName(TITLE_STYLE);
		add(metricsTitle);
		
		addSonarCloudLink();
		
		Footer footer = new Footer(null);
		add(footer);
	}
	
	/**
	 * Añadir análisis de la calidad del código de los proyectos realizados 
	 * con SonarCloud.
	 */
	private void addSonarCloudLink(){
		String urlSonar = config.getSetting("urlSonar");

		IFrame sonarCloud = new IFrame("https://sonarcloud.io/organizations/dbo1001/projects");
		sonarCloud.setWidth(85, Unit.PERCENTAGE);
		sonarCloud.setHeight(500, Unit.PIXELS);
		//sonarCloud.setAllow("encrypted-media; gyroscope; picture-in-picture");
		sonarCloud.getElement().setAttribute("frameborder", "0");
		sonarCloud.getElement().setAttribute("X-Frame-Options", "*");
		add(sonarCloud);
	}

}
