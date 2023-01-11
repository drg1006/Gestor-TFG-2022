package ubu.digit.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import ubu.digit.ui.views.AceptView;
import ubu.digit.ui.views.ActiveProjectsView;
import ubu.digit.ui.views.HistoricProjectsView;
import ubu.digit.ui.views.InformationView;
import ubu.digit.ui.views.LoginView;
import ubu.digit.ui.views.ProfesoresView;
import ubu.digit.ui.views.ReportView;
import ubu.digit.ui.views.UploadView;
import ubu.digit.ui.views.newProjectView;

import static ubu.digit.util.Constants.*;

/**
 * Barra de navegación común a todas las vistas.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 * @author David Renedo Gil
 */
public class NavigationBar extends HorizontalLayout{
	
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -3164191122994765469L;

	/**
	 * Botón de la vista de información.
	 */
	public Button buttonInfo;
	
	/**
	 * Botón de la vista de proyectos activos.
	 */
	public Button buttonActive;

	/**
	 * Botón de la vista de proyectos históricos.
	 */
	public Button buttonHistory;
	
	/**
	 * Botón de la vista de métricas.
	 */
	public Button buttonMetrics;

	/**
     * Botón de la vista de profesores.
     */
	public Button buttonProfessor;
    
    /**
     * Botón de la vista de informes.
     */
	public Button buttonReport;
    
    /**
     * Botón de la vista de subir TFGs.
     */
	public Button buttonUpload;
    
    /**
     * Botón de la vista de aceptar TFGs.
     */
	public Button buttonAcept;
    
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
        
        
            
		buttonInfo.setHeight(BUTTON_HEIGHT);
		buttonActive.setHeight(BUTTON_HEIGHT);
		buttonHistory.setHeight(BUTTON_HEIGHT);
		buttonMetrics.setHeight(BUTTON_HEIGHT);
		buttonProfessor.setHeight(BUTTON_HEIGHT);
        
		buttonInfo.setWidth("100%");
		buttonActive.setWidth("100%");
		buttonHistory.setWidth("100%");
		buttonMetrics.setWidth("100%");
		buttonProfessor.setWidth("100%");
         
        
		add(buttonInfo, buttonActive, buttonHistory, buttonMetrics,buttonProfessor);
		//LoginView.permiso=true;
		if(LoginView.permiso) {
		    
		    buttonReport = new Button(INFORME);
	        buttonReport.addClickListener(e -> UI.getCurrent().navigate(ReportView.class));
	        
	        buttonUpload = new Button(UPLOAD);
	        //buttonUpload.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
	        buttonUpload.addClickListener(e -> UI.getCurrent().navigate(newProjectView.class));
	        
	        buttonAcept = new Button(ACEPT);
	        //buttonAcept.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
	        buttonAcept.addClickListener(e -> UI.getCurrent().navigate(AceptView.class));
	        buttonReport.setHeight(BUTTON_HEIGHT);
	        buttonUpload.setHeight(BUTTON_HEIGHT);
	        buttonAcept.setHeight(BUTTON_HEIGHT);
	        buttonReport.setWidth("100%");
	        buttonUpload.setWidth("100%");
	        buttonAcept.setWidth("100%");
	        add(buttonReport,buttonUpload,buttonAcept);
	   }
	}
}
