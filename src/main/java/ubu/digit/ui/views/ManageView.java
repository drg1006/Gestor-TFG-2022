package ubu.digit.ui.views;

import static ubu.digit.util.Constants.DESCRIPCION_PROYECTOS;
import static ubu.digit.util.Constants.FILTROS;
import static ubu.digit.util.Constants.NOMBRE_BASES;
import static ubu.digit.util.Constants.PROYECTO;
import static ubu.digit.util.Constants.TITLE_STYLE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
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
import ubu.digit.ui.entity.ActiveProject;
import ubu.digit.util.ExternalProperties;

/**
 * Vista de los proyectos pendientes.
 * 
 * @author David Renedo Gil
 */
@Route(value = "Administrar")
@PageTitle("Proyectos pendientes")
public class ManageView extends VerticalLayout {

    /**
     * Serial Version UID.
     */
    public static final long serialVersionUID = 8857805864102975132L;

    /**
     * Logger de la clase.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ManageView.class.getName());

    /**
     * Nombre de la vista.
     */
    public static final String VIEW_NAME = "proyectos-pendientes";

    /**
     * Tabla de proyectos.
     */
    public Grid<ActiveProject> table;

    /**
     * Lista con los proyectos pendientes
     */
    List<ActiveProject> dataFilteredGrid;

    /**
     * Lista con los proyectos activos que se usarán el el grid
     * En este los tutores y alumnos se incluyen juntos en una columna.
     */
    List<ActiveProject> dataActiveProjects;

    /**
     * Lista con los proyectos activos filtrados
     */
    List<ActiveProject>  dataActiveProjectsGrid;

    /**
     * Campo de texto para filtrar por proyecto.
     */
    public TextField projectFilter;

    /**
     * Campo de texto para filtrar por descripción.
     */
    public TextField descriptionFilter;

    /**
     * Campo de texto para filtrar por tutor.
     */
    public TextField tutorsFilter;

    /**
     * Campo de texto para filtrar por alumno.
     */
    public TextField studentsFilter;
    
    /**
     * Campo de texto para filtrar por alumno.
     */
    public TextField statusFilter;

    /**
     * Fachada para obtener los datos
     */
    public SistInfDataAbstract fachadaDatos;

    /**
     * Constructor.
     */
    public ManageView() {

        fachadaDatos = SistInfDataFactory.getInstanceData();

        addClassName("active-projects-view");
        setMargin(true);
        setSpacing(true);

        NavigationBar bat = new NavigationBar();
        add(bat);

        createDataModel();
        CreateDataModelToGrid();
        createFilters();
        createCurrentProjectsTable();

        Footer footer = new Footer("N2_Proyecto.csv");
        add(footer);
    }

    /**
     * Crea los filtros de la tabla.
     */
    private void createFilters() {
        H1 filtersTitle = new H1(FILTROS);
        filtersTitle.addClassName(TITLE_STYLE);
        add(filtersTitle);

        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(true);
        filters.setMargin(false);
        
        projectFilter = new TextField("Filtrar por proyectos:");
        projectFilter.setWidth("300px");
        projectFilter.addValueChangeListener(event -> {
            if(!projectFilter.isEmpty()) {
                applyFilter("title", event.getValue());
            }else {
                table.setItems(dataActiveProjectsGrid);
            }
        });

        descriptionFilter = new TextField("Filtrar por descripción:");
        descriptionFilter.setWidth("300px");
        descriptionFilter.addValueChangeListener(event -> {
            if(!descriptionFilter.isEmpty()) {
                applyFilter("description", event.getValue());
            }else {
                table.setItems(dataActiveProjectsGrid);
            }
        });

        tutorsFilter = new TextField("Filtrar por tutores:");
        tutorsFilter.setWidth("300px");
        tutorsFilter.addValueChangeListener(event -> {
            if(!tutorsFilter.isEmpty()) {
                applyFilter("tutor", event.getValue());
            }else {
                table.setItems(dataActiveProjectsGrid);
            }
        });

        studentsFilter = new TextField("Filtrar por alumnos:");
        studentsFilter.setWidth("300px");
        studentsFilter.addValueChangeListener(event -> {
            if(!studentsFilter.isEmpty()) {
                applyFilter("student", event.getValue());
            }else {
                table.setItems(dataActiveProjectsGrid);
            }
        });

        statusFilter = new TextField("Filtrar por estado:");
        statusFilter.setWidth("300px");
        statusFilter.addValueChangeListener(event -> {
            if(!statusFilter.isEmpty()) {
                applyFilter("status", event.getValue());
            }else {
                table.setItems(dataActiveProjectsGrid);
            }
        });
        
        filters.add(projectFilter, descriptionFilter, tutorsFilter, studentsFilter, statusFilter);
        add(filters);
    }

    /**
     * Crea el modelo de datos de los proyectos activos.
     */
    private void createDataModel() { 
        //Se obtienen los datos del modelo
        List<String> listaDataModel = fachadaDatos.getDataModel();
        dataActiveProjects = new ArrayList<ActiveProject>();
        
        for(int i=0;i<listaDataModel.size();i++) {
            ActiveProject actives = new ActiveProject(listaDataModel.get(i), listaDataModel.get(++i), 
                    listaDataModel.get(++i),listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i),
                    listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i));
            dataActiveProjects.add(actives);
        }
    }
    /**
     * Crea la tabla de proyectos activos.
     */
    public void createCurrentProjectsTable() {
        H1 proyectosTitle = new H1(DESCRIPCION_PROYECTOS);
        proyectosTitle.addClassName(TITLE_STYLE);
        add(proyectosTitle);

        try {
            table = new Grid<>();
            table.addClassName("pending-projects-grid");
            table.setWidthFull();
            table.setSelectionMode(SelectionMode.MULTI);

            table.setItems(dataActiveProjectsGrid);

            table.addColumn(ActiveProject::getTitle).setHeader("Título").setFlexGrow(10);
            table.addColumn(ActiveProject::getDescription).setHeader("Descripción").setFlexGrow(25);
            table.addColumn(ActiveProject::getTutors).setHeader("Tutor/es").setFlexGrow(6);
            table.addColumn(ActiveProject::getStudents).setHeader("Alumno/s").setFlexGrow(6);
            table.addColumn(ActiveProject::getStatus).setHeader("Estado").setFlexGrow(6);

            table.getColumns().forEach(columna -> columna.setResizable(true));
            table.getColumns().forEach(columna -> columna.setSortable(true));
            table.getColumns().get(0).setTextAlign(ColumnTextAlign.START);
            table.getColumns().subList(1, table.getColumns().size())
                    .forEach(columna -> columna.setTextAlign(ColumnTextAlign.CENTER));

            table.setItemDetailsRenderer(
                    new ComponentRenderer<>(PendingProject -> {
                        VerticalLayout layout = new VerticalLayout();
                        layout.add(new Label("Título: " +
                                PendingProject.getTitle()));
                        layout.add(new Label("Descripción: " +
                                PendingProject.getDescription()));
                        layout.add(new Label("Tutor/es: " +
                                PendingProject.getTutors()));
                        layout.add(new Label("Alumno/s: " +
                                PendingProject.getStudents()));
                        layout.add(new Label("Estado: " +
                                PendingProject.getStatus()));
                        return layout;
                    }));
            table.addThemeVariants(GridVariant.LUMO_NO_BORDER);

            // Boton con la opcion de aceptar tfgs
            Button aceptar = new Button("ACEPTAR");
            // boton con la opcion de denegar tfgs
            Button denegar = new Button("DENEGAR");
            
            // boton con la opcion de denegar tfgs
            Button modificarTFG = new Button("MODIFICAR");
            
            // Cuando indicamos que la lista de TFGs seleccionada es la definitiva, se la
            // pasamos a seleccionarTFG
            aceptar.addClickListener(event -> {
                aceptarTFG(table.getSelectedItems());
            });

            denegar.addClickListener(event -> {
                denegarTFG(table.getSelectedItems());

            });

            modificarTFG.addClickListener(event ->{
                if(table.getSelectedItems().size()==1) {                    
                    for (ActiveProject tfg : table.getSelectedItems()) {
                        ModifyView.tituloTFG=tfg.getTitle();
                    }  
                    UI.getCurrent().navigate(ModifyView.class);
                }else {
                    //Si se han seleccionado varios tfgs y se ha dado a modificar
                    Dialog aviso = new Dialog();
                    // Añadidmos un texto
                   aviso.add("Puedes aceptar/denegar varios TFGs al mismo tiempo, pero para modificar solo puedes seleccionar uno. ");
                   aviso.open();
                   Button closeButton = new Button(new Icon("lumo", "cross"),
                           (e) -> aviso.close());
                   closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                  aviso.add(closeButton);
                }
            });
            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            add(table);
            layout.add(aceptar, denegar,modificarTFG);
            add(layout);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }

    }

    /**
     * Método que crea el dialogo de aceptar TFGs.
     * 
     * @param selectedItems listaTFGs
     */
    private void aceptarTFG(Set<ActiveProject> selectedItems) {
        // Cogemos los titulos seleccionados
        List<String> titulos = new ArrayList<>();
        for (ActiveProject tfg : selectedItems) {
            titulos.add(tfg.getTitle());
            
        }
        
        // Boton que confirma la modificacion (se introduce en ambos pop-ups)
        Button aceptarBtn = new Button("Sí");
        // Boton que deniega la modificacion (se introduce en ambos pop-ups)
        Button cancelBtn = new Button("No");
        // Dialog (pop-up) para aceptar TFGs (se crea aqui para que el texto no se añada
        // dos veces)
        Dialog confirmacion = new Dialog();
        // Añadidmos un texto
        confirmacion.add("¿Seguro que desea aceptar los TFGs seleccionados? ");

        // Los añadimos al pop-up
        confirmacion.add(cancelBtn, aceptarBtn);
        // Abrimos el dialogo
        confirmacion.open();

        // Si se desea confirmar la operacion se modificar el excel
        aceptarBtn.addClickListener(ev -> {
            modificar(titulos, "Aceptar");
            confirmacion.close();
        });
        // El boton cancelar que cierrra el pop-up
        cancelBtn.addClickListener(ev -> {
            confirmacion.close();
        });

    }

    /**
     * Método que crea el dialogo de denegar TFGs.
     * 
     * @param selectedItems listaTFGs
     */
    private void denegarTFG(Set<ActiveProject> selectedItems) {
        // Mismo funcionamiento que aceptarTFGs
        List<String> titulos = new ArrayList<>();
        for (ActiveProject tfg : selectedItems) {
            titulos.add(tfg.getTitle());
        }
        Button aceptarBtn = new Button("Sí");
        Button cancelBtn = new Button("No");
        Dialog confirmacion = new Dialog();
        confirmacion.add("¿Seguro que desea denegar los TFGs seleccionados? ");
        confirmacion.add(cancelBtn, aceptarBtn);
        confirmacion.open();
        aceptarBtn.addClickListener(ev -> {
            modificar(titulos, "Denegar");
            confirmacion.close();
        });

        cancelBtn.addClickListener(ev -> {
            confirmacion.close();
        });

    }

    /**
     * Crea una nueva lista con los valores filtrados
     * 
     * @param column
     * @param valueChange
     */
    public void applyFilter(String column, String valueChange) {
        dataFilteredGrid = new ArrayList<ActiveProject>();
        Iterator<ActiveProject> iterator = dataActiveProjectsGrid.iterator();
        String lowercase = valueChange.toLowerCase();
        if (!valueChange.equals(" ")) {
            while (iterator.hasNext()) {
                ActiveProject PendingProject = iterator.next();

                switch (column) {
                    case "title":
                        if (PendingProject.getTitle().toLowerCase().contains(lowercase)) {
                            dataFilteredGrid.add(PendingProject);
                        }
                        break;
                    case "description":
                        if (PendingProject.getDescription().toLowerCase().contains(lowercase)) {
                            dataFilteredGrid.add(PendingProject);
                        }
                        break;
                    case "tutor":
                        if (PendingProject.getTutors().toLowerCase().contains(lowercase)) {
                            dataFilteredGrid.add(PendingProject);
                        }
                        break;
                    case "student":
                        if (PendingProject.getStudents().toLowerCase().contains(lowercase)) {
                            dataFilteredGrid.add(PendingProject);
                        }
                        break;
                    case "status":
                        if (PendingProject.getStatus().toLowerCase().contains(lowercase)) {
                            dataFilteredGrid.add(PendingProject);
                        }
                        break;
                }
            }
            // Se establece los nuevos valores del grid
            table.setItems(dataFilteredGrid);
        }
    }

    /**
     * Se crea una nueva lista con los datos que se usarán en la tabla de
     * descripción de proyectos pendientes.
     */
    public void CreateDataModelToGrid() {
        String tutors = "";
        String students = "";
        dataActiveProjectsGrid = new ArrayList<ActiveProject>();
        Iterator<ActiveProject> iterator = dataActiveProjects.iterator();
        while (iterator.hasNext()) {
            ActiveProject activeproject = iterator.next();
            
            tutors = activeproject.getTutor1(); 
            if(!tutors.equals("")) {
                if(!activeproject.getTutor2().equals("")) {
                    tutors +=  ", " + activeproject.getTutor2();
                    if(!activeproject.getTutor3().equals("")) {
                        tutors +=  ", " + activeproject.getTutor3();
                    }
                }
            }
            
            students = activeproject.getStudent1();
            if(!students.equals("")) {
                if(!activeproject.getStudent2().equals("")) {
                    students +=  ", " + activeproject.getStudent2();
                    if(!activeproject.getStudent3().equals("")) {
                        students +=  ", " + activeproject.getStudent3();
                    }
                }
            }

            ActiveProject actives = new ActiveProject(activeproject.getTitle(), activeproject.getDescription(),
                    tutors, students, activeproject.getCourseAssignment(),activeproject.getStatus());
            dataActiveProjectsGrid.add(actives);
        }
    }

    /**
     * Método que modifica la base de datos.
     * 
     * @param seleccionados titulo del tfg a modificar
     * @param estado        nuevo estado que se introducira
     */
    private void modificar(List<String> titulos, String estado) {
        System.out.println(titulos);
        String newEstado;
        if (estado.equals("Aceptar")) {
            newEstado = "";
        } else
            newEstado = "Denegado";

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
            // Buscamos el numero de la columna con el titulo Estado
            int column = 0;
            for (Row row : hoja) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals("Estado")) {
                        column = cell.getColumnIndex();
                        break;
                    }
                }
            }
            for (String titulo : titulos) {
                // Recorremos la hoja para obtener el numero de fila de la celda que tiene el
                // titulo que se ha pasado por parametro, para no hardcodearlo si se cambia de columna
                for (Row row : hoja) {
                    for (Cell cell : row) {
                        if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals(titulo)) {
                            rowid = row.getRowNum();
                            break;
                        }
                    }
                }
                // Cambiamos la celda de: la fila rowid y columna column
                Row fila1 = hoja.getRow(rowid);
                Cell estadoNuevo = fila1.getCell(column);
                estadoNuevo.setCellValue(newEstado);
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
