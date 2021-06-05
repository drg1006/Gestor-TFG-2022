package ubu.digit.pesistence;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubu.digit.util.ExternalProperties;


/**
 * Clase que implementa el patrón Factory.
 * 
 * Se delegan las responsabilidades de la creación de los objectos, en este caso las Fachadas.
 * Ya sea, la fachada para los fichero de tipo .csv o la fachada para los .xls.
 * 
 * @author Diana Bringas Ochoa
 */
public class SistInfDataFactory implements Serializable { 

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -6019587024081762319L;
	
	/**
	 * Logger de la clase.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(SistInfDataFactory.class.getName());
	
	/**
	 * Fichero de configuración.
	 */
	private static ExternalProperties config;

	/**
	 * Variable que almacena el tipo de datos que se está usando
	 */
	private static String type="";
	
	/**
	 * Método para obtener la instancia de la clase fachada según el parámetro pasado.
	 */
	
	public static SistInfDataAbstract getInstanceData() {
		if(type.equals("")) {
			config = ExternalProperties.getInstance("/config.properties", false);
			setInstanceData(config.getSetting("sistInfData"));
			LOGGER.info("Tipo de Fachada de datos: " + type);
		}
		
		switch(type){
	    	case "csv":
	        case "CSV":
	        	return SistInfDataCsv.getInstance();
	        case "xls":
	        case "XLS":
	        	return SistInfDataXls.getInstance();
	        default:
	            throw new RuntimeException("Unsupported data type");
		}
	}
	
	/**
	 * Método para establecer la fachada de datos que se empleará (CSV o XLS)
	 * @param typeData
	 */
	public static void setInstanceData(String typeData) {
		if(typeData == "XLS") {
			SistInfDataXls.getUploadInstance();
		}
		type = typeData;
	}
}