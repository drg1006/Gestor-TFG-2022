package ubu.digit.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataFactory;
import ubu.digit.ui.MainLayout;
import ubu.digit.ui.beans.ActiveProject;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;

import static ubu.digit.util.Constants.*;

/**
 * Vista de proyectos activos.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 */
@Route(value = "active-projects", layout = MainLayout.class)
@PageTitle("Proyectos activos")
public class ActiveProjectsView extends VerticalLayout{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8857805864102975132L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveProjectsView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "active-projects";

	/**
	 * Tabla de proyectos.
	 */
	private Grid<ActiveProject> table;
	
	/**
	 * Lista con los proyectos activos
	 */
	List<ActiveProject> dataActiveProjects;
	
	/**
	 * Lista con los proyectos activos que se usarán el el grid
	 * En este los tutores y alumnos se incluyen juntos en una columna.
	 */
	List<ActiveProject> dataActiveProjectsGrid;

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
		
		fachadaDatos = SistInfDataFactory.getInstanceData();
		
		addClassName("active-projects-view");
		setMargin(true);
		setSpacing(true);

		createDataModel();
		CreateDataModelToGrid();
		createMetrics();
		createFilters();
		createCurrentProjectsTable();
		add(table);
		
		Footer footer = new Footer();
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

			add(totalProjects, totalFreeProject, aalumnos, totalStudent, totalTutor);
		} catch (Exception e) {
			LOGGER.error("Error en estadísticas", e);
		}
	}

	/**
	 * Crea los filtros de la tabla.
	 */
	private void createFilters() {
		H1 filtersTitle = new H1(FILTROS);
		filtersTitle.addClassName(TITLE_STYLE);
		add(filtersTitle);

		HorizontalLayout filters = new HorizontalLayout();
		filters.setSpacing(true);
		filters.setMargin(false);
		filters.setWidth("100%");
		
		projectFilter = new TextField("Filtrar por proyectos:");
		filters.add(projectFilter);

		descriptionFilter = new TextField("Filtrar por descripción:");
		filters.add(descriptionFilter);

		tutorsFilter = new TextField("Filtrar por tutores:");
		filters.add(tutorsFilter);

		studentsFilter = new TextField("Filtrar por alumnos:");
		filters.add(studentsFilter);

		courseFilter = new TextField("Filtrar por curso:");
		filters.add(courseFilter);
		
		add(filters);
	}

	/**
	 * Crea el modelo de datos de los proyectos activos.
	 */
	private void createDataModel() { 
		//Se obtienen los datos del modelo
		List<String> listaDataModel = fachadaDatos.getDataModel();
		dataActiveProjects = new ArrayList<ActiveProject>();
		
		for(int i=0;i<listaDataModel.size();i++) {
			ActiveProject actives = new ActiveProject(listaDataModel.get(i), listaDataModel.get(++i), 
					listaDataModel.get(++i),listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i),
					listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i));
			dataActiveProjects.add(actives);
		}
	}
	
	/**
	 * Crea la tabla de proyectos activos.
	 */
	private void createCurrentProjectsTable() {
		H1 proyectosTitle = new H1(DESCRIPCION_PROYECTOS);
		proyectosTitle.addClassName(TITLE_STYLE);
		add(proyectosTitle);
		
		try {
			table = new Grid<>();
			table.addClassName("active-projects-grid");
			
			table.setItems(dataActiveProjectsGrid);
			
			table.addColumn(ActiveProject::getTitle).setHeader("Título"); //setFlexGrow(2);
			table.addColumn(ActiveProject::getDescription).setHeader("Descripción");
			table.addColumn(ActiveProject::getTutors).setHeader("Tutor/es");
			table.addColumn(ActiveProject::getStudents).setHeader("Alumno/s");
			table.addColumn(ActiveProject::getCourseAssignment).setHeader("Curso Asignación");
			
			table.getColumns().forEach(columna -> columna.setAutoWidth(true));
			
		}catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		
	}
	
	/**
	 * Se crea una nueva lista con los datos que se usarán en la tabla de descripción de proyectos.
	 */
	private void CreateDataModelToGrid() {
		String tutors = "";
		String students = "";
		dataActiveProjectsGrid = new ArrayList<ActiveProject>();
		Iterator<ActiveProject> iterator = dataActiveProjects.iterator();
		while (iterator.hasNext()) {
			ActiveProject bean = iterator.next();
			tutors = bean.getTutor1() + "\n" + bean.getTutor2() + "\n" + bean.getTutor3();
			students = bean.getStudent1() + "\n" + bean.getStudent2() + "\n" + bean.getStudent3();
			
			ActiveProject actives = new ActiveProject(bean.getTitle(), bean.getDescription(),
					tutors, students, bean.getCourseAssignment());
			dataActiveProjectsGrid.add(actives);
		}	
	}
}
