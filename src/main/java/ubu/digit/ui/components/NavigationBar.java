package ubu.digit.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import ubu.digit.ui.views.ActiveProjectsView;
import ubu.digit.ui.views.HistoricProjectsView;
import ubu.digit.ui.views.InformationView;
import ubu.digit.ui.views.ProfesoresView;
import ubu.digit.ui.views.ReportView;

import static ubu.digit.util.Constants.*;

/**
 * Barra de navegación común a todas las vistas.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 */
public class NavigationBar extends HorizontalLayout{
	
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -3164191122994765469L;

	/**
	 * Botón de la vista de información.
	 */
	private Button buttonInfo;
	
	/**
	 * Botón de la vista de proyectos activos.
	 */
	private Button buttonActive;

	/**
	 * Botón de la vista de proyectos históricos.
	 */
	private Button buttonHistory;
	
	/**
	 * Botón de la vista de métricas.
	 */
	private Button buttonMetrics;

	/**
     * Botón de la vista de profesores.
     */
    private Button buttonProfessor;
    
    /**
     * Botón de la vista de informes.
     */
    private Button buttonReport;
    
	/**
	 * Constructor.
	 */
	public NavigationBar() {
		setMargin(false);
		setSpacing(false);
		setWidth("100%");

		initComponents();
	}
	
	/**
	 * Inicializa los botones de navegación.
	 */
	@SuppressWarnings("deprecation")
	private void initComponents() {
		buttonInfo = new Button(INFORMACION);
		buttonInfo.addClickListener(e -> UI.getCurrent().navigate(InformationView.class));
		
		buttonActive = new Button(PROYECTOS_ACTIVOS);
		buttonActive.addClickListener(e -> UI.getCurrent().navigate(ActiveProjectsView.class));
		
		buttonHistory = new Button(PROYECTOS_HISTORICOS);
		buttonHistory.addClickListener(e -> UI.getCurrent().navigate(HistoricProjectsView.class));

		buttonMetrics = new Button(METRICAS);
		buttonMetrics.addClickListener(e -> UI.getCurrent().getPage().executeJavaScript("window.open(\"https://sonarcloud.io/organizations/dbo1001/projects/\", \"_blank\");"));
		
		buttonProfessor = new Button(PROFESORES);
        buttonProfessor.addClickListener(e -> UI.getCurrent().navigate(ProfesoresView.class));
        
        buttonReport = new Button(INFORME);
        buttonReport.addClickListener(e -> UI.getCurrent().navigate(ReportView.class));
        
		buttonInfo.setHeight(BUTTON_HEIGHT);
		buttonActive.setHeight(BUTTON_HEIGHT);
		buttonHistory.setHeight(BUTTON_HEIGHT);
		buttonMetrics.setHeight(BUTTON_HEIGHT);
		buttonProfessor.setHeight(BUTTON_HEIGHT);
        buttonReport.setHeight(BUTTON_HEIGHT);
        
		buttonInfo.setWidth("100%");
		buttonActive.setWidth("100%");
		buttonHistory.setWidth("100%");
		buttonMetrics.setWidth("100%");
		buttonProfessor.setWidth("100%");
        buttonReport.setWidth("100%");
        
		add(buttonInfo, buttonActive, buttonHistory, buttonMetrics,buttonProfessor,buttonReport);
	}
}
