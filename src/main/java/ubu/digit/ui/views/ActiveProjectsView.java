package ubu.digit.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.ActiveProject;

import static ubu.digit.util.Constants.*;

/**
 * Vista de proyectos activos.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 */
@Route(value = "active-projects")
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
	 * Lista con los proyectos activos filtrados 
	 */
	List<ActiveProject> dataFilteredGrid;

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
		
		NavigationBar bat = new NavigationBar();
		add(bat);

		createDataModel();
		CreateDataModelToGrid();
		createMetrics();
		createFilters();
		createCurrentProjectsTable();
		add(table);
		
		Footer footer = new Footer("N2_Proyecto.csv");
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
		
		projectFilter = new TextField("Filtrar por proyectos:");
		projectFilter.setWidth("300px");
		projectFilter.addValueChangeListener(event -> {
			if(!projectFilter.isEmpty()) {
				applyFilter("title", event.getValue());
			}else {
				table.setItems(dataActiveProjectsGrid);
			}
		});

		descriptionFilter = new TextField("Filtrar por descripción:");
		descriptionFilter.setWidth("300px");
		descriptionFilter.addValueChangeListener(event -> {
			if(!descriptionFilter.isEmpty()) {
				applyFilter("description", event.getValue());
			}else {
				table.setItems(dataActiveProjectsGrid);
			}
		});

		tutorsFilter = new TextField("Filtrar por tutores:");
		tutorsFilter.setWidth("300px");
		tutorsFilter.addValueChangeListener(event -> {
			if(!tutorsFilter.isEmpty()) {
				applyFilter("tutor", event.getValue());
			}else {
				table.setItems(dataActiveProjectsGrid);
			}
		});

		studentsFilter = new TextField("Filtrar por alumnos:");
		studentsFilter.setWidth("300px");
		studentsFilter.addValueChangeListener(event -> {
			if(!studentsFilter.isEmpty()) {
				applyFilter("student", event.getValue());
			}else {
				table.setItems(dataActiveProjectsGrid);
			}
		});

		courseFilter = new TextField("Filtrar por curso:");
		courseFilter.setWidth("300px");
		courseFilter.addValueChangeListener(event -> {
			if(!courseFilter.isEmpty()) {
				applyFilter("course", event.getValue());
			}else {
				table.setItems(dataActiveProjectsGrid);
			}
		});
		
		filters.add(projectFilter, descriptionFilter, tutorsFilter, studentsFilter, courseFilter);
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
			table.setWidthFull();
			
			table.setItems(dataActiveProjectsGrid);
			
			table.addColumn(ActiveProject::getTitle).setHeader("Título").setFlexGrow(10);
			table.addColumn(ActiveProject::getDescription).setHeader("Descripción").setFlexGrow(25);
			table.addColumn(ActiveProject::getTutors).setHeader("Tutor/es").setFlexGrow(6);
			table.addColumn(ActiveProject::getStudents).setHeader("Alumno/s").setFlexGrow(6);
			table.addColumn(ActiveProject::getCourseAssignment).setHeader("Curso Asignación").setFlexGrow(5);
			
			table.getColumns().forEach(columna -> columna.setResizable(true));
			table.getColumns().forEach(columna -> columna.setSortable(true));
			
			table.setItemDetailsRenderer(
				    new ComponentRenderer<>(ActiveProject -> {
				        VerticalLayout layout = new VerticalLayout();
				        layout.add(new Label("Título: " +
				        		ActiveProject.getTitle()));
				        layout.add(new Label("Descripción: " +
				        		ActiveProject.getDescription()));
				        layout.add(new Label("Tutor/es: " +
				        		ActiveProject.getTutors()));
				        layout.add(new Label("Alumno/s: " +
				        		ActiveProject.getStudents()));
				        layout.add(new Label("Curso Asignación: " +
				        		ActiveProject.getCourseAssignment()));
				        return layout;
				}));
			table.addThemeVariants(GridVariant.LUMO_NO_BORDER);
			
		}catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Crea una nueva lista con los valores filtrados
	 * 
	 * @param column
	 * @param valueChange
	 */
	private void applyFilter(String column, String valueChange) {
		dataFilteredGrid = new ArrayList<ActiveProject>();
		Iterator<ActiveProject> iterator = dataActiveProjectsGrid.iterator();
		if(valueChange != " ") {
			while (iterator.hasNext()) {
				ActiveProject activeproject = iterator.next();
				
				switch(column) {
					case "title":
						if(activeproject.getTitle().contains(valueChange)) {
							dataFilteredGrid.add(activeproject);
						}
						break;
					case "description":
						if(activeproject.getDescription().contains(valueChange)) {
							dataFilteredGrid.add(activeproject);
						}
						break;
					case "tutor":
						if(activeproject.getTutors().contains(valueChange)) {
							dataFilteredGrid.add(activeproject);
						}
						break;
					case "student":
						if(activeproject.getStudents().contains(valueChange)) {
							dataFilteredGrid.add(activeproject);
						}
						break;
					case "course":
						if(activeproject.getCourseAssignment().contains(valueChange)) {
						dataFilteredGrid.add(activeproject);
						}
						break;
				}
			}
			//Se establece los nuevos valores del grid
			table.setItems(dataFilteredGrid);
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
			ActiveProject activeproject = iterator.next();
			
			tutors = activeproject.getTutor1();	
			if(tutors.equals("")) {
				tutors += activeproject.getTutor2();
			}else {
				tutors += ", " + activeproject.getTutor2();
			}
			if(tutors.equals("")) {
				tutors +=activeproject.getTutor3();
			}else{	
				tutors += ", " + activeproject.getTutor3();
			}
			
			students = activeproject.getStudent1();	
			if(students.equals("")) {
				students += activeproject.getStudent2();
			}else {
				students += ", " + activeproject.getStudent2();
			}
			if(students.equals("")) {
				students +=activeproject.getStudent3();
			}else{
				students += ", " + activeproject.getStudent3();
			}
			
			ActiveProject actives = new ActiveProject(activeproject.getTitle(), activeproject.getDescription(),
					tutors, students, activeproject.getCourseAssignment());
			dataActiveProjectsGrid.add(actives);
		}	
	}
}
