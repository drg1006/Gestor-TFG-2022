package ubu.digit.pesistence;

import java.io.Serializable;

import org.apache.log4j.Logger;

import ubu.digit.pesistence.SistInfDataCsv;
import ubu.digit.pesistence.SistInfDataXls;
import ubu.digit.util.ExternalProperties;


/**
 * Clase a partir de la cual se llamará a las fachadas singleton de los datos.
 * Ya sea, la fachada para los fichero de tipo .csv o la fachada para los .xls.
 * 
 * @author Diana Bringas Ochoa
 */
public class SistInfData implements Serializable {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -6019587024081762319L;
	
	/**
	 * Logger de la clase.
	 */
	protected static final Logger LOGGER = Logger.getLogger(SistInfData.class);
	
	/**
	 * URL donde encontramos el fichero con las propiedades del proyecto.
	 */
	private static ExternalProperties prop = ExternalProperties.getInstance("/WEB-INF/classes/config.properties",
			false);

	/**
	 * Directorio donde se encuentra los datos de entrada, es decir, los
	 * ficheros que contienen los datos que vamos a consultar.
	 */
	public static final String DIRCSV = prop.getSetting("dataIn");
	
	/**
	 * Instancia con los datos.
	 */
	private static SistInfData instance;

	/**
	 * Constructor vacío.
	 */
	private SistInfData() {
		super();
	}

	/**
	 * Método singleton para obtener la instancia de la clase fachada.
	 */
	public static SistInfData getInstance() {
		if (instance == null) {
			instance = new SistInfData();
		}
		return instance;
	}
	
	/**
	 * Método singleton para obtener la instancia de la clase fachada para ficheros .csv.
	 */
	public static SistInfDataCsv getInstanceCsv() {
		return SistInfDataCsv.getInstance();
	}
	
	/**
	 * Método singleton para obtener la instancia de la clase fachada para ficheros xls.
	 */
	public static SistInfDataXls getInstanceXls() {
		return SistInfDataXls.getInstance();
	}
	
	
}