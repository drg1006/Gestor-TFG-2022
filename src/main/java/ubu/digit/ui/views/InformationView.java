package ubu.digit.ui.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
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

import ubu.digit.persistence.SistInfDataAbstract;
import ubu.digit.persistence.SistInfDataFactory;
import ubu.digit.ui.components.*;
import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;

import java.util.List;

/**
 * Vista de información.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 * @author David Renedo Gil
 */
@Route("")
@CssImport(value = "./styles/shared-styles.css", themeFor = "sistinftheme")
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
     * Fachada para obtener los datos
     */
    private SistInfDataAbstract fachadaDatos;
    /**
     * Creamos el footer.
     */
    private Footer footer;

    /**
     * Nombre de la vista.
     */
    public static final String VIEW_NAME = "information";

    /**
     * Constructor.
     */
    public InformationView() {
        fachadaDatos = SistInfDataFactory.getInstanceData();
        config = ExternalProperties.getInstance("/config.properties", false);

        setMargin(true);
        setSpacing(true);

        NavigationBar bat = new NavigationBar();
        bat.buttonInfo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(bat);
        createTribunal();
        createNormas();
        createCalendar();
        createDocumentos();

        footer = new Footer("N1_Tribunal.csv");
        add(footer);
    }

    /**
     * Crea el tribunal.
     */
    private void createTribunal() {
        H1 tribunalTitle = new H1(MIEMBROS_DEL_TRIBUNAL);
        tribunalTitle.addClassName(TITLE_STYLE);
        add(tribunalTitle);

        final VerticalLayout tribunal = new VerticalLayout();
        tribunal.setSpacing(true);
        tribunal.setWidth(500, Unit.PIXELS);

        // Se obtienen los nombres y apellidos de los miembros del tribunal
        List<String> listaTribunal;
        listaTribunal = fachadaDatos.getTribunal();
        LOGGER.info("Obteniendo datos del tribunal ");
        for (int i = 0; i < listaTribunal.size(); i++) {
            tribunal.add(new Label(listaTribunal.get(i)));
        }
        add(tribunal);
    }

    /**
     * Crea las normas de entrega.
     */
    private void createNormas() {
        H1 rulesTitle = new H1(ESPECIFICACIONES_DE_ENTREGA);
        rulesTitle.addClassName(TITLE_STYLE);
        add(rulesTitle);

        final VerticalLayout rules = new VerticalLayout();
        rules.setSpacing(true);

        // Se obtienen las descripciones de las normas
        List<String> rulesList;
        rulesList = fachadaDatos.getNormas();
        LOGGER.info("Obteniendo normas de entrega ");

        for (int i = 0; i < rulesList.size(); i++) {
            rules.add(new Label(" - " + rulesList.get(i)));
        }
        add(rulesTitle, rules);
    }

    /**
     * Crea el calendario.
     */
    private void createCalendar() {
        H1 datesTitle = new H1(FECHAS_DE_ENTREGA);
        datesTitle.addClassName(TITLE_STYLE);

        String urlCalendario = config.getSetting("urlCalendario");
        IFrame calendar = new IFrame("https://" + urlCalendario);
        calendar.setWidth(85, Unit.PERCENTAGE);
        calendar.setHeight(500, Unit.PIXELS);
        add(datesTitle, calendar);
    }

    /**
     * Crea los documentos de entrega.
     */
    private void createDocumentos() {
        H1 documentsTitle = new H1(DOCUMENTOS);
        documentsTitle.addClassName(TITLE_STYLE);

        final VerticalLayout documents = new VerticalLayout();
        documents.setSpacing(true);

        // Se obtienen las descripciones y la url de los documentos
        List<String> listDocuments;
        listDocuments = fachadaDatos.getDocumentos();
        LOGGER.info("Obteniendo documentos a entregar ");

        for (int i = 0; i < listDocuments.size(); i++) {
            String nameLink = listDocuments.get(i);
            Anchor link = new Anchor(listDocuments.get(++i), nameLink);
            documents.add(new HorizontalLayout(new Icon(VaadinIcon.LINK), link));
        }
        add(documentsTitle, documents);
    }
}
