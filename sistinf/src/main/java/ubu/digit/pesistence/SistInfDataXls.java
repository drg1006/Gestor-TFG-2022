package ubu.digit.pesistence;

import java.io.Serializable;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Field;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.vaadin.server.VaadinService;

import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;


/**
 * Fachada Singleton de acceso a datos a través de fillo
 * 
 *@author Carlos López Nozal
 * @author Beatriz Zurera Martínez-Acitores
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 */
public class SistInfDataXls implements Serializable {

	/**
	 * Select.
	 */
	private static final String SELECT = "select ";
	
	/**
	 * Select all.
	 */
	private static final String SELECT_ALL = "select * ";
	
	/**
	 * Select distinct.
	 */
	private static final String SELECT_DISTINCT = "select distinct ";
	
	/**
	 * Count.
	 */
	private static final String COUNT = "count";
	
	/**
	 * From.
	 */
	private static final String FROM = " from ";
	
	/**
	 * Where.
	 */
	private static final String WHERE = " where ";
	
	/**
	 * Distinto de vacío.
	 */
	private static final String DISTINTO_DE_VACIO = " != ''";
	
	/**
	 * And.
	 */
	private static final String AND = " and ";
	
	/**
	 * Like.
	 */
	private static final String LIKE = " like ";
	
	/**
	 * Order by.
	 */
	private static final String ORDER_BY = " order by ";

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -6019587024081762319L;

	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = Logger.getLogger(SistInfDataXls.class);

	/**
	 * Conexión que se produce entre la base de datos(xls) y la aplicación.
	 */
	private transient com.codoid.products.fillo.Connection connection;

	/**
	 * Dirección de los ficheros en la aplicación del servidor.
	 */
	private String serverPath = "";

	/**
	 * Instancia con los datos.
	 */
	private static SistInfDataXls instance;

	/**
	 * URL donde encontramos el fichero con las propiedades del proyecto.
	 */
	private static ExternalProperties prop = ExternalProperties.getInstance("/WEB-INF/classes/config.properties",
			false);

	/**
	 * Directorio donde se encuentra los datos de entrada, es decir, los
	 * ficheros que contienen los datos que vamos a consultar.
	 */
	public static final String DIRCSV = prop.getSetting("dataIn");

	/**
	 * Constructor vacío.
	 */
	private SistInfDataXls() {
		super();
		this.connection = this.getConection(); 
	}

	/**
	 * Método singleton para obtener la instancia de la clase fachada.
	 */
	public static SistInfDataXls getInstance() {
		if (instance == null) {
			instance = new SistInfDataXls();
		}
		return instance;
	}
	
	/**
	 * Inicializa la conexión odbc al almacen de datos en formato .xsl
	 * 
	 * @return con
	 * 				conexión con el fichero .xsl 
	 */
	private com.codoid.products.fillo.Connection getConection() { //TODO:
		com.codoid.products.fillo.Connection conn = null;
        try {
      	   Fillo fillo=new Fillo();
      	   /*if (DIRCSV.startsWith("/")) {
				serverPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
			}
      	   new BOMRemoveUTF().bomRemoveUTFDirectory(serverPath + DIRCSV);*/
      	   
      	   //conn = fillo.getConnection(serverPath + DIRCSV + "BaseDeDatosTFGTFM.xls");
      	   conn = fillo.getConnection("C:\\Users\\DianaBO\\Documents\\UNIVERSIDAD\\TFG\\Proyecto\\Gestor-TFG-2016\\sistinf\\src\\main\\resources\\data\\BaseDeDatosTFGTFM.xls");

      	}catch (FilloException e) {
      		LOGGER.error(e);
      	} 	  
		return conn;
	}

	/**
	 * Ejecuta una sentencia SQL sumando todos los datos Float contenidos en la
	 * primera columna.
	 * 
	 * @param SQL
	 *            sentencia sql a ejecutar
	 * @return suma todas la filas de la primera columna
	 */
	private Number getResultSetNumber(String sql) { //TODO:revisar tipos porque antes era number no float
		Float number = 0.0f;;
		Float castNumber = null;

		try {
			Recordset rs = connection.executeQuery(sql);
			while(rs.next()) {
				String primeraCol = rs.getField(1).toString();
				//castNumber = NumberFormat.getInstance().parse(rs.getField(primeraCol)); //Cast de String a Number
				castNumber = Float.parseFloat(rs.getField(primeraCol));
				number += castNumber; //Se van sumando lo que vamos obteniendo
			}
			
		}catch (FilloException e) {
			LOGGER.error(e);
		}
		return number;
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo la media aritmética de la columna de
	 * la tabla ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return media aritmética
	 * @throws FilloException
	 */
	public Number getAvgColumn(String columnName, String tableName) throws FilloException {
		List<Float> media = obtenerDatos(columnName, tableName);
		Float resultadoMedia = new Float(0);
		for (Float numero : media) {
			resultadoMedia += numero;
		}
		return resultadoMedia / media.size();
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo el valor máximo de la columna de una
	 * tabla, ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return valor máximo de la columna
	 * @throws FilloException
	 */
	public Number getMaxColumn(String columnName, String tableName) throws FilloException {
		return Collections.max(obtenerDatos(columnName, tableName));
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo el valor mínimo de la columna de una
	 * tabla, ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return valor mínimo de la columna
	 * @throws FilloException
	 */
	public Number getMinColumn(String columnName, String tableName) throws FilloException {
		return Collections.min(obtenerDatos(columnName, tableName));
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo la desviación estandart de la
	 * columna de una tabla, ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return desviación estandar
	 * @throws FilloException
	 */
	public Number getStdvColumn(String columnName, String tableName) throws FilloException {
		return calculateStdev(obtenerDatos(columnName, tableName));
	}

	/**
	 * Obtiene los datos de una columna determinada de una tabla determinada.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return Listado con los datos de dicha columna.
	 */
	private List<Float> obtenerDatos(String columnName, String tableName) throws FilloException {
		String sql = SELECT + columnName + FROM + tableName + ";";
		List<Float> media = new ArrayList<>();
		String fieldName;
		try {
			Recordset rs = connection.executeQuery(sql);
			ArrayList<String> fieldNames = rs.getFieldNames();
			while(rs.next()) {
	        	for(int i=0;i<fieldNames.size();i++){                
	               fieldName = fieldNames.get(i);
	               media.add(Float.parseFloat(rs.getField(fieldName)));
	        	}  
			}
		}catch (FilloException e) {
			LOGGER.error(e);
		}
		return media;
	}
	
	/**
	 * Calcula la desviación standard.
	 * 
	 * @param list
	 *            Listado de los números de los que calcular la desviación.
	 * @return Desviación standard.
	 */
	private Double calculateStdev(List<Float> list) {
		double sum = 0;

		for (int i = 0; i < list.size(); i++) {
			sum = sum + list.get(i);
		}
		double mean = sum / list.size();
		double[] deviations = new double[list.size()];

		for (int i = 0; i < deviations.length; i++) {
			deviations[i] = list.get(i) - mean;
		}
		double[] squares = new double[list.size()];

		for (int i = 0; i < squares.length; i++) {
			squares[i] = deviations[i] * deviations[i];
		}
		sum = 0;

		for (int i = 0; i < squares.length; i++) {
			sum = sum + squares[i];
		}
		double result = sum / (list.size() - 1);
		return Math.sqrt(result);
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo la mediana de la columna de una
	 * tabla, ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @param percent
	 * @return mediana
	 * @throws FilloException
	 * @throws FilloException
	 */
	public Number getQuartilColumn(String columnName, String tableName, double percent) throws FilloException {
		Number nTotalValue = getTotalNumber(columnName, tableName);
		String sql = SELECT + columnName + FROM + tableName + WHERE + columnName + DISTINTO_DE_VACIO + ORDER_BY
				+ columnName;
		List<Double> listValues = getListNumber(columnName, sql);
		int indexMedian = (int) (nTotalValue.intValue() * percent);
		return listValues.get(indexMedian);
	}

	/**
	 * Obtiene la lista de valores de una columna de una consulta SQL.
	 * 
	 * @param columnName
	 *            Nombre de la columna.
	 * @param SQL
	 *            Sentencia a ejecutar.
	 * @return listado con los números.
	 * @throws FilloException
	 */
	private List<Double> getListNumber(String columnName, String sql) throws FilloException {
		List<Double> listValues = new ArrayList<>(100);
		try {
			Recordset rs = connection.executeQuery(sql);
			while(rs.next()) {
				addNumbersToList(columnName, listValues, rs);
			}
		}catch (FilloException e) {
			LOGGER.error(e);
		}
		return listValues;
	}

	/**
	 * Añade los valores de una columna a una lista.
	 * 
	 * @param columnName
	 *            Nombre de la columna.
	 * @param listValues
	 *            Lista que guarda los valores.
	 * @param result
	 *            ResultSet a partir del cual obtener los valores.
	 * @throws FilloException
	 */
	private void addNumbersToList(String columnName, List<Double> listValues, Recordset result) throws FilloException {
		while (result.next()) {
			listValues.add(Double.parseDouble(result.getField(columnName)));
		}
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo el número total de filas diferentes,
	 * distintas de null y cumplen la claúsula where de la columna de una tabla.
	 * 
	 * @param columnName
	 *            nombre de la columna.
	 * @param tableName
	 *            nombre de la tabla de datos.
	 * @return número total de filas distintas
	 * @throws FilloException
	 */
	public Number getTotalNumber(String columnName, String tableName) throws FilloException {
		String sql = SELECT_DISTINCT + COUNT + "(" + columnName + ")" + FROM + tableName + WHERE + columnName
				+ DISTINTO_DE_VACIO;
		return getResultSetNumber(sql);
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo el número total de filas diferentes,
	 * distintas de null y que cumplan el filtro de la columna de una tabla.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @param whereCondition
	 *            filtro con la condición
	 * @return número total de filas distintas
	 * @throws FilloException
	 */
	public Number getTotalNumber(String columnName, String tableName, String whereCondition) throws FilloException {
		String sql = SELECT_DISTINCT + COUNT + "(" + columnName + ")" + FROM + tableName + WHERE + columnName
				+ DISTINTO_DE_VACIO + AND + whereCondition + " ;";
		return getResultSetNumber(sql);
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo el número total de filas diferentes
	 * y distintas de null de las columnas de una tabla pasadas como parámetro.
	 * 
	 * @param columnsName
	 *            nombre de las columnas
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return número total de filas distintas
	 * @throws FilloException
	 */
	public Number getTotalNumber(String[] columnsName, String tableName) throws FilloException {
		String sql;
		Set<String> noDups = new HashSet<>();
		if (columnsName != null) {
			for (int i = 0; i < columnsName.length; i++) {
				sql = SELECT + columnsName[i] + FROM + tableName + WHERE + columnsName[i] + DISTINTO_DE_VACIO;
				try {
					Recordset rs = connection.executeQuery(sql);
					int numColumns = rs.getFieldNames().size();
					addUniqueStrings(noDups, rs, numColumns);
				}catch (FilloException e) {
					LOGGER.error(e);
				}
			}
			return (float) noDups.size();
		} else {
			return 0;
		}
	}
	
	/**
	 * Añade a una colección sin duplicados los valores de las columnas.
	 * 
	 * @param resultSet
	 *            resultado de ejecutar la sentencia sql correspondiente
	 * @param noDups
	 *            colección sin duplicados donde almacenar los valores
	 * @param numColumns
	 *            número de columnas que tiene el resultset
	 * @throws FilloException
	 */
	private void addUniqueStrings(Set<String> noDups, Recordset resultSet, int numColumns) throws FilloException {
		String nameCol;
		while (resultSet.next()) {
			for (int j = 1; j <= numColumns; ++j) {
				nameCol = resultSet.getField(1).toString(); //Obtenemos el nombre de la columna
				noDups.add(resultSet.getField(nameCol)); //Añadimos los datos obtenidos en esa columna
			}
		}
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo el número total de proyectos sin
	 * asignar. Se busca una cadena que contenga la subcadena "Aal".
	 * 
	 * @return número total de proyectos sin asignar
	 * @throws FilloException
	 */
	public Number getTotalFreeProject() throws FilloException {
		String sql = SELECT + COUNT + "(*)" + FROM + "Proyecto" + WHERE + ALUMNO1 + LIKE + "'%Aal%'";
		return getResultSetNumber(sql);
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo un conjunto de filas distintas de
	 * nulo de una columna de una tabla, ambas pasadas como parámetros.
	 * 
	 * @param columnName
	 *            nombre de la columna a discriminar el contador.
	 * @param tableName
	 *            nombre de la tabla de datos.
	 * @return conjunto de filas distintas de null.
	 * @throws FilloException
	 */
	public Recordset getResultSet(String tableName, String columnName) throws FilloException {
		String sql = SELECT_ALL + FROM + tableName + WHERE + columnName + DISTINTO_DE_VACIO;
		Recordset rs = connection.executeQuery(sql);
		return rs;
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo un conjunto de filas distintas de
	 * nulo y cumplen la condición pasada como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna a discriminar con nulos.
	 * @param tableName
	 *            nombre de la tabla de datos.
	 * @param whereCondition
	 *            condición de la claúsula where.
	 * @return conjunto de filas distintas de null y condición de la claúsula
	 *         where.
	 * @throws FilloException
	 */
	public Recordset getResultSet(String tableName, String columnName, String whereCondition) throws FilloException {
		String sql = SELECT_ALL + FROM + tableName + WHERE + whereCondition + ";";
		Recordset rs = connection.executeQuery(sql);
		return rs;
	}//TODO: Revisar return return rs.getResultSet();

	/**
	 * Ejecuta una sentencia SQL obteniendo un conjunto de filas distintas de
	 * nulo y que contienen las cadenas pasadas como filtros de una columna de
	 * una tabla, ambas pasadas como parámetro.
	 * 
	 * @param tableName
	 *            nombre de la tabla de datos.
	 * @param columnName
	 *            nombre de la columna a discriminar el contador.
	 * @param filters
	 *            valores de las columnas que continen las cadenas filters.
	 * @param columnsName
	 *            nombres de las columnas a seleccionar
	 * @return conjunto de filas distintas de null.
	 * @throws FilloException
	 */
	public Recordset getResultSet(String tableName, String columnName, String[] filters, String[] columnsName) throws FilloException {
		StringBuilder sql = new StringBuilder();
		if (columnsName == null) {
			sql.append(SELECT_ALL);
		} else {
			sql.append(SELECT);
			int index = 0;
			for (String selectedColumn : columnsName) {
				if (index == 0) {
					sql.append(" " + selectedColumn);
					index++;
				} else {
					sql.append(", " + selectedColumn);
				}
			}
		}
		sql.append(" \n" + FROM + tableName + " \n" + WHERE + " (" + columnName + DISTINTO_DE_VACIO + ")");
		if (filters != null) {
			sql.append(AND + "(");
			int index = 0;
			for (String filter : filters) {
				if (index == 0) {
					sql.append(" \n" + columnName + " = '" + filter + "'");
					index++;
				} else {
					sql.append(" \nOR " + columnName + " = '" + filter + "'");
				}
			}
			sql.append(");");
		}
		Recordset rs = connection.executeQuery(sql.toString());
		return rs;
	}//TODO: revisar return statement.getResultSet();

	/**
	 * Método que obtiene el curso más bajo o más alto, según el booleano, que
	 * tiene la base de datos.
	 * 
	 * @param columnName
	 *            Nombre de la columna.
	 * @param tableName
	 *            Nombre de la tabla.
	 * @param minimo
	 *            True si queremos el curso mínimo, false si queremos el curso
	 *            máximo.
	 * @return Curso más bajo que ha encontrado.
	 * @throws FilloException
	 */
	public LocalDate getYear(String columnName, String tableName, Boolean minimo) throws FilloException {
		String sql = SELECT + columnName + FROM + tableName + ";";
		List<LocalDate> listadoFechas = new ArrayList<>();
		
		Recordset rs = connection.executeQuery(sql);
		while(rs.next()) {
			listadoFechas.add(transform(rs.getField(columnName))); //transform --> pasa de string a date
		}
		
		if (minimo) {
			return Collections.min(listadoFechas);
		} else {
			return Collections.max(listadoFechas);
		}
	}

	/**
	 * Método que obtiene la fecha de asignación, la fecha de presentación, el
	 * total de días y la nota respectiva del proyecto.
	 * 
	 * @param columnName
	 *            Nombre de la columna de la fecha de asignación.
	 * @param columnName2
	 *            Nombre de la columna de la fecha de presentación.
	 * @param columnName3
	 *            Nombre de la columna del total de días.
	 * @param columnName4
	 *            Nombre de la columna de la nota del proyecto.
	 * @param tableName
	 *            Nombre de la tabla.
	 * @param curso
	 *            Curso del que queremos los datos.
	 * @return Un lista con todos los datos que hemos solicitado.
	 * @throws FilloException
	 */
	public List<List<Object>> getProjectsCurso(String columnName, String columnName2, String columnName3,
			String columnName4, String tableName, Number curso) throws FilloException {
		List<Object> lista;
		List<List<Object>> resultados = new ArrayList<>();
		String sql = SELECT + columnName + "," + columnName2 + "," + columnName3 + "," + columnName4 + ", " 
				+ ALUMNO1 + ", " + ALUMNO2 + ", " + ALUMNO3 + ", " 
				+ TUTOR1 + ", " + TUTOR2 + ", " + TUTOR3 + FROM + tableName
				+ WHERE + columnName + LIKE + "'%" + curso + "';";
		
		Recordset rs = connection.executeQuery(sql);
		while(rs.next()) {
			lista = new ArrayList<>();
			// Fecha asignación
			lista.add(transform(rs.getField(columnName)));
			// Fecha presentación
			lista.add(transform(rs.getField(columnName2)));
			// Dias
			lista.add(Integer.parseInt(rs.getField(columnName3))); //cast de string a int
			// Nota
			lista.add(Double.parseDouble(rs.getField(columnName))); //cast de string a double
			lista.add(rs.getField(ALUMNO1));
			lista.add(rs.getField(ALUMNO2));
			lista.add(rs.getField(ALUMNO3));
			lista.add(rs.getField(TUTOR1));
			lista.add(rs.getField(TUTOR2));
			lista.add(rs.getField(TUTOR3));
			resultados.add(lista);
		}
		return resultados;
	}

	/**
	 * Transforma el string que le llega en un tipo Date.
	 * 
	 * @param date
	 *            Fecha en tipo String
	 * @return Fecha con formato Date
	 */
	private LocalDate transform(String date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return LocalDate.parse(date, dateTimeFormatter);
	}

	/**
	 * Destructor elimina la conexión al sistema de acceso a datos.
	 * 
	 **/
	@Override
	protected void finalize() throws Throwable {
		try {
			connection.close();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		super.finalize();
		
	}
}
