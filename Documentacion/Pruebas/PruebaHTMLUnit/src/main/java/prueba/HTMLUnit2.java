package prueba;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.FileWriter;

/**
 * Ejemplo de extracion de los enlaces de una pagina usando HTMLUnit.
 * Necesitas las librerías de HTMLUnit
 *  
 * @author Chuidiang
 *
 */
public class HTMLUnit2 {

   public static void main(String[] args) throws Exception {
	   
	   WebClient webClient = new WebClient(BrowserVersion.CHROME);

	   try {
	      HtmlPage page = webClient.getPage("https://foodnetwork.co.uk/italian-family-dinners/");

	      webClient.getCurrentWindow().getJobManager().removeAllJobs();
	      webClient.getOptions().setCssEnabled(false);
	      webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	      webClient.getOptions().setThrowExceptionOnScriptError(false);
	      webClient.getOptions().setPrintContentOnFailingStatusCode(false);
	      
	      String title = page.getTitleText();
	      LOGGER.info("Page Title: " + title);
	     List<HtmlAnchor> links = page.getAnchors();
	      for (HtmlAnchor link : links) {
	         String href = link.getHrefAttribute();
	          LOGGER.info("Link: " + href);
	      }
	      List<?> anchors = page.getByXPath("//a[@class='card-link']");
	      String recipeTitle =null;
	      String recipeLink = null;
	      int i=0;
	      for (i=0; i < anchors.size(); i++) {
	         HtmlAnchor link = (HtmlAnchor) anchors.get(i);
	         recipeTitle = link.getAttribute("title").replace(',', ';');
	         recipeLink = link.getHrefAttribute();
	      }
	      
	      FileWriter recipesFile = new FileWriter("recipes.csv", true);
	      recipesFile.write("id,name,link\n");
	      recipesFile.write(i + "," + recipeTitle + "," + recipeLink + "\n");
	      webClient.close();
	      recipesFile.close();

	   } catch (IOException e) {
	       LOGGER.info("An error occurred: " + e);
	   }
   }

}