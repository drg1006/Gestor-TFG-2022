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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
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
 * @author David Renedo gil
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
    public List<HistoricProject> dataFilteredGrid;

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
     * Fachada para obtener los datos
     */
    public SistInfDataAbstract fachadaDatos;

    List<String> courses;
    
    Map<String, Number> tfgsPerCourse;

    /**
     * Constructor.
     * 
     * @throws SQLException
     */
    public HistoricProjectsView() {

        fachadaDatos = SistInfDataFactory.getInstanceData();
        config = ExternalProperties.getInstance("/config.properties", false);
        numberFormatter = NumberFormat.getInstance();
        numberFormatter.setMaximumFractionDigits(2);

        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        setMargin(true);
        setSpacing(true);
        // VerticalLayout layout=new VerticalLayout();
        NavigationBar bat = new NavigationBar();
        bat.buttonHistoric.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        bat.buttonProjectsHistoric.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(bat);
        // Este submenu solo les sale a los profesores/administradores
        // Comprobamos si es profesor o administrador
        if (UI.getCurrent().getSession().getAttribute("update") != null) {
            if (UI.getCurrent().getSession().getAttribute("update").equals("true")) {
                add(bat.subMenu());
            }
        } else if (UI.getCurrent().getSession().getAttribute("reports") != null) {
            if (UI.getCurrent().getSession().getAttribute("reports").equals("true")) {
                add(bat.subMenu());
            }
        }
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
     * 
     * @throws SQLException
     */
    public void createDataModel() {
        // Se obtienen los datos del modelo
        dataHistoric = new ArrayList<HistoricProject>();

        @SuppressWarnings("rawtypes")
        ArrayList listaDataModel = fachadaDatos.getDataModelHistoric(dateTimeFormatter);
        for (int i = 0; i < listaDataModel.size(); i++) {
            HistoricProject historic = new HistoricProject((String) listaDataModel.get(i),
                    (String) listaDataModel.get(++i),
                    (String) listaDataModel.get(++i), (String) listaDataModel.get(++i),
                    (String) listaDataModel.get(++i), (String) listaDataModel.get(++i),
                    (String) listaDataModel.get(++i), (String) listaDataModel.get(++i), (int) listaDataModel.get(++i),
                    (int) listaDataModel.get(++i),
                    (LocalDate) listaDataModel.get(++i), (LocalDate) listaDataModel.get(++i),
                    (Double) listaDataModel.get(++i), (int) listaDataModel.get(++i),
                    (String) listaDataModel.get(++i), (String) listaDataModel.get(++i), (int) listaDataModel.get(++i),
                    (int) listaDataModel.get(++i));
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
            Number totalProjectsNumber = fachadaDatos.getTotalNumber(TITULO_CORTO, HISTORICO);
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

        // Comprobamos a que curso pertenecen las fechas obtenidas
        int startMonth = Integer.parseInt(config.getSetting("inicioCurso.mes"));
        int startDay = Integer.parseInt(config.getSetting("inicioCurso.dia"));

        LocalDate fechaMinCourse = LocalDate.of(getCourse(true).getYear(), startMonth, startDay);
        LocalDate fechaMaxCourse = LocalDate.of(getCourse(false).getYear(), startMonth, startDay);

        if (fechaMinCourse.isAfter(getCourse(true))) {
            minCourse = getCourse(true).getYear() - 1;
        } else {
            minCourse = getCourse(true).getYear();
        }

        if (fechaMaxCourse.isAfter(getCourse(false))) {
            maxCourse = getCourse(false).getYear() - 1;
        } else {
            maxCourse = getCourse(false).getYear();
        }
        courses = new ArrayList<>();
        for (int year = minCourse; year <= maxCourse; year++) {
            courses.add(year + "/" + (year + 1));
        }

        // LES ASIGNAREMOS UN CURSO A CADA HISTORICO
        Iterator<HistoricProject> iterator = dataHistoric.iterator();
        while (iterator.hasNext()) {
            HistoricProject prj = iterator.next();

            LocalDate fechaPres = prj.getPresentationDate();
            LocalDate fechaINI = LocalDate.of(prj.getPresentationDate().getYear(), startMonth, startDay);
            LocalDate fechaFIN = LocalDate.of(prj.getPresentationDate().getYear() + 1, startMonth, startDay);

            if (fechaPres.isAfter(fechaINI) && fechaPres.isBefore(fechaFIN)) {
                String cursoPrj = prj.getPresentationDate().getYear() + "/" + (prj.getPresentationDate().getYear() + 1);
                prj.setCourse(cursoPrj);
            } else {
                String cursoPrj = (prj.getPresentationDate().getYear() - 1) + "/" + prj.getPresentationDate().getYear();
                prj.setCourse(cursoPrj);
            }
        }
        //organizeProjects();
    }



    /**
     * Genera las estadísticas (medias aritméticas) anuales de los proyectos
     * históricos.
     * Y crear a partir de esas estadísticas un gráfico para representar la media de
     * notas
     * y meses por curso academico.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void createYearlyAverageStats() {
        //MAPAS CON EL CURSO COMO KEY
        Map<String, Number> averageScores = getAverageScores();
        Map<String, Number> averageTotalDays = getAverageTotalDays();
        Map<String, Number> averageMonths = new HashMap<>();

        for (String curso:courses) {
            Number averageDays= averageTotalDays.get(curso);
            averageMonths.put(curso, averageDays.floatValue() / 31);
        }

        List<String> scores = new ArrayList<>();
        List<String> months = new ArrayList<>();

        List<String> avgScores = new ArrayList<>();
        List<String> avgMonths = new ArrayList<>();

        DecimalFormatSymbols separadoresPersonalizados = new DecimalFormatSymbols();
        separadoresPersonalizados.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.00", separadoresPersonalizados);

        for (String curso:courses) {
            scores.add(numberFormatter.format(averageScores.get(curso)));
            months.add(numberFormatter.format(averageMonths.get(curso)));
            avgScores.add(df.format(averageScores.get(curso)));
            avgMonths.add(df.format(averageMonths.get(curso)));
        }

        ApexCharts lineChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.line)
                        .withZoom(ZoomBuilder.get()
                                .withEnabled(false)
                                .build())
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withColors("#E91E63", "#4682B4")
                        .withCurve(Curve.straight)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Nota media y número de meses agrupadas por curso")
                        .withAlign(Align.center)
                        .build())
                .withGrid(GridBuilder.get()
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.5).build())
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(courses)
                        .build())
                .withSeries(new Series("Notas", avgScores.toArray()), new Series("Nº Meses", avgMonths.toArray()))
                .withColors("#E91E63", "#4682B4")
                .build();
        lineChart.setWidth("800px");
        add(lineChart);
    }

    /**
     * Obtiene la media aritmética de las notas de cada curso.
     * 
     * @return media aritmética de las notas de cada curso.
     */
    public Map<String, Number> getAverageScores() {
        Map<String, Number> averageScores = new HashMap<>();        
        tfgsPerCourse = new HashMap<>();
        // Recorremos los cursos
        for (String curso : courses) {
            double mean = 0;
            // contador para saber cuantos tfgs hay por curso
            int i = 0;
            Iterator<HistoricProject> iterator = dataHistoric.iterator();
            while (iterator.hasNext()) {               
                HistoricProject prj = iterator.next();
                if (curso.equals(prj.getCourse())) {
                    mean += prj.getScore();
                    i++;
                }
            }
            tfgsPerCourse.put(curso, i);
            mean = mean / i;
            averageScores.put(curso, mean);
        }
        return averageScores;
    }

    /**
     * Obtiene la media del número de días que duran los proyectos cada curso.
     * 
     * @return media del número de días que duran los proyectos cada curso.
     */
    public Map<String, Number> getAverageTotalDays() {
        Map<String, Number> averageTotalDays = new HashMap<>();        
        // Recorremos los cursos
        for (String curso : courses) {
            double mean = 0;
            // contador para saber cuantos tfgs hay por curso
            int i = 0;
            Iterator<HistoricProject> iterator = dataHistoric.iterator();
            while (iterator.hasNext()) {               
                HistoricProject prj = iterator.next();
                if (curso.equals(prj.getCourse())) {
                    mean += prj.getTotalDays();
                    i++;
                }
            }
            mean = mean / i;
            averageTotalDays.put(curso, mean);
        }      
        return averageTotalDays;
    }

    /**
     * Genera las estadísticas (totales) anuales de los proyectos históricos.
     * Y crear a partir de esas estadísticas un gráfico para representar la media de
     * proyectos,
     * estudiantes, tutores asignados por cursos.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void createYearlyTotalStats() {
        //Hashmaps con la estadistica por curso
        Map<String,Number> yearlyStudentsCount=getStudentsCount();
        Map<String,Number> yearlyTutorsCount=getTutorsCount();
        
        //Arrays con las estaditicas recogidas 
        List<Number> yearlyAssignedProjects = new ArrayList<>();
        List<Number> yearlyAssignedStudents = new ArrayList<>();
        List<Number> yearlyAssignedTutors = new ArrayList<>();
        
       
        for(String curso:courses) {
            Number nTfgs= tfgsPerCourse.get(curso);
            yearlyAssignedProjects.add(nTfgs);
            yearlyAssignedStudents.add(yearlyStudentsCount.get(curso));
            yearlyAssignedTutors.add(yearlyTutorsCount.get(curso));
        }

        Label asignedYearlyProjects = new Label(
                "Número total de proyectos asignados por curso: " + yearlyAssignedProjects);
        Label asignedYearlyStudents = new Label(
                "Número total de alumnos asignados por curso: " + yearlyAssignedStudents);
        Label asignedYearlyTutors = new Label(
                "Número total de tutores con nuevas asignaciones por curso: " + yearlyAssignedTutors);
        add(asignedYearlyProjects, asignedYearlyStudents, asignedYearlyTutors);

        ApexCharts lineChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.line)
                        .withZoom(ZoomBuilder.get()
                                .withEnabled(false)
                                .build())
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withColors("#E91E63", "#4a6f22", "#9C27B0")
                        .withCurve(Curve.straight)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Métricas agrupadas por curso")
                        .withAlign(Align.center)
                        .build())
                .withGrid(GridBuilder.get()
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.5).build())
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(courses)
                        .build())
                .withSeries(new Series("Proyectos Asignados", yearlyAssignedProjects.toArray()),
                        new Series("Alumnos Asignados", yearlyAssignedStudents.toArray()),
                        new Series("Tutores Asignados", yearlyAssignedTutors.toArray()))
                .withColors("#E91E63", "#4a6f22", "#9C27B0")
                .build();
        lineChart.setWidth("800px");
        add(lineChart);
    }

    private Map<String, Number> getTutorsCount() {
        Map<String, Number> tutorsCount  = new HashMap<>();        
        // Recorremos los cursos
        for (String curso : courses) {
            int nTutors=0;
            Iterator<HistoricProject> iterator = dataHistoric.iterator();
            while (iterator.hasNext()) {               
                HistoricProject prj = iterator.next();
                if (curso.equals(prj.getCourse())) {
                    nTutors += prj.getNumTutors();
                }
            }
            tutorsCount.put(curso,nTutors);
        }      
        return tutorsCount;
    }

    /**
     * Obtiene el número de alumnos con proyectos de nueva asignación en cada
     * curso.
     * 
     * @return número de alumnos con proyectos de nueva asignación en cada
     *         curso.
     */
    public Map<String, Number> getStudentsCount() {
        Map<String, Number> studentsCount  = new HashMap<>();        
        // Recorremos los cursos
        for (String curso : courses) {
            int nStudents=0;
            Iterator<HistoricProject> iterator = dataHistoric.iterator();
            while (iterator.hasNext()) {               
                HistoricProject prj = iterator.next();
                if (curso.equals(prj.getCourse())) {
                    nStudents += prj.getNumStudents();
                }
            }
            studentsCount.put(curso,nStudents);
        }      
        return studentsCount;
    }

    /**
     * Obtiene el curso mínimo o máximo de los proyectos históricos.
     * 
     * @param isMinimum
     *                  si se quiere obtener el curso mínimo o máximo
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
            if (!projectFilter.isEmpty()) {
                applyFilter("title", event.getValue());
            } else {
                gridHistoric.setItems(dataHistoricGrid);
            }
        });

        tutorsFilter = new TextField("Filtrar por tutores:");
        tutorsFilter.setWidth("300px");
        tutorsFilter.addValueChangeListener(event -> {
            if (!tutorsFilter.isEmpty()) {
                applyFilter("tutor", event.getValue());
            } else {
                gridHistoric.setItems(dataHistoricGrid);
            }
        });

        assignmentDateFilter = new TextField("Filtrar por fecha de asignación:");
        assignmentDateFilter.setWidth("300px");
        assignmentDateFilter.addValueChangeListener(event -> {
            if (!assignmentDateFilter.isEmpty()) {
                applyFilter("assignmentDate", event.getValue());
            } else {
                gridHistoric.setItems(dataHistoricGrid);
            }
        });

        presentationDateFilter = new TextField("Filtrar por fecha de presentación:");
        presentationDateFilter.setWidth("300px");
        presentationDateFilter.addValueChangeListener(event -> {
            if (!presentationDateFilter.isEmpty()) {
                applyFilter("presentationDate", event.getValue());
            } else {
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
        gridHistoric.getColumns().subList(1, gridHistoric.getColumns().size())
                .forEach(columna -> columna.setTextAlign(ColumnTextAlign.CENTER));

        gridHistoric.setItemDetailsRenderer(
                new ComponentRenderer<>(HistoricProject -> {
                    VerticalLayout layout = new VerticalLayout();
                    layout.add(new Label("Título: " +
                            HistoricProject.getTitle()));
                    layout.add(new Label("Descripción: " +
                            HistoricProject.getDescription()));
                    if (HistoricProject.getRepositoryLink() != "") {
                        String repo = HistoricProject.getRepositoryLink();
                        Html link = new Html("<label>Repositorio: <a href=\"" + repo + "\" target=\"_blank\">" + repo
                                + "</a></label>");
                        layout.add(link);
                    } else {
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
        String lowercase = valueChange.toLowerCase();
        if (!valueChange.equals(" ")) {
            while (iterator.hasNext()) {
                HistoricProject historicProject = iterator.next();

                switch (column) {
                    case "title":
                        if (historicProject.getTitle().contains(lowercase)) {
                            dataFilteredGrid.add(historicProject);
                        }
                        break;
                    case "tutor":
                        if (historicProject.getTutors().contains(lowercase)) {
                            dataFilteredGrid.add(historicProject);
                        }
                        break;
                    case "assignmentDate":
                        dateChange = LocalDate.parse(valueChange, dateTimeFormatter);
                        if (historicProject.getAssignmentDate().equals(dateChange)) {
                            dataFilteredGrid.add(historicProject);
                        }
                        break;
                    case "presentationDate":
                        dateChange = LocalDate.parse(valueChange, dateTimeFormatter);
                        if (historicProject.getPresentationDate().equals(dateChange)) {
                            dataFilteredGrid.add(historicProject);
                        }
                        break;
                }
            }
            // Se establece los nuevos valores del grid
            gridHistoric.setItems(dataFilteredGrid);
        }
    }

    /**
     * Se crea una nueva lista con los datos que se usarán en la tabla de
     * descripción de proyectos.
     */
    public void CreateDataModelToGrid() {
        String tutors = "";
        dataHistoricGrid = new ArrayList<HistoricProject>();
        Iterator<HistoricProject> iterator = dataHistoric.iterator();
        while (iterator.hasNext()) {
            HistoricProject historicProject = iterator.next();

            // para mi gusto, esta tiene que ser la lógica de la función getTutors()
            tutors = historicProject.getTutor1();
            if (!tutors.equals("")) {
                if (!historicProject.getTutor2().equals("")) {
                    tutors += ", " + historicProject.getTutor2();
                    if (!historicProject.getTutor3().equals("")) {
                        tutors += ", " + historicProject.getTutor3();
                    }
                }
            }

            historicProject.setTutors(tutors);
            dataHistoricGrid.add(historicProject);
        }
    }
}
