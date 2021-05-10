package ubu.digit.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;

/**
 * Es el punto de entrada de la aplicación.
 */
@Route("")
@CssImport("./styles/shared-styles.css")
public class MainLayout extends VerticalLayout implements RouterLayout{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -4568743602891945769L;
	
	
	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
     * Construct a new Vaadin view.
     */
	public MainLayout() {
		NavigationBar bat = new NavigationBar();
		add(bat);
	}

		
}  
