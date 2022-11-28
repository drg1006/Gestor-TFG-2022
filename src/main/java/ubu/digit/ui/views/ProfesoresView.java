package ubu.digit.ui.views;

import static ubu.digit.util.Constants.INFO_ESTADISTICA;

import java.sql.SQLException;
import java.text.NumberFormat;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.HistoricProject;
import ubu.digit.util.ExternalProperties;

/**
 * Vista de proyectos históricos.
 * 
 * @author Javier de la Fuente Barrios.
 * @author Diana Bringas Ochoa
 */
@Route(value = "Profesores")
@PageTitle("Histórico de los profesores")

public class ProfesoresView extends VerticalLayout {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8431807779365780674L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfesoresView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "professor";
	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Formateador de números.
	 */
	private NumberFormat numberFormatter;
	
	/**
	 * Formateador de fechas.
	 */
	private transient DateTimeFormatter dateTimeFormatter;


	
	/**
	 *  Fachada para obtener los datos
	 */
	private SistInfDataAbstract fachadaDatos;

	/**
	 * Constructor.
	 * @throws SQLException 
	 */
	public ProfesoresView(){
		
		fachadaDatos = SistInfDataFactory.getInstanceData();
		config = ExternalProperties.getInstance("/config.properties", false);
		numberFormatter = NumberFormat.getInstance();
		numberFormatter.setMaximumFractionDigits(2);
		dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		setMargin(true);
		setSpacing(true);

		NavigationBar bat = new NavigationBar();
		add(bat);
		createMetrics();

		Footer footer = new Footer("N4_Profesores.csv");
		add(footer);
	}
	
	/**
     * Crea las métricas de los proyectos activos.
     */
    private void createMetrics() {
        H1 metricsTitle = new H1(INFO_ESTADISTICA);
        metricsTitle.addClassName("lbl-title");
        add(metricsTitle);

        try {
            //Number totalProjectsNumber = fachadaDatos.getTotalNumber(TITULO, PROYECTO);
            Label totalProfessors = new Label("- Número total de profesores: " );

            //Number totalFreeProjectNumber = fachadaDatos.getTotalFreeProject();
            Label totalAreas = new Label("- Número total de areas: " );
            //Label aalumnos = new Label("Buscar la cadena 'Aalumnos sin asignar' en columna Alumnos.");
            
            //Number totalStudentNumber = fachadaDatos.getTotalNumber(APELLIDOS_NOMBRE, ALUMNO);
            Label totalDepartments = new Label("- Número total de departamentos: ");

            //String[] tutorColumnNames = { TUTOR1, TUTOR2, TUTOR3 };
            //Number totalTutorNumber = fachadaDatos.getTotalNumber(tutorColumnNames, PROYECTO);
            Label totalTFGs = new Label("- TFGs asignados por Curso: ");

            add(totalProfessors, totalAreas, totalDepartments, totalTFGs);
        } catch (Exception e) {
            LOGGER.error("Error en estadísticas", e);
        }
    }

}
