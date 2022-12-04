package ubu.digit.ui.views;


import java.io.File;
import java.io.FileOutputStream;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.NumberFormat;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.server.StreamResource;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;

/**
 * Vista de proyectos históricos.
 * 
 * @author Javier de la Fuente Barrios.
 * @author Diana Bringas Ochoa
 */
@Route(value = "Informe")
@PageTitle("Generar Informe")

public class ReportView extends VerticalLayout {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8431807779365780674L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "Report";
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
	public ReportView(){
		
		fachadaDatos = SistInfDataFactory.getInstanceData();
		config = ExternalProperties.getInstance("/config.properties", false);
		numberFormatter = NumberFormat.getInstance();
		numberFormatter.setMaximumFractionDigits(2);
		dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		setMargin(true);
		setSpacing(true);

		NavigationBar bat = new NavigationBar();
		add(bat);
		
		opciones();
		
		Footer footer = new Footer("");
		//
		add(footer);
	}
	


	public void opciones() {
	    Checkbox checkbox = new Checkbox("Seleccionar Todas");
	    List<String> areas= fachadaDatos.getAreas();
	    CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
	    checkboxGroup.setLabel("Areas");
	    checkboxGroup.setItems(areas);
	    
	    //checkboxGroup.addThemeVariants(CheckboxGroupVariant.MATERIAL_VERTICAL);
	    checkboxGroup.addValueChangeListener(event -> {
	        if (event.getValue().size() == areas.size()) {
	            checkbox.setValue(true);
	            checkbox.setIndeterminate(false);
	        } else if (event.getValue().size() == 0) {
	            checkbox.setValue(false);
	            checkbox.setIndeterminate(false);
	        } else {
	            checkbox.setIndeterminate(true);
	        }
	    });
	    checkbox.addValueChangeListener(event -> {
	        if (checkbox.getValue()) {
	            checkboxGroup.setValue(new HashSet<>(areas));
	        } else {
	            checkboxGroup.deselectAll();
	        }

	    });
	    //SELECCIONAR POR DEFECTO
	        //checkboxGroup.select(areas.get(0), areas.get(2));
	    //ARRAY CON LA LISTA DE AREAS SELECCIONADAS
            //System.out.println("Value changed:"+ checkboxGroup.getValue());
	    
	    //TEXTO PARA AÑADIR NOMBRE AL INFORME
	    TextField nombreInforme = new TextField();
	    nombreInforme.setLabel("Indique el nombre del informe");
	    nombreInforme.setWidth("25%");

	    nombreInforme.addValueChangeListener(event ->{
	       nombreInforme.setValue(event.getValue());
	    });
	    
	    //BOTON PARA CREAR EL INFORME 
	    Button crearInforme = new Button("Crear Informe");
	    crearInforme.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    
	    ComboBox<String> años = new ComboBox<String>("Seleccione un curso");
	    //List<String> curso = fachadaDatos.get;
       // años.setItems(curso);
	    add(checkbox, checkboxGroup,nombreInforme,crearInforme);
	    //Anchor anchor = new Anchor(getStreamResource("default.txt", "default content"), "click me to download");
        //anchor.getElement().setAttribute("download",true);
        
	    crearInforme.addClickListener(event -> {
	       File file= creacionInforme(checkboxGroup.getValue(),nombreInforme.getValue());
	       // descargarInforme(file);
	       // anchor.setHref(file.getValue(), file.getValue());
	        
	    });
	    

	}
	/*
	public StreamResource getStreamResource(String filename, String content) {
        return new StreamResource(filename,
                () -> new ByteArrayInputStream(content.getBytes()));
    }
*/

    public File creacionInforme(Set<String> listaAreas, String nombreInforme) {
	       
	       File archivo = new File(nombreInforme+".xls");
	       Map<String, Object[]> dataTFG = new TreeMap<String, Object[]>();
	       Workbook workbook = new HSSFWorkbook();
	       try {

	            for(String area: listaAreas) {
	                Sheet hoja=workbook.createSheet(area);
	                dataTFG = obtencionDatos(area);
	                Row rowCount=null;
	                
	                Set<String> keyid = dataTFG.keySet();
	                int rowid = 0;
	                
	                //https://es.acervolima.com/como-escribir-datos-en-una-hoja-de-excel-usando-java/
	                // writing the data into the sheets...
	                for (String key : keyid) {

	                    rowCount = workbook.getSheet(hoja.getSheetName()).createRow(rowid++);
	                    Object[] objectArr = dataTFG.get(key);
	                    
	                    int cellid = 0;
	                    for (Object obj : objectArr) {
	                        Cell cell = rowCount.createCell(cellid++);
	                        cell.setCellValue((String)obj);
	                    }
	                }
	            }
	            //Se genera el documento
	            FileOutputStream out = new FileOutputStream(archivo);
	            workbook.write(out);
	            workbook.close();
	            out.close();
	            Notification notification = Notification.show("Se ha generado el fichero en " + archivo);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        return archivo;
	   }



    private Map<String, Object[]> obtencionDatos(String area) {
        Map<String, Object[]> dataTFG = new TreeMap<String, Object[]>();
        List<String> profes =fachadaDatos.getProfesoresDeArea(area);
        int i=1;
        for(String prof:profes) {
            i++;
            //TFGs dirigidos
            Number tfgs = fachadaDatos.getNumTFGsProfesor(prof);
            //TFGs codirigidos
            Number tfgs2 = fachadaDatos.getNumTFGsCOProfesor(prof);
            String [] profesor= {prof,tfgs.toString(),tfgs2.toString()};
            if(i==2) {
                dataTFG.put("1", new Object[] {"Tutor","TFGs Dirigidos","TFGs CoDirigidos"});
                dataTFG.put("2",profesor);
            }else {
                String id=Integer.toString(i);
                dataTFG.put(id,profesor);
            } 
        }   

        return dataTFG;
    }
	   
    
    private void descargarInforme(File file) {
      //Directorio destino para las descargas
       
    }
    
}



