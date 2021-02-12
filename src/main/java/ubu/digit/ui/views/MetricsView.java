package ubu.digit.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

/**
 * Vista de métricas.
 * 
 * @author Javier de la Fuente Barrios
 */
public class MetricsView extends VerticalLayout implements View {
	

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
		addComponent(navBar);

		addSonarImageLink();
		
		Footer footer = new Footer(null);
		addComponent(footer);
	}

	/**
	 * Añade la imagen y el link de SonarQube.
	 */
	private void addSonarImageLink(){
		Label metricsTitle = new Label(METRICAS);
		metricsTitle.setStyleName(TITLE_STYLE);
		addComponent(metricsTitle);
		
		String urlSonar = config.getSetting("urlSonar");
		Link sonarLink = new Link(null, new ExternalResource("https://" + urlSonar));
		sonarLink.setIcon(new ThemeResource("img/tfgsonar.png"));
		sonarLink.setTargetName("_blank");
		addComponent(sonarLink);
	}

	/**
	 * La vista se inicializa en el constructor.
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// Se inicializa el contenido de la vista en el constructor
	}
}
