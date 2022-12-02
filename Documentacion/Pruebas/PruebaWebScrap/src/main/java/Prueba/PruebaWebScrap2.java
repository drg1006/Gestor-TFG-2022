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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


import com.opencsv.CSVWriter;

public class PruebaWebScrap2{

    public static void main (String args[]) {
    	
    	List<String[]> profesores = new ArrayList<String[]>();
    	Map<String, Object[]> dataTFG = new TreeMap<String, Object[]>(); 
    	Response response = null;
		try {
			//Realizamos la petición de la url
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
            System.out.println("Número de profesores de la EPS : "+entradas.size()+"\n");
            // Paseo cada una de las entradas
            int i=1;
            for (Element elem : entradas) {
            	
            	/*Referencia de estas dos lineas de código:
            	 * https://stackoverflow.com/questions/30408174/jsoup-how-to-get-href*/
            	
            	//Cogemos la url de detalles para posteriormente coger su departamento
            	Element link = elem.select("div.c-persona-card__detalles > a").first();
            	String url = link.absUrl("href");
            	
            	//Cogemos los elementos que necesitamos
                String nombre = elem.getElementsByClass("c-persona-card__nombre").text();
                String apellidos = elem.getElementsByClass("c-persona-card__apellidos").text();
                String area = elem.getElementsByClass("c-persona-card__area").text();
				//Imprimimos por pantalla
               //System.out.println("Nombre: "+nombre);
                //System.out.println("Apellidos: " +apellidos);
                //System.out.println("Area: "+area);

                //Para sacar el Departamento debemos ir a otra url 
                try {
        			response = Jsoup.connect(url)
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

            	Document doc2 = null;
        		try {
        			doc2 = response.parse();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		//Obtenemos el contenido donde está la información del profesor
        		Elements entrada= doc2.select("div.main-content");
        		//Cogemos el primer link de tipo "a"(href) que tiene la información sobre el departamento y sacamos su texto
        		Element link2= entrada.select("a").first();
        		String departamento = link2.text();	
        		//System.out.println("Departamento: "+ departamento + "\n");

                // Con el método "text()" obtengo el contenido que hay dentro de las etiquetas HTML
                // Con el método "toString()" obtengo todo el HTML con etiquetas incluidas
        		//https://commons.apache.org/proper/commons-lang//apidocs/org/apache/commons/lang3/StringUtils.html#stripAccents-java.lang.String-
        		nombre = StringUtils.stripAccents(nombre);
            	apellidos =StringUtils.stripAccents(apellidos);
            	area =StringUtils.stripAccents(area);
            	departamento =StringUtils.stripAccents(departamento);
        		String [] profesor= {nombre +" "+ apellidos, area, departamento};
            	
            	profesores.add(profesor);
            	i++;
            	if(i==2) {
            		dataTFG.put("1", new Object[] {"NombreApellidos", "Area","Departamento"});
            		dataTFG.put("2",profesor);
            	}else {
            		String id=Integer.toString(i);
            		dataTFG.put(id,profesor);
            	}
            	
            	
            	
            }
            
        	try {
				guardarDatosXLS(dataTFG);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            try {
				guardarDatosCSV(profesores);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    }
    
    public static void guardarDatosCSV(List<String[]> profesores) throws IOException {
    	//https://www.campusmvp.es/recursos/post/como-leer-y-escribir-archivos-csv-con-java.aspx 
    	String excelFilePath = "src/main/resources/data/N4_Profesores.csv";
    	File file = new File(excelFilePath);
        String absPath = file.getAbsolutePath();
    	CSVWriter writer = new CSVWriter(new FileWriter(absPath));   	
    	writer.writeAll(profesores);
    	writer.close();
    }
    
    public static void guardarDatosXLS(Map<String, Object[]> dataTFG) throws IOException {
    	//https://www.codejava.net/coding/java-example-to-update-existing-excel-files-using-apache-poi
    	
    	String excelFilePath = "src/main/resources/data/BaseDeDatosTFGTFM.xls";
    	File file = new File(excelFilePath);
        String absPath = file.getAbsolutePath();
        
        try {
            FileInputStream inputStream = new FileInputStream(new File(absPath));
            Workbook workbook = WorkbookFactory.create(inputStream);
 
            Sheet hoja= workbook.getSheet("N4_Profesores");
      
            Row rowCount;
              
            Set<String> keyid = dataTFG.keySet();
            
            int rowid = 0;
            
            //https://es.acervolima.com/como-escribir-datos-en-una-hoja-de-excel-usando-java/
            // writing the data into the sheets...
      
            for (String key : keyid) {
      
                rowCount = hoja.createRow(rowid++);
                Object[] objectArr = dataTFG.get(key);
                int cellid = 0;
      
                for (Object obj : objectArr) {
                    Cell cell = rowCount.createCell(cellid++);
                    cell.setCellValue((String)obj);
                }
            }
 
            FileOutputStream outputStream = new FileOutputStream(absPath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
             
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}