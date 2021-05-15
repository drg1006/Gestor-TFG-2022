package ubu.digit.ui.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import elemental.json.Json;
import ubu.digit.pesistence.SistInfDataFactory;
import ubu.digit.ui.MainLayout;
import ubu.digit.util.ExternalProperties;

/**
 * Vista de administración.
 * 
 * @author Javier de la Fuente Barrios
 */

@Route(value = "Upload", layout = MainLayout.class)
@PageTitle("Actualización de ficheros")
public class UploadView extends VerticalLayout{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8460171059055033456L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadView.class.getName());

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "upload";

	/**
	 * Botón para cerrar sesión.
	 */
	private Button logout;

	/**
	 * Elemento para subida de archivos.
	 */
	private Upload upload;

	/**
	 * Ruta del servidor.
	 */
	private String serverPath;

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Directorio de los archivos csv y xls.
	 */
	private String dir;

	/**
	 * Ruta completa a los archivos csv y xls.
	 */
	private String completeDir;


	/**
	 * Constructor.
	 */
	public UploadView() {
		setMargin(true);
		setSpacing(true);

  		String path = this.getClass().getClassLoader().getResource("").getPath();
  		serverPath = path.substring(0, path.length()-17);
  		
		config = ExternalProperties.getInstance("/config.properties", false);
		dir = config.getSetting("dataIn");
		completeDir = serverPath + dir + "/";
		
		MemoryBuffer  buffer = new MemoryBuffer ();
        upload = new Upload(buffer);
        Div output = new Div();
        
        upload.setWidth("2000px");
        upload.addStartedListener(event -> {
            upload.getElement().getPropertyNames()
                    .forEach(prop -> System.out.println(prop + " "
                            + upload.getElement().getProperty(prop)));
        });
        upload.addFileRejectedListener(event -> {
            Notification.show(event.getErrorMessage());
        });
        
        upload.setAcceptedFileTypes(".csv",".xls");
        upload.setAutoUpload(false);

        upload.addSucceededListener(event -> {
        	 try {
                 byte[] buf = new byte[(int)event.getContentLength()];
                 InputStream is = buffer.getInputStream();
                 is.read(buf);
                 File fileDeleted = new File(completeDir + event.getFileName());
                 if (fileDeleted.exists()) {
         			boolean deleted = fileDeleted.delete();
         			LOGGER.info("Fichero " + fileDeleted + " eliminado");
         			if (!deleted) {
         				LOGGER.error("Fichero " + fileDeleted.getName() + " no se ha borrado correctamente");
         			}
                 }
                 File targetFile = new File(completeDir + event.getFileName());
                 OutputStream outStream = new FileOutputStream(targetFile);
                 outStream.write(buf);
                 LOGGER.info("Fichero " + event.getFileName() +" cargado!");
                 outStream.flush();
                 outStream.close();
             } catch (IOException ex) {
            	 LOGGER.error("Error: " + UploadView.class.getName() + " " + ex);
             }
            
            output.removeAll();
            showOutput(event.getFileName(), output);
        });
        
        upload.addFinishedListener(event ->{
        	if (!upload.isUploading()) {
	    		 if(event.getFileName().endsWith(".csv")) {
	  				SistInfDataFactory.setInstanceData("CSV");
	  				LOGGER.info("Actualización del fichero "+ event.getFileName() + " finalizada");
	  			}else {
	  				SistInfDataFactory.setInstanceData("XLS");
	  				LOGGER.info("Actualización del fichero "+ event.getFileName() + " finalizada");
	  			}
        	}
        });

		upload.addFileRejectedListener(event -> {
		    output.removeAll();
		});
		upload.getElement().addEventListener("file-remove", event -> {
		    output.removeAll();
		});
		
        upload.getElement().addEventListener("file-abort", event1 -> {
            String files = upload.getElement().getProperty("files");
            Notification.show(files);
            System.out.println(files);
        });

        upload.addFailedListener(event -> {
	        Notification.show("Error al cargar el fichero: "
	        +event.getFileName()).addThemeVariants(NotificationVariant.LUMO_ERROR);
	        upload.getElement().setPropertyJson("files", Json.createArray());});

        add(upload, output);
        logout = new Button("Desconectar");
		logout.addClickListener(e -> UI.getCurrent().navigate(InformationView.class));
		add(logout);
	}

  private void showOutput(String text ,
            HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText("Actualización de " + text + " completada");
        outputContainer.add(p);
    }
}
