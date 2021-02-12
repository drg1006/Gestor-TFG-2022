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
	 * Método para obtener la instancia de la clase fachada según el parámetro pasado.
	 */
	public static SistInfDataAbstract getInstanceData(String type) {
		if(type == null) {
			return null;
		}
		//String dbType = ConfigLoader.getDBType();
        //System.out.println("DBType => " + dbType);
        switch(type){
            case "CSV":
            	return SistInfDataCsv.getInstance();
            case "XLS":
            	return SistInfDataXls.getInstance();
            default:
                throw new RuntimeException("Unsupported data type");
        }
    }
}