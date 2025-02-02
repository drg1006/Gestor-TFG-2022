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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.Formularios;
import ubu.digit.util.ExternalProperties;

/**
 * Vista para subir nuevos proyectos, por parte de administradores y profesores.
 * 
 * @author David Renedo Gil
 */
@Route(value = "SubirProyecto")
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
     * Fachada para obtener los datos
     */
    private SistInfDataAbstract fachadaDatos;

    /**
     * Constructor.
     * 
     * @throws SQLException
     */
    public newProjectView() {

        fachadaDatos = SistInfDataFactory.getInstanceData();
        config = ExternalProperties.getInstance("/config.properties", false);
        numberFormatter = NumberFormat.getInstance();
        numberFormatter.setMaximumFractionDigits(2);
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        setMargin(true);
        setSpacing(true);

        NavigationBar bat = new NavigationBar();
        bat.buttonUpload.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(bat);
        introducirDatos();
        Footer footer = new Footer(null);
        add(footer);
    }

    /**
     * Metodo que permite la introducción de todos los datos del TFG y los guarda.
     */
    private void introducirDatos() {

        TextArea titulo = new TextArea("Nombre del Trabajo de Fin de Grado");
        titulo.setWidth("40%");
        titulo.addValueChangeListener(event -> {
            titulo.setValue(event.getValue());
        });

        TextArea descripcion = new TextArea("Descripción");
        descripcion.setWidth("40%");
        descripcion.setHeight("30%");
        descripcion.addValueChangeListener(event -> {
            descripcion.setValue(event.getValue());
        });

        List<String> profesores = fachadaDatos.getProfesores();

        ComboBox<String> tutor1 = new ComboBox<>("Tutor 1");
        tutor1.setAllowCustomValue(true);
        tutor1.setWidth("40%");
        tutor1.setItems(profesores);
        tutor1.setValue((String) UI.getCurrent().getSession().getAttribute("tutorRegistrado"));
        tutor1.addValueChangeListener(event -> {
            tutor1.setValue(event.getValue());
        });
        tutor1.addCustomValueSetListener(event -> {
            tutor1.setValue(event.getDetail());
            if (!profesores.contains(tutor1.getValue()))
                Notification.show("Estas introduciendo un nombre de tutor1 que no está en la EPS, ¿estas seguro?");

        });
        ComboBox<String> tutor2 = new ComboBox<>("Tutor 2");
        tutor2.setAllowCustomValue(true);
        tutor2.setWidth("40%");
        tutor2.setItems(profesores);
        tutor2.addValueChangeListener(event -> {
            tutor2.setValue(event.getValue());
        });
        tutor2.addCustomValueSetListener(event -> {
            tutor2.setValue(event.getDetail());
            if (!profesores.contains(tutor2.getValue()))
                Notification.show("Estas introduciendo un nombre de tutor2 que no está en la EPS, ¿estas seguro?");
        });
        ComboBox<String> tutor3 = new ComboBox<>("Tutor 3");
        tutor3.setAllowCustomValue(true);
        tutor3.setWidth("40%");
        tutor3.setItems(profesores);
        tutor3.addValueChangeListener(event -> {
            tutor3.setValue(event.getValue());
        });
        tutor3.addCustomValueSetListener(event -> {
            tutor3.setValue(event.getDetail());
            if (!profesores.contains(tutor3.getValue()))
                Notification.show("Estas introduciendo un nombre de tutor3 que no está en la EPS, ¿estas seguro?");
        });

        TextArea alumno1 = new TextArea("Alumno 1");
        alumno1.setWidth("40%");
        alumno1.setValue("Aalumnos sin asignar");
        alumno1.addValueChangeListener(event -> {
            alumno1.setValue(event.getValue());
        });
        TextArea alumno2 = new TextArea("Alumno 2");
        alumno2.setWidth("40%");
        alumno2.addValueChangeListener(event -> {
            alumno2.setValue(event.getValue());
        });

        // Cogemos la fecha de hoy y comprobamos si está después de la fecha de inicio
        // de curso de ese mismo año
        // Indicarlo por defecto
        // Fecha de hoy
        LocalDate today = LocalDate.now();

        String fechaIni = today.now().getYear() + "-09-01";
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaINI = LocalDate.parse(fechaIni, formato);

        valoresPorDefecto(today, fechaINI, titulo);

        // Fecha de asignacion
        DatePicker fechaAsignacion = new DatePicker("Fecha de oferta/asignacion");
        fechaAsignacion.setLocale(getLocale());
        // Asignamos por defecto la fecha del día de subida
        fechaAsignacion.setValue(today);
        fechaAsignacion.setWidth("40%");
        // Si se desea modificar el curso
        fechaAsignacion.addValueChangeListener(event -> {
            fechaAsignacion.setValue(event.getValue());
        });
        // Indicando que los campos son obligatorios
        Binder<Formularios> binder = new Binder<>(Formularios.class);
        binder.forField(titulo).asRequired("Debes indicar un titulo").bind("titulo");
        binder.forField(descripcion).asRequired("Debes indicar una descripción").bind("descripcion");
        binder.forField(tutor1).asRequired("Debes indicar un tutor1").bind("tutor1");
        binder.forField(alumno1).asRequired("Debes indicar un alumno").bind("alumno1");

        Button crear = new Button("Crear TFG");
        crear.addClickListener(event -> {
            if (binder.validate().isOk()) {
                if (tutor1.getValue().equals(tutor2.getValue())) {
                    LOGGER.info("Tutor1 y tutor2 no pueden tener el mismo valor");
                    Notification notif2 = Notification.show("Tutor1 y tutor2 no pueden tener el mismo valor");
                    notif2.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else {

                    Notification.show("Se ha añadido correctamente el TFG propuesto");
                    escribirDatos(titulo.getValue(), descripcion.getValue(), tutor1.getValue(), tutor2.getValue(),
                            tutor3.getValue(),
                            alumno1.getValue(), alumno2.getValue(), fechaAsignacion);
                    try {
                        Thread.sleep(2000); // retraso en milisegundos
                        UI.getCurrent().getPage().reload();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                LOGGER.info("FALTAN PARAMETROS POR RELLENAR");
            }
        });
        add(titulo, descripcion, tutor1, tutor2, tutor3, alumno1, alumno2, fechaAsignacion, crear);

    }

    private void valoresPorDefecto(LocalDate today, LocalDate fechaINI, TextArea titulo) {
        if (today.now().isAfter(fechaINI)) {

            // Para el titulo hacemos lo mismo y del año 2022 cogemos solo el '22'
            // ya que queremos indicar por defecto el valor GII 'año'-'numeroSiguienteTfg'
            String año = String.valueOf(today.getYear());
            titulo.setValue("GII " + año.substring(2, 4) + "." + obtenerNumeroTFG(año));
        } else {

            String año = String.valueOf(today.getYear() - 1);
            titulo.setValue("GII " + año.substring(2, 4) + "." + obtenerNumeroTFG(año));
        }

    }

    /**
     * Obtiene el número que le corresponde al siguiente TFG.
     * 
     * @return numero del TFG
     */
    private String obtenerNumeroTFG(String año) {
        String numeroTFG;
        String titulo = fachadaDatos.getUltimoTFG();
        int temp = 0;
        // Comprobamos si es un TFG de un curso nuevo, es decir, si el último TFG es de
        // GII 22.XX y ahora estamos en el curso 2023
        // El nuevo numero sería el 0

        // Si es un año distino se inicia en 0
        if (Integer.parseInt("20" + titulo.substring(4, 6)) != Integer.parseInt(año)) {
            numeroTFG = "01";
        } else {
            // Mismo año, se suma uno
            temp = (Integer.parseInt(titulo.substring(7, 9)) + 1);
            // Por si es 01
            if (temp < 10)
                numeroTFG = "0" + temp;
            else
                numeroTFG = String.valueOf(temp);
        }

        return numeroTFG;
    }

    /**
     * Metodo que guarda los datos en un Array [].
     * 
     * @param titulo          titulo del tfg
     * @param descripcion     descripcion del tfg
     * @param tutor1          tutor1
     * @param tutor2          tutor2
     * @param tutor3          tutor3
     * @param alumno1         alumno1
     * @param alumno2         alumno2
     * @param fechaAsignacion curso
     */
    private void escribirDatos(String titulo, String descripcion, String tutor1, String tutor2, String tutor3,
            String alumno1, String alumno2,
            DatePicker fechaAsignacion) {
        String fecha = cambiarFormato(fechaAsignacion);
        String[] TFG = { titulo, descripcion, tutor1, tutor2, tutor3, alumno1, alumno2, " ", fecha, "Pendiente" };
        try {
            guardarDatosXLS(TFG);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Cambiamos el formata en el que esta la fecha de YYYY-MM-DD a DD/MM/YYYY.
     * 
     * @param fecha
     * @return string con la fecha
     */
    private String cambiarFormato(DatePicker fecha) {
        int year = fecha.getValue().getYear();
        int month = fecha.getValue().getMonthValue();
        int day = fecha.getValue().getDayOfMonth();
        // Una fecha con un día o menos menor que diez se guardaría como 1/1/2023 en
        // lugar de 01/01/2023 que es como queremos
        String monthBien = "";
        String dayBien = "";
        if (month < 10) {
            monthBien = "0" + month;
        } else {
            monthBien = String.valueOf(month);
        }
        if (day < 10) {
            dayBien = "0" + day;
        } else {
            dayBien = String.valueOf(day);
        }
        String fechaFormat = dayBien + "/" + monthBien + "/" + String.valueOf(year);
        return fechaFormat;
    }

    /**
     * Metodo para escribir los datos en el archivo XLS.
     * 
     * @param tFG
     * @throws IOException
     */
    public void guardarDatosXLS(String[] TFG) throws IOException {
        // https://www.codejava.net/coding/java-example-to-update-existing-excel-files-using-apache-poi
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String serverPath = path.substring(0, path.length() - 17);

        ExternalProperties config = ExternalProperties.getInstance("/config.properties", false);
        String dir = config.getSetting("dataIn");
        String completeDir = serverPath + dir + "/";
        String fileName = NOMBRE_BASES;
        File file = new File(completeDir + fileName);

        String absPath = file.getAbsolutePath();
        try {
            FileInputStream inputStream = new FileInputStream(new File(absPath));
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet hoja = workbook.getSheet(PROYECTO);
            int rowid = hoja.getLastRowNum();

            Row fila = hoja.createRow(++rowid);
            Object[] objectArr = TFG;

            int cellid = 0;

            for (Object obj : objectArr) {
                Cell cell = fila.createCell(cellid++);
                cell.setCellValue((String) obj);
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