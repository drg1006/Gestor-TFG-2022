package ubu.digit.pesistence;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import ubu.digit.util.ExternalProperties;


/**
 * Clase abstracta de la cual heredan las fachadas Singleton de acceso a datos.
 * 
 * @author Diana Bringas Ochoa
 */
public abstract class SistInfDataAbstract implements Serializable { 

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -6019587024081762319L;
	
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
	 * Dirección de los ficheros en la aplicación del servidor.
	 */
	protected String serverPath = "";

	/**
	 * URL donde encontramos el fichero con las propiedades del proyecto.
	 */
	protected static ExternalProperties prop = ExternalProperties.getInstance("/WEB-INF/classes/config.properties",
			false);
	
	/**
	 * Directorio donde se encuentra los datos de entrada, es decir, los
	 * ficheros que contienen los datos que vamos a consultar.
	 */
	public static final String DIRCSV = prop.getSetting("dataIn"); //TODO: crear get para clase updateCSVVIew

	/**
	 * Logger de la clase.
	 */
	protected static final Logger LOGGER = Logger.getLogger(SistInfDataAbstract.class);
	
	protected abstract Number getResultSetNumber(String sql);
	
	public abstract Number getAvgColumn(String columnName, String tableName) throws Exception;
	
	public abstract Number getMaxColumn(String columnName, String tableName) throws Exception;
	
	public abstract Number getMinColumn(String columnName, String tableName) throws Exception;

	public abstract Number getStdvColumn(String columnName, String tableName) throws Exception;

	protected abstract List<Float> obtenerDatos(String columnName, String tableName) throws Exception;

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
	}

	protected abstract Number getQuartilColumn(String columnName, String tableName, double percent) throws Exception;
	
	public abstract List<String> getNotePercentile() throws Exception;
	
	protected abstract List<Double> getListNumber(String columnName, String sql) throws Exception;
	
	//protected abstract void addNumbersToList(String columnName, List<Double> listValues, ResultSet result) throws Exception;
	//TODO: Intentar poner tipo generico en lugar de ResultSet
	
	public abstract Number getTotalNumber(String columnName, String tableName) throws Exception;

	protected abstract Number getTotalNumber(String columnName, String tableName, String whereCondition) throws Exception;

	public abstract Number getTotalNumber(String[] columnsName, String tableName) throws Exception;

	public abstract Number getTotalFreeProject() throws Exception;
	
	//TODO: modificado tipo que devuelven de resultset a object
	protected abstract Object getResultSet(String tableName, String columnName) throws Exception;

	protected abstract Object getResultSet(String tableName, String columnName, String whereCondition) throws Exception;

	protected abstract Object getResultSet(String tableName, String columnName, String[] filters, String[] columnsName) throws Exception;

	public abstract LocalDate getYear(String columnName, String tableName, Boolean minimo) throws Exception;

	protected abstract List<List<Object>> getProjectsCurso(String columnName, String columnName2, String columnName3,
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
	}

	protected abstract void finalize() throws Throwable;
	
	public abstract List<String> getDataModel();
	
	public abstract List<String> getTribunal();
	
	public abstract List<String> getNormas();
	
	public abstract List<String> getDocumentos();
	
	@SuppressWarnings("rawtypes")
	public abstract ArrayList getDataModelHistoric(DateTimeFormatter dateTimeFormatter);
	
}
