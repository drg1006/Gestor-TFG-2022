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

public class PruebaWebScrap2{

    public static void main (String args[]) {
    	
    	Response response = null;
		try {
			response = Jsoup.connect("https://investigacion.ubu.es/unidades/2682/investigadores")
			       .ignoreContentType(true)
			       .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")  
			       .referrer("http://www.google.com")   
			       .timeout(12000) 
			       .followRedirects(true)
			       .execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	Document doc = null;
		try {
			doc = response.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
            // Busco todas las entradas que estan dentro de: 
            Elements entradas = doc.select("div.c-persona-card__detalles");
            System.out.println("Número de entradas en la página inicial : "+entradas.size()+"\n");
            // Paseo cada una de las entradas
            for (Element elem : entradas) {

                String nombre = elem.getElementsByClass("c-persona-card__nombre").text();
                String apellidos = elem.getElementsByClass("c-persona-card__apellidos").text();
                String area = elem.getElementsByClass("c-persona-card__area").text();
				
                System.out.println(nombre+"\n"+apellidos+"\n"+area+"\n\n");
				
                // Con el método "text()" obtengo el contenido que hay dentro de las etiquetas HTML
                // Con el método "toString()" obtengo todo el HTML con etiquetas incluidas
            }

    }
}