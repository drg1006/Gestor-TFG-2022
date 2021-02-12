package ubu.digit.ui.beans;

import java.io.Serializable;

/**
 * POJO que contiene los elementos comunes de un proyecto activo e histórico.
 * 
 * @author Javier de la Fuente Barrios
 *
 */
public class ProjectBean implements Serializable {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -843095073424042061L;

	/**
	 * Título del proyecto.
	 */
	protected String title;

	/**
	 * Descripción del proyecto.
	 */
	protected String description;

	/**
	 * Primer tutor del proyecto.
	 */
	protected String tutor1;

	/**
	 * Segundo tutor del proyecto.
	 */
	protected String tutor2;

	/**
	 * Tercer tutor del proyecto.
	 */
	protected String tutor3;

	/**
	 * Primer alumno 1 del proyecto.
	 */
	protected String student1;

	/**
	 * Segundo alumno del proyecto.
	 */
	protected String student2;

	/**
	 * Tercer alumno del proyecto.
	 */
	protected String student3;

	/**
	 * Constructor vacío sin parámetros (convención JavaBean).
	 */
	public ProjectBean() {
		// Constructor vacío (convención JavaBean)
	}

	/**
	 * Obtiene el título del proyecto.
	 * 
	 * @return título del proyecto.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Establece el título del proyecto.
	 * 
	 * @param title
	 *            título del proyecto.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Obtiene la descripción del proyecto.
	 * 
	 * @return descripción del proyecto.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Establece la descripción del proyecto.
	 * 
	 * @param description
	 *            descripción del proyecto.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Obtiene el primer tutor del proyecto.
	 * 
	 * @return primer tutor del proyecto.
	 */
	public String getTutor1() {
		return tutor1;
	}

	/**
	 * Establece el primer tutor del proyecto.
	 * 
	 * @param tutor1
	 *            primer tutor del proyecto.
	 */
	public void setTutor1(String tutor1) {
		this.tutor1 = tutor1;
	}

	/**
	 * Obtiene el segundo tutor del proyecto.
	 * 
	 * @return segundo tutor del proyecto.
	 */
	public String getTutor2() {
		return tutor2;
	}

	/**
	 * Establece el segundo tutor del proyecto.
	 * 
	 * @param tutor2
	 *            segundo tutor del proyecto.
	 */
	public void setTutor2(String tutor2) {
		this.tutor2 = tutor2;
	}

	/**
	 * Obtiene el tercer tutor del proyecto.
	 * 
	 * @return tercer tutor del proyecto.
	 */
	public String getTutor3() {
		return tutor3;
	}

	/**
	 * Establece el tercer tutor del proyecto.
	 * 
	 * @param tutor3
	 *            tercer tutor del proyecto.
	 */
	public void setTutor3(String tutor3) {
		this.tutor3 = tutor3;
	}

	/**
	 * Obtiene el primer alumno del proyecto.
	 * 
	 * @return primer alumno del proyecto.
	 */
	public String getStudent1() {
		return student1;
	}

	/**
	 * Establece el primer alumno del proyecto.
	 * 
	 * @param student1
	 *            primer alumno del proyecto.
	 */
	public void setStudent1(String student1) {
		this.student1 = student1;
	}

	/**
	 * Obtiene el segundo alumno del proyecto.
	 * 
	 * @return segundo alumno del proyecto.
	 */
	public String getStudent2() {
		return student2;
	}

	/**
	 * Establece el segundo alumno del proyecto.
	 * 
	 * @param student2
	 *            segundo alumno del proyecto.
	 */
	public void setStudent2(String student2) {
		this.student2 = student2;
	}

	/**
	 * Obtiene el tercer alumno del proyecto.
	 * 
	 * @return tercer alumno del proyecto.
	 */
	public String getStudent3() {
		return student3;
	}

	/**
	 * Establece el tercer alumno del proyecto.
	 * 
	 * @param student3
	 *            tercer alumno del proyecto.
	 */
	public void setStudent3(String student3) {
		this.student3 = student3;
	}
}
