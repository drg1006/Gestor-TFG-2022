package ubu.digit.pesistence;

import java.io.Serializable;

import org.apache.log4j.Logger;

import ubu.digit.util.ExternalProperties;


/**
 * Clase que implementa el patrón Factory.
 * 
 * Se delegan las responsabilidades de la creación de los objectos, en este caso las Fachadas.
 * Ya sea, la fachada para los fichero de tipo .csv o la fachada para los .xls.
 * 
 * @author Diana Bringas Ochoa
 */
public class SistInfDataFactory implements Serializable { //TODO: Serializabble??

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -6019587024081762319L;
	
	/**
	 * Logger de la clase.
	 */
	protected static final Logger LOGGER = Logger.getLogger(SistInfDataFactory.class);
	
	/**
	 * Fichero de configuración.
	 */
	private static ExternalProperties config;
	
	/**
	 * Variable que almacena el tipo de datos que se está usando
	 */
	public static String type="";
	
	/**
	 * Método para obtener la instancia de la clase fachada según el parámetro pasado.
	 */
	public static SistInfDataAbstract getInstanceData() {
		if(type == "") {
			config = ExternalProperties.getInstance("/WEB-INF/classes/config.properties", false);
			setInstanceData(config.getSetting("sistInfData"));
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
	
	public static void setInstanceData(String typeData) {
		type = typeData;
	}
}