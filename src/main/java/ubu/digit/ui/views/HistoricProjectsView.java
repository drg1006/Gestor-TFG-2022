package ubu.digit.ui.views;

import java.awt.Color;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.vaadin.addon.JFreeChartWrapper;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataFactory;

import ubu.digit.ui.beans.ActiveProjectBean;
import ubu.digit.ui.beans.HistoricProjectBean;
import ubu.digit.ui.columngenerators.ProjectsColumnGenerator;
import ubu.digit.ui.columngenerators.TutorsColumnGenerator;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.listeners.OrSimpleStringFilterListener;
import ubu.digit.ui.listeners.SimpleStringFilterListener;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;
/**
 * Vista de proyectos históricos.
 * 
 * @author Javier de la Fuente Barrios.
 */
public class HistoricProjectsView extends VerticalLayout implements View {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8431807779365780674L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = Logger.getLogger(HistoricProjectsView.class);

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "historic-projects";

	/**
	 * Contenedor de POJOS de proyectos históricos.
	 */
	private BeanItemContainer<HistoricProjectBean> beans;

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Tabla de proyectos históricos.
	 */
	private Table table;

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
	private transient Map<Integer, List<HistoricProjectBean>> yearOfProjects;
	
	/**
	 * Mapa con los cursos que tienen proyectos de nueva asignación.
	 */
	private transient Map<Integer, List<HistoricProjectBean>> newProjects;

	/**
	 * Mapa con los cursos que tienen proyectos ya asignados.
	 */
	private transient Map<Integer, List<HistoricProjectBean>> oldProjects;

	/**
	 * Mapa con los cursos que tienen proyectos presentados.
	 */
	private transient Map<Integer, List<HistoricProjectBean>> presentedProjects;

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
	 */
	public HistoricProjectsView() {
		fachadaDatos = SistInfDataFactory.getInstanceData("XLS"); //TODO: 
		
		config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);
		numberFormatter = NumberFormat.getInstance();
		numberFormatter.setMaximumFractionDigits(2);
		dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		setMargin(true);
		setSpacing(true);
		
		NavigationBar navBar = new NavigationBar();
		addComponent(navBar);

		createDataModel(); 
		createGlobalMetrics();
		createYearlyMetrics();
		createFilters();
		createHistoricProjectsTable();
		addFiltersListeners();

		Footer footer = new Footer("Historico.csv");
		addComponent(footer);
	}

	/**
	 * Crea el modelo de datos de los proyectos históricos.
	 */
	private void createDataModel() { //TODO:
		beans = new BeanItemContainer<>(HistoricProjectBean.class);
		
		//Se obtienen los datos del modelo
		@SuppressWarnings("rawtypes")
		ArrayList listaDataModel; //TODO: revisar tipos
		listaDataModel = fachadaDatos.getDataModelHistoric(dateTimeFormatter);

		for(int i=0;i<listaDataModel.size()-1;i++) {
			HistoricProjectBean bean = new HistoricProjectBean((String)listaDataModel.get(i),(String)listaDataModel.get(++i), 
					(String)listaDataModel.get(++i),(String)listaDataModel.get(++i), (String)listaDataModel.get(++i), (String)listaDataModel.get(++i),
					(String)listaDataModel.get(++i), (String)listaDataModel.get(++i), (int)listaDataModel.get(++i), (int)listaDataModel.get(++i), 
					(LocalDate)listaDataModel.get(++i),(LocalDate)listaDataModel.get(++i), (Double)listaDataModel.get(++i), (int)listaDataModel.get(++i), (String)listaDataModel.get(++i));
			beans.addBean(bean);
		}
	}

	/**
	 * Crea las métricas globales de los proyectos históricos.
	 */
	private void createGlobalMetrics() {
		Label metricsTitle = new Label(INFO_ESTADISTICA);
		metricsTitle.setStyleName(TITLE_STYLE);
		addComponent(metricsTitle);
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

			addComponents(totalProjects, totalStudents, scoreStats, daysStats);
		} catch (Exception e) { //TODO:SQLException FilloException
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

		Iterator<HistoricProjectBean> iterator = beans.getItemIds().iterator();
		while (iterator.hasNext()) {
			HistoricProjectBean beanId = iterator.next();
			HistoricProjectBean bean = beans.getItem(beanId).getBean();
			int year = bean.getAssignmentDate().getYear();
			if (yearOfProjects.containsKey(year)) {
				yearOfProjects.get(year).add(bean);
			} else {
				List<HistoricProjectBean> aux = new ArrayList<>();
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
				HistoricProjectBean project = yearOfProjects.get(year).get(index);
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
	private void assignProjectCourses(int year, HistoricProjectBean project, int totalYears, boolean isCurrentCourse) {
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
	private void assignProject(int year, int yearCount, HistoricProjectBean project, boolean isCurrentCourse) {
		int before = 0;
		if (!isCurrentCourse) {
			before = 1;
		}
		if (yearCount == 0) {
			if (newProjects.containsKey(year + before)) {
				newProjects.get(year + before).add(project);
			} else {
				List<HistoricProjectBean> aux = new ArrayList<>();
				aux.add(project);
				newProjects.put(year + before, aux);
			}
		} else {
			if (oldProjects.containsKey(year + yearCount + before)) {
				oldProjects.get(year + yearCount + before).add(project);
			} else {
				List<HistoricProjectBean> aux = new ArrayList<>();
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
	private void buildPresentedProjects(HistoricProjectBean project) {
		LocalDate presentedDate = project.getPresentationDate();
		LocalDate startDate = LocalDate.of(presentedDate.getYear(), Integer.parseInt(config.getSetting("finPresentaciones.mes")),
				Integer.parseInt(config.getSetting("finPresentaciones.dia")));
		if (presentedDate.isBefore(startDate)) {
			if (presentedProjects.containsKey(presentedDate.getYear())) {
				presentedProjects.get(presentedDate.getYear()).add(project);
			} else {
				List<HistoricProjectBean> aux = new ArrayList<>();
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
			HistoricProjectBean project;
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
			HistoricProjectBean current;
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
	 */
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
		List<Number> avgScores = new ArrayList<>();
		List<Number> avgMonths = new ArrayList<>();
		
		for (int year = minCourse; year <= maxCourse; year++) {
			scores.add(numberFormatter.format(averageScores.get(year)));
			days.add(numberFormatter.format(averageTotalDays.get(year)));
			months.add(numberFormatter.format(averageMonths.get(year)));
			courses.add(year - 1 + "/" + year);
			avgScores.add(averageScores.get(year));
			avgMonths.add(averageMonths.get(year));
		}
		
		addComponent(new Label("Media de notas por curso: " + scores));
		addComponent(new Label("Media de dias por curso: " + days));
		addComponent(new Label("Media de meses por curso: " + months));
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int i = 0; i < courses.size(); i++) {
			dataset.addValue(avgScores.get(i), "Nota", courses.get(i));
			dataset.addValue(avgMonths.get(i), "Nº meses", courses.get(i));
		}
		createChart(dataset);
	}

	/**
	 * Obtiene la media aritmética de las notas de cada curso.
	 * 
	 * @return media aritmética de las notas de cada curso.
	 */
	private Map<Integer, Number> getAverageScores() {
		Map<Integer, Number> averageScores = new HashMap<>();
		for (int year = minCourse; year <= maxCourse; year++) {
			HistoricProjectBean project;
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
			HistoricProjectBean project;
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
	 */
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
		addComponents(asignedYearlyProjects, presentedYearlyProjects, asignedYearlyStudents, asignedYearlyTutors,
				allCourses);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int i = 0; i < courses.size(); i++) {
			dataset.addValue(yearlyAssignedProjects.get(i), "Proyectos Asignados", courses.get(i));
			dataset.addValue(yearlyAssignedStudents.get(i), "Alumnos Asignados", courses.get(i));
			dataset.addValue(yearlyAssignedTutors.get(i), "Tutores Asignados", courses.get(i));
			dataset.addValue(yearlyOldProjects.get(i), "Proyectos Ya Asignados", courses.get(i));
		}

		createChart(dataset);
	}

	/**
	 * Crea una gráfica a partir de unos datos.
	 * 
	 * @param dataset datos para generar la gráfica
	 */
	private void createChart(DefaultCategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createLineChart("Métricas agrupadas por curso", "Curso", "Nº", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.gray);
		plot.setRenderer(new LineAndShapeRenderer());

		CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JFreeChartWrapper chartWrapper = new JFreeChartWrapper(chart);
		addComponent(chartWrapper);
	}

	/**
	 * Obtiene el número de proyectos de cada curso.
	 * 
	 * @param projects
	 *            projectos agrupados por curso
	 * @return número de proyectos de cada curso.
	 */
	private Map<Integer, Number> getProjectsCount(Map<Integer, List<HistoricProjectBean>> projects) {
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
		} catch (Exception e) { //TODO: FilloException SQLException
			LOGGER.error("Error en getYear", e);
		}
		return dateTime;
	}

	/**
	 * Crea los filtros de la tabla de proyectos históricos.
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

		tutorsFilter = new TextField("Filtrar por tutores:");
		filters.addComponent(tutorsFilter);

		assignmentDateFilter = new TextField("Filtrar por fecha de asignación:");
		filters.addComponent(assignmentDateFilter);

		presentationDateFilter = new TextField("Filtrar por fecha de presentación:");
		filters.addComponent(presentationDateFilter);
	}

	/**
	 * Crea la tabla de proyectos históricos.
	 */
	private void createHistoricProjectsTable() {
		Label projectsTitle = new Label(DESCRIPCION_PROYECTOS);
		projectsTitle.setStyleName(TITLE_STYLE);
		addComponent(projectsTitle);

		table = new Table();
		addComponent(table);
		table.setWidth("100%");
		table.setPageLength(10);
		table.setColumnCollapsingAllowed(true);
		table.setContainerDataSource(beans);
		addGeneratedColumns();
		table.setVisibleColumns(PROJECTS, TUTORS, NUM_STUDENTS, ASSIGNMENT_DATE, PRESENTATION_DATE, SCORE);
		setTableColumnHeaders();
		setColumnExpandRatios();
		showDescriptionOnClick();
	}

	/**
	 * Añade las columnas genereadas a la tabla de proyectos históricos.
	 */
	private void addGeneratedColumns(){
		table.addGeneratedColumn(TUTORS, new TutorsColumnGenerator());
		table.addGeneratedColumn(PROJECTS, new ProjectsColumnGenerator());
		
	}

	/**
	 * Estalece las cabeceras de las columnas de la tabla de proyectos
	 * históricos.
	 */
	private void setTableColumnHeaders() {
		table.setColumnHeader(PROJECTS, "Título");
		table.setColumnHeader(TUTORS, "Tutor/es");
		table.setColumnHeader(NUM_STUDENTS, "Nº Alumnos");
		table.setColumnHeader(ASSIGNMENT_DATE, "Fecha Asignación");
		table.setColumnHeader(PRESENTATION_DATE, "Fecha Presentación");
		table.setColumnHeader(SCORE, "Nota");
	}

	/**
	 * Establece los ratios de expansión de las columnas de la tabla de
	 * proyectos históricos.
	 */
	private void setColumnExpandRatios() {
		table.setColumnExpandRatio(PROJECTS, 35);
		table.setColumnExpandRatio(TUTORS, 9);
		table.setColumnExpandRatio(NUM_STUDENTS, 4);
		table.setColumnExpandRatio(ASSIGNMENT_DATE, 6);
		table.setColumnExpandRatio(PRESENTATION_DATE, 6);
		table.setColumnExpandRatio(SCORE, 3);
	}

	/**
	 * Activa el mostrar la información de un proyecto al hacer click sobre él
	 * en la tabla.
	 */
	private void showDescriptionOnClick() {
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setImmediate(true);
		table.setNullSelectionAllowed(true);
		table.addValueChangeListener(new TableValueChangeListener());
	}

	/**
	 * Listener para mostrar la información de un proyecto al hacer click sobre
	 * él.
	 * 
	 * @author Javier de la Fuente Barrios
	 */
	private class TableValueChangeListener implements Property.ValueChangeListener {
		private static final long serialVersionUID = -5055796506090094836L;

		/**
		 * Acción a realizar al recibir el evento.
		 */
		@Override
		public void valueChange(ValueChangeEvent event) {
			if (event.getProperty().getValue() != null) {
				HistoricProjectBean value = (HistoricProjectBean) event.getProperty().getValue();
				String description = value.getDescription();

				if (description != null && !"".equals(description)) {
					Notification notification = new Notification("", description, Notification.Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(10000);
					notification.setPosition(Position.BOTTOM_CENTER);
					notification.setIcon(FontAwesome.FILE_TEXT_O);
					notification.show(Page.getCurrent());
				} else {
					Notification.show("Información", "No hay una descripción disponible para ese proyecto",
							Notification.Type.HUMANIZED_MESSAGE);
				}
			}
		}
	}

	/**
	 * Añade los listeners de los filtros a los campos de texto.
	 */
	private void addFiltersListeners() {
		projectFilter.addTextChangeListener(new SimpleStringFilterListener(table, TITLE));
		tutorsFilter.addTextChangeListener(new OrSimpleStringFilterListener(table, TUTOR1, TUTOR2, TUTOR3));
		assignmentDateFilter.addTextChangeListener(new SimpleStringFilterListener(table, ASSIGNMENT_DATE));
		presentationDateFilter.addTextChangeListener(new SimpleStringFilterListener(table, PRESENTATION_DATE));
	}
	
	/**
	 * La vista se inicializa en el constructor.
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// Se inicializa el contenido de la vista en el constructor
	}
}
