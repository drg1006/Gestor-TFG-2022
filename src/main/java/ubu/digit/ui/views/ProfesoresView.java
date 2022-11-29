package ubu.digit.ui.views;

import static ubu.digit.util.Constants.INFO_ESTADISTICA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
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

import com.opencsv.CSVWriter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;
//import ubu.digit.ui.entity.Profesores;

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
		/*
		String path = this.getClass().getClassLoader().getResource("").getPath();
		
        String serverPath = path.substring(0, path.length()-17);
        String dir = config.getSetting("dataIn");
        String completeDir = serverPath + dir + "/";
        File file = new File(completeDir + "BaseDeDatosTFGTFG.xls");
        System.out.println("PATH:" + file.getAbsoluteFile());
        */
		
		preguntarSiActualizar();
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
   
    private void preguntarSiActualizar() {
        Button actualizar = new Button("Si");
        actualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Footer footer= new Footer("");
        String lastModifiedXls = footer.getLastModified("BaseDeDatosTFGTFM.xls");
        
        Label ultimaAct= new Label("La última actualización de los datos fue el: " + lastModifiedXls+" ¿Quiere actualizar los datos? ");
        Label AVISO= new Label("Este proceso puede llevar un tiempo");
        add(ultimaAct,AVISO,actualizar);
        actualizar.addClickListener(event -> {
           actualizarDatos();
            
        });
        
    }
    
    
    public  void actualizarDatos() {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = response.parse();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
            // Busco todas las entradas que estan dentro de: 
            Elements entradas = doc.select("div.c-persona-card__detalles");
            System.out.println("Número de profesores de la EPS : "+entradas.size()+"\n");
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
                //Imprimimos por pantalla
                //System.out.println("Nombre: "+nombre);
                //System.out.println("Apellidos: " +apellidos);
                //System.out.println("Area: "+area);
                //System.out.println("Departamento: "+ departamento + "\n");

                // Con el método "text()" obtengo el contenido que hay dentro de las etiquetas HTML
                // Con el método "toString()" obtengo todo el HTML con etiquetas incluidas
                //https://commons.apache.org/proper/commons-lang//apidocs/org/apache/commons/lang3/StringUtils.html#stripAccents-java.lang.String-
                nombre = StringUtils.stripAccents(nombre);
                apellidos =StringUtils.stripAccents(apellidos);
                area =StringUtils.stripAccents(area);
                departamento =StringUtils.stripAccents(departamento);
                String [] profesor= {nombre, apellidos, area, departamento};
                
                profesores.add(profesor);
                i++;
                if(i==2) {
                    dataTFG.put("1", new Object[] {"Nombre", "Apellidos", "Area","Departamento"});
                    dataTFG.put("2",profesor);
                }else {
                    String id=Integer.toString(i);
                    dataTFG.put(id,profesor);
                }
                
                
                
            }
            
            try {
                guardarDatosXLS(dataTFG);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            try {
                guardarDatosCSV(profesores);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
    }
    
    public  void guardarDatosCSV(List<String[]> profesores) throws IOException {
        //https://www.campusmvp.es/recursos/post/como-leer-y-escribir-archivos-csv-con-java.aspx 
        String excelFilePath = "src/main/resources/data/N4_Profesores.csv";
        File file = new File(excelFilePath);
        String absPath = file.getAbsolutePath();
        CSVWriter writer = new CSVWriter(new FileWriter(absPath));      
        writer.writeAll(profesores);
        writer.close();
    }
    
    public  void guardarDatosXLS(Map<String, Object[]> dataTFG) throws IOException {
        //https://www.codejava.net/coding/java-example-to-update-existing-excel-files-using-apache-poi
        
        String excelFilePath = "src/main/resources/data/BaseDeDatosTFGTFM.xls";
        File file = new File(excelFilePath);
        String absPath = file.getAbsolutePath();
        
        try {
            FileInputStream inputStream = new FileInputStream(new File(absPath));
            Workbook workbook = WorkbookFactory.create(inputStream);
 
            Sheet hoja= workbook.getSheet("N4_Profesores");
      
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
             
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
