package ubu.digit.ui.views;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.GridBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.TitleSubtitleBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.helper.Series;
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
import ubu.digit.ui.beans.HistoricProject;
import ubu.digit.ui.components.Footer;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

/**
 * Vista de proyectos históricos.
 * 
 * @author Javier de la Fuente Barrios.
 * @author Diana Bringas Ochoa
 */

@Route(value = "Historic", layout = MainLayout.class)
@PageTitle("Histórico de los proyectos")
public class HistoricProjectsView extends VerticalLayout {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8431807779365780674L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoricProjectsView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "historic-projects";

	/**
	 * Lista con los proyectos históricos.
	 */
	private List<HistoricProject> dataHistoric;
	
	/**
	 * Lista con los proyectos históricos que se usará 
	 * en el grid descripción de proyectos. 
	 */
	private List<HistoricProject> dataHistoricGrid;
	
	/**
	 * Lista con los proyectos históricos filtrados
	 */
	private List<HistoricProject>  dataFilteredGrid;

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Tabla de proyectos históricos.
	 */
	 private Grid<HistoricProject> gridHistoric;
	 
	/**
	 * Formateador de números.
	 */
	private NumberFormat numberFormatter;
	
	/**
	 * Formateador de fechas.
	 */
	private transient DateTimeFormatter dateTimeFormatter;
	
	/**
	 * Mapa auxiliar con los diferentes cursos.
	 */
	private transient Map<Integer, List<HistoricProject>> yearOfProjects;
	
	/**
	 * Mapa con los cursos que tienen proyectos de nueva asignación.
	 */
	private transient Map<Integer, List<HistoricProject>> newProjects;

	/**
	 * Mapa con los cursos que tienen proyectos ya asignados.
	 */
	private transient Map<Integer, List<HistoricProject>> oldProjects;

	/**
	 * Mapa con los cursos que tienen proyectos presentados.
	 */
	private transient Map<Integer, List<HistoricProject>> presentedProjects;

	/**
	 * Menor curso total (más antiguo).
	 */
	private int minCourse;

	/**
	 * Mayor curso total (más actual).
	 */
	private int maxCourse;

	/**
	 * Campo de texto para filtrar por proyecto.
	 */
	private TextField projectFilter;
	
	/**
	 * Campo de texto para filtrar por tutores.
	 */
	private TextField tutorsFilter;
	
	/**
	 * Campo de texto para filtrar por fecha de asignación.
	 */
	private TextField assignmentDateFilter;

	/**
	 * Campo de texto para filtrar por fecha de presentación.
	 */
	private TextField presentationDateFilter;
	
	/**
	 *  Fachada para obtener los datos
	 */
	private SistInfDataAbstract fachadaDatos;

	/**
	 * Constructor.
	 * @throws SQLException 
	 */
	public HistoricProjectsView(){
		
		fachadaDatos = SistInfDataFactory.getInstanceData();
		config = ExternalProperties.getInstance("/config.properties", false);
		numberFormatter = NumberFormat.getInstance();
		numberFormatter.setMaximumFractionDigits(2);
		dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		setMargin(true);
		setSpacing(true);

		createDataModel();
		CreateDataModelToGrid();
		createGlobalMetrics();
		createYearlyMetrics();
		createFilters();
		createHistoricProjectsTable();
		add(gridHistoric);

		Footer footer = new Footer("N3_Historico");
		add(footer);
	}

	/**
	 * Crea el modelo de datos de los proyectos históricos.
	 * @throws SQLException 
	 */
	private void createDataModel(){ 	
		//Se obtienen los datos del modelo
		dataHistoric = new ArrayList<HistoricProject>();
		@SuppressWarnings("rawtypes")
		ArrayList listaDataModel = fachadaDatos.getDataModelHistoric(dateTimeFormatter);
		
		for(int i=0;i<listaDataModel.size();i++) {
			HistoricProject historic = new HistoricProject((String)listaDataModel.get(i),(String)listaDataModel.get(++i), 
					(String)listaDataModel.get(++i),(String)listaDataModel.get(++i), (String)listaDataModel.get(++i), (String)listaDataModel.get(++i),
					(String)listaDataModel.get(++i), (String)listaDataModel.get(++i), (int)listaDataModel.get(++i), (int)listaDataModel.get(++i), 
					(LocalDate)listaDataModel.get(++i),(LocalDate)listaDataModel.get(++i), (Double)listaDataModel.get(++i), (int)listaDataModel.get(++i), 
					(String)listaDataModel.get(++i), (String)listaDataModel.get(++i), (int)listaDataModel.get(++i),(int)listaDataModel.get(++i));
			dataHistoric.add(historic);
		}
	}

	/**
	 * Crea las métricas globales de los proyectos históricos.
	 */
	private void createGlobalMetrics() {
		H1 metricsTitle = new H1(INFO_ESTADISTICA);
		metricsTitle.addClassName(TITLE_STYLE);
		add(metricsTitle);
		try {
			Number totalProjectsNumber = fachadaDatos.getTotalNumber(TITULO, HISTORICO);
			Label totalProjects = new Label("Número total de proyectos: " + totalProjectsNumber.intValue());

			String[] studentColumnNames = { ALUMNO1, ALUMNO2, ALUMNO3 };
			Number totalStudentNumber = fachadaDatos.getTotalNumber(studentColumnNames, HISTORICO);
			Label totalStudents = new Label("Número total de alumnos: " + totalStudentNumber.intValue());

			Number avgScore = fachadaDatos.getAvgColumn(NOTA, HISTORICO);
			Number minScore = fachadaDatos.getMinColumn(NOTA, HISTORICO);
			Number maxScore = fachadaDatos.getMaxColumn(NOTA, HISTORICO);
			Number stdvScore = fachadaDatos.getStdvColumn(NOTA, HISTORICO);
			List<String> scores = new ArrayList<>();
			scores.add(numberFormatter.format(avgScore));
			scores.add(numberFormatter.format(minScore));
			scores.add(numberFormatter.format(maxScore));
			scores.add(numberFormatter.format(stdvScore));
			Label scoreStats = new Label("Calificación [media,min,max,stdv]: " + scores);

			Number avgDays = fachadaDatos.getAvgColumn(TOTAL_DIAS, HISTORICO);
			Number minDays = fachadaDatos.getMinColumn(TOTAL_DIAS, HISTORICO);
			Number maxDays = fachadaDatos.getMaxColumn(TOTAL_DIAS, HISTORICO);
			Number stdvDays = fachadaDatos.getStdvColumn(TOTAL_DIAS, HISTORICO);
			List<String> days = new ArrayList<>();
			days.add(numberFormatter.format(avgDays));
			days.add(numberFormatter.format(minDays));
			days.add(numberFormatter.format(maxDays));
			days.add(numberFormatter.format(stdvDays));
			Label daysStats = new Label("Tiempo/días [media,min,max,stdv]: " + days);

			add(totalProjects, totalStudents, scoreStats, daysStats);
		} catch (Exception e) { 
			LOGGER.error("Error en históricos (metricas)", e);
		}
	}

	/**
	 * Crea las métricas anuales de los proyectos históricos.
	 */
	private void createYearlyMetrics() {
		initProjectsStructures();
		createYearlyAverageStats();
		createYearlyTotalStats();
	}

	/**
	 * Inicializa los mapas.
	 */
	private void initProjectsStructures() {
		minCourse = getCourse(true).getYear();
		maxCourse = getCourse(false).getYear();

		yearOfProjects = new HashMap<>();
		newProjects = new HashMap<>();
		oldProjects = new HashMap<>();
		presentedProjects = new HashMap<>();

		Iterator<HistoricProject> iterator = dataHistoric.iterator();
		while (iterator.hasNext()) {
			HistoricProject bean = iterator.next();
			int year = bean.getAssignmentDate().getYear();
			if (yearOfProjects.containsKey(year)) {
				yearOfProjects.get(year).add(bean);
			} else {
				List<HistoricProject> aux = new ArrayList<>();
				aux.add(bean);
				yearOfProjects.put(year, aux);
			}
		}
		organizeProjects();
	}

	/**
	 * Organiza los proyectos (nueva o vieja asignación, y presentados).
	 */
	private void organizeProjects() {
		Iterator<Integer> iterator = yearOfProjects.keySet().iterator();
		while (iterator.hasNext()) {
			Integer year = iterator.next();
			for (int index = 0; index < yearOfProjects.get(year).size(); index++) {
				HistoricProject project = yearOfProjects.get(year).get(index);
				LocalDate assignmentDate = project.getAssignmentDate();

				int startMonth = Integer.parseInt(config.getSetting("inicioCurso.mes"));
				int startDay = Integer.parseInt(config.getSetting("inicioCurso.dia"));
				LocalDate startDate = LocalDate.of(year, startMonth, startDay);
			
				int totalDays = project.getTotalDays();
				int totalYears = totalDays / 365;

				if (assignmentDate.isBefore(startDate)) {
					assignProjectCourses(year, project, totalYears, true);
				} else {
					assignProjectCourses(year, project, totalYears, false);
				}
				buildPresentedProjects(project);
			}
		}
	}

	/**
	 * Asigna un proyecto a los distintos cursos que pertenece.
	 * 
	 * @param year
	 *            curso actual
	 * @param project
	 *            proyecto actual
	 * @param totalYears
	 *            nº de años que dura el proyecto
	 * @param isCurrentCourse
	 *            si el proyecto se corresponde con el curso actual
	 */
	private void assignProjectCourses(int year, HistoricProject project, int totalYears, boolean isCurrentCourse) {
		for (int yearCount = 0; yearCount <= totalYears; yearCount++) {
			assignProject(year, yearCount, project, isCurrentCourse);
		}
	}

	/**
	 * Asigna un proyecto a su respectivo curso.
	 * 
	 * @param year
	 *            curso actual
	 * @param yearCount
	 *            nº de año
	 * @param project
	 *            proyecto actual
	 * @param isCurrentCourse
	 *            si el proyecto se corresponde con el curso actual
	 */
	private void assignProject(int year, int yearCount, HistoricProject project, boolean isCurrentCourse) {
		int before = 0;
		if (!isCurrentCourse) {
			before = 1;
		}
		if (yearCount == 0) {
			if (newProjects.containsKey(year + before)) {
				newProjects.get(year + before).add(project);
			} else {
				List<HistoricProject> aux = new ArrayList<>();
				aux.add(project);
				newProjects.put(year + before, aux);
			}
		} else {
			if (oldProjects.containsKey(year + yearCount + before)) {
				oldProjects.get(year + yearCount + before).add(project);
			} else {
				List<HistoricProject> aux = new ArrayList<>();
				aux.add(project);
				oldProjects.put(year + yearCount + before, aux);
			}
		}
	}

	/**
	 * Añade un proyecto a presentedProjects si este se ha presentado.
	 * 
	 * @param project
	 *            proyecto actual
	 */
	private void buildPresentedProjects(HistoricProject project) {
		LocalDate presentedDate = project.getPresentationDate();
		LocalDate startDate = LocalDate.of(presentedDate.getYear(), Integer.parseInt(config.getSetting("finPresentaciones.mes")),
				Integer.parseInt(config.getSetting("finPresentaciones.dia")));
		if (presentedDate.isBefore(startDate)) {
			if (presentedProjects.containsKey(presentedDate.getYear())) {
				presentedProjects.get(presentedDate.getYear()).add(project);
			} else {
				List<HistoricProject> aux = new ArrayList<>();
				aux.add(project);
				presentedProjects.put(presentedDate.getYear(), aux);
			}
		}
	}

	/**
	 * Obtiene el número de alumnos con proyectos de nueva asignación en cada
	 * curso.
	 * 
	 * @return número de alumnos con proyectos de nueva asignación en cada
	 *         curso.
	 */
	private Map<Integer, Number> getStudentsCount() {
		Map<Integer, Number> studentsCount = new HashMap<>();
		for (int year = minCourse; year <= maxCourse; year++) {
			HistoricProject project;
			int numStudents = 0;
			if (newProjects.containsKey(year)) {
				for (int index = 0; index < newProjects.get(year).size(); index++) {
					project = newProjects.get(year).get(index);
					numStudents += project.getNumStudents();
				}
			}
			studentsCount.put(year, numStudents);
		}
		return studentsCount;
	}

	/**
	 * Obtiene el número de tutores con proyectos de nueva asignación en cada
	 * curso.
	 * 
	 * @return número de tutores con proyectos de nueva asignación en cada
	 *         curso.
	 */
	private Map<Integer, Number> getTutorsCount() {
		Map<Integer, Number> tutorsCount = new HashMap<>();
		for (int year = minCourse; year <= maxCourse; year++) {
			HistoricProject current;
			int numTutors = 0;
			if (newProjects.containsKey(year)) {
				for (int index = 0; index < newProjects.get(year).size(); index++) {
					current = newProjects.get(year).get(index);
					numTutors += current.getNumTutors();
				}
			}
			tutorsCount.put(year, numTutors);
		}
		return tutorsCount;
	}

	/**
	 * Genera las estadísticas (medias aritméticas) anuales de los proyectos históricos.
	 * Y crear a partir de esas estadísticas un gráfico para representar la media de notas 
	 * y meses por curso academico.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createYearlyAverageStats() {
		Map<Integer, Number> averageScores = getAverageScores();
		Map<Integer, Number> averageTotalDays = getAverageTotalDays();
		Map<Integer, Number> averageMonths = new HashMap<>();

		for (int index = minCourse; index <= maxCourse; index++) {
			Number averageDays = averageTotalDays.get(index);
			averageMonths.put(index, averageDays.floatValue() / 31);
		}

		List<String> scores = new ArrayList<>();
		List<String> days = new ArrayList<>();
		List<String> months = new ArrayList<>();
		
		List<String> courses = new ArrayList<>();
		List<String> avgScores = new ArrayList<>();
		List<String> avgMonths = new ArrayList<>();
		
		DecimalFormatSymbols separadoresPersonalizados = new DecimalFormatSymbols();
		separadoresPersonalizados.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#.00", separadoresPersonalizados);
		
		for (int year = minCourse; year <= maxCourse; year++) {
			scores.add(numberFormatter.format(averageScores.get(year)));
			days.add(numberFormatter.format(averageTotalDays.get(year)));
			months.add(numberFormatter.format(averageMonths.get(year)));
			courses.add(year - 1 + "/" + year);
			avgScores.add(df.format(averageScores.get(year)));
			avgMonths.add(df.format(averageMonths.get(year)));
		}
		
		add(new Label("Media de notas por curso: " + scores));
		add(new Label("Media de dias por curso: " + days));
		add(new Label("Media de meses por curso: " + months));
		
		 ApexCharts lineChart = ApexChartsBuilder.get()
	                .withChart(ChartBuilder.get()
	                        .withType(Type.line)
	                        .withZoom(ZoomBuilder.get()
	                                .withEnabled(false)
	                                .build())
	                        .build())
	                .withStroke(StrokeBuilder.get()
	                		.withColors("#E91E63","#4682B4")
	                        .withCurve(Curve.straight)
	                        .build())
	                .withTitle(TitleSubtitleBuilder.get()
	                        .withText("Métricas agrupadas por curso")
	                        .withAlign(Align.center)
	                        .build())
	                .withGrid(GridBuilder.get()
	                        .withRow(RowBuilder.get()
	                                .withColors("#f3f3f3", "transparent")
	                                .withOpacity(0.5).build()
	                        ).build())
	                .withXaxis(XAxisBuilder.get()
	                        .withCategories(courses)
	                        .build())
	                .withSeries(new Series("Notas", avgScores.toArray()),new Series("Nº Meses", avgMonths.toArray()))
	                .build();
	        add(lineChart);
	        setWidth("35%");
	}

	/**
	 * Obtiene la media aritmética de las notas de cada curso.
	 * 
	 * @return media aritmética de las notas de cada curso.
	 */
	private Map<Integer, Number> getAverageScores() {
		Map<Integer, Number> averageScores = new HashMap<>();
		for (int year = minCourse; year <= maxCourse; year++) {
			HistoricProject project;
			double mean = 0;
			if (newProjects.containsKey(year)) {
				for (int index = 0; index < newProjects.get(year).size(); index++) {
					project = newProjects.get(year).get(index);
					mean +=  project.getScore();
				}
				mean = mean / newProjects.get(year).size();
			}
			averageScores.put(year, mean);
		}
		return averageScores;
	}

	/**
	 * Obtiene la media del número de días que duran los proyectos cada curso.
	 * 
	 * @return media del número de días que duran los proyectos cada curso.
	 */
	private Map<Integer, Number> getAverageTotalDays() {
		Map<Integer, Number> averageTotalDays = new HashMap<>();
		for (int year = minCourse; year <= maxCourse; year++) {
			HistoricProject project;
			double mean = 0;
			if (newProjects.containsKey(year)) {
				for (int index = 0; index < newProjects.get(year).size(); index++) {
					project = newProjects.get(year).get(index);
					mean += project.getTotalDays();
				}
				mean = mean / newProjects.get(year).size();
			}
			averageTotalDays.put(year, mean);
		}
		return averageTotalDays;
	}

	/**
	 * Genera las estadísticas (totales) anuales de los proyectos históricos.
	 * Y crear a partir de esas estadísticas un gráfico para representar la media de proyectos, 
	 * estudiantes, tutores asignados por cursos.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createYearlyTotalStats() {
		List<String> courses = new ArrayList<>();
		List<Number> yearlyAssignedProjects = new ArrayList<>();
		List<Number> yearlyOldProjects = new ArrayList<>();
		List<Number> yearlyPresentedProjects = new ArrayList<>();
		List<Number> yearlyAssignedStudents = new ArrayList<>();
		List<Number> yearlyAssignedTutors = new ArrayList<>();

		for (int year = minCourse; year <= maxCourse; year++) {
			courses.add(year - 1 + "/" + year);
			yearlyAssignedProjects.add(getProjectsCount(newProjects).get(year));
			yearlyOldProjects.add(getProjectsCount(oldProjects).get(year));
			yearlyAssignedStudents.add(getStudentsCount().get(year));
			yearlyAssignedTutors.add(getTutorsCount().get(year));
			yearlyPresentedProjects.add(getProjectsCount(presentedProjects).get(year));
		}

		Label asignedYearlyProjects = new Label(
				"Número total de proyectos asignados por curso: " + yearlyAssignedProjects);
		Label presentedYearlyProjects = new Label(
				"Número total de proyectos presentados por curso: " + yearlyPresentedProjects);
		Label asignedYearlyStudents = new Label(
				"Número total de alumnos asignados por curso: " + yearlyAssignedStudents);
		Label asignedYearlyTutors = new Label(
				"Número total de tutores con nuevas asignaciones por curso: " + yearlyAssignedTutors);
		Label allCourses = new Label("Cursos: " + courses);
		add(asignedYearlyProjects, presentedYearlyProjects, asignedYearlyStudents, asignedYearlyTutors,
				allCourses);

		 ApexCharts lineChart = ApexChartsBuilder.get()
	                .withChart(ChartBuilder.get()
	                        .withType(Type.line)
	                        .withZoom(ZoomBuilder.get()
	                                .withEnabled(false)
	                                .build())
	                        .build())
	                .withStroke(StrokeBuilder.get()
	                		.withColors("#E91E63","#4a6f22", "#9C27B0","#4682B4")
	                        .withCurve(Curve.straight)
	                        .build())
	                .withTitle(TitleSubtitleBuilder.get()
	                        .withText("Métricas agrupadas por curso")
	                        .withAlign(Align.center)
	                        .build())
	                .withGrid(GridBuilder.get()
	                        .withRow(RowBuilder.get()
	                                .withColors("#f3f3f3", "transparent")
	                                .withOpacity(0.5).build()
	                        ).build())
	                .withXaxis(XAxisBuilder.get()
	                        .withCategories(courses)
	                        .build())
	                .withSeries(new Series("Proyectos Asignados", yearlyAssignedProjects.toArray()),new Series("Alumnos Asignados", yearlyAssignedStudents.toArray()),
	                		new Series("Tutores Asignados", yearlyAssignedTutors.toArray()),new Series("Proyectos Ya Asignados", yearlyOldProjects.toArray()))
	                .build();
	        add(lineChart);
	        setWidth("35%");
	}

	/**
	 * Obtiene el número de proyectos de cada curso.
	 * 
	 * @param projects
	 *            projectos agrupados por curso
	 * @return número de proyectos de cada curso.
	 */
	private Map<Integer, Number> getProjectsCount(Map<Integer, List<HistoricProject>> projects) {
		Map<Integer, Number> projectsCount = new HashMap<>();
		for (int year = minCourse; year <= maxCourse; year++) {
			int totalProjects = 0;
			if (projects.containsKey(year))
				totalProjects += projects.get(year).size();
			projectsCount.put(year, totalProjects);
		}
		return projectsCount;
	}

	/**
	 * Obtiene el curso mínimo o máximo de los proyectos históricos.
	 * 
	 * @param isMinimum
	 *            si se quiere obtener el curso mínimo o máximo
	 * @return curso mínimo o máximo
	 */
	private LocalDate getCourse(Boolean isMinimum) {
		LocalDate dateTime = null;
		try {
			dateTime = fachadaDatos.getYear(FECHA_PRESENTACION, HISTORICO, isMinimum);
		} catch (Exception e) {
			LOGGER.error("Error en getYear", e);
		}
		return dateTime;
	}

	/**
	 * Crea los filtros de la tabla de proyectos históricos.
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
				gridHistoric.setItems(dataHistoricGrid);
			}
		});

		tutorsFilter = new TextField("Filtrar por tutores:");
		tutorsFilter.setWidth("300px");
		tutorsFilter.addValueChangeListener(event -> {
			if(!tutorsFilter.isEmpty()) {
				applyFilter("tutor", event.getValue());
			}else {
				gridHistoric.setItems(dataHistoricGrid);
			}
		});

		assignmentDateFilter = new TextField("Filtrar por fecha de asignación:");
		assignmentDateFilter.setWidth("300px");
		assignmentDateFilter.addValueChangeListener(event -> {
			if(!assignmentDateFilter.isEmpty()) {
				applyFilter("assignmentDate", event.getValue());
			}else {
				gridHistoric.setItems(dataHistoricGrid);
			}
		});

		presentationDateFilter = new TextField("Filtrar por fecha de presentación:");
		presentationDateFilter.setWidth("300px");
		presentationDateFilter.addValueChangeListener(event -> {
			if(!presentationDateFilter.isEmpty()) {
				applyFilter("presentationDate", event.getValue());
			}else {
				gridHistoric.setItems(dataHistoricGrid);
			}
		});
		
		filters.add(projectFilter, tutorsFilter, assignmentDateFilter, presentationDateFilter);
		add(filters);
		
	}

	/**
	 * Crea la tabla de proyectos históricos.
	 */
	private void createHistoricProjectsTable() {
		H1 projectsTitle = new H1(DESCRIPCION_PROYECTOS);
		projectsTitle.addClassName(TITLE_STYLE);
		add(projectsTitle);
		
		gridHistoric = new Grid<>();
		gridHistoric.addClassName("historic-projects-grid");
		gridHistoric.setWidthFull();
		
		gridHistoric.setItems(dataHistoricGrid);
				
		gridHistoric.addColumn(HistoricProject::getTitle).setHeader("Título");
		gridHistoric.addColumn(HistoricProject::getTutors).setHeader("Tutor/es");
		gridHistoric.addColumn(HistoricProject::getNumStudents).setHeader("Nº Alumnos");
		gridHistoric.addColumn(HistoricProject::getAssignmentDate).setHeader("Fecha Asignación");
		gridHistoric.addColumn(HistoricProject::getPresentationDate).setHeader("Fecha Presentación");
		gridHistoric.addColumn(HistoricProject::getRankingPercentile).setHeader("Ranking Percentiles");
		gridHistoric.addColumn(HistoricProject::getRankingTotal).setHeader("Ranking Total");
		gridHistoric.addColumn(HistoricProject::getRankingCurse).setHeader("Ranking por curso");
		
		//gridHistoric.getColumns().forEach(columna -> columna.setAutoWidth(true));
	}
	
	/**
	 * Crea una nueva lista con los valores filtrados
	 * 
	 * @param column
	 * @param valueChange
	 */
	private void applyFilter(String column, String valueChange) {
		LocalDate dateChange = null;
		dataFilteredGrid = new ArrayList<HistoricProject>();
		Iterator<HistoricProject> iterator = dataHistoricGrid.iterator();
		if(valueChange != " ") {
			while (iterator.hasNext()) {
				HistoricProject historicProject = iterator.next();
				
				switch(column) {
					case "title":
						if(historicProject.getTitle().contains(valueChange)){
							dataFilteredGrid.add(historicProject);
						}
						break;
					case "tutor":
						if(historicProject.getTutors().contains(valueChange)){
							dataFilteredGrid.add(historicProject);
						}
						break;
					case "assignmentDate":
						dateChange = LocalDate.parse(valueChange, dateTimeFormatter);
						if(historicProject.getAssignmentDate().equals(dateChange)){
							dataFilteredGrid.add(historicProject);
						}
						break;
					case "presentationDate":
						dateChange = LocalDate.parse(valueChange, dateTimeFormatter);
						if(historicProject.getPresentationDate().equals(dateChange)){
						dataFilteredGrid.add(historicProject);
						}
						break;
				}
			}
			//Se establece los nuevos valores del grid
			gridHistoric.setItems(dataFilteredGrid);
		}
	}


	/**
	 * Se crea una nueva lista con los datos que se usarán en la tabla de descripción de proyectos.
	 */
	private void CreateDataModelToGrid() {
		String tutors = "";
		dataHistoricGrid = new ArrayList<HistoricProject>();
		Iterator<HistoricProject> iterator = dataHistoric.iterator();
		while (iterator.hasNext()) {
			HistoricProject historicProject = iterator.next();
			tutors = historicProject.getTutor1() + "\n" + historicProject.getTutor2() + "\n" + historicProject.getTutor3();
			
			HistoricProject historic = new HistoricProject(historicProject.getTitle(), tutors, historicProject.getNumStudents(), 
					historicProject.getAssignmentDate(), historicProject.getPresentationDate(), historicProject.getRankingPercentile(),
					historicProject.getRankingTotal(),historicProject.getRankingCurse());
			dataHistoricGrid.add(historic);
		}	
	}
}
