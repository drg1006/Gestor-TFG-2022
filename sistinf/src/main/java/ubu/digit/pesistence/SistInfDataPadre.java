package ubu.digit.pesistence;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vaadin.server.VaadinService;

import ubu.digit.util.ExternalProperties;
import static ubu.digit.util.Constants.*;


/**
 * Clase Padre de las fachadas Singleton de acceso a datos.
 * 
 * @author Diana Bringas Ochoa
 */
public abstract class SistInfDataPadre implements Serializable {

	/**
	 * Select.
	 */
	protected static final String SELECT = "select ";
	
	/**
	 * Select all.
	 */
	protected static final String SELECT_ALL = "select * ";
	
	/**
	 * Select distinct.
	 */
	protected static final String SELECT_DISTINCT = "select distinct ";
	
	/**
	 * Count.
	 */
	protected static final String COUNT = "count";
	
	/**
	 * From.
	 */
	protected static final String FROM = " from ";
	
	/**
	 * Where.
	 */
	protected static final String WHERE = " where ";
	
	/**
	 * Distinto de vacío.
	 */
	protected static final String DISTINTO_DE_VACIO = " != ''";
	
	/**
	 * And.
	 */
	protected static final String AND = " and ";
	
	/**
	 * Like.
	 */
	protected static final String LIKE = " like ";
	
	/**
	 * Order by.
	 */
	protected static final String ORDER_BY = " order by ";

	/**
	 * Serial Version UID.
	 */
	protected static final long serialVersionUID = -6019587024081762319L;

	/**
	 * Logger de la clase.
	 */
	protected static final Logger LOGGER = Logger.getLogger(SistInfDataPadre.class);
	
	/**
	 * Conexión que se produce entre la base de datos(csv) y la aplicación.
	 */
	protected transient Connection connection;

	/**
	 * Constructor vacío.
	 */
	protected SistInfDataPadre() {
		super();
		this.connection = this.getConection();
	}
	
	//TODO: SistInfDataPadre fachada = new SistInfDataCsv();

	public abstract Connection getConection();
	
	protected abstract Number getResultSetNumber(String sql);

	/**
	 * Ejecuta una sentencia SQL obteniendo la media aritmética de la columna de
	 * la tabla ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return media aritmética
	 * @throws Exception
	 */
	public Number getAvgColumn(String columnName, String tableName) throws Exception {
		List<Float> media = obtenerDatos(columnName, tableName);
		Float resultadoMedia = new Float(0);
		for (Float numero : media) {
			resultadoMedia += numero;
		}
		return resultadoMedia / media.size();
	}//TODO:

	/**
	 * Ejecuta una sentencia SQL obteniendo el valor máximo de la columna de una
	 * tabla, ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return valor máximo de la columna
	 * @throws Exception
	 */
	public Number getMaxColumn(String columnName, String tableName) throws Exception {
		return Collections.max(obtenerDatos(columnName, tableName));
	}//TODO:

	/**
	 * Ejecuta una sentencia SQL obteniendo el valor mínimo de la columna de una
	 * tabla, ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return valor mínimo de la columna
	 * @throws Exception
	 */
	public Number getMinColumn(String columnName, String tableName) throws Exception {
		return Collections.min(obtenerDatos(columnName, tableName));
	}//TODO:

	/**
	 * Ejecuta una sentencia SQL obteniendo la desviación estandart de la
	 * columna de una tabla, ambas pasadas como parámetro.
	 * 
	 * @param columnName
	 *            nombre de la columna
	 * @param tableName
	 *            nombre de la tabla de datos
	 * @return desviación estandar
	 * @throws Exception
	 */
	public Number getStdvColumn(String columnName, String tableName) throws Exception {
		return calculateStdev(obtenerDatos(columnName, tableName));
	}//TODO:


	public abstract List<Float> obtenerDatos(String columnName, String tableName) throws Exception;

	/**
	 * Calcula la desviación standard.
	 * 
	 * @param list
	 *            Listado de los números de los que calcular la desviación.
	 * @return Desviación standard.
	 */
	protected Double calculateStdev(List<Float> list) {
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
	}//TODO:

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
	 * @throws Exception
	 * @throws Exception
	 */
	public Number getQuartilColumn(String columnName, String tableName, double percent) throws Exception {
		Number nTotalValue = getTotalNumber(columnName, tableName);
		String sql = SELECT + columnName + FROM + tableName + WHERE + columnName + DISTINTO_DE_VACIO + ORDER_BY
				+ columnName;
		List<Double> listValues = getListNumber(columnName, sql);
		int indexMedian = (int) (nTotalValue.intValue() * percent);
		return listValues.get(indexMedian);
	}//TODO:

	protected abstract List<Double> getListNumber(String columnName, String sql) throws Exception;
	
	protected abstract void addNumbersToList(String columnName, List<Double> listValues, ResultSet result) throws Exception;

	/**
	 * Ejecuta una sentencia SQL obteniendo el número total de filas diferentes,
	 * distintas de null y cumplen la claúsula where de la columna de una tabla.
	 * 
	 * @param columnName
	 *            nombre de la columna.
	 * @param tableName
	 *            nombre de la tabla de datos.
	 * @return número total de filas distintas
	 * @throws Exception
	 */
	public Number getTotalNumber(String columnName, String tableName) throws Exception {
		String sql = SELECT_DISTINCT + COUNT + "(" + columnName + ")" + FROM + tableName + WHERE + columnName
				+ DISTINTO_DE_VACIO;
		return getResultSetNumber(sql);
	}//TODO:

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
	 * @throws Exception
	 */
	public Number getTotalNumber(String columnName, String tableName, String whereCondition) throws Exception {
		String sql = SELECT_DISTINCT + COUNT + "(" + columnName + ")" + FROM + tableName + WHERE + columnName
				+ DISTINTO_DE_VACIO + AND + whereCondition + " ;";
		return getResultSetNumber(sql);
	}//TODO:

	public abstract Number getTotalNumber(String[] columnsName, String tableName) throws Exception;

	protected abstract void addUniqueStrings(Set<String> noDups, ResultSet resultSet, int numColumns) throws Exception;

	/**
	 * Ejecuta una sentencia SQL obteniendo el número total de proyectos sin
	 * asignar. Se busca una cadena que contenga la subcadena "Aal".
	 * 
	 * @return número total de proyectos sin asignar
	 * @throws Exception
	 */
	public Number getTotalFreeProject() throws Exception {
		String sql = SELECT + COUNT + "(*)" + FROM + "Proyecto" + WHERE + ALUMNO1 + LIKE + "'%Aal%'";
		return getResultSetNumber(sql);
	}//TODO:

	public abstract ResultSet getResultSet(String tableName, String columnName) throws Exception;

	public abstract ResultSet getResultSet(String tableName, String columnName, String whereCondition) throws Exception;

	public abstract ResultSet getResultSet(String tableName, String columnName, String[] filters, String[] columnsName) throws Exception;

	public abstract LocalDate getYear(String columnName, String tableName, Boolean minimo) throws Exception;

	public abstract List<List<Object>> getProjectsCurso(String columnName, String columnName2, String columnName3,
			String columnName4, String tableName, Number curso) throws Exception;

	/**
	 * Transforma el string que le llega en un tipo Date.
	 * 
	 * @param date
	 *            Fecha en tipo String
	 * @return Fecha con formato Date
	 */
	protected LocalDate transform(String date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return LocalDate.parse(date, dateTimeFormatter);
	}//TODO:

	protected abstract void finalize() throws Throwable;
}
