package ubu.digit.ui.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
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
import ubu.digit.ui.entity.PendingProject;
import ubu.digit.util.ExternalProperties;

import static ubu.digit.util.Constants.*;

/**
 * Vista de proyectos activos.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 */
@Route(value = "Acept")
@PageTitle("Proyectos pendientes")
public class AceptView extends VerticalLayout{

	/**
	 * Serial Version UID.
	 */
	public static final long serialVersionUID = 8857805864102975132L;

	/**
	 * Logger de la clase.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(AceptView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "proyectos-pendientes";

	/**
	 * Tabla de proyectos.
	 */
	public Grid<PendingProject> table;
	
	/**
	 * Lista con los proyectos pendientes
	 */
	List<PendingProject> dataPendingProjects;
	
	/**
	 * Lista con los proyectos activos que se usarán el el grid
	 * En este los tutores y alumnos se incluyen juntos en una columna.
	 */
	List<PendingProject> dataPendingProjectsGrid;
	
	/**
	 * Lista con los proyectos activos filtrados 
	 */
	List<PendingProject> dataFilteredGrid;

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
	 *  Fachada para obtener los datos
	 */
	public SistInfDataAbstract fachadaDatos;

	/**
	 * Constructor.
	 */
	public AceptView() {
		
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
		add(table);
		
		seleccionarTFG();
		Footer footer = new Footer("N2_Proyecto.csv");
		add(footer);
	}

	
    /**
	 * Crea los filtros de la tabla.
	 */
	public void createFilters() {
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
				table.setItems(dataPendingProjectsGrid);
			}
		});

		descriptionFilter = new TextField("Filtrar por descripción:");
		descriptionFilter.setWidth("300px");
		descriptionFilter.addValueChangeListener(event -> {
			if(!descriptionFilter.isEmpty()) {
				applyFilter("description", event.getValue());
			}else {
				table.setItems(dataPendingProjectsGrid);
			}
		});

		tutorsFilter = new TextField("Filtrar por tutores:");
		tutorsFilter.setWidth("300px");
		tutorsFilter.addValueChangeListener(event -> {
			if(!tutorsFilter.isEmpty()) {
				applyFilter("tutor", event.getValue());
			}else {
				table.setItems(dataPendingProjectsGrid);
			}
		});

		studentsFilter = new TextField("Filtrar por alumnos:");
		studentsFilter.setWidth("300px");
		studentsFilter.addValueChangeListener(event -> {
			if(!studentsFilter.isEmpty()) {
				applyFilter("student", event.getValue());
			}else {
				table.setItems(dataPendingProjectsGrid);
			}
		});
		
		filters.add(projectFilter, descriptionFilter, tutorsFilter, studentsFilter);
		add(filters);
	}

	/**
	 * Crea el modelo de datos de los proyectos pendientes.
	 */
	public void createDataModel() { 
		//Se obtienen los datos del modelo
		List<String> listaDataModel = fachadaDatos.getDataModelPending();
		dataPendingProjects = new ArrayList<PendingProject>();
		
		for(int i=0;i<listaDataModel.size();i++) {
			PendingProject pending = new PendingProject(listaDataModel.get(i), listaDataModel.get(++i), 
					listaDataModel.get(++i),listaDataModel.get(++i), listaDataModel.get(++i), listaDataModel.get(++i),
					listaDataModel.get(++i), listaDataModel.get(++i));
			dataPendingProjects.add(pending);
		}
	}
	
	/**
	 * Crea la tabla de proyectos pendientes.
	 */
	public void createCurrentProjectsTable() {
		H1 proyectosTitle = new H1(DESCRIPCION_PROYECTOS);
		proyectosTitle.addClassName(TITLE_STYLE);
		add(proyectosTitle);
		
		try {
			table = new Grid<>();
			table.addClassName("active-projects-grid");
			table.setWidthFull();
			
			table.setItems(dataPendingProjectsGrid);
			
			table.addColumn(PendingProject::getTitle).setHeader("Título").setFlexGrow(10);
			table.addColumn(PendingProject::getDescription).setHeader("Descripción").setFlexGrow(25);
			table.addColumn(PendingProject::getTutors).setHeader("Tutor/es").setFlexGrow(6);
			table.addColumn(PendingProject::getStudents).setHeader("Alumno/s").setFlexGrow(6);
			table.addColumn(PendingProject::getStatus).setHeader("Estado").setFlexGrow(6);
			
			table.getColumns().forEach(columna -> columna.setResizable(true));
			table.getColumns().forEach(columna -> columna.setSortable(true));
			table.getColumns().get(0).setTextAlign(ColumnTextAlign.START);
			table.getColumns().subList(1, table.getColumns().size()).forEach(columna -> columna.setTextAlign(ColumnTextAlign.CENTER));
			
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
			
		}catch(Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Crea una nueva lista con los valores filtrados
	 * 
	 * @param column
	 * @param valueChange
	 */
	public void applyFilter(String column, String valueChange) {
		dataFilteredGrid = new ArrayList<PendingProject>();
		Iterator<PendingProject> iterator = dataPendingProjectsGrid.iterator();
		String lowercase=valueChange.toLowerCase();
		if(!valueChange.equals(" ")) {
			while (iterator.hasNext()) {
				PendingProject PendingProject = iterator.next();
				
				switch(column) {
					case "title":
						if(PendingProject.getTitle().toLowerCase().contains(lowercase)) {
							dataFilteredGrid.add(PendingProject);
						}
						break;
					case "description":
						if(PendingProject.getDescription().toLowerCase().contains(lowercase)) {
							dataFilteredGrid.add(PendingProject);
						}
						break;
					case "tutor":
						if(PendingProject.getTutors().toLowerCase().contains(lowercase)) {
							dataFilteredGrid.add(PendingProject);
						}
						break;
					case "student":
						if(PendingProject.getStudents().toLowerCase().contains(lowercase)) {
							dataFilteredGrid.add(PendingProject);
						}
						break;
					case "status":
						if(PendingProject.getStatus().toLowerCase().contains(lowercase)) {
						dataFilteredGrid.add(PendingProject);
						}
						break;
				}
			}
			//Se establece los nuevos valores del grid
			table.setItems(dataFilteredGrid);
		}
	}
	
	/**
	 * Se crea una nueva lista con los datos que se usarán en la tabla de descripción de proyectos pendientes.
	 */
	public void CreateDataModelToGrid() {
		String tutors = "";
		String students = "";
		dataPendingProjectsGrid = new ArrayList<PendingProject>();
		Iterator<PendingProject> iterator = dataPendingProjects.iterator();
		while (iterator.hasNext()) {
			PendingProject PendingProject = iterator.next();
			
			tutors = PendingProject.getTutor1();	
			if(!tutors.equals("")) {
				if(!PendingProject.getTutor2().equals("")) {
					tutors +=  ", " + PendingProject.getTutor2();
					if(!PendingProject.getTutor3().equals("")) {
						tutors +=  ", " + PendingProject.getTutor3();
					}
				}
			}
			
			students = PendingProject.getStudent1();
			if(!students.equals("")) {
				if(!PendingProject.getStudent2().equals("")) {
					students +=  ", " + PendingProject.getStudent2();
					if(!PendingProject.getStudent3().equals("")) {
						students +=  ", " + PendingProject.getStudent3();
					}
				}
			}

			PendingProject actives = new PendingProject(PendingProject.getTitle(), PendingProject.getDescription(),
					tutors, students, PendingProject.getStatus());
			dataPendingProjectsGrid.add(actives);
		}	
	}
	
	
	/**
	 * Metodo que nos permite seleccionar los parametros del TFG a modificar y el nuevo estado.
	 */
    private void seleccionarTFG() {

        ComboBox<String> estado= new ComboBox<>("Indique el nuevo estado del TFG");
        Button actualizar= new Button("Actualizar estado");
        ComboBox<String> tfgAModificar=new ComboBox<>("Indique que TFG desea modificar");
        //Opciones del estado del tfg
        estado.setEnabled(false);
        estado.setItems("Aceptar","Denegar");
        estado.addValueChangeListener(event->{
            estado.setValue(event.getValue());
            actualizar.setEnabled(true);
        });
        
        //Titulos de los TFGs
        List<String> titulos=new ArrayList<>();
        
        //Opciones de los titulos
        tfgAModificar.setWidth("30%");
        for(PendingProject pending: dataPendingProjects) {
           titulos.add(pending.getTitle());
        }
        tfgAModificar.setItems(titulos);
        tfgAModificar.addValueChangeListener(event -> {
            tfgAModificar.setValue(event.getValue());
            estado.setEnabled(true);
        });
        
        
        //Opciones del boton de actualizar
        actualizar.setEnabled(false);
        actualizar.addClickListener(event ->{
                modificar(tfgAModificar.getValue(),estado.getValue());}
        );
        
        add(tfgAModificar,estado,actualizar);        
        
    }
    /**
     * Método que modifica la base de datos.
     * @param titulo titulo del tfg a modificar
     * @param estado nuevo estado que se introducira
     */
    private void modificar(String titulo, String estado) {

        String newEstado;
        if(estado.equals("Aceptar")) {
            newEstado="Aceptado";
        }else
            newEstado="Denegado";
        
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

            Sheet hoja= workbook.getSheet(PROYECTO);
            int rowid = 0;
            //Recorremos la hoja para obtener el numero de fila de la celda que tiene el titulo que se ha pasado por parametro
            for (Row row : hoja) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals(titulo)) {
                        rowid = row.getRowNum(); 
                        break;
                    }
                }
            }
            //Buscamos el numero de la columna con el titulo Estado
            int column=0;
            for (Row row : hoja) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals("Estado")) {
                       column = cell.getColumnIndex(); 
                        break;
                    }
                }
            }
            
            //Cambiamos la celda de:  la fila rowid y columna column
            Row fila1 = hoja.getRow(rowid);
            Cell estadoNuevo=fila1.getCell(column);
            estadoNuevo.setCellValue(newEstado);

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
