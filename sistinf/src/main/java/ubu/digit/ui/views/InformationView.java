package ubu.digit.ui.views;

import org.apache.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.pesistence.SistInfDataFactory;

import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

import java.util.List;
/**
 * Vista de información.
 * 
 * @author Javier de la Fuente Barrios
 */
public class InformationView extends VerticalLayout implements View {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 7820866989198327219L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = Logger.getLogger(InformationView.class);

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;
	
	/**
	 *  Fachada para obtener los datos
	 */
	private SistInfDataAbstract fachadaDatos;

	private AbstractOrderedLayout normas;

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "information";

	/**
	 * Constructor.
	 */
	public InformationView() {
		fachadaDatos = SistInfDataFactory.getInstanceData("XLS"); //TODO: 
		
		config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);

		setMargin(true);
		setSpacing(true);

		NavigationBar navBar = new NavigationBar();
		addComponent(navBar);

		createTribunal();
		createNormas();
		createCalendar();
		createDocumentos();
		
		Footer footer = new Footer("Tribunal.csv"); //TODO:
		addComponent(footer);
	}
	
	/**
	 * Crea el tribunal.
	 */
	private void createTribunal() { //TODO:
		Label tribunalTitle = new Label(MIEMBROS_DEL_TRIBUNAL);
		tribunalTitle.setStyleName(TITLE_STYLE);

		final HorizontalLayout horizontalTribunal = new HorizontalLayout();
		horizontalTribunal.setSpacing(true);
		horizontalTribunal.setMargin(new MarginInfo(false, true, false, true));

		Label iconoTribunal = new Label(FontAwesome.USERS.getHtml(), ContentMode.HTML);
		iconoTribunal.setStyleName("icon-big");
		iconoTribunal.setWidth(130, Unit.PIXELS);

		final VerticalLayout tribunal = new VerticalLayout();
		tribunal.setSpacing(true);
		tribunal.setWidth(350, Unit.PIXELS);

		//Se obtienen los nombres y apellidos de los miembros del tribunal
		List<String> listaTribunal;
		listaTribunal = fachadaDatos.getTribunal();
		for(int i=0;i<listaTribunal.size();i++) {
			tribunal.addComponent(new Label(listaTribunal.get(i)));
		}
		horizontalTribunal.addComponents(iconoTribunal, tribunal);

		String yearIndex = config.getSetting("indexAño");
		int nextYearIndex = Integer.parseInt(yearIndex) + 1;
		Label curso = new Label("Programa en vigor a partir del Curso " + yearIndex + "-" + nextYearIndex + ".");
		addComponents(tribunalTitle, horizontalTribunal, curso);
	}
	
	/**
	 * Crea las normas de entrega.
	 */
	private void createNormas() { //TODO:
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
		Label normasTitle = new Label(ESPECIFICACIONES_DE_ENTREGA);
		normasTitle.setStyleName(TITLE_STYLE);

		final VerticalLayout normas = new VerticalLayout();
		normas.setSpacing(true);

		//Se obtienen las descripciones de las normas 
		List<String> listaNormas;
		listaNormas = fachadaDatos.getNormas();
		
		for(int i=0;i<listaNormas.size();i++) {
			normas.addComponent(new Label(" - " + listaNormas.get(i)));
		}
		
		addComponents(normasTitle, normas);
	}
	
	/**
	 * Crea el calendario.
	 */
	private void createCalendar() {
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
		Label fechasTitle = new Label(FECHAS_DE_ENTREGA);
		fechasTitle.setStyleName(TITLE_STYLE);

		String urlCalendario = config.getSetting("urlCalendario");
		BrowserFrame calendar = new BrowserFrame("", new ExternalResource("https://" + urlCalendario));
		calendar.setWidth(85, Unit.PERCENTAGE);
		calendar.setHeight(500, Unit.PIXELS);
		addComponents(fechasTitle, calendar);
	}

	/**
	 * Crea los documentos de entrega.
	 */
	private void createDocumentos() { //TODO:
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
		Label documentosTitle = new Label(DOCUMENTOS);
		documentosTitle.setStyleName(TITLE_STYLE);

		final VerticalLayout documentos = new VerticalLayout();
		documentos.setSpacing(true);

		//Se obtienen las descripciones y la url de los documentos 
		List<String> listaDocumentos;
		listaDocumentos = fachadaDatos.getDocumentos();
		
		for(int i=0;i<listaDocumentos.size();i++) {
			Link link = new Link(listaDocumentos.get(i), new ExternalResource(listaDocumentos.get(++i)));
			link.setIcon(FontAwesome.LINK);
			documentos.addComponent(link);
		}
		addComponents(documentosTitle, documentos);
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
	}

	/**
	 * La vista se inicializa en el constructor. 
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// Se inicializa el contenido de la vista en el constructor
	}
}
