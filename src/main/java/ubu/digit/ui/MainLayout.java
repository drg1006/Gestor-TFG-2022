package ubu.digit.ui;

import static ubu.digit.util.Constants.DESCRIPCION_PROYECTOS;
import static ubu.digit.util.Constants.TITLE_STYLE;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataCsv;
import ubu.digit.pesistence.SistInfDataFactory;
import ubu.digit.ui.beans.ActiveProject;
import ubu.digit.ui.beans.HistoricProject;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.views.ActiveProjectsView;
import ubu.digit.ui.views.HistoricProjectsView;
import ubu.digit.ui.views.InformationView;
import ubu.digit.ui.views.LoginView;
import ubu.digit.ui.views.MetricsView;
import ubu.digit.ui.views.UploadView;
import ubu.digit.util.ExternalProperties;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataFactory;

/**
 * La UI es el punto de entrada de la aplicación.
 * @author Javier de la Fuente Barrios.
 */

@Route("")
@PageTitle("Sistemas Informáticos")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainLayout extends VerticalLayout implements RouterLayout{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -4568743602891945769L;
	
	private InformationView informationViews;
	
	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param informationView
     *            The information view. Automatically injected Spring managed
     *            bean.
     */
	public MainLayout() {
		this.informationViews = new InformationView();

	}

		
}  
