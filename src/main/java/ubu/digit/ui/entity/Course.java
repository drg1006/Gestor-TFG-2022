package ubu.digit.ui.entity;

import java.io.Serializable;

/**
 * Clase curso o asignatura. Almacena información acerca de las asignaturas.
 * 
 * @author Diana Bringas Ochoa
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String shortName;
    private String fullName;
    private String idNumber;
    private String userFullName;
    @SuppressWarnings("unused")
    private boolean courseAccess;
    private boolean reportAccess;

    /**
     * Constructor que inicializa con el id del curso.
     * 
     * @param id
     */
    public Course(int id) {
        this();
        this.id = id;
    }

    /**
     * Constructor vacío.
     */
    public Course() {
    }

    /**
     * Obtiene el id del curso.
     * 
     * @return id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Establece id del curso.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre corto del curso.
     * 
     * @return shortName
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Establece el nombre corto del curso.
     * 
     * @param shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Obtiene el nombre del curso.
     * 
     * @return fullName
     */
    public String getFullName() {
        return this.fullName;
    }

    /**
     * Establece el nombre del curso.
     * 
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Establece el acesso a la asignatura.
     * 
     * @param courseAccess the courseAccess to set
     */
    public void setCourseAccess(boolean courseAccess) {
        this.courseAccess = courseAccess;
    }

    /**
     * Comprueba si tiene el informe de acceso.
     * 
     * @return the reportAccess
     */
    public boolean hasReportAccess() {
        return reportAccess;
    }

    /**
     * Establece el el informe de acceso.
     * 
     * @param reportAccess
     */
    public void setReportAccess(boolean reportAccess) {
        this.reportAccess = reportAccess;
    }

    /**
     * Obtiene el idNumber del curso.
     * 
     * @return idNumber
     */
    public String getIdNumber() {
        return this.idNumber;
    }

    /**
     * Establece el idNumber del curso.
     * 
     * @param idNumber
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * Obtiene el nombre completo del usuario.
     * 
     * @return userFullName
     */
    public String getUserFullName() {
        return userFullName;
    }
}
