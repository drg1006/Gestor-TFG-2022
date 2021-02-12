package ubu.digit.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.vaadin.server.VaadinService;

/**
 * Clase para la obtención de los valores de las propiedades.
 * 
 * @author Beatriz Zurera Martínez-Acitores.
 * @author Javier de la Fuente Barrios
 * @since 3.0
 */
public class ExternalProperties implements Serializable {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 3667519929144990872L;

	/**
     * Logger de la clase.
     */
    private static final Logger LOGGER = Logger.getLogger(ExternalProperties.class);

    /**
     * Propiedad.
     */
    private static final Properties PROPERTIES = new Properties();

    /**
     * Fichero del que leeremos las propiedades.
     */
    private static String file;

    /**
     * Instancia que tendrá la propiedad.
     */
    private static ExternalProperties instance;

    /**
     * Dirección de los ficheros en la aplicación del servidor.
     */
	private static String basePath = "";

    /**
     * Constructor.
     */
    private ExternalProperties() {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

	/**
	 * Método singleton para obtener la instancia del fichero de propiedades.
	 * 
	 * @param propFileName
	 *            nombre del fichero
	 * @param testFlag
	 *            bandera para diferenciar los ficheros de propiedades de test y
	 *            ejecución (true: test, false: no test)
	 * @return
	 */
    public static ExternalProperties getInstance(String propFileName, Boolean testFlag) { 
    	if(!testFlag) {
    		basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    	}
    	
        if (instance == null) {
            file = basePath + propFileName;
            instance = new ExternalProperties();
        }
        return instance;
    }
    
    /**
     * Método que obtiene el valor de la propiedad que se pasa.
     * 
     * @param key
     *            Propiedad de la cual queremos conocer el valor.
     * @return El valor de la propiedad.
     */
    public String getSetting(String key) {
		return PROPERTIES.getProperty(key).trim();
	}

}