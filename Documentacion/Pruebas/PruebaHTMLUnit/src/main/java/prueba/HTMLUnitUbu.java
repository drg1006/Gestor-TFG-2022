package prueba;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Ejemplo de extracion de los enlaces de una pagina usando HTMLUnit.
 * Necesitas las librerías de HTMLUnit
 *  
 * @author Chuidiang
 *
 */
public class HTMLUnitUbu {

   public static void main(String[] args) throws Exception {
      WebClient webClient = new WebClient();
      
      HtmlPage page = webClient.getPage("https://investigacion.ubu.es/");
       LOGGER.info(page.getTitleText());
      webClient.close();      
      /*
      List<HtmlAnchor> anchors = page.getAnchors();
      for (HtmlAnchor anchor : anchors) {
        LOGGER.info(anchor.getAttribute("href"));
      }*/
   }

}