package ubu.digit.ui.views;

import static ubu.digit.util.Constants.PROYECTO;
import static ubu.digit.util.Constants.TITULO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
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
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.ui.entity.Formularios;
import ubu.digit.util.ExternalProperties;

/**
 * Vista de la creación de los informes.
 * 
 * @author David Renedo Gil
 */
@Route(value = "Informes")
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
     * Vista de historicos.
     */
    HistoricProjectsView vista = new HistoricProjectsView();
    /**
     * Vista de activos.
     */
    ActiveProjectsView activos = new ActiveProjectsView();

    /**
     * Fachada para obtener los datos
     */
    private SistInfDataAbstract fachadaDatos;

    /**
     * Constructor.
     * 
     * @throws SQLException
     */
    public ReportView() {

        fachadaDatos = SistInfDataFactory.getInstanceData();
        config = ExternalProperties.getInstance("/config.properties", false);
        numberFormatter = NumberFormat.getInstance();
        numberFormatter.setMaximumFractionDigits(2);
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        setMargin(true);
        setSpacing(true);

        NavigationBar bat = new NavigationBar();
        bat.buttonReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(bat);

        opciones();

        Footer footer = new Footer(null);

        add(footer);
    }

    /**
     * Metodo para seleccionar las areas.
     */
    public void opciones() {
        // obtendriamos el total de alumnos matriculados en el curso
        NumberField nAlum = new NumberField("Indique el número de alumnos matriculados en este curso académico");
        nAlum.setWidth("25%");
        nAlum.addValueChangeListener(event -> {
            nAlum.setValue(event.getValue());
        });
        // Checkbox con todas las areas
        Checkbox checkbox = new Checkbox("Seleccionar Todas");
        List<String> areas = fachadaDatos.getAreas();
        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("Áreas");
        checkboxGroup.setItems(areas);
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

        // TEXTO PARA AÑADIR NOMBRE AL INFORME
        TextField nombreInforme = new TextField();
        nombreInforme.setLabel("Indique el nombre del informe");
        nombreInforme.setWidth("25%");
        nombreInforme.setHelperText("No introduzcas la extensión del archivo (.xls)");
        nombreInforme.addValueChangeListener(event -> {
            nombreInforme.setValue(event.getValue());
        });

        // BOTON PARA CREAR EL INFORME
        Button crearInforme = new Button("Crear Informe");
        crearInforme.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // Indicando que los campos son obligatorios
        Binder<Formularios> binder = new Binder<>(Formularios.class);
        binder.forField(nombreInforme).asRequired("Debes indicar nombre para el informe").bind("nombreInforme");
        binder.forField(nAlum).asRequired("Debes indicar un numero de alumnos").bind("nAlumnos");

        // Boton para descargar el archivo que se genera
        Anchor download = new Anchor();
        // Lo ocultamos
        download.setVisible(false);
        // Cuando pulsamos en crear informe
        crearInforme.addClickListener(event -> {
            if (binder.validate().isOk() && !checkboxGroup.getValue().isEmpty()) {
                // Creamos el informe
                File file = creacionInforme(checkboxGroup.getValue(), nombreInforme.getValue(), nAlum.getValue());

                // Generamos el recurso descargable
                StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));

                // Lo introducimos en el boton para descargar
                download.setText("Descargar " + file.getName());
                download.setHref(streamResource);
                download.getElement().setAttribute("download", true);
                download.add(new Button(new Icon(VaadinIcon.DOWNLOAD_ALT)));
                download.setVisible(true);
            } else {
                Notification.show("Debes indicar un valor para todos los campos")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        add(nAlum, checkboxGroup, checkbox, nombreInforme, crearInforme, download);

    }

    /**
     * Metodo que crea el archivo xls y escribe los datos.
     * 
     * @param listaAreas
     * @param nombreInforme
     * @param string
     * @param años
     * @return File
     */
    public File creacionInforme(Set<String> listaAreas, String nombreInforme, Double nAlumn) {

        File archivo = new File(nombreInforme + ".xls");
        Map<String, Object[]> dataTFG = new TreeMap<String, Object[]>();
        Workbook workbook = new HSSFWorkbook();
        try {
            // Recorremos el listado de areas pasados
            for (String area : listaAreas) {
                // Creamos la hoja con el nombre del area
                Sheet hoja = workbook.createSheet(area);
                dataTFG = obtencionDatos(area, nAlumn);
                Row rowCount = null;
                // Cogemos los ids
                Set<String> keyid = dataTFG.keySet();
                int rowid = 0;

                // https://es.acervolima.com/como-escribir-datos-en-una-hoja-de-excel-usando-java/
                // writing the data into the sheets...
                for (String key : keyid) {

                    rowCount = workbook.getSheet(hoja.getSheetName()).createRow(rowid++);
                    Object[] objectArr = dataTFG.get(key);

                    int cellid = 0;
                    for (Object obj : objectArr) {
                        Cell cell = rowCount.createCell(cellid++);
                        if (obj.getClass().equals(Double.class)) {

                            cell.setCellValue((double) obj);
                        } else
                            cell.setCellValue((String) obj);

                    }
                }
            }
            // Se genera el documento
            FileOutputStream out = new FileOutputStream(archivo);
            workbook.write(out);
            workbook.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return archivo;
    }

    /**
     * Metodo que obtiene los datos del area seleccionada sobre el ultimo curso
     * academico.
     * 
     * @param area
     * @param nAlumn
     * @return mapa con los datos.
     */
    private Map<String, Object[]> obtencionDatos(String area, Double nAlumn) {
        Map<String, Object[]> dataTFG = new TreeMap<String, Object[]>();

        List<String> profes = fachadaDatos.getProfesoresDeArea(area);
        List<String> profesEPS = fachadaDatos.getProfesores();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String cursoActual = obtenerCursoActual();
        int i = 1;

        for (String prof : profes) {

            i++;
            double tfgs = 0;
            double tfgs2 = 0;
            // obtenemos los creditos
            double creditos = obtenerCreditos(prof, nAlumn);
            // Recorremos todos los tfgs y buscamos los del año y tutor correspondientes
            for (int n = 0; n < activos.dataActiveProjects.size(); n++) {
                // Proyectos con alumno asignado
                if (!activos.dataActiveProjects.get(n).getStudent1().equals("Aalumnos sin asignar")) {
                    // Es tutor 1
                    System.out.println("TUTOR"+prof);
                   
                    if (activos.dataActiveProjects.get(n).getTutor1().equals(prof)) {
                        // Tiene tutor 2 de la EPS
                        if (profesEPS.contains(activos.dataActiveProjects.get(n).getTutor2())) {
                            tfgs2++;
                        } else { // No tiene tutor2 de la EPS
                            tfgs++;
                        }
                        // Es tutor2 del tfg
                    } else if (activos.dataActiveProjects.get(n).getTutor2().equals(prof)) {
                        tfgs2++;
                    }

                }
            }
            // OBTENER LOS TFGS DEL HISTORICO DE ESTE CURSO ACADÉMICO QUE CUENTA
            for (int p = 0; p < vista.dataHistoric.size(); p++) {
                String cursoTFG = "";
                int año = vista.dataHistoric.get(p).getPresentationDate().getYear();
                String fechaIni = año + "-09-01";
                String fechaFin = (año + 1) + "-09-01";
                LocalDate fechaINI = LocalDate.parse(fechaIni, formato);
                LocalDate fechaFIN = LocalDate.parse(fechaFin, formato);
                // Obtenemos el curso del TFG
                if (vista.dataHistoric.get(p).getPresentationDate().isAfter(fechaINI)
                        && vista.dataHistoric.get(p).getPresentationDate().isBefore(fechaFIN)) {
                    // asignamos el curso
                    cursoTFG = año + "/" + String.valueOf(año + 1);

                } else {
                    cursoTFG = año - 1 + "/" + año;
                }

                // Miramos si nos interesa para este curso y si es el profesor que buscamos
                // sumamos
                if (cursoTFG.equals(cursoActual)) {
                    // Cogemos los tfgs del profesor que nos interesa
                    if (vista.dataHistoric.get(p).getTutor1().equals(prof)) {
                        tfgs++;
                    } else if (vista.dataHistoric.get(p).getTutor2().equals(prof)) {
                        tfgs2++;

                    }
                }

            }

            // Array con toda la informacion
            Object[] profesor = { prof, tfgs, tfgs2, creditos };
            // Si esla primera linea se añaden los titulos y el primer profesor
            if (i == 2) {
                dataTFG.put("1", new Object[] { "Tutor", "TFGs Dirigidos", "TFGs CoDirigidos", "ETCS" });
                dataTFG.put("2", profesor);
            } else {
                String id = Integer.toString(i);
                dataTFG.put(id, profesor);
            }
        }

        return dataTFG;
    }

    private String obtenerCursoActual() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String cursoActual = "";
        // Obtenemos el curso actual
        LocalDate today = LocalDate.now();
        int esteAño = today.now().getYear();

        String fechahoy = today.now().getYear() + "-09-01";
        String fechaIniEsteAño = esteAño + "-09-01";
        String fechaFinEsteAño = (esteAño + 1) + "-09-01";
        LocalDate fechaINIESTEAÑO = LocalDate.parse(fechaIniEsteAño, formato);
        LocalDate fechaFINESTEAÑO = LocalDate.parse(fechaFinEsteAño, formato);
        LocalDate fechaHOY = LocalDate.parse(fechahoy, formato);
        if (fechaHOY.isAfter(fechaINIESTEAÑO) && fechaHOY.isBefore(fechaFINESTEAÑO)) {
            cursoActual = String.valueOf(esteAño) + "/" + String.valueOf(esteAño + 1);
        } else {
            cursoActual = String.valueOf(esteAño - 1) + "/" + String.valueOf(esteAño);
        }
        return cursoActual;

    }

    /**
     * Metodo que obtiene el numero de creditos de un tutor
     * 
     * @param profesor  profesor a buscar
     * @param ultimoAño el año que se busca
     * @param nAlumn    nAlumnos matriculados en TFG
     * @return valor con el numero de creditos
     */
    private float obtenerCreditos(String profesor, Double nAlumn) {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String cursoActual = obtenerCursoActual();

        // creditos del tutor
        float nCreditosTutor = 0;
        // TOTAL de creditos a repartir
        float valorCred = Float.parseFloat(config.getSetting("ECTS"));
        float total = (float) (nAlumn * valorCred);

        // Obtenemos todos los TFGs activos de este año
        Number tfgsActivos = fachadaDatos.getTotalNumber(TITULO, PROYECTO);

        // Total de creditos a asignar entre todos los directores
        float crDir = (float) ((total * 0.6) / tfgsActivos.intValue());
        // Total de creditos a asignar para tribunal (a dividir entre 6 porque hay 6
        // miembros en el tribunal)
        float crTri = (float) (total * 0.4 / 6);
        // float crePorTFG=crDir/;

        // Recorremos todos los tfgs activos y con alumno asignado y buscamos si es
        // tutor1 o tutor2
        // y comprobamos si es codirigido por otro tutor de la EPS
        for (int n = 0; n < activos.dataActiveProjects.size(); n++) {
            // RECORREMOS SOLO LOS TFGS ASIGNADOS
            if (!activos.dataActiveProjects.get(n).getStudent1().equals("Aalumnos sin asignar")) {
                // SI ES EL TUTOR 1
                if (activos.dataActiveProjects.get(n).getTutor1().equals(profesor)) {
                    // SI EL TUTOR 2 ESTA O NO EN LA EPS
                    if (fachadaDatos.getProfesores().contains(activos.dataActiveProjects.get(n).getTutor2())) {
                        // Se reparten los creditos para este profesor en este tfg
                        nCreditosTutor += crDir * 0.3;
                    } else {
                        // Se queda todo el porcentaje de los creditos
                        nCreditosTutor += crDir * 0.6;
                    }
                    // SI ES EL TUTOR 2
                } else if (activos.dataActiveProjects.get(n).getTutor2().equals(profesor)) {
                    nCreditosTutor += crDir * 0.3;
                }
            }
        }
        // OBTENER LOS CREDITOS DE LOS TFGS DEL HISTORICO DE ESTE CURSO ACADÉMICO QUE
        // CUENTA
        for (int p = 0; p < vista.dataHistoric.size(); p++) {
            String cursoTFG = "";
            int año = vista.dataHistoric.get(p).getPresentationDate().getYear();
            String fechaIni = año + "-09-01";
            String fechaFin = (año + 1) + "-09-01";
            LocalDate fechaINI = LocalDate.parse(fechaIni, formato);
            LocalDate fechaFIN = LocalDate.parse(fechaFin, formato);
            // Obtenemos el curso del TFG
            if (vista.dataHistoric.get(p).getPresentationDate().isAfter(fechaINI)
                    && vista.dataHistoric.get(p).getPresentationDate().isBefore(fechaFIN)) {
                // asignamos el curso
                cursoTFG = String.valueOf(año) + "/" + String.valueOf(año + 1);

            } else {
                cursoTFG = String.valueOf(año - 1) + "/" + String.valueOf(año);
            }

            // Miramos si nos interesa para este curso y si es el profesor que buscamos
            // sumamos
            if (cursoTFG.equals(cursoActual)) {
                // COMPROBAMOS SI ES TUTOR1 O 2
                if (vista.dataHistoric.get(p).getTutor1().equals(profesor)) {
                    //es tutor 1 y si el tutor 2 es de la EPS
                    if (fachadaDatos.getProfesores().contains(vista.dataHistoric.get(p).getTutor2())) {
                        nCreditosTutor += crDir * 0.3;
                    } else {
                        nCreditosTutor += crDir * 0.6;
                    }
                  //ES TUTOR 2 
                } else if (vista.dataHistoric.get(p).getTutor2().equals(profesor)) {
                    nCreditosTutor += crDir * 0.3;
                }
            }

        }

        // Si el tutor pertenece al tribunal
        if (fachadaDatos.getNombresTribunal().contains(profesor)) {
            nCreditosTutor += crTri;
        }
        return nCreditosTutor;
    }

    /**
     * Metodo para transformar un archivo en InputStream.
     * 
     * @param file
     * @return InputStream
     */
    private InputStream getStream(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.getMessage();
        }

        return stream;
    }

}
