package ubu.digit.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.sql.SQLException;
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

import ubu.digit.util.ExternalProperties;
import ubu.digit.persistence.SistInfDataCsv;

/**
 * Conjunto de método que verifican la cobertura de la clase SistInfDataCsv.
 * 
 * @author Beatriz Zurera Martínez-Acitores
 * @author Diana Bringas Ochoa
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ExternalProperties.class, SistInfDataCsv.class })
public class SistInfDataTestCSV {

    /**
     * Clase fachada a testear.
     */
    SistInfDataCsv sistInfData;

    /**
     * URL del fichero donde se encuentra el fichero de configuración del test.
     */
    ExternalProperties test = ExternalProperties
    		.getInstance("/testConfig.properties", true);

    /**
     * Método que se ejecuta antes de cualquier test. Modifica el fichero de
     * configuración, por el propio del test.
     */
    @Before
    public void setUp() {
        mockStatic(ExternalProperties.class);
        when(ExternalProperties.getInstance("/config.properties", false)).thenReturn(test);
        sistInfData = SistInfDataCsv.getInstance(); 
    }

    /**
     * Test que comprueba que el método que obtiene la media de una lista de
     * números funciona correctamente.
     * 
     * En este caso, la columna "notas" es una lista de notas desde el 1 hasta
     * el 10 por lo que el resultado que esperamos es 5.5.
     * 
     * @throws SQLException
     */
    @Test
    public void testAvg() throws SQLException {
        Number esperado = sistInfData.getAvgColumn("Nota", "Prueba");
        assertEquals(esperado, is((Number) 5.5F));
    }

    /**
     * Test que comprueba que el método que obtiene el valor máximo de un
     * listado funciona correctamente. En este caso, la columna "numero" tiene
     * como máximo un 9 y la columna "nota" tiene como máximo un 10.
     * 
     * @throws SQLException
     */
    @Test
    public void testMaxColumn() throws SQLException {

        String tableName = "Prueba";
        Number num = sistInfData.getMaxColumn("Numero", tableName);
        Number not = sistInfData.getMaxColumn("Nota", tableName);
        assertEquals(num, is((Number) 9.0F));
        assertEquals(not, is((Number) 10.0F));
        assertNotEquals(num, not);
    }

    /**
     * Test que comprueba que el método que obtiene el valor mínimo de un
     * listado funciona correctamente. En este caso, la columna "numero" tiene
     * como mínimo un 0 y la columna "nota" tiene como mínimo un 1.
     * 
     * @throws SQLException
     */
    @Test
    public void testMinColumn() throws SQLException {
        String tableName = "Prueba";
        Number num = sistInfData.getMinColumn("Numero", tableName);
        Number not = sistInfData.getMinColumn("Nota", tableName);
        assertEquals(num, is((Number) 0.0F));
        assertEquals(not, is((Number) 1.0F));
        assertNotEquals(num, not);
    }

    /**
     * Test que comprueba que el método que obtiene la desviación estándar de un
     * listado funciona correctamente. En este caso, la columna "numero" tiene
     * un listado de números de 0 a 9 y la columna "nota" un listado de 1 a 10,
     * por lo que en ambas esperamos la misma desviación.
     * 
     * @throws SQLException
     */
    @Test
    public void testStdvColumn() throws SQLException { 
        String tableName = "Prueba";
        Number num = sistInfData.getStdvColumn("Numero", tableName);
        Number not = sistInfData.getStdvColumn("Nota", tableName);
        assertEquals(num, is((Number) 3.0276503540974917));
        assertEquals(not, is((Number) 3.0276503540974917));
        assertEquals(num, not);
    }

    /**
     * Test que comprueba que el método no nos devuelve conjunto vacíos de
     * datos.
     */
    @Test
    public void testResultSet() {
    	assertEquals(sistInfData.getResultSet("Prueba", "Numero"), notNullValue());

        assertEquals(sistInfData.getResultSet("Prueba", "Nota",
                "Descripcion='hola'"), notNullValue());

        String[] metricValoreSelect = { "Numero", "Nota", "Descripcion" };
        assertEquals(sistInfData.getResultSet("Prueba", "Numero", null,
                metricValoreSelect), notNullValue());
        assertEquals(sistInfData.getResultSet("Prueba", "Numero",
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
     * @throws SQLException
     */
    @Test(expected = SQLException.class)
    public void testTotalNumber() throws SQLException { 

        Number obtenido = sistInfData.getTotalNumber("Nota", "Prueba", "Nota='5'");
        assertEquals(obtenido, is((Number) 1.0F));

        String[] columnNames = { "Nota", "Descripcion" };
        Number obt = sistInfData.getTotalNumber(columnNames, "Prueba");
        assertEquals(obt, is((Number) 12.0F));

        columnNames = null;
        obt = sistInfData.getTotalNumber(columnNames, "Prueba");
        assertEquals(obt, is((Number) 0));

       /* String[] columnNam = { "Nada" };
        obt = sistInfData.getTotalNumber(columnNam, "Prueba");*/

    }

    @Test
    public void testQuartilColumn() throws SQLException {
    	Double quartil = 0.25;
    	assertEquals(sistInfData.getQuartilColumn("Nota", "Prueba", quartil), notNullValue());
    }

    /**
     * En este test comprobamos que la obtención de la fecha máxima y la fecha
     * mínima funcione correctamente. La fecha mínima que esperamos es
     * 1/01/1649. Y la fecha máxima es 1/01/2015.
     * 
     * @throws SQLException
     */
    @Test
    public void testFechas() throws SQLException {
        LocalDate esperado = sistInfData.getYear("Fecha", "Prueba", true);
        LocalDate valor = LocalDate.of(1649, 1, 1);

        assertEqualDates(esperado, valor);

        LocalDate esperado2 = sistInfData.getYear("Fecha", "Prueba", false);
        LocalDate valor2 = LocalDate.of(2015, 1, 1);

        assertEqualDates(esperado2, valor2);
    }
    
    /**
     * Se comprueba que el formato de las fechas sea dd/mm/yyyy
     * 
     * @throws SQLException
     * @throws ParseException 
     */
    @Test
    public void testFormatDatesHistorico() throws SQLException, ParseException { 
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
     * Test que comprueba que salta la excepción SQLException al intentar
     * obtener datos de una tabla que no existe.
     * 
     * @throws SQLException
     */
    @Test(expected = SQLException.class)
    public void testSinTabla() throws SQLException { 
        sistInfData.getAvgColumn("Nada", "Nada");
    }

    /**
     * Test que comprueba que salta la excepción SQLException al intentar
     * obtener datos de una tabla que está vacía.
     * 
     * @throws SQLException
     */
    @Test(expected = SQLException.class)
    public void testTablaVacia() throws SQLException { 
        sistInfData.getAvgColumn("Nada", "Vacia");
    }

    /**
     * Método main.
     * 
     * @param args
     */
    public static void main(String[] args) {
        JUnitCore.main("ubu.digit.pesistence.SistInfDataTestCSV");
    } 
}