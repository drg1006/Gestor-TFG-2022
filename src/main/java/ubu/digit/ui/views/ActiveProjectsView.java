package ubu.digit.ui.views;

import java.util.List;

import org.apache.log4j.Logger;


import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataFactory;

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
 * @author Diana Bringas Ochoa
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
	 *  Fachada para obtener los datos
	 */
	private SistInfDataAbstract fachadaDatos;

	/**
	 * Constructor.
	 */
	public ActiveProjectsView() {
		
		fachadaDatos = SistInfDataFactory.getInstanceData("CSV"); //TODO: 
		
		setMargin(true);
		setSpacing(true);

		NavigationBar navBar = new NavigationBar();
		addComponent(navBar);

		createDataModel();
		createMetrics();
		createFilters();
		createCurrentProjectsTable();
		addFiltersListeners();

		Footer footer = new Footer("N2_Proyecto.csv"); //TODO: Revisar Proyecto
		addComponent(footer);
	}
	
	/**
	 * Crea el modelo de datos de los proyectos activos.
	 */
	private void createDataModel() { //TODO:
		beans = new BeanItemContainer<>(ActiveProjectBean.class);
		
		//Se obtienen los datos del modelo
		List<String> listaDataModel;
		listaDataModel = fachadaDatos.getDataModel();
		
		for(int i=0;i<listaDataModel.size();i++) {
			ActiveProjectBean bean = new ActiveProjectBean(listaDataModel.get(i), listaDataModel.get(++i), 
					listaDataModel.get(++i),listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i),
					listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i));
			beans.addBean(bean);
		}
	}
	
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
