package ubu.digit.ui.views;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.codoid.products.fillo.Recordset;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import ubu.digit.pesistence.SistInfData;
import ubu.digit.pesistence.SistInfDataCsv;
import ubu.digit.pesistence.SistInfDataXls;
import ubu.digit.ui.components.Footer;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;
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
	private static final Logger LOGGER = Logger.getLogger(SistInfData.class);

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Fachada para obtener los datos.
	 */
	private SistInfDataCsv fachadaDatosCsv;
	
	/**
	 * Fachada para obtener los datos.
	 */
	private SistInfDataXls fachadaDatos;

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "information";

	/**
	 * Constructor.
	 */
	public InformationView() {
		//fachadaDatos = SistInfDataCsv.getInstance();
		fachadaDatos = SistInfDataXls.getInstance();
		//fachadaDatos = SistInfData.getInstanceCsv();
		config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);

		setMargin(true);
		setSpacing(true);

		NavigationBar navBar = new NavigationBar();
		addComponent(navBar);

		createTribunal();
		createNormas();
		createCalendar();
		createDocumentos();
		
		Footer footer = new Footer("Tribunal.csv");
		addComponent(footer);
	}
	
	/**
	 * Crea el tribunal.
	 */
	private void createTribunal() { 
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

		try {
			Recordset result = fachadaDatos.getResultSet(TRIBUNAL, NOMBRE_APELLIDOS);
			while (result.next()) {
				String cargo = result.getField(CARGO);
				String nombre = result.getField(NOMBRE_APELLIDOS);
				String filaTribunal = cargo + ": " + nombre;
				tribunal.addComponent(new Label(filaTribunal));
			}
		} catch (Exception e) {
			LOGGER.error("Error en tribunal", e);
		}
		horizontalTribunal.addComponents(iconoTribunal, tribunal);

		String yearIndex = config.getSetting("indexAño");
		int nextYearIndex = Integer.parseInt(yearIndex) + 1;
		Label curso = new Label("Programa en vigor a partir del Curso " + yearIndex + "-" + nextYearIndex + ".");
		addComponents(tribunalTitle, horizontalTribunal, curso);
	}
	
	/*private void createTribunal() { 
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

		try (ResultSet result = fachadaDatos.getResultSet(TRIBUNAL, NOMBRE_APELLIDOS)) {
			while (result.next()) {
				String cargo = result.getString(CARGO);
				String nombre = result.getString(NOMBRE_APELLIDOS);
				String filaTribunal = cargo + ": " + nombre;
				tribunal.addComponent(new Label(filaTribunal));
			}
		} catch (SQLException e) {
			LOGGER.error("Error en tribunal", e);
		}
		horizontalTribunal.addComponents(iconoTribunal, tribunal);

		String yearIndex = config.getSetting("indexAño");
		int nextYearIndex = Integer.parseInt(yearIndex) + 1;
		Label curso = new Label("Programa en vigor a partir del Curso " + yearIndex + "-" + nextYearIndex + ".");
		addComponents(tribunalTitle, horizontalTribunal, curso);
	}
	*/

	/**
	 * Crea las normas de entrega.
	 */
	private void createNormas() {
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
		Label normasTitle = new Label(ESPECIFICACIONES_DE_ENTREGA);
		normasTitle.setStyleName(TITLE_STYLE);

		final VerticalLayout normas = new VerticalLayout();
		normas.setSpacing(true);

		try {
			Recordset result = fachadaDatos.getResultSet(NORMA, DESCRIPCION);
			while (result.next()) {
				String descripcion = result.getField(DESCRIPCION);
				normas.addComponent(new Label(" - " + descripcion));
			}
		} catch (Exception e) {
			LOGGER.error("Error en normas", e);
		}
		addComponents(normasTitle, normas);
	}
	
	/*private void createNormas() {
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
		Label normasTitle = new Label(ESPECIFICACIONES_DE_ENTREGA);
		normasTitle.setStyleName(TITLE_STYLE);

		final VerticalLayout normas = new VerticalLayout();
		normas.setSpacing(true);

		try (ResultSet result = fachadaDatos.getResultSet(NORMA, DESCRIPCION)) {
			while (result.next()) {
				String descripcion = result.getString(DESCRIPCION);
				normas.addComponent(new Label(" - " + descripcion));
			}
		} catch (SQLException e) {
			LOGGER.error("Error en normas", e);
		}
		addComponents(normasTitle, normas);
	}
	*/

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
	private void createDocumentos() {
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
		Label documentosTitle = new Label(DOCUMENTOS);
		documentosTitle.setStyleName(TITLE_STYLE);

		final VerticalLayout documentos = new VerticalLayout();
		documentos.setSpacing(true);

		try{
			Recordset result = fachadaDatos.getResultSet(DOCUMENTO, DESCRIPCION);
			while (result.next()) {
				String descripcion = result.getField(DESCRIPCION);
				String url = result.getField("Url");
				Link link = new Link(descripcion, new ExternalResource(url));
				link.setIcon(FontAwesome.LINK);
				documentos.addComponent(link);
			}
		} catch (Exception e) {
			LOGGER.error("Error en documentos", e);
		}
		addComponents(documentosTitle, documentos);
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
	}
	
	/*private void createDocumentos() {
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
		Label documentosTitle = new Label(DOCUMENTOS);
		documentosTitle.setStyleName(TITLE_STYLE);

		final VerticalLayout documentos = new VerticalLayout();
		documentos.setSpacing(true);

		try (ResultSet result = fachadaDatos.getResultSet(DOCUMENTO, DESCRIPCION)) {
			while (result.next()) {
				String descripcion = result.getString(DESCRIPCION);
				String url = result.getString("Url");
				Link link = new Link(descripcion, new ExternalResource(url));
				link.setIcon(FontAwesome.LINK);
				documentos.addComponent(link);
			}
		} catch (SQLException e) {
			LOGGER.error("Error en documentos", e);
		}
		addComponents(documentosTitle, documentos);
		addComponent(new Label(WHITE_LINE, ContentMode.HTML));
	}*

	/**
	 * La vista se inicializa en el constructor. 
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// Se inicializa el contenido de la vista en el constructor
	}
}
