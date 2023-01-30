package ubu.digit.webService;

/**
 * Clase correspondiente al webService CORE_ENROL_GET_USERS_COURSES
 * donde figuran los cursos de un usuario determinado.
 * 
 * @author Diana Bringas Ochoa
 *
 */
public class CoreUsersCourses extends WSFunctionAbstract {

    public CoreUsersCourses(int userId) {
        super(WSFunctionEnum.CORE_ENROL_GET_USERS_COURSES);
        setUserid(userId);
    }

    /**
     * Id del usuario
     * 
     * @param userId
     */
    public void setUserid(int userId) {
        parameters.put("userid", userId);

    }
}
