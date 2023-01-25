/**
 * Con esta método compruebo el Status code de la respuesta que recibo al hacer la petición
 * EJM:
 * 		200 OK			300 Multiple Choices
 * 		301 Moved Permanently	305 Use Proxy
 * 		400 Bad Request		403 Forbidden
 * 		404 Not Found		500 Internal Server Error
 * 		502 Bad Gateway		503 Service Unavailable
 * @param url
 * @return Status Code
 */

package Prueba;

import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PruebaWebScrap{
	
	public static int getStatusConnectionCode(String url) {
		
    	Response response = null;
	
    	try {
    		response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
    	} catch (IOException ex) {
    		// LOGGER.info("Excepción al obtener el Status Code: " + ex.getMessage());
    	}
    	 //LOGGER.info("response: "+ response.statusMessage());
    	return response.statusCode();
	}
	
	/**
	 * Con este método devuelvo un objeto de la clase Document con el contenido del
	 * HTML de la web que me permitirá parsearlo con los métodos de la librelia JSoup
	 * @param url
	 * @return Documento con el HTML
	 */
	public static Document getHtmlDocument(String url) {

	    Document doc = null;
		try {
		    doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").timeout(100000).get();
		    } catch (IOException ex) {
			// LOGGER.info("Excepción al obtener el HTML de la página" + ex.getMessage());
		    }
	    return doc;
	}
	
public static final String url = "https://investigacion.ubu.es/unidades/2682/investigadores";
	
    public static void main (String args[]) {
		
        // Compruebo si me da un 200 al hacer la petición
        if (getStatusConnectionCode(url) == 200) {
			
            // Obtengo el HTML de la web en un objeto Document
            Document document = getHtmlDocument(url);
			
            // Busco todas las entradas que estan dentro de: 
            Elements entradas = document.select("div.c-persona-card__detalles");
          	//LOGGER.info("Número de entradas en la página inicial : "+entradas.size()+"\n");
			
            // Paseo cada una de las entradas
            for (Element elem : entradas) {
                String nombre = elem.getElementsByClass("c-persona-card_nombre").text();
                String apellidos = elem.getElementsByClass("c-persona-card_nombre").text();
                String area = elem.getElementsByClass("c-persona-card_nombre").text();
				
                 //LOGGER.info(nombre+"\n"+apellidos+"\n"+area+"\n\n");
				
                // Con el método "text()" obtengo el contenido que hay dentro de las etiquetas HTML
                // Con el método "toString()" obtengo todo el HTML con etiquetas incluidas
            }
				
        }else {
            //LOGGER.info("El Status Code no es OK es: "+getStatusConnectionCode(url));
        }
    }
}