package ubu.digit.webService;

import java.util.Collection;

/**
 * Clase que obtiene los permisos o opciones de administracion del usuario
 * con el web service CORE_COURSE_GET_USER_ADMINISTRATION_OPTIONS respecto a una
 * asignatura.
 * Estos permisos pueden figurar como disponibles o no para el usuario.
 * 
 */
public class CoreCourseGetUserAdministrationOptions extends WSFunctionAbstract {

    public CoreCourseGetUserAdministrationOptions(Collection<Integer> courseids) {
        super(WSFunctionEnum.CORE_COURSE_GET_USER_ADMINISTRATION_OPTIONS);
        setCouseids(courseids);
    }

    /**
     * Id del curso necesario para obtener los permisos.
     * 
     * @param courseids
     *                  id del curso
     */
    public void setCouseids(Collection<Integer> courseids) {
        if (courseids != null && !courseids.isEmpty()) {
            parameters.put("courseids", courseids);
        }
    }

}
