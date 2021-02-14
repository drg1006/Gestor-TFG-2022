package ubu.digit.pesistence;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.*;

import com.vaadin.server.VaadinService;

import static ubu.digit.util.Constants.*;


/**
 * Fachada Singleton de acceso a datos a través de fillo
 * 
 * @author Diana Bringas Ochoa
 */
public class SistInfDataXls extends SistInfDataAbstract implements Serializable {

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
	 * Instancia con los datos.
	 */
	private static SistInfDataXls instance;

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
	private com.codoid.products.fillo.Connection getConection() {
		com.codoid.products.fillo.Connection conn = null;
        try {
      	   Fillo fillo=new Fillo();
			
      	   if (DIRCSV.startsWith("/")) {
				serverPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
			}
      	   new BOMRemoveUTF().bomRemoveUTFDirectory(serverPath + DIRCSV);
      	   
      	   conn = fillo.getConnection(serverPath + DIRCSV + "/BaseDeDatosTFGTFM.xls");

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
	@Override
	protected Number getResultSetNumber(String sql) { 
		Float number = 0.0f;;

		try {
			Recordset rs = connection.executeQuery(sql);
			number = (float) rs.getCount();//Se cuenta el número de filas que devuelve
			
		}catch (FilloException e) {
			LOGGER.error(e);
		}
		return (Number) number;
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	protected List<Float> obtenerDatos(String columnName, String tableName) throws FilloException {
		String sql = SELECT + columnName + FROM + tableName + WHERE + columnName + DISTINTO_DE_VACIO;
		List<Float> media = new ArrayList<>();
		String data;
		try {
			Recordset rs = connection.executeQuery(sql);
			while(rs.next()) {
				data = rs.getField(columnName);
				media.add(Float.parseFloat(data));
			}
		}catch (FilloException e) {
			LOGGER.error(e);
		}
		return media;
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
	@Override
	protected Number getQuartilColumn(String columnName, String tableName, double percent) throws FilloException {
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
	@Override
	protected List<Double> getListNumber(String columnName, String sql) throws FilloException {
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
	protected void addNumbersToList(String columnName, List<Double> listValues, Recordset result) throws FilloException {
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
	@Override
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
	@Override
	protected Number getTotalNumber(String columnName, String tableName, String whereCondition) throws FilloException {
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
	@Override
	public Number getTotalNumber(String[] columnsName, String tableName) throws FilloException {
		String sql;
		Set<String> noDups = new HashSet<>();
		if (columnsName != null) {
			for (int i = 0; i < columnsName.length; i++) {
				sql = SELECT + columnsName[i] + FROM + tableName + WHERE + columnsName[i] + DISTINTO_DE_VACIO;
				try {
					Recordset rs = connection.executeQuery(sql);
					//int numColumns = rs.getFieldNames().size();
					addUniqueStrings(noDups, rs, columnsName[i]);
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
	protected void addUniqueStrings(Set<String> noDups, Recordset resultSet, String columnsName) throws FilloException {
		while (resultSet.next()) {
			String data = resultSet.getField(columnsName);//Obtenemos los datos de la columna
			noDups.add(data);
		}
	}

	/**
	 * Ejecuta una sentencia SQL obteniendo el número total de proyectos sin
	 * asignar. Se busca una cadena que contenga la subcadena "Aal".
	 * 
	 * @return número total de proyectos sin asignar
	 * @throws FilloException
	 */
	public Number getTotalFreeProject() throws FilloException { //TODO:metodo contains
		String sql = SELECT + COUNT + "(*)" + FROM + PROYECTO + WHERE + ALUMNO1 + " = 'Aalumnos sin asignar'";//TODO:"'%" 
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
	protected Recordset getResultSet(String tableName, String columnName) throws FilloException {
		String sql = SELECT_ALL + FROM + tableName + WHERE + columnName + DISTINTO_DE_VACIO;
		return connection.executeQuery(sql);
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
	protected Recordset getResultSet(String tableName, String columnName, String whereCondition) throws FilloException {
		String sql = SELECT_ALL + FROM + tableName + WHERE + whereCondition;
		return connection.executeQuery(sql);
	}

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
	protected Recordset getResultSet(String tableName, String columnName, String[] filters, String[] columnsName) throws FilloException{
		StringBuilder sql = new StringBuilder(); 
		Recordset rs = null;
		Recordset rs_aux = null;
		String whereCondition = "";
		//ArrayList<String> lista = new ArrayList<String>();
		
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
		
		try {
			//Se ejecuta la primera parte de la query
			rs = connection.executeQuery(sql.toString());//TODO:Revisar LinkedListMap<K,V>
			
			//Segunda parte de la query 
			if (filters != null) {
				sql.append(AND + "(");
				for (String filter : filters) {
					whereCondition = " \n" + columnName + " = '" + filter + "')";
					rs = getSubQuery(sql,whereCondition);
					
					/*if(rs_aux!= null) {
						rs = rs_aux;
					}*/
				}
					
			}
		}catch(FilloException ex) {
			//LOGGER.error(ex);
			System.out.println("\nFilloException: "+ex.getMessage());
		}
		
		return rs;
	}
	
	/**
	 * Método que ejecuta las diferentes partes de una query.
	 * 
	 * @param sql
	 * @param whereCondition
	 * @return RecordSet resultado de la query pasada
	 * @throws FilloException 
	 */
	private Recordset getSubQuery(StringBuilder sql, String whereCondition){
		Recordset rs = null;
		try {
			rs = connection.executeQuery(sql.toString()+whereCondition);
		}catch(FilloException ex) {
			//LOGGER.error(ex);
			System.out.println("\nFilloException: "+ex.getMessage());
		}
		return rs;
	}

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
	@Override
	public LocalDate getYear(String columnName, String tableName, Boolean minimo) throws FilloException {
		String sql = SELECT + columnName + FROM + tableName + WHERE + columnName + DISTINTO_DE_VACIO;
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
	@Override
	protected List<List<Object>> getProjectsCurso(String columnName, String columnName2, String columnName3,
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
	 * Destructor elimina la conexión al sistema de acceso a datos.
	 * 
	 **/
	@Override
	protected void finalize() throws Throwable { //TODO:REVISAR
		try {
			connection.close();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		//super.finalize();	
	}
	
	//Funciones trasladadas de las vistas
	
	/**
	 * Obtener los datos del modelo de datos de los proyectos activos.
	 */
	public List<String> getDataModel() { //TODO:
		List<String> listaDataModel = new ArrayList<String>();
		try{
			Recordset result = getResultSet(PROYECTO, TITULO);
			while (result.next()) {
				String title = result.getField(TITULO);
				String description = result.getField(DESCRIPCION);
				String tutor1 = result.getField(TUTOR1);
				String tutor2 = result.getField(TUTOR2);
				if (tutor2 == null) {
					tutor2 = "";
				}
				String tutor3 = result.getField(TUTOR3);
				if (tutor3 == null) {
					tutor3 = "";
				}
				String student1 = result.getField(ALUMNO1);
				String student2 = result.getField(ALUMNO2);
				if (student2 == null) {
					student2 = "";
				}
				String student3 = result.getField(ALUMNO3);
				if (student3 == null) {
					student3 = "";
				}
				String courseAssignment = result.getField(CURSO_ASIGNACION);

				listaDataModel.add(title);
				listaDataModel.add(description);
				listaDataModel.add(tutor1);
				listaDataModel.add(tutor2);
				listaDataModel.add(tutor3);
				listaDataModel.add(student1);
				listaDataModel.add(student2);
				listaDataModel.add(student3);
				listaDataModel.add(courseAssignment);
			}
		} catch (FilloException e) {
			LOGGER.error("Error al obtener los datos del actuales", e);
		}
		return listaDataModel;
	}
	
	/**
	 * Obtener nombre y apellidos del tribunal
	 */
	public List<String> getTribunal(){
		List<String> listaTribunal = new ArrayList<String>();
		try {
			Recordset result = getResultSet(TRIBUNAL, NOMBRE_APELLIDOS);
			while (result.next()) {
				String cargo = result.getField(CARGO);
				String nombre = result.getField(NOMBRE_APELLIDOS);
				String filaTribunal = cargo + ": " + nombre;
				listaTribunal.add(filaTribunal);
			}
		} catch (Exception e) {
			LOGGER.error("Error al obtener los datos del tribunal", e);
		}
		return listaTribunal;
	}
	
	/**
	 * Obtener la descripción de las normas
	 */
	public List<String> getNormas(){
		List<String> listaNormas = new ArrayList<String>();
		try {
			Recordset result = getResultSet(NORMA, DESCRIPCION);
			while (result.next()) {
				String descripcion = result.getField(DESCRIPCION);
				listaNormas.add(descripcion);
			}
		} catch (Exception e) {
			LOGGER.error("Error al obtener los datos del normas", e);
		}
		return listaNormas;
	}
	
	/**
	 * Obtener la descripción y la url de los documentos
	 */
	public List<String> getDocumentos(){
		List<String> listaDocumentos = new ArrayList<String>();
		try{
			Recordset result = getResultSet(DOCUMENTO, DESCRIPCION);
			while (result.next()) {
				String descripcion = result.getField(DESCRIPCION);
				String url = result.getField("Url");
				listaDocumentos.add(descripcion);
				listaDocumentos.add(url);
			}
		} catch (Exception e) {
			LOGGER.error("Error al obtener los datos del documentos", e);
		}
		return listaDocumentos;
	}
	
	/**
	 * Obtener los datos del modelo de datos de los históricos
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList getDataModelHistoric(DateTimeFormatter dateTimeFormatter) { //TODO: revisar tipos arraylist
		//List<T> listaDataModel = new ArrayList<>();
		ArrayList listaDataModel=new ArrayList();
		try{ 
			Recordset result = getResultSet(HISTORICO, TITULO);
			while (result.next()) {
				int numStudents = 0;
				int numTutors = 0;
				String title = result.getField(TITULO_CORTO);
				String description = result.getField(DESCRIPCION);
				String tutor1 = result.getField(TUTOR1);
				if (tutor1 == null || "".equals(tutor1)) {
					tutor1 = "";
				} else {
					numTutors++;
				}
				String tutor2 = result.getField(TUTOR2);
				if (tutor2 == null || "".equals(tutor2)) {
					tutor2 = "";
				} else {
					numTutors++;
				}
				String tutor3 = result.getField(TUTOR3);
				if (tutor3 == null || "".equals(tutor3)) {
					tutor3 = "";
				} else {
					numTutors++;
				}
				String student1 = result.getField(ALUMNO1);
				if (student1 == null || "".equals(student1)) {
					student1 = "";
				} else {
					numStudents++;
				}
				String student2 = result.getField(ALUMNO2);
				if (student2 == null || "".equals(student2)) {
					student2 = "";
				} else {
					numStudents++;
				}
				String student3 = result.getField(ALUMNO3);
				if (student3 == null || "".equals(student3)) {
					student3 = "";
				} else {
					numStudents++;
				}
				LocalDate assignmentDate = LocalDate.parse(result.getField(FECHA_ASIGNACION), dateTimeFormatter);
				LocalDate presentationDate = LocalDate.parse(result.getField(FECHA_PRESENTACION), dateTimeFormatter);
				Double score = Double.parseDouble(result.getField(NOTA));
				int totalDays = Integer.parseInt(result.getField(TOTAL_DIAS));
				String repoLink = result.getField(ENLACE_REPOSITORIO);
				if (repoLink == null) {
					repoLink = "";
				}

				listaDataModel.add(title);
				listaDataModel.add(description);
				listaDataModel.add(tutor1);
				listaDataModel.add(tutor2);
				listaDataModel.add(tutor3);
				listaDataModel.add(student1);
				listaDataModel.add(student2);
				listaDataModel.add(student3);
				listaDataModel.add(numStudents);				
				listaDataModel.add(numTutors);
				listaDataModel.add(assignmentDate);
				listaDataModel.add(presentationDate);
				listaDataModel.add(score);
				listaDataModel.add(totalDays);
				listaDataModel.add(repoLink);
				
			}
		} catch (FilloException e) {
			LOGGER.error("Error al obtener los datos de los históricos", e);
		}
		return listaDataModel;
	}	
}

