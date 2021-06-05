package ubu.digit.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubu.digit.ui.entity.Course;
import ubu.digit.ui.views.LoginView;
import ubu.digit.util.Constants;
import ubu.digit.util.UtilMethods;
import ubu.digit.webService.CoreUsersCourses;

/**
 * En esta clase se recupera a través de un webService los cursos de un 
 * usuario determinado. Se obtendrá la respuesta en formato JSON.
 * 
 * @author Diana Bringas Ochoa
 *
 */
public class CreateUserCourses {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserCourses.class);
	private WebService webService;

	public CreateUserCourses(WebService webService) {
		this.webService = webService;
	}

	/**
	 * Obtiene el JSONArray de los cursos obtenido en la clase GetUsersCourses y 
	 * devuelve la lista con los cursos obtenidos de ese JSON ( en el metodo createCourses)
	 * @param userid
	 * @return lista de cursos del usuario
	 */
	public List<Course> getUserCourses(int userId) {
		List<Course> EMPTY_LIST = Collections.emptyList();
		try {
			LOGGER.info("Se intenta adquerir el JSON con los cursos ");
			JSONArray jsonArray = UtilMethods.getJSONArrayResponse(webService, new CoreUsersCourses(userId));
			return createCourses(jsonArray);
		} catch (Exception e) {
			LOGGER.error("Error CoreEnrolGetUsersCourses: ", e);
			return EMPTY_LIST;
		}
	}

	/**
	 * Comprueba que el usuario tenga la asignatura del TFG
	 * @param jsonArray
	 * @return course
	 * 			la asignatura correspondiente al Trabajo de fin de Grado
	 */
	public Course checkCourseTFG(List<Course> courses) {
		Course course = null;
		
		if (courses.size() < 0)
			return course;
		LOGGER.info("Comprobando si el usuario tiene la asignatura correspondiente al TFG");
		for (int i = 0; i < courses.size(); i++) {
			if (courses.get(i).getFullName().contains("TRABAJO FIN DE GRADO")) {
				course = courses.get(i);
				LOGGER.info("El usuario tiene la asignatura: " + course.getFullName());		
				return course;
			}
		}
		return course;
	}

	/**
	 * Obtiene a partir del JSONArray con los cursos un JSONObject donde figuran los cursos de un usuario. 
	 * Los cursos son devuelvos en una lisa.
	 * @param jsonArray
	 * @return lista de cursos de un usuario
	 */
	public List<Course> createCourses(JSONArray jsonArray) {
		if (jsonArray == null)
			return Collections.emptyList();

		List<Course> courses = new ArrayList<>();
		LOGGER.info("Se obtienen las asignaturas del usuario");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject != null) {
				Course course = new Course();
				course.setShortName(jsonObject.optString(Constants.SHORTNAME));
				course.setFullName(jsonObject.optString(Constants.FULLNAME));
				course.setIdNumber(jsonObject.optString(Constants.IDNUMBER));
				course.setId(jsonObject.optInt(Constants.ID));
				courses.add(course);
			}
		}
		return courses;
	}
	
	/**
	 * Metodo que se encarga de obtener los permisos del usuario en cada asignatura.
	 * 
	 * Sí el usuario tiene permisos de actualización (update) devuelve true. 
	 * Y, por tanto este usuario tendrá permisos de actualización también en la app Gestor-TFG-2021.
	 * En caso contrario, devolverá false y no podrá acceder a la vista de UploadView.
	 * 
	 * @param jsonArray
	 * @param course
	 * @param permission
	 * @param consumer
	 * @return Boolean 
	 * 			True si tiene permisos de "update" y false en caso contrario.
	 */
	public Boolean findPermission(JSONArray jsonArray, Course course, String permission) {
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			course.setCourseAccess(true);
			JSONArray options = jsonObject.getJSONArray(Constants.OPTIONS);
			for (int j = 0; j < options.length(); ++j) {
				JSONObject option = options.getJSONObject(j);
				if (permission.equals(option.getString(Constants.NAME)) && option.getBoolean(Constants.AVAILABLE)== false) {
					LOGGER.info("Permiso de actualización disponible ");
					return true;
				}
			}
		}
		
		LOGGER.info("El usuario no tiene los permisos requeridos para acceder.");
		LoginView.createErrorLogin("El usuario no tiene los permisos requeridos para acceder", "Acceda con un usuario con permisos de actualización");
		return false;
	}
}
