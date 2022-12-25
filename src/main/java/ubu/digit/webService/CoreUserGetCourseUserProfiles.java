package ubu.digit.webService;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Clase que obtiene los cursos con el web service CORE_USER_GET_COURSE_USER_PROFILES de un alumno.
 * 
 */
public class CoreUserGetCourseUserProfiles extends WSFunctionAbstract {

    public CoreUserGetCourseUserProfiles(String id) {
		super(WSFunctionEnum.CORE_USER_GET_COURSE_USER_PROFILES);
		setId(id);
	}

	private void put(String field, Object value) {
		parameters.put("field", field);
		parameters.put("value", value);
	}

	/**
	 * Establece el curso id.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		put("id", id);
	}

	/**
	 * Establece el separador, una coma, de los cursos id.
	 * 
	 * @param ids
	 */
	public void setIds(Collection<Integer> ids) {
		put("ids", ids.stream()
				.map(Object::toString)
				.collect(Collectors.joining(",")));
	}

	/**
	 * Establece El nombre corto del curso.
	 * 
	 * @param shortname
	 */
	public void setShortname(String shortname) {
		put("shortname", shortname);
	}

	/**
	 * Establece el id number del curso.
	 * 
	 * @param idnumber Course id number
	 */
	public void setIdnumber(int idnumber) {
		put("idnumber", idnumber);
	}

	/**
	 * Establece la categoria del id a la que el curso pertecenece.
	 * 
	 * @param categoryId 
	 */
	public void setCategoryid(int categoryId) {
		put("categoryId", categoryId);
	}

	@Override
	public void addToMapParemeters() {
		if (parameters.length() != 2) {
			throw new IllegalArgumentException("Solamente debe haber un valor por parámetro");
		}
	}
}
