package ubu.digit.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.VaadinServlet;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalProperties.class.getName());

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
        	LOGGER.info("ExternalProperties");
        	inputStream = ExternalProperties.class.getClassLoader().getResourceAsStream(file);
        	//ExternalProperties.class.getResourceAsStream("/config.properties");
        	
        	LOGGER.info("ExternalProperties - InputStream de " + file + " :" + inputStream);
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("ExternalProperties error al crear el InputStream y leerlo :", e);
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
    	LOGGER.info("ExternalProperties - GetInstance");
    	if(!testFlag) {
    		String path = ExternalProperties.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    		basePath = path.substring(1, path.length()-1);
    	}
    	
        if (instance == null) {
            file = propFileName;
            LOGGER.info("ExternalProperties - GetInstance rute: " + basePath + file);
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