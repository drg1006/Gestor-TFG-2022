package ubu.digit.ui.views;

import java.sql.ResultSet;
import org.apache.log4j.Logger;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Recordset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import ubu.digit.pesistence.SistInfData;
import ubu.digit.pesistence.SistInfDataCsv;
import ubu.digit.pesistence.SistInfDataXls;
import ubu.digit.ui.beans.ActiveProjectBean;
import ubu.digit.ui.columngenerators.StudentsColumnGenerator;
import ubu.digit.ui.columngenerators.TutorsColumnGenerator;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.listeners.OrSimpleStringFilterListener;
import ubu.digit.ui.listeners.SimpleStringFilterListener;
import static ubu.digit.util.Constants.*;

/**
 * Vista de proyectos activos.
 * 
 * @author Javier de la Fuente Barrios
 */
public class ActiveProjectsView extends VerticalLayout implements View {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8857805864102975132L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = Logger.getLogger(ActiveProjectsView.class);

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "active-projects";

	/**
	 * Contenedor de POJOS de proyectos activos.
	 */
	private BeanItemContainer<ActiveProjectBean> beans;

	/**
	 * Tabla de proyectos.
	 */
	private Table table;

	/**
	 * Campo de texto para filtrar por proyecto.
	 */
	private TextField projectFilter;

	/**
	 * Campo de texto para filtrar por descripción.
	 */
	private TextField descriptionFilter;

	/**
	 * Campo de texto para filtrar por tutor.
	 */
	private TextField tutorsFilter;

	/**
	 * Campo de texto para filtrar por alumno.
	 */
	private TextField studentsFilter;

	/**
	 * Campo de texto para filtrar por curso.
	 */
	private TextField courseFilter;
	
	/**
	 * Fachada para obtener los datos.
	 */
	//private SistInfDataCsv fachadaDatosCsv;
	
	/**
	 * Fachada para obtener los datos.
	 */
	private SistInfDataXls fachadaDatos;

	/**
	 * Constructor.
	 */
	public ActiveProjectsView() {
		//fachadaDatos = SistInfDataCsv.getInstance();
		fachadaDatos = SistInfDataXls.getInstance();
		//fachadaDatos = SistInfData.getInstanceCsv();
		setMargin(true);
		setSpacing(true);

		NavigationBar navBar = new NavigationBar();
		addComponent(navBar);

		createDataModel();
		createMetrics();
		createFilters();
		createCurrentProjectsTable();
		addFiltersListeners();

		Footer footer = new Footer("N2_Proyecto.csv");
		addComponent(footer);
	}

	private void createDataModel() { //TODO:
		beans = new BeanItemContainer<>(ActiveProjectBean.class);
		try{
			Recordset result = fachadaDatos.getResultSet(PROYECTO, TITULO);
			while (result.next()) {
				String title = result.getField(TITULO);
				String description = result.getField(DESCRIPCION);
				String tutor1 = result.getField(TUTOR1);
				String tutor2 = result.getField(TUTOR2);
				if (tutor2 == null) {
					tutor2 = "";
				}
				String tutor3 = result.getField(TUTOR3);
				if (tutor3 == null) {
					tutor3 = "";
				}
				String student1 = result.getField(ALUMNO1);
				String student2 = result.getField(ALUMNO2);
				if (student2 == null) {
					student2 = "";
				}
				String student3 = result.getField(ALUMNO3);
				if (student3 == null) {
					student3 = "";
				}
				String courseAssignment = result.getField(CURSO_ASIGNACION);

				ActiveProjectBean bean = new ActiveProjectBean(title, description, tutor1, tutor2, tutor3, student1,
						student2, student3, courseAssignment);
				beans.addBean(bean);
			}
		} catch (FilloException e) {
			LOGGER.error("Error en actuales", e);
		}
	}
	
	/**
	 * Crea el modelo de datos de los proyectos activos.
	 */
	/*private void createDataModel() {
		beans = new BeanItemContainer<>(ActiveProjectBean.class);
		try (ResultSet result = fachadaDatos.getResultSet(PROYECTO, TITULO)) {
			while (result.next()) {
				String title = result.getString(TITULO);
				String description = result.getString(DESCRIPCION);
				String tutor1 = result.getString(TUTOR1);
				String tutor2 = result.getString(TUTOR2);
				if (tutor2 == null) {
					tutor2 = "";
				}
				String tutor3 = result.getString(TUTOR3);
				if (tutor3 == null) {
					tutor3 = "";
				}
				String student1 = result.getString(ALUMNO1);
				String student2 = result.getString(ALUMNO2);
				if (student2 == null) {
					student2 = "";
				}
				String student3 = result.getString(ALUMNO3);
				if (student3 == null) {
					student3 = "";
				}
				String courseAssignment = result.getString(CURSO_ASIGNACION);

				ActiveProjectBean bean = new ActiveProjectBean(title, description, tutor1, tutor2, tutor3, student1,
						student2, student3, courseAssignment);
				beans.addBean(bean);
			}
		} catch (SQLException e) {
			LOGGER.error("Error en actuales", e);
		}
	}*/

	/**
	 * Crea las métricas de los proyectos activos.
	 */
	private void createMetrics() {
		Label metricsTitle = new Label(INFO_ESTADISTICA);
		metricsTitle.setStyleName("lbl-title");
		addComponent(metricsTitle);

		try {
			Number totalProjectsNumber = fachadaDatos.getTotalNumber(TITULO, PROYECTO);
			Label totalProjects = new Label("- Número total de proyectos: " + totalProjectsNumber.intValue());

			Number totalFreeProjectNumber = fachadaDatos.getTotalFreeProject();
			Label totalFreeProject = new Label(
					"- Número total de proyectos sin asignar: " + totalFreeProjectNumber.intValue());
			Label aalumnos = new Label("Buscar la cadena 'Aalumnos sin asignar' en columna Alumnos.");
			
			Number totalStudentNumber = fachadaDatos.getTotalNumber(APELLIDOS_NOMBRE, ALUMNO);
			Label totalStudent = new Label("- Número total de alumnos: " + totalStudentNumber.intValue());

			String[] tutorColumnNames = { TUTOR1, TUTOR2, TUTOR3 };
			Number totalTutorNumber = fachadaDatos.getTotalNumber(tutorColumnNames, PROYECTO);
			Label totalTutor = new Label("- Número total de tutores involucrados: " + totalTutorNumber.intValue());

			addComponents(totalProjects, totalFreeProject, aalumnos, totalStudent, totalTutor);
		} catch (Exception e) {
			LOGGER.error("Error en estadísticas", e);
		}
	}

	/**
	 * Crea los filtros de la tabla.
	 */
	private void createFilters() {
		Label filtersTitle = new Label(FILTROS);
		filtersTitle.setStyleName(TITLE_STYLE);
		addComponent(filtersTitle);

		HorizontalLayout filters = new HorizontalLayout();
		filters.setSpacing(true);
		filters.setMargin(false);
		filters.setWidth("100%");
		addComponent(filters);

		projectFilter = new TextField("Filtrar por proyectos:");
		filters.addComponent(projectFilter);

		descriptionFilter = new TextField("Filtrar por descripción:");
		filters.addComponent(descriptionFilter);

		tutorsFilter = new TextField("Filtrar por tutores:");
		filters.addComponent(tutorsFilter);

		studentsFilter = new TextField("Filtrar por alumnos:");
		filters.addComponent(studentsFilter);

		courseFilter = new TextField("Filtrar por curso:");
		filters.addComponent(courseFilter);
	}

	/**
	 * Crea la tabla de proyectos activos.
	 */
	private void createCurrentProjectsTable() {
		Label proyectosTitle = new Label(DESCRIPCION_PROYECTOS);
		proyectosTitle.setStyleName(TITLE_STYLE);
		addComponent(proyectosTitle);

		table = new Table();
		addComponent(table);
		table.setWidth("100%");
		table.setPageLength(5);
		table.setColumnCollapsingAllowed(true);
		table.setContainerDataSource(beans);
		addGeneratedColumns();
		table.setVisibleColumns(TITLE, DESCRIPTION, TUTORS, STUDENTS, COURSE_ASSIGNMENT);

		setTableColumnHeaders();
		setColumnExpandRatios();
	}

	/**
	 * Añade las columnas generadas a la tabla.
	 */
	private void addGeneratedColumns(){
		table.addGeneratedColumn(TUTORS, new TutorsColumnGenerator());
		table.addGeneratedColumn(STUDENTS, new StudentsColumnGenerator());
		
	}
	
	/**
	 * Establece las cabeceras de las columnas de la tabla.
	 */
	private void setTableColumnHeaders() {
		table.setColumnHeader(TITLE, "Título");
		table.setColumnHeader(DESCRIPTION, "Descripción");
		table.setColumnHeader(TUTORS, "Tutor/es");
		table.setColumnHeader(STUDENTS, "Alumno/s");
		table.setColumnHeader(COURSE_ASSIGNMENT, "Curso Asignación");
	}

	/**
	 * Establece el ratio de expansión de las columnas.
	 */
	private void setColumnExpandRatios() {
		table.setColumnExpandRatio(TITLE, 5);
		table.setColumnExpandRatio(DESCRIPTION, 17);
		table.setColumnExpandRatio(TUTORS, 6);
		table.setColumnExpandRatio(STUDENTS, 6);
		table.setColumnExpandRatio(COURSE_ASSIGNMENT, 3);
	}

	/**
	 * Añade los listeners de los filtros. 
	 */
	private void addFiltersListeners() {
		projectFilter.addTextChangeListener(new SimpleStringFilterListener(table, TITLE));
		descriptionFilter.addTextChangeListener(new SimpleStringFilterListener(table, DESCRIPTION));
		tutorsFilter.addTextChangeListener(new OrSimpleStringFilterListener(table, TUTOR1, TUTOR2, TUTOR3));
		studentsFilter.addTextChangeListener(new OrSimpleStringFilterListener(table, STUDENT1, STUDENT2, STUDENT3));
		courseFilter.addTextChangeListener(new SimpleStringFilterListener(table, COURSE_ASSIGNMENT));
	}

	/**
	 * La vista se inicializa en el constructor.
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// Se inicializa el contenido de la vista en el constructor
	}
}
