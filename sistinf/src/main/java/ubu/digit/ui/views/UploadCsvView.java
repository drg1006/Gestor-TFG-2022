package ubu.digit.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

import ubu.digit.pesistence.BOMRemoveUTF;
import ubu.digit.pesistence.SistInfDataAbstract;
import ubu.digit.ui.components.NavigationBar;
import ubu.digit.util.ExternalProperties;

import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

/**
 * Vista de administración.
 * 
 * @author Javier de la Fuente Barrios
 */
public class UploadCsvView extends VerticalLayout implements View {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 8460171059055033456L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = Logger.getLogger(UploadCsvView.class);

	/**
	 * Nombre de la vista.
	 */
	public static final String VIEW_NAME = "upload";

	/**
	 * Etiqueta para mostrar el nombre de usuario.
	 */
	private Label userText;

	/**
	 * Botón para cerrar sesión.
	 */
	private Button logout;

	/**
	 * Elemento para subida de archivos.
	 */
	private Upload upload;

	/**
	 * Barra de progreso para la subida de archivos.
	 */
	private ProgressBar progress;

	/**
	 * 
	 */
	private CsvReceiver csvReceiver;

	/**
	 * Etiqueta para mostrar el estado de la subida.
	 */
	private Label state;

	/**
	 * Ruta del servidor.
	 */
	private String serverPath;

	/**
	 * Fichero de configuración.
	 */
	private ExternalProperties config;

	/**
	 * Directorio de los archivos csv.
	 */
	private String dirCsv;

	/**
	 * Ruta completa a los archivos csv.
	 */
	private String completeDir;

	/**
	 * Constructor.
	 */
	public UploadCsvView() {
		setMargin(true);
		setSpacing(true);

		serverPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);
		dirCsv = config.getSetting("dataIn");
		completeDir = serverPath + dirCsv + "/";

		NavigationBar navBar = new NavigationBar();
		addComponent(navBar);

		userText = new Label();

		progress = new ProgressBar();
		progress.setWidth("300px");
		progress.setCaption("Progreso:");

		csvReceiver = new CsvReceiver();

		upload = new Upload("Subida de ficheros CSV", null);
		upload.setButtonCaption("Subir fichero");
		upload.setReceiver(csvReceiver);
		upload.addStartedListener(csvReceiver);
		upload.addProgressListener(csvReceiver);
		upload.addFinishedListener(csvReceiver);
		upload.addSucceededListener(csvReceiver);
		upload.addFailedListener(csvReceiver);

		state = new Label();
		state.setCaption("Estado:");
		state.setValue("Idle");

		logout = new Button("Desconectar", new LogoutListener());

		addComponents(userText, upload, progress, state, logout);
	}

	/**
	 * Receptor de ficheros csv.
	 * 
	 * @author Javier de la Fuente Barrios
	 */
	class CsvReceiver implements Receiver, StartedListener, ProgressListener, FinishedListener, SucceededListener,
			FailedListener {
		/**
		 * Serial Version UID.
		 */
		private static final long serialVersionUID = -1414096228596596894L;

		/**
		 * Stream de salida para escribir ficheros.
		 */
		private transient FileOutputStream fos;

		/**
		 * Proporciona un stream de salida para escribir el fichero a subir.
		 */
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			fos = null;
			File file;
			try {
				file = new File(completeDir + filename);
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				LOGGER.error("Error en CsvReceiver", e);
				return new NullOutputStream();
			}
			return fos;
		}

		/**
		 * Stream nulo para evitar execpciones.
		 * 
		 * @author Javier de la Fuente Barrios
		 */
		private class NullOutputStream extends OutputStream {

			/**
			 * Operación de escritura, no realiza nada.
			 */
			@Override
			public void write(int b) throws IOException {
				// Null output stream to write to nowhere
			}
		}

		/**
		 * Comienzo de la subida.
		 */
		@Override
		public void uploadStarted(StartedEvent event) {
			if (event.getFilename().isEmpty() || event.getFilename() == null) {
				Notification.show("Error", "Seleccione un fichero primero.", Type.ERROR_MESSAGE);
				upload.interruptUpload();
				return;
			}

			if (!event.getFilename().endsWith(".csv")) {
				Notification.show("Error",
						"El formato del fichero no esta soportado. Seleccione un fichero con extensión .csv",
						Type.ERROR_MESSAGE);
				upload.interruptUpload();
				return;
			}

			progress.setValue(0.0f);
			state.setValue("Subiendo fichero");
		}

		/**
		 * Progreso de la subida.
		 */
		@Override
		public void updateProgress(long readBytes, long contentLength) {
			progress.setValue(new Float(readBytes / (float) contentLength));
			state.setValue("Procesados " + readBytes + " bytes de un total de " + contentLength + " bytes.");

		}

		/**
		 * Finalización de la subida.
		 */
		@Override
		public void uploadFinished(FinishedEvent event) {
			state.setValue("Idle");
			try {
				new BOMRemoveUTF().bomRemoveUTF(serverPath  + SistInfDataAbstract.DIRCSV + "/" + event.getFilename());
			} catch (FileNotFoundException e) {
				LOGGER.error("Error en upload finished", e);
				
			} catch (IOException e) {
				LOGGER.error("Error en upload finished ", e);
			}
			closeResources();
		}

		/**
		 * Subida completada y satisfactoria.
		 */
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			state.setValue("Subida de fichero " + event.getFilename() + " existosa.");
		}

		/**
		 * Subida completada pero errónea.
		 */
		@Override
		public void uploadFailed(FailedEvent event) {
			state.setValue("Subida de fichero " + event.getFilename() + " fallida");
			File file = new File(completeDir + event.getFilename());
			if (file.exists()) {
				boolean deleted = file.delete();
				if (!deleted) {
					LOGGER.error("Fichero " + file.getName() + " no borrado correctamente");
				}
			}
		}

		/**
		 * Cierra los recursos.
		 */
		private void closeResources() {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error("Error en uploadcsv", e);
				}
			}
		}
	}

	/**
	 * Listener para el botón de cerrar sesión.
	 * 
	 * @author Javier de la Fuente Barrios
	 */
	private class LogoutListener implements Button.ClickListener {

		/**
		 * Serial Version UID.
		 */
		private static final long serialVersionUID = -6910251607481142610L;

		/**
		 * Acción a realizar al recibir el evento.
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			getSession().setAttribute("user", null);
			getUI().getNavigator().navigateTo(InformationView.VIEW_NAME);
			Notification.show("Has cerrado sesión satisfactoriamente.");
		}
	}

	/**
	 * Acción a realizar al entrar en la vista.
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		String username = String.valueOf(getSession().getAttribute("user"));
		userText.setValue("Hola " + username);
	}
}
