package ubu.digit.webService;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerados de las funciones de webService usadas.
 * 
 * @author Diana Bringas Ochoa
 */
public enum WSFunctionEnum {
	
	/**
	 * Return some site info / user info / list web service functions
	 */
	CORE_WEBSERVICE_GET_SITE_INFO("core_webservice_get_site_info", 2.1),

	/**
	 * Get list of course ids that a user is enrolled in (if you are allowed to see
	 * that).
	 */
	CORE_ENROL_GET_USERS_COURSES("core_enrol_get_users_courses", 2.0),

	/**
	 * Get courses matching a specific field (id/s, shortname, idnumber, category)
	 */
	CORE_COURSE_GET_COURSES_BY_FIELD("core_course_get_courses_by_field", 3.2),
	
	/**
	 * Return a list of administration options in a set of courses that are
	 * avaialable or not for the current user.
	 */
	CORE_COURSE_GET_USER_ADMINISTRATION_OPTIONS("core_course_get_user_administration_options", 3.2),
	
	/**
	 * Retrieve users information for a specified unique field - If you want to do a
	 * user search, use core_user_get_users().
	 */
	CORE_USER_GET_USERS_BY_FIELD("core_user_get_users_by_field", 2.5),
	
	 /**
     * Get course user profiles by id
     */
    
    CORE_USER_GET_COURSE_USER_PROFILES("core_user_get_course_user_profiles",2.1),

	/**
	 * Return the user token.
	 */
	MOODLE_MOBILE_APP("moodle_mobile_app", 2.0);

	private static Map<String, WSFunctionEnum> map = new HashMap<>();

	static {
		for (WSFunctionEnum value : WSFunctionEnum.values()) {
			map.put(value.name, value);
		}
	}
	
	private String name;
	private double since;

	private WSFunctionEnum(String name, double since) {
		this.name = name;
		this.since = since;
	}

	public WSFunctionEnum get(String name) {
		if (map.containsKey(name))
			return map.get(name);
		throw new IllegalArgumentException(name + " web service function not avaible.");
	}

	public double getSince() {
		return since;
	}

	@Override
	public String toString() {
		return name;
	}
}
