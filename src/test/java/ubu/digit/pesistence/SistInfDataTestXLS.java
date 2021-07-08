package ubu.digit.pesistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.codoid.products.exception.FilloException;

import ubu.digit.persistence.SistInfDataXls;
import ubu.digit.util.ExternalProperties;

/**
 * Conjunto de método que verifican la cobertura de la clase SistInfDataXls.
 * 
 * @author Diana Bringas Ochoa
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ExternalProperties.class, SistInfDataXls.class })
public class SistInfDataTestXLS {

    /**
     * Clase fachada a testear.
     */
	SistInfDataXls sistInfData;

    /**
     * URL del fichero donde se encuentra el fichero de configuración del test.
     */
    ExternalProperties test = ExternalProperties.getInstance("/testConfig.properties", true);
   
    /**
     * Método que se ejecuta antes de cualquier test. Modifica el fichero de
     * configuración, por el propio del test.
     */
    @Before
    public void setUp() {
        mockStatic(ExternalProperties.class);

        when(ExternalProperties.getInstance("/config.properties", false)).thenReturn(test);

        sistInfData = SistInfDataXls.getInstance(); 
    }

    /**
     * Test que comprueba que el método que obtiene la media de una lista de
     * números funciona correctamente.
     *
     * En este caso, la columna "notas" es una lista de notas desde el 1 hasta
     * el 10 por lo que el resultado que esperamos es 5.5.
     * 
     * @throws FilloException
     */
    @Test
    public void testAvg(){
        Number esperado = sistInfData.getAvgColumn("Nota", "N3_Historico");
        assertEquals(esperado, is((Number) 7.0625F));
    }

    /**
     * Test que comprueba que el método que obtiene el valor máximo de un
     * listado funciona correctamente. En este caso, la columna "numero" tiene
     * como máximo un 9 y la columna "nota" tiene como máximo un 10.
     * 
     * @throws FilloException
     */
    @Test
    public void testMaxColumn() throws FilloException {

        String tableName = "N3_Historico";
        Number not = sistInfData.getMaxColumn("Nota", tableName);
        assertEquals(not, is((Number) 9F));
    }

    /**
     * Test que comprueba que el método que obtiene el valor mínimo de un
     * listado funciona correctamente. En este caso, la columna "numero" tiene
     * como mínimo un 0 y la columna "nota" tiene como mínimo un 1.
     * 
     * @throws FilloException
     */
    @Test
    public void testMinColumn() throws FilloException {
        String tableName = "N3_Historico";
        Number not = sistInfData.getMinColumn("Nota", tableName);
        assertEquals(not, is((Number) 4F));
    }

    /**
     * Test que comprueba que el método que obtiene la desviación estándar de un
     * listado funciona correctamente. En este caso, la columna "numero" tiene
     * un listado de números de 0 a 9 y la columna "nota" un listado de 1 a 10,
     * por lo que en ambas esperamos la misma desviación.
     * 
     * @throws FilloException
     */
    @Test
    public void testStdvColumn() throws FilloException { 
        String tableName = "N3_Historico";
        Number not = sistInfData.getStdvColumn("Nota", tableName);
        assertEquals(not, is((Number) 1.8980723303996008));
    }

    /**
     * Este test comprueba que el método no nos devuelve conjunto vacíos de
     * datos.
     * 
     * @throws FilloException
     */
    @Test
    public void testResultSet() throws FilloException {
    	String tableName = "N3_Historico";

    	assertEquals(sistInfData.getResultSet(tableName, "Nota",
                "Descripcion='La descripción del TFG/TFM'"), notNullValue());

        String[] metricValoreSelect = { "Titulo", "Nota", "Descripcion" };
        assertEquals(sistInfData.getResultSet(tableName, "Nota", null,
                metricValoreSelect), notNullValue());
        assertEquals(sistInfData.getResultSet(tableName, "Nota",
                metricValoreSelect, null), notNullValue());
    }

    /**
     * Este test comprueba que el método devuelva correctamente el número de
     * filas según la ejecución SQL.
     * 
     * En la primera pedimos que nos cuente el número de filas distintas donde
     * la nota sea un 5, que solo tenemos 1.
     * 
     * En la segunda le pedimos que nos cuente el número de filas distintas de
     * la columna "nota" y "descripcion", por lo que esperamos 10 de la columna
     * "nota" + 2 de la columna "descripcion". Un total de 12.
     * 
     * Y por último, pedimos que nos cuente una columna nula, que como no
     * existe pues nos devolverá 0. Y una columna que no existe, por lo que
     * lanzará una excepción .
     * 
     * @throws FilloException
     */
    public void testTotalNumber() {
    	String tableName = "N3_Historico";
        Number obtenido = sistInfData.getTotalNumber("Nota", tableName,
                "Nota='5'");
        assertEquals(obtenido, is((Number) 1F));

        String[] columnNames = { "Nota", "Descripcion" };
        Number obt = sistInfData.getTotalNumber(columnNames, tableName);
        assertEquals(obt, is((Number) 8F));

        columnNames = null;
        obt = sistInfData.getTotalNumber(columnNames, tableName);
        assertEquals(obt, is((Number) 0));

    }

    @Test
    public void testQuartilColumn() throws FilloException {
    	String tableName = "N3_Historico";
    	Double quartil = 0.25;
    	assertEquals(sistInfData.getQuartilColumn("Nota", tableName, quartil), notNullValue());
    }

    /**
     * En este test comprobamos que la obtención de la fecha máxima y la fecha
     * mínima funcione correctamente. La fecha mínima que esperamos es
     * 1/01/1649. Y la fecha máxima es 1/01/2015.
     * 
     * @throws FilloException
     */
    @Test
    public void testFechas() throws FilloException {
    	String tableName = "N3_Historico";
        LocalDate esperado = sistInfData.getYear("FechaAsignacion", tableName, true);
        LocalDate valor = LocalDate.of(2014, 11, 07);

        assertEqualDates(esperado, valor);

        LocalDate esperado2 = sistInfData.getYear("FechaAsignacion", tableName, false);
        LocalDate valor2 = LocalDate.of(2017, 10, 06);
        
        assertEqualDates(esperado2, valor2);
        
        LocalDate esperado3 = sistInfData.getYear("FechaPresentacion", tableName, true);
        LocalDate valor3 = LocalDate.of(2015, 07, 07);

        assertEqualDates(esperado3, valor3);

        LocalDate esperado4 = sistInfData.getYear("FechaPresentacion", tableName, false);
        LocalDate valor4 = LocalDate.of(2018, 07, 06);

        assertEqualDates(esperado4, valor4);
    }
    
    /**
     * Se comprueba que el formato de las fechas sea dd/mm/yyyy
     * 
     * @throws FilloException
     * @throws ParseException 
     */
    @Test
    public void testFormatDateHistorico() throws FilloException, ParseException { 
    	String tableName = "N3_Historico";
        List<String> dates = sistInfData.getDates("FechaAsignacion", tableName);
        
        String dateWithFormat = "";
        Date date = null;
        SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");//Convierte String a Date con el formato
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy"); //Convierte de Date a String con el formato
        
        for(int i=0;i<dates.size();i++) {
        	date = parseador.parse(dates.get(i));
        	dateWithFormat = formateador.format(date);
        	assertEquals(dates.get(i), dateWithFormat);
        }
    }

    /**
     * Método que formatea las fechas y comprueba si son iguales.
     * 
     * @param expected
     *            Fecha esperada.
     * @param value
     *            Fecha obtenida.
     */
    private static void assertEqualDates(LocalDate expected, LocalDate value) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String strExpected = dateTimeFormatter.format(expected);
        String strValue = dateTimeFormatter.format(value);
        assertEquals(strExpected, strValue);
    }

    /**
     * Test que comprueba que salta la excepción FilloException al intentar
     * obtener datos de una tabla que no existe.
     * 
     * @throws FilloException
     */
    @Test(expected = FilloException.class)
    public void testSinTabla() throws FilloException {
        sistInfData.getAvgColumn("Nada", "Nada");
    }

    /**
     * Test que comprueba que salta la excepción FilloException al intentar
     * obtener datos de una tabla que está vacía.
     * 
     * @throws FilloException
     */
    @Test(expected = FilloException.class)
    public void testTablaVacia() throws FilloException {
        sistInfData.getAvgColumn("Nada", "Vacia");
    }

    /**
     * Método main.
     * 
     * @param args
     */
    public static void main(String[] args) {
        JUnitCore.main("ubu.digit.pesistence.SistInfDataTestXLS");
    }
}