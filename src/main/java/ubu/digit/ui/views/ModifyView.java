package ubu.digit.ui.views;


import static ubu.digit.util.Constants.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;

import java.util.List;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.ActiveProject;
import ubu.digit.ui.entity.FormularioTFG;
import ubu.digit.ui.entity.HistoricProject;
import ubu.digit.util.ExternalProperties;

/**
 * Vista de proyectos históricos.
 * 
 * @author Javier de la Fuente Barrios.
 * @author Diana Bringas Ochoa
 */
@Route(value = "Modify")
@PageTitle("Modificar TFG")

public class ModifyView extends VerticalLayout {
    
    public static String tituloTFG;

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8431807779365780674L;

    /**
     * Logger de la clase.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyView.class.getName());

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
    public ModifyView(){
        
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
        add(footer);
    }

    /**
     * Metodo que permite la introducción de todos los datos del TFG y los guarda.
     */
    private void introducirDatos() {
        //Obtenemos los datos del TFG seleccionado previamente
        ActiveProject TFG= fachadaDatos.getTFG(tituloTFG);
        
        TextArea titulo =new TextArea("Título del TFG");
        titulo.setWidth("40%");
        titulo.addValueChangeListener(event->{
           titulo.setValue(event.getValue()); 
        });
        
        TextArea tituloCorto =new TextArea("Título corto del TFG");
        tituloCorto.setWidth("40%");
        tituloCorto.setValue(TFG.getTitle());
        tituloCorto.addValueChangeListener(event->{
            tituloCorto.setValue(event.getValue()); 
        });
        
        TextArea descripcion =new TextArea("Descripción del TFG");
        descripcion.setWidth("40%");
        descripcion.setHeight("30%");
        descripcion.setValue(TFG.getDescription());
        descripcion.addValueChangeListener(event->{
            descripcion.setValue(event.getValue()); 
        });
        
        List<String> profesores = fachadaDatos.getProfesores();
        
        ComboBox<String> tutor1=new ComboBox<>("Tutor 1 del TFG");
        tutor1.setAllowCustomValue(true);
        tutor1.setWidth("40%");
        tutor1.setItems(profesores);
        tutor1.setValue(TFG.getTutor1());
        tutor1.addValueChangeListener(event -> {
            tutor1.setValue(event.getValue());
        });
        tutor1.addCustomValueSetListener(event -> {
            tutor1.setValue(event.getDetail());
            Notification.show("Estas introduciendo un nombre que no está en la EPS, ¿estas seguro?");
        });
        ComboBox<String> tutor2=new ComboBox<>("Tutor 2 del TFG");
        tutor2.setAllowCustomValue(true);
        tutor2.setWidth("40%");
        tutor2.setItems(profesores);
        tutor2.setValue(TFG.getTutor2());
        tutor2.addValueChangeListener(event -> {
            tutor2.setValue(event.getValue());          
        });
        tutor2.addCustomValueSetListener(event -> {
            tutor2.setValue(event.getDetail());
            Notification.show("Estas introduciendo un nombre que no está en la EPS, ¿estas seguro?");
        });
        ComboBox<String> tutor3=new ComboBox<>("Tutor 3 del TFG");
        tutor3.setAllowCustomValue(true);
        tutor3.setWidth("40%");
        tutor3.setItems(profesores);
        tutor3.setValue(TFG.getTutor3());
        tutor3.addValueChangeListener(event -> {
            tutor3.setValue(event.getValue());          
        });
        tutor3.addCustomValueSetListener(event -> {
            tutor3.setValue(event.getDetail());
            Notification.show("Estas introduciendo un nombre que no está en la EPS, ¿estas seguro?");
        });
        
        TextArea alumno1 =new TextArea("Alumno 1 del TFG");
        alumno1.setWidth("40%");
        alumno1.setValue(TFG.getStudent1());
        alumno1.addValueChangeListener(event->{
            alumno1.setValue(event.getValue()); 
        });
        TextArea alumno2 =new TextArea("Alumno 2 del TFG");
        alumno2.setWidth("40%");
        alumno2.setValue(TFG.getStudent2());
        alumno2.addValueChangeListener(event->{
            alumno2.setValue(event.getValue()); 
        });
        
        TextArea alumno3 =new TextArea("Alumno 3 del TFG");
        alumno3.setWidth("40%");
        alumno3.setValue(TFG.getStudent3());
        alumno3.addValueChangeListener(event->{
            alumno3.setValue(event.getValue()); 
        });

        TextArea cursoAsignacion=new TextArea("Curso de asignacion del TFG");
        cursoAsignacion.setWidth("40%");
        cursoAsignacion.setValue(TFG.getCourseAssignment());
        //Si se desea modificar el curso
        cursoAsignacion.addValueChangeListener(event->{
            cursoAsignacion.setValue(event.getValue()); 
        });
        
        DatePicker fechaAsignacion=new DatePicker("Fecha de asignacion del TFG");
        fechaAsignacion.setWidth("40%");
        fechaAsignacion.addValueChangeListener(event->{         
            fechaAsignacion.setValue(event.getValue()); 

        });

        DatePicker fechaPresentacion=new DatePicker("Fecha de presentacion del TFG");
        fechaPresentacion.setWidth("40%");
        fechaPresentacion.addValueChangeListener(event->{
            fechaPresentacion.setValue(event.getValue()); 
        });
        
        NumberField nota=new NumberField("Indique una nota del TFG");
        nota.setWidth("40%");
        nota.addValueChangeListener(event->{
            nota.setValue(event.getValue()); 
        });
        
        //int TotalDias= fechaAsignacion-fechaPresentacion;
        
        TextArea repo=new TextArea("Indique el enlace URL del repositorio");
        repo.setWidth("40%");
        repo.addValueChangeListener(event->{
            repo.setValue(event.getValue()); 
        });
        
        //Indicando que los campos son obligatorios para aceptar y mantener abierto
        Binder<FormularioTFG> binder= new Binder<>(FormularioTFG.class);
        binder.forField(tituloCorto).asRequired("Debes indicar un titulo").bind("tituloCorto");
        binder.forField(descripcion).asRequired("Debes indicar una descripción").bind("descripcion");
        binder.forField(tutor1).asRequired("Debes indicar un tutor1").bind("tutor1");
        binder.forField(alumno1).asRequired("Debes indicar un alumno").bind("alumno1");
        
        //AÑADIMOS LOS PARAMETROS QUE DEBEN SER OBLIGATORIOS PARA CERRAR UN TFG (los mismos que para dejarlo abierto y alguno más)
        Binder<FormularioTFG> binder2= binder;
       /* binder2.forField(fechaAsignacion).asRequired("Debes indicar una fecha de asignacion").bind("fechaAsignacion");
        binder2.forField(fechaPresentacion).asRequired("Debes indicar una fecha de presentacion").bind("fechaPresentacion");
        binder2.forField(nota).asRequired("Debes indicar una nota").bind("nota");
        binder2.forField(repo).asRequired("Debes indicar un repositorio").bind("repo");
        */
        
        Button AceptaryAbierto= new Button("Aceptar cambios y dejar abierto");
        Button AceptaryCerrado= new Button("Aceptar cambios y mover a histórico");
        
        //Cancelamos y volvemos a la pestaña de administrar tfgs
        Button cancelar= new Button("Cancelar cambios");
        cancelar.addClickListener(event -> {
            UI.getCurrent().navigate(ManageView.class);
        });
        
        AceptaryAbierto.addClickListener(event ->{
            if(binder.validate().isOk()) {
                Notification.show("Se ha modificado correctamente el TFG propuesto"); 
            }else {
                LOGGER.info("FALTAN PARAMETROS POR RELLENAR");
            }
        });
        
        AceptaryCerrado.addClickListener(event ->{
            if(binder2.validate().isOk()) {
                Notification.show("Se ha modificado correctamente el TFG propuesto");
            }else {
                LOGGER.info("FALTAN PARAMETROS POR RELLENAR");
            }
        });
 
        add(titulo,tituloCorto,descripcion,tutor1,tutor2,tutor3,alumno1,alumno2,alumno3,cursoAsignacion,fechaAsignacion,fechaPresentacion,nota,repo);
       HorizontalLayout layout= new HorizontalLayout();
       layout.add(AceptaryAbierto,AceptaryCerrado,cancelar);
       add(layout);
        
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

       public HistoricProject detallesTFG(String TFG){
        
        return null;
           
       }
    
    
}