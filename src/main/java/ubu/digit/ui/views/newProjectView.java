package ubu.digit.ui.views;


import static ubu.digit.util.Constants.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.FormularioTFG;
import ubu.digit.util.ExternalProperties;

/**
 * Vista de proyectos históricos.
 * 
 * @author Javier de la Fuente Barrios.
 * @author Diana Bringas Ochoa
 */
@Route(value = "Subir Proyecto")
@PageTitle("Proponer TFG")

public class newProjectView extends VerticalLayout {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8431807779365780674L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(newProjectView.class.getName());

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
	public newProjectView(){
	    
        fachadaDatos = SistInfDataFactory.getInstanceData();
		config = ExternalProperties.getInstance("/config.properties", false);
		numberFormatter = NumberFormat.getInstance();
		numberFormatter.setMaximumFractionDigits(2);
		dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		setMargin(true);
		setSpacing(true);

		NavigationBar bat = new NavigationBar();
		add(bat);
		introducirDatos();
		Footer footer = new Footer("");
		//
		add(footer);
	}

	/**
	 * Metodo que permite la introducción de todos los datos del TFG y los guarda.
	 */
    private void introducirDatos() {
        
        TextArea titulo =new TextArea("Indique un nombre para el TFG");
        titulo.setWidth("40%");
        titulo.addValueChangeListener(event->{
           titulo.setValue(event.getValue()); 
        });
        TextArea descripcion =new TextArea("Indique una descripción para el TFG");
        descripcion.setWidth("40%");
        descripcion.setHeight("30%");
        descripcion.addValueChangeListener(event->{
            descripcion.setValue(event.getValue()); 
        });
        TextArea tutor1 =new TextArea("Indique el tutor 1 del TFG");
        tutor1.setWidth("40%");
        tutor1.addValueChangeListener(event->{
            tutor1.setValue(event.getValue()); 
        });
        TextArea tutor2 =new TextArea("Indique el tutor 2 del TFG");
        tutor2.setWidth("40%");
        tutor2.addValueChangeListener(event->{
            tutor2.setValue(event.getValue()); 
        });
        TextArea tutor3 =new TextArea("Indique el tutor 3 del TFG");
        tutor3.setWidth("40%");
        tutor3.addValueChangeListener(event->{
            tutor3.setValue(event.getValue()); 
        });
        
        TextArea alumno1 =new TextArea("Indique el alumno 1 del TFG");
        alumno1.setWidth("40%");
        alumno1.setValue("Aalumnos sin asignar");
        alumno1.addValueChangeListener(event->{
            alumno1.setValue(event.getValue()); 
        });
        TextArea alumno2 =new TextArea("Indique el alumno 2 del TFG");
        alumno2.setWidth("40%");
        alumno2.addValueChangeListener(event->{
            alumno2.setValue(event.getValue()); 
        });
        

        TextArea cursoAsignacion=new TextArea("Indique el curso de asigancion del TFG");
        cursoAsignacion.setWidth("40%");

        Calendar c2 = new GregorianCalendar();
        Date hoy = new Date();
        int annio = c2.get(Calendar.YEAR);
        Date comienzoCurso= new Date(annio);
        // Compare the two dates using the compareTo() method
        if(hoy.after(comienzoCurso)) {
           cursoAsignacion.setValue(String.valueOf(annio)+"-"+String.valueOf(annio+1));
        }else {
            cursoAsignacion.setValue(String.valueOf(annio-1)+"-"+String.valueOf(annio));
        }
        
        
        cursoAsignacion.addValueChangeListener(event->{
            cursoAsignacion.setValue(event.getValue()); 
        });
        //Indicando que los campos son obligatorios
        Binder<FormularioTFG> binder= new Binder<>(FormularioTFG.class);
        binder.forField(titulo).asRequired("Debes indicar un titulo").bind("titulo");
        binder.forField(descripcion).asRequired("Debes indicar una descripción").bind("descripcion");
        binder.forField(tutor1).asRequired("Debes indicar un tutor1").bind("tutor1");
        binder.forField(alumno1).asRequired("Debes indicar un alumno").bind("alumno1");
        
        Button crear= new Button("Crear TFG");
        crear.addClickListener(event ->{
            if(binder.validate().isOk()) {
            escribirDatos(titulo.getValue(),descripcion.getValue(),tutor1.getValue(),tutor2.getValue(),tutor3.getValue(),
                    alumno1.getValue(),alumno2.getValue(),cursoAsignacion.getValue());
            }else {
                LOGGER.info("FALTAN PARAMETROS POR RELLENAR");
            }
        });
        add(titulo,descripcion,tutor1,tutor2,tutor3,alumno1,alumno2,cursoAsignacion,crear);
       
        
    }
    /**
     * Metodo que guarda los datos en un Array [].
     * @param titulo titulo del tfg
     * @param descripcion descripcion del tfg
     * @param tutor1 tutor1
     * @param tutor2 tutor2
     * @param tutor3 tutor3
     * @param alumno1 alumno1
     * @param alumno2 alumno2
     * @param cursoAsignacion curso
     */
    private void escribirDatos(String titulo, String descripcion, String tutor1, String tutor2, String tutor3, String alumno1, String alumno2,
            String cursoAsignacion) {
        String [] TFG= {titulo,descripcion,tutor1,tutor2,tutor3,alumno1,alumno2," ",cursoAsignacion,"Pendiente"};
        try {
            guardarDatosXLS(TFG);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    /**
     * Metodo para escribir los datos en el archivo XLS.
     * @param tFG
     * @throws IOException
     */
    public void guardarDatosXLS(String[] TFG) throws IOException {
        //https://www.codejava.net/coding/java-example-to-update-existing-excel-files-using-apache-poi
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String serverPath = path.substring(0, path.length()-17);
        
        ExternalProperties config = ExternalProperties.getInstance("/config.properties", false);
        String dir = config.getSetting("dataIn");
        String completeDir = serverPath + dir + "/";
        String fileName = NOMBRE_BASES;
        File file = new File(completeDir + fileName);
        
        String absPath = file.getAbsolutePath();       
        System.out.println("absPath "+absPath);
        try {
            FileInputStream inputStream = new FileInputStream(new File(absPath));
            Workbook workbook = WorkbookFactory.create(inputStream);
       
            
            Sheet hoja= workbook.getSheet(PROYECTO);
            int rowid = hoja.getLastRowNum();

            Row fila = hoja.createRow(++rowid);
            Object[] objectArr = TFG;
            //Creamos la columna de Estado, fila 1, columna 9
            Row fila1 = hoja.getRow(0);
            Cell estado=fila1.createCell(9);
            estado.setCellValue("Estado");
            
            int cellid = 0;
      
            for (Object obj : objectArr) {
                Cell cell = fila.createCell(cellid++);
                cell.setCellValue((String)obj);
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

    
	
}