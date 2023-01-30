package ubu.digit.ui.views;

import static ubu.digit.util.Constants.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.ActiveProject;
import ubu.digit.util.ExternalProperties;

/**
 * Vista para modificar un proyecto activo.
 * 
 * @author David Renedo Gil
 */
@Route(value = "Modify")
@PageTitle("Modificar TFG")

public class ModifyView extends VerticalLayout {

    /**
     * Variable en la que guardamos el titulo del TFG seleccionado.
     */
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
     * Fachada para obtener los datos
     */
    private SistInfDataAbstract fachadaDatos;

    /**
     * Constructor.
     * 
     * @throws SQLException
     */
    public ModifyView() {

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
        Footer footer = new Footer(null);
        add(footer);
    }

    /**
     * Metodo que permite la introducción de todos los datos del TFG y los guarda.
     */
    private void introducirDatos() {
        // Obtenemos los datos del TFG seleccionado previamente
        ActiveProject TFG = fachadaDatos.getTFG(tituloTFG, dateTimeFormatter);

        TextArea tituloCorto = new TextArea("Título corto del TFG");
        tituloCorto.setWidth("40%");
        tituloCorto.setValue(TFG.getTitle());
        tituloCorto.addValueChangeListener(event -> {
            tituloCorto.setValue(event.getValue());
        });

        TextArea descripcion = new TextArea("Descripción del TFG");
        descripcion.setWidth("40%");
        descripcion.setHeight("30%");
        descripcion.setValue(TFG.getDescription());
        descripcion.addValueChangeListener(event -> {
            descripcion.setValue(event.getValue());
        });

        List<String> profesores = fachadaDatos.getProfesores();

        ComboBox<String> tutor1 = new ComboBox<>("Tutor 1 del TFG");
        tutor1.setAllowCustomValue(true);
        tutor1.setWidth("40%");
        tutor1.setItems(profesores);
        tutor1.setValue(TFG.getTutor1());
        tutor1.addValueChangeListener(event -> {
            tutor1.setValue(event.getValue());
        });
        tutor1.addCustomValueSetListener(event -> {
            tutor1.setValue(event.getDetail());
            if (!profesores.contains(tutor1.getValue()))
                Notification.show("Estas introduciendo un nombre de tutor1 que no está en la EPS, ¿estas seguro?");
        });
        ComboBox<String> tutor2 = new ComboBox<>("Tutor 2 del TFG");
        tutor2.setAllowCustomValue(true);
        tutor2.setWidth("40%");
        tutor2.setItems(profesores);
        tutor2.setValue(TFG.getTutor2());
        tutor2.addValueChangeListener(event -> {
            tutor2.setValue(event.getValue());
        });
        tutor2.addCustomValueSetListener(event -> {
            tutor2.setValue(event.getDetail());
            if (!profesores.contains(tutor2.getValue()))
                Notification.show("Estas introduciendo un nombre de tutor2 que no está en la EPS, ¿estas seguro?");
        });
        ComboBox<String> tutor3 = new ComboBox<>("Tutor 3 del TFG");
        tutor3.setAllowCustomValue(true);
        tutor3.setWidth("40%");
        tutor3.setItems(profesores);
        tutor3.setValue(TFG.getTutor3());
        tutor3.addValueChangeListener(event -> {
            tutor3.setValue(event.getValue());
        });
        tutor3.addCustomValueSetListener(event -> {
            tutor3.setValue(event.getDetail());
            if (!profesores.contains(tutor3.getValue()))
                Notification.show("Estas introduciendo un nombre de tutor3 que no está en la EPS, ¿estas seguro?");
        });

        TextArea alumno1 = new TextArea("Alumno 1 del TFG");
        alumno1.setWidth("40%");
        alumno1.setValue(TFG.getStudent1());
        alumno1.addValueChangeListener(event -> {
            alumno1.setValue(event.getValue());
        });
        TextArea alumno2 = new TextArea("Alumno 2 del TFG");
        alumno2.setWidth("40%");
        alumno2.setValue(TFG.getStudent2());
        alumno2.addValueChangeListener(event -> {
            alumno2.setValue(event.getValue());
        });

        TextArea alumno3 = new TextArea("Alumno 3 del TFG");
        alumno3.setWidth("40%");
        alumno3.setValue(TFG.getStudent3());
        alumno3.addValueChangeListener(event -> {
            alumno3.setValue(event.getValue());
        });

        DatePicker fechaAsignacion = new DatePicker("Fecha de asignacion del TFG");
        fechaAsignacion.setValue(TFG.getDateAssignment());
        fechaAsignacion.setLocale(getLocale());
        fechaAsignacion.setWidth("40%");
        fechaAsignacion.addValueChangeListener(event -> {
            fechaAsignacion.setValue(event.getValue());

        });

        DatePicker fechaPresentacion = new DatePicker("Fecha de presentacion del TFG");
        fechaPresentacion.setEnabled(false);
        fechaPresentacion.setLocale(getLocale());
        fechaPresentacion.setWidth("40%");
        fechaPresentacion.addValueChangeListener(event -> {
            fechaPresentacion.setValue(event.getValue());
        });

        NumberField nota = new NumberField("Indique una nota del TFG");
        nota.setEnabled(false);
        nota.setMax(10);
        nota.setMin(0);
        nota.setErrorMessage("El valor debe estar entre 0 y 10");
        nota.setWidth("40%");
        nota.addValueChangeListener(event -> {
            nota.setValue(event.getValue());
        });

        TextArea repo = new TextArea("Indique el enlace URL del repositorio");
        repo.setEnabled(false);
        repo.setWidth("40%");
        repo.addValueChangeListener(event -> {
            repo.setValue(event.getValue());
        });

        // Botones para aceptar y cerrar o mantener abierto
        Button AceptaryAbierto = new Button("Aceptar cambios y dejar abierto");
        AceptaryAbierto.setEnabled(false);
        Button AceptaryCerrado = new Button("Aceptar cambios y mover a histórico");
        AceptaryCerrado.setEnabled(false);

        Button si = new Button("Sí");
        Button no = new Button("No");

        si.addClickListener(e -> {
            AceptaryCerrado.setEnabled(true);
            fechaPresentacion.setEnabled(true);
            nota.setEnabled(true);
            repo.setEnabled(true);
            si.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            no.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            // Por si se había pulsado previamente el botón de no
            AceptaryAbierto.setEnabled(false);

        });

        no.addClickListener(e -> {
            AceptaryAbierto.setEnabled(true);
            // Por si se había pulsado previamente el botón de sí
            AceptaryCerrado.setEnabled(false);
            fechaPresentacion.setEnabled(false);
            nota.setEnabled(false);
            repo.setEnabled(false);
            no.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            si.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

        HorizontalLayout layoutHist = new HorizontalLayout();
        layoutHist.add(si, no);

        // Cancelamos y volvemos a la pestaña de administrar tfgs
        Button cancelar = new Button("Cancelar cambios");
        cancelar.addClickListener(event -> {
            UI.getCurrent().navigate(ManageView.class);
        });

        AceptaryAbierto.addClickListener(event -> {
            // Comprobamos si los campos están o no rellenos
            if (vacios1(tutor1, alumno1, descripcion, tituloCorto)) {
                Dialog aviso1 = new Dialog();
                // Añadidmos un texto
                aviso1.add(
                        "Los parámetros tutor1, alumno1, tituloCorto y descripción son obligatorios para modificar y mantener activo un proyecto.");
                aviso1.open();
                Button closeButton = new Button(new Icon("lumo", "cross"),
                        (e) -> aviso1.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                aviso1.add(closeButton);
                LOGGER.info("FALTAN PARAMETROS POR RELLENAR");

            } else {
                // Guardamos los datos y actualizamos
                String fechaAsig = cambiarFormato(fechaAsignacion);
                stringAbierto(tituloCorto.getValue(), descripcion.getValue(), tutor1.getValue(), tutor2.getValue(),
                        tutor3.getValue(), alumno1.getValue(), alumno2.getValue(), alumno3.getValue(), fechaAsig);
                Notification.show("Se ha modificado correctamente el TFG propuesto en la pestaña de activos.");
            }
        });

        AceptaryCerrado.addClickListener(event -> {
            if (vacios1(tutor1, alumno1, descripcion, tituloCorto) ||
                    vacios2(fechaAsignacion, fechaPresentacion, nota, repo)) {
                Dialog aviso2 = new Dialog();
                // Añadidmos un texto
                aviso2.add(
                        "Los parámetros tutor1, alumno1, tituloCorto, descripción, curso de asignación, fecha de asignación, "
                                + "fecha de presentación, nota y enlace URL son obligatorios para modificar y cerrar un proyecto.");
                aviso2.open();
                Button closeButton = new Button(new Icon("lumo", "cross"),
                        (e) -> aviso2.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                aviso2.add(closeButton);
                LOGGER.info("FALTAN PARAMETROS POR RELLENAR");

            } else {
                // Obtenemos los dias totales de duracion del proyecto
                long dias = obtenerDiasTotales(fechaAsignacion, fechaPresentacion);
                // Cambiamos el formato de las fechas
                String fechaAsig = cambiarFormato(fechaAsignacion);
                String fechaPresen = cambiarFormato(fechaPresentacion);
                stringCerrado(tituloCorto.getValue(), descripcion.getValue(), tutor1.getValue(), tutor2.getValue(),
                        tutor3.getValue(), alumno1.getValue(), alumno2.getValue(), alumno3.getValue(), fechaAsig,
                        fechaPresen, nota.getValue(), dias, repo.getValue());
                Notification.show("Se ha eliminado el TFG de activos y se ha añadido en historicos correctamente.");
            }
        });

        add(tituloCorto, descripcion, tutor1, tutor2, tutor3, alumno1, alumno2, alumno3, fechaAsignacion,
                fechaPresentacion, nota, repo);
        H3 askHist = new H3("¿Desea mover el proyecto a histórico?");
        add(askHist, layoutHist);
        HorizontalLayout layout = new HorizontalLayout();
        layout.add(AceptaryAbierto, AceptaryCerrado, cancelar);
        add(layout);

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
     * Método para obtener el número de días que ha estado asignado el proyecto al
     * alumno.
     * Diferencia entre fecha de asignacion y de presentacion.
     * 
     * @param fechaAsignacion   asignacion del proyecto
     * @param fechaPresentacion presentacion del trabajo
     * @return dias de diferencia
     */
    private long obtenerDiasTotales(DatePicker fechaAsignacion, DatePicker fechaPresentacion) {
        // Obtener las fechas seleccionadas en los DatePickers
        // default time zone
        ZoneId defaultZoneId = ZoneId.systemDefault();

        // creating the instance of LocalDate
        LocalDate fechaPre = fechaPresentacion.getValue();
        LocalDate fechaAsig = fechaAsignacion.getValue();
        // local date + atStartOfDay() + default time zone + toInstant() = Date
        Date date1 = Date.from(fechaPre.atStartOfDay(defaultZoneId).toInstant());
        Date date2 = Date.from(fechaAsig.atStartOfDay(defaultZoneId).toInstant());

        // Calcular la diferencia entre las dos fechas en días en milisegundos
        long daysBetween = date1.getTime() - date2.getTime();

        TimeUnit unidad = TimeUnit.DAYS;
        long dias = unidad.convert(daysBetween, TimeUnit.MILLISECONDS);
        return dias;

    }

    /**
     * Método para comprobar si están vacíos los parametros pasados por argumentos,
     * utilizado en ambas opciones de modificacion.
     * Para reutilizar codigo.
     * 
     * @param tutor1
     * @param alumno1
     * @param descripcion
     * @param tituloCorto
     * @return true or false
     */
    private boolean vacios1(ComboBox<String> tutor1, TextArea alumno1, TextArea descripcion, TextArea tituloCorto) {
        return tutor1.getValue().isBlank() ||
                tituloCorto.getValue().isBlank() ||
                descripcion.getValue().isBlank() ||
                alumno1.getValue().isBlank();
    }

    /**
     * Método para comprobar si están vacíos los parametros pasados por argumentos,
     * utilizado en la opcion de cerrar tfg.
     * 
     * @param cursoAsignacion
     * @param fechaAsignacion
     * @param fechaPresentacion
     * @param nota
     * @param repo
     * @return true or false
     */
    private boolean vacios2(DatePicker fechaAsignacion, DatePicker fechaPresentacion, NumberField nota, TextArea repo) {
        return fechaAsignacion.getValue() == null ||
                fechaPresentacion.getValue() == null ||
                nota.getValue() == null ||
                repo.getValue().isBlank();

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

    /**
     * Guardamos en un array toda la información si es un tfg para mantener abierto,
     * para que funcione como una fila.
     * 
     * @param tituloCorto     titulo del tfg
     * @param descripcion
     * @param tutor1
     * @param tutor2
     * @param tutor3
     * @param alumno1
     * @param alumno2
     * @param alumno3
     * @param cursoAsignacion
     */
    private void stringAbierto(String tituloCorto, String descripcion, String tutor1, String tutor2, String tutor3,
            String alumno1, String alumno2, String alumno3, String fechaAsignacion) {

        String[] TFG = { tituloCorto, descripcion, tutor1, tutor2, tutor3, alumno1, alumno2, alumno3, fechaAsignacion };
        // Esta variable se va a utilizar para saber si lo que se tiene que hacer es
        // actualizar la fila o si se tiene que borrar
        // Es decir, si se llama desde cerrar un fichero
        int estado = 0;
        try {
            actFicheroActivos(TFG, tituloTFG, estado);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Actualizamos en titulo del tfg ya que si modificamos un titulo
        // tenemos que buscar el titulo anterior a ese para cambiar la fila deseada
        tituloTFG = tituloCorto;
    }

    /**
     * Guardamos en un array toda la información si es un tfg que queremos cerrar,
     * para que funcione como una fila.
     * 
     * @param titulo
     * @param tituloCorto
     * @param descripcion
     * @param tutor1
     * @param tutor2
     * @param tutor3
     * @param alumno1
     * @param alumno2
     * @param alumno3
     * @param fechaAsig
     * @param fechaPresen
     * @param nota
     * @param dias
     * @param repo
     */
    private void stringCerrado(String tituloCorto, String descripcion, String tutor1, String tutor2,
            String tutor3, String alumno1, String alumno2, String alumno3, String fechaAsig,
            String fechaPresen, Double nota, long dias, String repo) {

        String[] TFG = { "", tituloCorto, descripcion, tutor1, tutor2, tutor3, alumno1, alumno2, alumno3, fechaAsig,
                fechaPresen, nota.toString(), String.valueOf(dias), repo };
        int estado = 1;
        // Borramos el TFG del listado de activos;
        try {
            actFicheroActivos(TFG, tituloTFG, estado);
            actFicheroHistoricos(TFG);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Metodo que actualiza la información del tfg pasado por parametro en la
     * pestaña de proyectos activos.
     * 
     * @param TFG    informacion del tfg
     * @param titulo titulo del tfg para actualizar
     * @param estado 0 o 1 si hay que borrar el tfg o solo actualizar
     * @throws IOException
     */
    private void actFicheroActivos(String[] TFG, String titulo, int estado) throws IOException {
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
            int rowid = 0;
            for (Row row : hoja) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals(titulo)) {
                        rowid = row.getRowNum();
                        break;
                    }
                }
            }
            Object[] objectArr = TFG;
            int cellid = 0;
            Row fila1 = hoja.getRow(rowid);
            // Comprobamos si tenemos que actualizar la fila o borrarla
            for (Object obj : objectArr) {
                if (estado == 0) {
                    Cell cell = fila1.createCell(cellid++);
                    cell.setCellValue((String) obj);
                } else {
                    Cell cell = fila1.createCell(cellid++);
                    cell.setCellValue("");
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
     * Metodo que añade la información del tfg pasado por parametro en la pestaña de
     * proyectos historicos.
     * 
     * @param TFG informacion del tfg
     * @throws IOException
     */
    private void actFicheroHistoricos(String[] TFG) throws IOException {
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

            Sheet hoja = workbook.getSheet(HISTORICO);
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