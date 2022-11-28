package ubu.digit.ui.views;


import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.NumberFormat;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


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
	    //checkboxGroup.setItemLabelGenerator(
	            //person -> person.getFirstName() + " " + person.getLastName());
	    checkboxGroup.setItems(areas);
	    
	    checkboxGroup.addThemeVariants(CheckboxGroupVariant.MATERIAL_VERTICAL);
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
	       System.out.println(nombreInforme.getValue());
	    });
	    
	    //BOTON PARA CREAR EL INFORME 
	    Button crearInforme = new Button("Crear Informe");
	    crearInforme.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    add(checkbox, checkboxGroup,nombreInforme,crearInforme);
	    
	    crearInforme.addClickListener(event -> {
	        creacionInforme(checkboxGroup.getValue(),nombreInforme.getValue());
	        
	    });

	}
	
	   public void creacionInforme(Set<String> listaAreas, String nombreInforme) {
	       
	       File archivo = new File(nombreInforme+".xls");
	       //String absPath=archivo.getAbsolutePath();
	       Workbook workbook = new HSSFWorkbook();
	       
	        
	       //int tamano =listaAreas.size();
	      
	       
	       try {
	            //FileInputStream inputStream = new FileInputStream(new File(absPath));
	            //Workbook workbook = WorkbookFactory.create(inputStream);
	            for(String area: listaAreas) {
	                workbook.createSheet(area);
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
	   }
	   
}
