package prueba;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Ejemplo de extracion de los enlaces de una pagina usando HTMLUnit.
 * Necesitas las librerías de HTMLUnit
 *  
 * @author Chuidiang
 *
 */
public class HTMLUnit {

   public static void main(String[] args) throws Exception {
      WebClient webClient = new WebClient();
      HtmlPage page = webClient.getPage("http://www.gnu.org/home.es.html");
      
      DomNodeList<DomElement> nodeList = page.getElementsByTagName("a");
      for (DomElement element : nodeList){
        LOGGER.info(element.getTextContent()+ " -> " +element.getAttribute("href"));
      }
      
   }

}