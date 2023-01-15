package ubu.digit.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import ubu.digit.ui.views.ManageView;
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
	public Button buttonHistoric;
	/**
     * Botón de la vista de históricos.
     */
    public Button buttonProjectsHistoric;
	
	/**
	 * Botón de la vista de métricas.
	 */
	public Button buttonMetrics;

	/**
     * Botón de la vista de profesores.
     */
	public Button buttonProfessorHistoric;
    
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
	    //MenuBar menuBar = new MenuBar();
	    
		buttonInfo = new Button(INFORMACION);
		buttonInfo.addClickListener(e -> UI.getCurrent().navigate(InformationView.class));
		
		buttonActive = new Button(PROYECTOS_ACTIVOS);
		buttonActive.addClickListener(e -> UI.getCurrent().navigate(ActiveProjectsView.class));
		
		buttonHistoric = new Button(HISTORICOS);
        buttonHistoric.addClickListener(e -> UI.getCurrent().navigate(HistoricProjectsView.class));
      
		buttonMetrics = new Button(METRICAS);
		buttonMetrics.addClickListener(e -> UI.getCurrent().getPage().executeJavaScript("window.open(\"https://sonarcloud.io/organizations/drg1006/projects/\", \"_blank\");"));
        
        buttonReport = new Button(INFORME);
        buttonReport.addClickListener(e -> UI.getCurrent().navigate(ReportView.class));
        
        buttonUpload = new Button(UPLOAD);
        buttonUpload.addClickListener(e -> {
            newProjectView.tutorRegistrado=LoginView.tutorRegistrado;
            UI.getCurrent().navigate(newProjectView.class);
            });
        
        buttonAcept = new Button(ACEPT);
        buttonAcept.addClickListener(e -> UI.getCurrent().navigate(ManageView.class));
        
        buttonProfessorHistoric = new Button(PROFESORES);
        buttonProfessorHistoric.addClickListener(e -> UI.getCurrent().navigate(ProfesoresView.class));
        
        buttonProjectsHistoric = new Button(PROYECTOS_HISTORICOS);
        buttonProjectsHistoric.addClickListener(e -> UI.getCurrent().navigate(HistoricProjectsView.class));
        
		buttonInfo.setHeight(BUTTON_HEIGHT);
		buttonActive.setHeight(BUTTON_HEIGHT);
		buttonHistoric.setHeight(BUTTON_HEIGHT);
		buttonMetrics.setHeight(BUTTON_HEIGHT);
		buttonReport.setHeight(BUTTON_HEIGHT);
        buttonUpload.setHeight(BUTTON_HEIGHT);
        buttonAcept.setHeight(BUTTON_HEIGHT);
        
		buttonInfo.setWidth("100%");
		buttonActive.setWidth("100%");
		buttonHistoric.setWidth("100%");
		buttonMetrics.setWidth("100%");
		buttonReport.setWidth("100%");
        buttonUpload.setWidth("100%");
        buttonAcept.setWidth("100%");
 
		//LoginView.permiso.add("reports");
		LoginView.permiso.add("update");
		if(LoginView.permiso.contains("update")) {
		    //EL BOTON DE HISTORICO ES UN DESPLEGABLE CON DOS BOTONES
		    //ROL DE ADMINISTRADOR
		    add(buttonInfo,buttonActive,buttonHistoric, buttonMetrics,buttonReport,buttonUpload,buttonAcept);
		}else if(LoginView.permiso.contains("reports")){
	     //ROL DE PROFESOR
           add(buttonInfo, buttonActive,buttonHistoric, buttonMetrics,buttonReport,buttonUpload);
		}else {
	       //ROL ALUMNO
	       add(buttonInfo, buttonActive, buttonHistoric, buttonMetrics);
		}
	}
	/**
	 * SubMenu que sale en las pantallas de Historic, de tfgs y de profesores.
	 * @return horizontallayout con los dos botones
	 */
	public Component subMenu() {
	    HorizontalLayout layout= new HorizontalLayout();
	    layout.add(  buttonProjectsHistoric,buttonProfessorHistoric);
	    return layout;
                

    }
}
