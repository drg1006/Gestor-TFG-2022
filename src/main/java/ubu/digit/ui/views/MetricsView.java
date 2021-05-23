package ubu.digit.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.ui.MainLayout;
import ubu.digit.ui.components.Footer;
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
		config = ExternalProperties.getInstance("/config.properties", false);
		setMargin(true);
		setSpacing(true);
		
		H1 metricsTitle = new H1(METRICAS);
		metricsTitle.addClassName(TITLE_STYLE);
		add(metricsTitle);
		
		Footer footer = new Footer(null);
		add(footer);
	}
}
