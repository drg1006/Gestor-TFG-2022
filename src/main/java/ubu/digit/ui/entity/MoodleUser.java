package ubu.digit.ui.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase MoodleUser que representa al usuario logueado en el moodle de
 * UbuVirtual.
 * 
 * @author Diana Bringas Ochoa
 */
public class MoodleUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id del usuario en el moodle.
     */
    private int id;

    /**
     * Nombre de usuario en el moodle.
     */
    private String userName;

    /**
     * Nombre completo del usuario.
     */
    private String fullName;

    /**
     * Email del usuario.
     */
    private String email;

    /**
     * Lista de las asignaturas que tiene el usuario.
     */
    private List<Course> courses;

    /**
     * Constructor
     */
    public MoodleUser() {
        this.courses = new ArrayList<>();
    }

    /**
     * Devuelve el id del usuario.
     * 
     * @return id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Modifica el id del usuario.
     * 
     * @param id
     *           El id del usuario.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre de usuario.
     * 
     * @return userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Modifica el nombre del usuario.
     * 
     * @param userName
     *                 El nombre de usuario.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Devuelve el nombre completo del usuario.
     * 
     * @return fullName
     */
    public String getFullName() {
        return this.fullName;
    }

    /**
     * Modifica el nombre completo del usuario.
     * 
     * @param fullName
     *                 El nombre completo.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Devuelve el email del usuario.
     * 
     * @return email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Modifica el email del usuario.
     * 
     * @param email
     *              EL email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve la lista de cursos que en los que está atriculado el usuario.
     * 
     * @return lista de cursos
     */
    public List<Course> getCourses() {
        return this.courses;
    }

    /**
     * Modifica la lista de cursos en los que está matriculado el usuario.
     * 
     * @param courses
     *                La lista de cursos.
     */
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
