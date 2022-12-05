package ubu.digit.webService;

/**
 * Clase correspondiente al webService core_enrol_search_users
 * donde se buscan los usuariso de un curso determinado.
 * 
 * @author David Renedo Gil
 *
 */
public class CoreGetEnrolledUsers extends WSFunctionAbstract {

	public CoreGetEnrolledUsers(String id) {
		super(WSFunctionEnum.CORE_ENROL_GET_ENROLLED_USERS);
		setCourse(id);
	}

	/**
     * Id del usuario
     * 
     * @param userId
     */
    public void setCourse(String course) {
        parameters.put("userid", course);

    }
}
