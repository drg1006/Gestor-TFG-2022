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
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
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

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.HistoricProject;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

/**
 * Vista de proyectos históricos.
 * 
 * @author Javier de la Fuente Barrios.
 * @author Diana Bringas Ochoa
 */
@Route(value = "Historic")
@PageTitle("Histórico de los proyectos")
@CssImport(value = "./styles/shared-styles.css", themeFor = "sistinftheme")
@HtmlImport("./styles/grid-styles.html")
public class HistoricProjectsView extends VerticalLayout {

	/**
	 * Serial Version UID.
	 */
	public static final long serialVersionUID = 8431807779365780674L;

	/**
	 * Logger de la clase.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(HistoricProjectsView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "historic-projects";

	/**
	 * Lista con los proyectos históricos.
	 */
	public List<HistoricProject> dataHistoric;
	
	/**
	 * Lista con los proyectos históricos que se usará 
	 * en el grid descripción de proyectos. 
	 */
	public List<HistoricProject> dataHistoricGrid;
	
	/**
	 * Lista con los proyectos históricos filtrados
	 */
	public List<HistoricProject>  dataFilteredGrid;

	/**
	 * Fichero de configuración.
	 */
	public ExternalProperties config;

	/**
	 * Tabla de proyectos históricos.
	 */
	 public Grid<HistoricProject> gridHistoric;
	 
	/**
	 * Formateador de números.
	 */
	public NumberFormat numberFormatter;
	
	/**
	 * Formateador de fechas.
	 */
	public transient DateTimeFormatter dateTimeFormatter;
	
	/**
	 * Mapa auxiliar con los diferentes cursos.
	 */
	public transient Map<Integer, List<HistoricProject>> yearOfProjects;
	
	/**
	 * Mapa con los cursos que tienen proyectos de nueva asignación.
	 */
	public transient Map<Integer, List<HistoricProject>> newProjects;

	/**
	 * Mapa con los cursos que tienen proyectos ya asignados.
	 */
	public transient Map<Integer, List<HistoricProject>> oldProjects;

	/**
	 * Mapa con los cursos que tienen proyectos presentados.
	 */
	public transient Map<Integer, List<HistoricProject>> presentedProjects;

	/**
	 * Menor curso total (más antiguo).
	 */
	public int minCourse;

	/**
	 * Mayor curso total (más actual).
	 */
	public int maxCourse;

	/**
	 * Campo de texto para filtrar por proyecto.
	 */
	public TextField projectFilter;
	
	/**
	 * Campo de texto para filtrar por tutores.
	 */
	public TextField tutorsFilter;
	
	/**
	 * Campo de texto para filtrar por fecha de asignación.
	 */
	public TextField assignmentDateFilter;

	/**
	 * Campo de texto para filtrar por fecha de presentación.
	 */
	public TextField presentationDateFilter;
	
	/**
	 *  Fachada para obtener los datos
	 */
	public SistInfDataAbstract fachadaDatos;

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

		NavigationBar bat = new NavigationBar();
		add(bat);
		
		createDataModel();
		CreateDataModelToGrid();
		createGlobalMetrics();
		createYearlyMetrics();
		createFilters();
		createHistoricProjectsTable();
		add(gridHistoric);

		Footer footer = new Footer("N3_Historico.csv");
		add(footer);
	}

	/**
	 * Crea el modelo de datos de los proyectos históricos.
	 * @throws SQLException 
	 */
	public void createDataModel(){ 	
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
	public void createGlobalMetrics() {
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
	public void createYearlyMetrics() {
		initProjectsStructures();
		createYearlyAverageStats();
		createYearlyTotalStats();
	}

	/**
	 * Inicializa los mapas.
	 */
	public void initProjectsStructures() {
		if(getCourse(true) != null && getCourse(false) != null) {
			minCourse = getCourse(true).getYear();
			maxCourse = getCourse(false).getYear();
		}

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
	public void organizeProjects() {
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
	public void assignProjectCourses(int year, HistoricProject project, int totalYears, boolean isCurrentCourse) {
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
	public void assignProject(int year, int yearCount, HistoricProject project, boolean isCurrentCourse) {
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
	public void buildPresentedProjects(HistoricProject project) {
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
	public Map<Integer, Number> getStudentsCount() {
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
	public Map<Integer, Number> getTutorsCount() {
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
	public void createYearlyAverageStats() {
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
	                .withColors("#E91E63","#4682B4")
	                .build();
		 	lineChart.setWidth("800px");
	        add(lineChart);
	}

	/**
	 * Obtiene la media aritmética de las notas de cada curso.
	 * 
	 * @return media aritmética de las notas de cada curso.
	 */
	public Map<Integer, Number> getAverageScores() {
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
	public Map<Integer, Number> getAverageTotalDays() {
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
	public void createYearlyTotalStats() {
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
	                .withColors("#E91E63","#4a6f22", "#9C27B0","#4682B4")
	                .build();
		 lineChart.setWidth("800px");
	     add(lineChart);
	}

	/**
	 * Obtiene el número de proyectos de cada curso.
	 * 
	 * @param projects
	 *            projectos agrupados por curso
	 * @return número de proyectos de cada curso.
	 */
	public Map<Integer, Number> getProjectsCount(Map<Integer, List<HistoricProject>> projects) {
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
	public LocalDate getCourse(Boolean isMinimum) {
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
	public void createFilters() {
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
	public void createHistoricProjectsTable() {
		H1 projectsTitle = new H1(DESCRIPCION_PROYECTOS);
		projectsTitle.addClassName(TITLE_STYLE);
		add(projectsTitle);
		
		gridHistoric = new Grid<>();
		gridHistoric.addClassName("historic-projects-grid");
		gridHistoric.setWidthFull();
		
		gridHistoric.setItems(dataHistoricGrid);
				
		gridHistoric.addColumn(HistoricProject::getTitle).setHeader("Título").setFlexGrow(30);
		gridHistoric.addColumn(HistoricProject::getTutors).setHeader("Tutor/es").setFlexGrow(9);
		gridHistoric.addColumn(HistoricProject::getNumStudents).setHeader("Nº Alumnos").setFlexGrow(4);
		gridHistoric.addColumn(HistoricProject::getAssignmentDate).setHeader("Fecha Asignación").setFlexGrow(6);
		gridHistoric.addColumn(HistoricProject::getPresentationDate).setHeader("Fecha Presentación").setFlexGrow(6);
		
		gridHistoric.addColumn(HistoricProject::getRankingPercentile).setHeader("Ranking Percentiles").setFlexGrow(5);
		gridHistoric.addColumn(HistoricProject::getRankingTotal).setHeader("Ranking Total").setFlexGrow(5);
		gridHistoric.addColumn(HistoricProject::getRankingCurse).setHeader("Ranking por curso").setFlexGrow(5);
		
		gridHistoric.getColumns().forEach(columna -> columna.setResizable(true));
		gridHistoric.getColumns().forEach(columna -> columna.setSortable(true));
		
		gridHistoric.getColumns().get(0).setTextAlign(ColumnTextAlign.START);
		gridHistoric.getColumns().subList(1, gridHistoric.getColumns().size()).forEach(columna -> columna.setTextAlign(ColumnTextAlign.CENTER));
		
		gridHistoric.setItemDetailsRenderer(
			    new ComponentRenderer<>(HistoricProject -> {
			        VerticalLayout layout = new VerticalLayout();
			        layout.add(new Label("Título: " +
			        		HistoricProject.getTitle()));
			        layout.add(new Label("Descripción: " +
			        		HistoricProject.getDescription()));
			        if(HistoricProject.getRepositoryLink() != "") {
			        	String repo = HistoricProject.getRepositoryLink();
			        	Html link = new Html("<label>Repositorio: <a href=\"" + repo + "\" target=\"_blank\">"+ repo +"</a></label>");
			        	layout.add(link);
			        }else {
			        	layout.add(new Label("Repositorio: No tiene"));
			        }
			        layout.add(new Label("Tutor/es: " +
			        		HistoricProject.getTutors()));
			        layout.add(new Label("Nº Alumnos: " +
			        		HistoricProject.getNumStudents()));
			        layout.add(new Label("Fecha Asignación: " +
			        		HistoricProject.getAssignmentDate()));
			        layout.add(new Label("Fecha Presentación: " +
			        		HistoricProject.getPresentationDate()));
			        layout.add(new Label("Ranking Percentiles: " +
			        		HistoricProject.getRankingPercentile()));
			        layout.add(new Label("Ranking Total: " +
			        		HistoricProject.getRankingTotal()));
			        layout.add(new Label("Ranking por curso: " +
			        		HistoricProject.getRankingCurse()));
			        return layout;
			}));
		
		gridHistoric.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		
	}
	
	/**
	 * Crea una nueva lista con los valores filtrados.
	 * 
	 * @param column
	 * @param valueChange
	 */
	public void applyFilter(String column, String valueChange) {
		LocalDate dateChange = null;
		dataFilteredGrid = new ArrayList<HistoricProject>();
		Iterator<HistoricProject> iterator = dataHistoricGrid.iterator();
		if(!valueChange.equals(" ")) {
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
	public void CreateDataModelToGrid() {
		String tutors = "";
		dataHistoricGrid = new ArrayList<HistoricProject>();
		Iterator<HistoricProject> iterator = dataHistoric.iterator();
		while (iterator.hasNext()) {
			HistoricProject historicProject = iterator.next();
			
			// para mi gusto, esta tiene que ser la lógica de la función getTutors()
			tutors = historicProject.getTutor1();	
			if(!tutors.equals("")) {
				if(!historicProject.getTutor2().equals("")) {
					tutors +=  ", " + historicProject.getTutor2();
					if(!historicProject.getTutor3().equals("")) {
						tutors +=  ", " + historicProject.getTutor3();
					}
				}
			}
			
			historicProject.setTutors(tutors);
			dataHistoricGrid.add(historicProject);
		}	
	}
}
