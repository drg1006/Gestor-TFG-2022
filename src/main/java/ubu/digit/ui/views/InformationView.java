package ubu.digit.ui.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataFactory;
import ubu.digit.ui.components.*;
import ubu.digit.ui.MainLayout;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

import java.util.List;
/**
 * Vista de información.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 */

@Route(value = "Information", layout = MainLayout.class)
@PageTitle("Información general")
public class InformationView extends VerticalLayout {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 7820866989198327219L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InformationView.class.getName());

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;
	
	/**
	 *  Fachada para obtener los datos
	 */
	private SistInfDataAbstract fachadaDatos;

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "information";

	/**
	 * Constructor.
	 */
	public InformationView() {    
		LOGGER.info("InformationView");
        fachadaDatos = SistInfDataFactory.getInstanceData();
		config = ExternalProperties.getInstance("/config.properties", false);

		setMargin(true);
		setSpacing(true);
		
		createTribunal();
		createNormas();
		createCalendar();
		createDocumentos();
		
		Footer footer = new Footer("N1_Tribunal");
		add(footer);
	}
	
	/**
	 * Crea el tribunal.
	 */
	private void createTribunal() { 
		H1 tribunalTitle = new H1(MIEMBROS_DEL_TRIBUNAL);
		tribunalTitle.addClassName(TITLE_STYLE);
		add(tribunalTitle);
		
		final HorizontalLayout horizontalTribunal = new HorizontalLayout();
		horizontalTribunal.setSpacing(true);
		horizontalTribunal.setMargin(true);//new MarginInfo(false, true, false, true));

		Icon iconoTribunal = new Icon(VaadinIcon.USER);
		iconoTribunal.addClassName("icon-big");
		iconoTribunal.setSize("100px");
		
		final VerticalLayout tribunal = new VerticalLayout();
		tribunal.setSpacing(true);
		tribunal.setWidth(350, Unit.PIXELS);

		//Se obtienen los nombres y apellidos de los miembros del tribunal
		List<String> listaTribunal;
		listaTribunal = fachadaDatos.getTribunal();
		for(int i=0;i<listaTribunal.size();i++) {
			tribunal.add(new Label(listaTribunal.get(i)));
		}
		horizontalTribunal.add(iconoTribunal, tribunal);

		String yearIndex = config.getSetting("indexAño");
		int nextYearIndex = Integer.parseInt(yearIndex) + 1;
		Label curso = new Label("Programa en vigor a partir del Curso " + yearIndex + "-" + nextYearIndex + ".");
		add(horizontalTribunal, curso);
	}
	
	/**
	 * Crea las normas de entrega.
	 */
	private void createNormas() {
		H1 normasTitle = new H1(ESPECIFICACIONES_DE_ENTREGA);
		normasTitle.addClassName(TITLE_STYLE);
		add(normasTitle);
		
		final VerticalLayout normas = new VerticalLayout();
		normas.setSpacing(true);

		//Se obtienen las descripciones de las normas 
		List<String> listaNormas;
		listaNormas = fachadaDatos.getNormas();
		
		for(int i=0;i<listaNormas.size();i++) {
			normas.add(new Label(" - " + listaNormas.get(i)));
		}
		add(normasTitle, normas);
	}
	
	/**
	 * Crea el calendario.
	 */
	private void createCalendar() {	
		H1 fechasTitle = new H1(FECHAS_DE_ENTREGA);
		fechasTitle.addClassName(TITLE_STYLE);

		String urlCalendario = config.getSetting("urlCalendario");
		IFrame calendar = new IFrame("https://" + urlCalendario);
		calendar.setWidth(85, Unit.PERCENTAGE);
		calendar.setHeight(500, Unit.PIXELS);
		add(fechasTitle, calendar);
	}

	/**
	 * Crea los documentos de entrega.
	 */
	private void createDocumentos() { 
		H1 documentosTitle = new H1(DOCUMENTOS);
		documentosTitle.addClassName(TITLE_STYLE);

		final VerticalLayout documentos = new VerticalLayout();
		documentos.setSpacing(true);

		//Se obtienen las descripciones y la url de los documentos 
		List<String> listaDocumentos;
		listaDocumentos = fachadaDatos.getDocumentos();
		
		for(int i=0;i<listaDocumentos.size();i++) {
			Anchor link = new Anchor(listaDocumentos.get(i), listaDocumentos.get(++i));
			//link.setIcon(FontAwesome.LINK);
			documentos.add(link);
		}
		add(documentosTitle, documentos);
	}
}
