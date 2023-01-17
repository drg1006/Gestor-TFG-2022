package ubu.digit.ui.views;

import static ubu.digit.util.Constants.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import com.opencsv.CSVWriter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;


/**
 * Vista del histórico de profesores.
 *
 * @author David Renedo Gil
 */
@Route(value = "Profesores")
@PageTitle("Histórico de los profesores")

public class ProfesoresView extends VerticalLayout {
    VerticalLayout layout = new VerticalLayout();
    
    
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
     * ArrayList con los profesores seleccionados del desplegable.
     */
     List<String> profSelect = new ArrayList<>();
     
    /**
     * Constructor.
     * @throws SQLException 
     */
    public ProfesoresView(){
        layout.setSpacing(true);
        fachadaDatos = SistInfDataFactory.getInstanceData();
        config = ExternalProperties.getInstance("/config.properties", false);
        numberFormatter = NumberFormat.getInstance();
        numberFormatter.setMaximumFractionDigits(2);
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        setMargin(true);
        setSpacing(true);

        NavigationBar bat = new NavigationBar();
        bat.buttonHistoric.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        bat.buttonProfessorHistoric.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(bat);
        //Este submenu solo les sale a los profesores/administradores
        if(LoginView.permiso.contains("update")||LoginView.permiso.contains("reports")){
           add( bat.subMenu());}
        
        
        crearEstadisticas();
        datosGraficas();
        //Si tiene permisos de administrador para actualizar archivos
        if(LoginView.permiso.contains("update")) {
        preguntarSiActualizar();}
        add(layout);
        Footer footer = new Footer("N4_Profesores.csv");
        add(footer);
    }
    
    /**
     * Crea las métricas de los proyectos activos.
     */
    private void crearEstadisticas() {
        H1 metricsTitle = new H1(INFO_ESTADISTICA);
        metricsTitle.addClassName("lbl-title");
        layout.add(metricsTitle);
       
        try {
            
            Number numProfes= fachadaDatos.getNumProfesores();
            Label totalProfessors = new Label("- Número total de profesores: " +numProfes.intValue());
            
            Number numAreas= fachadaDatos.getNumAreas();
            Label totalAreas = new Label("- Número total de areas: "+numAreas.intValue() );
           
            Number numDepartamentos= fachadaDatos.getNumDepartamentos();
            Label totalDepartments = new Label("- Número total de departamentos: "+ numDepartamentos.intValue());
            layout.add(totalProfessors, totalAreas, totalDepartments);
        } catch (Exception e) {
            LOGGER.error("Error en estadísticas", e);
        }
    }
   /**
    * Metodo para preguntar al usuario si quiere actualizar los datos de los archivos.
    */
    private void preguntarSiActualizar() {
        Button actualizar = new Button("Si");
        actualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Footer footer= new Footer("");
        String lastModifiedXls = footer.getLastModified(NOMBRE_BASES);
        
        Label ultimaAct= new Label("La última actualización de los datos fue el: " + lastModifiedXls+" ¿Quiere actualizar los datos? ");
        Label AVISO= new Label("Este proceso puede llevar un tiempo");
        layout.add(ultimaAct,AVISO,actualizar);   
        actualizar.addClickListener(event -> {
            long startTime = System.nanoTime();
           actualizarDatos();
           long estimatedTime = System.nanoTime() - startTime;
           double seconds =   (double)estimatedTime/1000000000.0;
           Notification notification = Notification.show("Se han actualizado los archivos, el proceso ha tardado: "+seconds + " segundos");
            
        });
        
    }
    /**
     * Metodo que actualiza los datos realizando el webscraping a la web de investigadores dela ubu.
     */
    public void actualizarDatos() {
        List<String[]> profesores = new ArrayList<String[]>();
        Map<String, Object[]> dataTFG = new TreeMap<String, Object[]>(); 
        Response response = null;
        try {
            //Realizamos la petición de la url
            response = Jsoup.connect("https://investigacion.ubu.es/unidades/2682/investigadores")
                   .ignoreContentType(true)
                   .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")  
                   .referrer("http://www.google.com")   
                   .timeout(12000) 
                   .followRedirects(true)
                   .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = response.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
            
            // Busco todas las entradas que estan dentro de: 
            Elements entradas = doc.select("div.c-persona-card__detalles");
            //System.out.println("Número de profesores de la EPS : "+entradas.size()+"\n");
            // Paseo cada una de las entradas
            int i=1;
            for (Element elem : entradas) {
                
                /*Referencia de estas dos lineas de código:
                 * https://stackoverflow.com/questions/30408174/jsoup-how-to-get-href*/
                
                //Cogemos la url de detalles para posteriormente coger su departamento
                Element link = elem.select("div.c-persona-card__detalles > a").first();
                String url = link.absUrl("href");
                
                //Cogemos los elementos que necesitamos
                String nombre = elem.getElementsByClass("c-persona-card__nombre").text();
                String apellidos = elem.getElementsByClass("c-persona-card__apellidos").text();
                String area = elem.getElementsByClass("c-persona-card__area").text();


                //Para sacar el Departamento debemos ir a otra url 
                try {
                    response = Jsoup.connect(url)
                           .ignoreContentType(true)
                           .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")  
                           .referrer("http://www.google.com")   
                           .timeout(12000) 
                           .followRedirects(true)
                           .execute();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Document doc2 = null;
                try {
                    doc2 = response.parse();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //Obtenemos el contenido donde está la información del profesor
                Elements entrada= doc2.select("div.main-content");
                //Cogemos el primer link de tipo "a"(href) que tiene la información sobre el departamento y sacamos su texto
                Element link2= entrada.select("a").first();
                String departamento = link2.text(); 

                // Con el método "text()" obtengo el contenido que hay dentro de las etiquetas HTML
                // Con el método "toString()" obtengo todo el HTML con etiquetas incluidas
                //https://commons.apache.org/proper/commons-lang//apidocs/org/apache/commons/lang3/StringUtils.html#stripAccents-java.lang.String-
                String [] profesor= {nombre +" "+ apellidos, area, departamento};
                
                profesores.add(profesor);
                i++;
                if(i==2) {
                    dataTFG.put("1", new Object[] {"NombreApellidos", "Area","Departamento"});
                    dataTFG.put("2",profesor);
                }else {
                    String id=Integer.toString(i);
                    dataTFG.put(id,profesor);
                }  
                
            }
            
            try {
                guardarDatosXLS(dataTFG);
                guardarDatosCSV(profesores);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }
    /**
     * Metodo paara escribir los datos en el archivo csv.
     * @param profesores
     * @throws IOException
     */
    public void guardarDatosCSV(List<String[]> profesores) throws IOException {
        //https://www.campusmvp.es/recursos/post/como-leer-y-escribir-archivos-csv-con-java.aspx 
 
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String serverPath = path.substring(0, path.length()-17);
        
        ExternalProperties config = ExternalProperties.getInstance("/config.properties", false);
        String dir = config.getSetting("dataIn");
        String completeDir = serverPath + dir + "/";
        String fileName = "N4_Profesores.csv";
        File file = new File(completeDir + fileName);
        String absPath = file.getAbsolutePath();
        CSVWriter writer = new CSVWriter(new FileWriter(absPath));      
        writer.writeAll(profesores);
        writer.close();
        SistInfDataFactory.setInstanceData("CSV");
    }
    /**
     * Metodo para escribir los datos en el archivo XLS.
     * @param dataTFG
     * @throws IOException
     */
    public void guardarDatosXLS(Map<String, Object[]> dataTFG) throws IOException {
        //https://www.codejava.net/coding/java-example-to-update-existing-excel-files-using-apache-poi
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String serverPath = path.substring(0, path.length()-17);
        
        ExternalProperties config = ExternalProperties.getInstance("/config.properties", false);
        String dir = config.getSetting("dataIn");
        String completeDir = serverPath + dir + "/";
        String fileName = NOMBRE_BASES;
        File file = new File(completeDir + fileName);
 
        String absPath = file.getAbsolutePath();       
        try {
            FileInputStream inputStream = new FileInputStream(new File(absPath));
            Workbook workbook = WorkbookFactory.create(inputStream);
 
            Sheet hoja= workbook.getSheet(PROFESOR);
      
            Row rowCount;
              
            Set<String> keyid = dataTFG.keySet();
            
            int rowid = 0;
            
            //https://es.acervolima.com/como-escribir-datos-en-una-hoja-de-excel-usando-java/
            // writing the data into the sheets...
      
            for (String key : keyid) {
      
                rowCount = hoja.createRow(rowid++);
                Object[] objectArr = dataTFG.get(key);
                int cellid = 0;
      
                for (Object obj : objectArr) {
                    Cell cell = rowCount.createCell(cellid++);
                    cell.setCellValue((String)obj);
                }
            }
 
            FileOutputStream outputStream = new FileOutputStream(absPath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            SistInfDataFactory.setInstanceData("XLS");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Metodo que despliega los datos de departamentos,areas y profesores para seleccionarlos.
     */
    public void datosGraficas(){
        H2 metricsTitle = new H2("DATOS A MOSTRAR");
        metricsTitle.addClassName("lbl-title");
        layout.add(metricsTitle);
        List<String> courses = new ArrayList<>();
        //REUTILIZAMOS PARTES DEL CODIGO DE OTRAS CLASES
        HistoricProjectsView vista= new  HistoricProjectsView();
        for (int year = vista.minCourse; year <= vista.maxCourse; year++) {
            courses.add(year - 1 + "/" + year);
        }
        String[] colors =getRandomColors();
        //Grafico
        ApexCharts lineChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.line)
                        .withZoom(ZoomBuilder.get()
                                .withEnabled(false)
                                .build())
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withColors(colors)
                        .withCurve(Curve.straight)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Número de TFGs asignados por curso")
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
                .withSeries(new Series())
                .withColors(colors)
                .build();
        lineChart.setWidth("800px");
        
        //Checkbox de areas
        Checkbox checkboxA = new Checkbox("Seleccionar todas las Areas");
        List<String> areas= fachadaDatos.getAreas();
       
        CheckboxGroup<String> selectAreas = new CheckboxGroup<>();
        selectAreas.setLabel("Areas:");
        selectAreas.setItems(areas);
        selectAreas.addValueChangeListener(event -> {
            if (event.getValue().size() == areas.size()) {
                checkboxA.setValue(true);
                checkboxA.setIndeterminate(false);
            } else if (event.getValue().size() == 0) {
                checkboxA.setValue(false);
                checkboxA.setIndeterminate(false);
            } else {
                checkboxA.setIndeterminate(true);
            }
        });
        checkboxA.addValueChangeListener(event -> {
            if (checkboxA.getValue()) {
                selectAreas.setValue(new HashSet<>(areas));
            } else {
                selectAreas.deselectAll();
            }
        });
      //Checkbox de departamentos
        Checkbox checkboxD = new Checkbox("Seleccionar todos los departamentos");
        List<String> departamentos= fachadaDatos.getDepartamentos();
        CheckboxGroup<String> selectDepart = new CheckboxGroup<>();
        
        selectDepart.setLabel("Departamentos:");
        selectDepart.setItems(departamentos);
           
        selectDepart.addValueChangeListener(event -> {
            if (event.getValue().size() == departamentos.size()) {
                checkboxD.setValue(true);
                checkboxD.setIndeterminate(false);
            } else if (event.getValue().size() == 0) {
                checkboxD.setValue(false);
                checkboxD.setIndeterminate(false);
            } else {
                checkboxD.setIndeterminate(true);
            }
            });
            checkboxD.addValueChangeListener(event -> {
                if (checkboxD.getValue()) {
                    selectDepart.setValue(new HashSet<>(departamentos));
                } else {
                    selectDepart.deselectAll();
                }
         

        });
            
          //ComboBox con los profesores
            ComboBox<String> filtroProfesores=new ComboBox<>("Indique el profesor");
            List<String> profesores = fachadaDatos.getProfesores();
            filtroProfesores.setItems(profesores);
            HorizontalLayout profes= new HorizontalLayout();

            filtroProfesores.addValueChangeListener(event -> {
                profSelect.add(event.getValue());
                
                Button buton = new Button(event.getValue(),new Icon(VaadinIcon.CLOSE_SMALL));
                buton.getElement().setAttribute("aria-label", "Close");
                buton.addClickListener(event2 ->{
                    profSelect.remove(event.getValue());
                    profes.remove(buton);
                    pintarGrafica(selectAreas.getValue(),selectDepart.getValue(),profSelect,lineChart);
                });
                profes.add(buton);
                layout.addComponentAtIndex(11, profes);
            });
                       
            //Boton para actualizar los datos de las graficas
            Button actualizar= new Button("Actualizar gráfica");
            actualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            actualizar.addClickListener(event -> {
                pintarGrafica(selectAreas.getValue(),selectDepart.getValue(),profSelect,lineChart);
            });
           
           layout.add(selectAreas,checkboxA,selectDepart,checkboxD,filtroProfesores,actualizar,lineChart);  
    } 
    
    /**
     * Metodo para obtener el array de colores aleatorios.
     * @return String[] colores
     */
    private String[] getRandomColors() {
        Random rand = new Random();
        String[] colors = new String[10];
        for (int i = 0; i < 10; i++) {
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            colors[i] = String.format("#%02x%02x%02x", r, g, b);
        }
        return colors;
    }
 
    /**
     * Metodo para actualizar y pintar la grafica cuando el usuario ha elegido los parametros.
     * @param areas
     * @param departamentos
     * @param profSelect
     * @param lineChart grafico
     */
    public void pintarGrafica(Set<String> areas, Set<String> departamentos, List<String> profSelect, ApexCharts lineChart) {
        //Creamos el array de series
        Series[] series = new Series[departamentos.size()+areas.size()+profSelect.size()];
        //variable para indicar el index del array
        int n=0;
        //Cogemos los tfgs de los departamentos
        for(String dep:departamentos) { 
            series[n]=new Series(dep,obtenerTFGSañoDepartamento(dep).toArray());      
            n++;
         }
      //Cogemos los tfgs de las areas
         for(String area:areas) {
             series[n]=new Series(area,obtenerTFGSañoArea(area).toArray());
             n++;
         }
         
         for (String profe: profSelect) {
             series[n]=new Series(profe,obtenerTFGSañoProfesor(profe).toArray());
             n++;
             
         }  
         lineChart.updateSeries(series);
    }
    /**
     * Metodo que obtiene el array con los TFGs del profesor que se le pasa por parametro.
     * @param profesor
     * @return list<Integer> de tfgs por curso
     */
    private List<Integer> obtenerTFGSañoProfesor(String profesor) {
        List<Integer> TFGs = new ArrayList<>();
        //Datos ya obtenidos en historicos
        HistoricProjectsView vista= new  HistoricProjectsView();
           //Formato de la fecha
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Bucle para sacar los TFGs que tiene asignado un profesor por cada año, siendo el año de presentacion del tfg el curso en el que se incluye
        //Ejemplo: Fecha de presentacion 10-01-2021: (Curso 2020-2021)

        for(int año=vista.minCourse-1;año<vista.maxCourse;año++) {
            int num1=0;
            //El curso va del 1 de septiembre de un año al siguiente
            String fechaIni=año+"-09-01";
            String fechaFin=(año+1)+"-09-01";
            LocalDate fechaINI = LocalDate.parse(fechaIni,formato);
            LocalDate fechaFIN = LocalDate.parse(fechaFin,formato);
            for(int i=0;i<vista.dataHistoric.size();i++) {
                if(vista.dataHistoric.get(i).getPresentationDate().isAfter(fechaINI)
                    && vista.dataHistoric.get(i).getPresentationDate().isBefore(fechaFIN)
                    && vista.dataHistoric.get(i).getTutor1()==profesor) {
                    num1++; 
                }
           }
           TFGs.add(num1); 
        }
        return TFGs;   
        
    }
    /**
     * Metodo que obtiene el array con los TFGs por curso del departamento que se le pasa por parametro.
     * @param departamento
     * @return list<Integer> de tfgs por curso
     */
    private List<Integer> obtenerTFGSañoDepartamento(String departamento) {
        // Sacamos los profesores que pertenecen a ese departamento y sumamos sus TFGs
        List<Integer> TFGs = new ArrayList<>();
        List<String> profes = new ArrayList<>();
        profes.addAll(fachadaDatos.getProfesoresDeDepartamento(departamento));
        int n=0;
            for(String profe: profes) {
                //Obtenemos los tfgs de los profesores que pertenecen al departamento
                List<Integer> tfgprofes=obtenerTFGSañoProfesor(profe);
                if(n==0) {
                    //Si es el primer profesor se añade al array
                    n++;
                    TFGs.addAll(tfgprofes); 
                }else {
                    //Si no es el primero se suma al anterior
                    for(int i=0;i<TFGs.size();i++) {
                        TFGs.set(i, TFGs.get(i)+tfgprofes.get(i));
                    }
                }          
        }
        return TFGs;
        
    }
    /**
     * Metodo que obtiene el array con los TFGs por curso del area que se le pasa por parametro.
     * @param area 
     * @return list<Integer> de tfgs por curso
     */
    private List<Integer> obtenerTFGSañoArea(String area) {
     // Sacamos los profesores que pertenecen a ese area y sumamos sus TFGs
        List<Integer> TFGs = new ArrayList<>();
        List<String> profes = new ArrayList<>();
        profes.addAll(fachadaDatos.getProfesoresDeArea(area));
        int n=0;
            for(String profe: profes) {
                List<Integer> tfgprofes=obtenerTFGSañoProfesor(profe);
                if(n==0) {
                    n++;
                    TFGs.addAll(tfgprofes); 
                }else {
                    for(int i=0;i<TFGs.size();i++) {
                        TFGs.set(i, TFGs.get(i)+tfgprofes.get(i));
                    }
                }          
        }
        return TFGs;
    }
    
    
}