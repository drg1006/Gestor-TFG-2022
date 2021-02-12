package ubu.digit.ui.beans;

import java.io.Serializable;
import java.time.LocalDate;
/**
 * POJO para almacenar un proyecto histórico.
 * 
 * @author Javier de la Fuente Barrios
 */
public class HistoricProjectBean extends ProjectBean implements Serializable {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = -4441566584690195452L;

	/**
	 * Número de alumnos que participaron en el proyecto.
	 */
	private int numStudents;

	/**
	 * Número de tutores que participaron en el proyecto.
	 */
	private int numTutors;

	/**
	 * Fecha de asignación del proyecto.
	 */
	private LocalDate assignmentDate;

	/**
	 * Fecha de presentación del proyecto.
	 */
	private LocalDate presentationDate;

	/**
	 * Calificación que obtuvo el proyecto.
	 */
	private Double score;

	/**
	 * Número de días que duró el proyecto.
	 */
	private int totalDays;

	/**
	 * Enlace al repositorio del proyecto.
	 */
	private String repositoryLink;

	/**
	 * Constructor vacío sin parámetros (convención JavaBean).
	 */
	public HistoricProjectBean() {
		// Constructor vacío (convención JavaBean)
	}

	/**
	 * Constructor.
	 * 
	 * @param title
	 *            Título del proyecto.
	 * @param description
	 *            Descripción del proyecto.
	 * @param tutor1
	 *            Primer tutor del proyecto.
	 * @param tutor2
	 *            Segundo tutor del proyecto.
	 * @param tutor3
	 *            Tercer tutor del proyecto.
	 * @param student1
	 *            Primer alumno del proyecto.
	 * @param student2
	 *            Segundo alumno del proyecto.
	 * @param student3
	 *            Tercer alumno del proyecto.
	 * @param numStudents
	 *            Número de alumnos.
	 * @param numTutors
	 *            Número de tutores.
	 * @param assignmentDate
	 *            Fecha de asignación.
	 * @param presentationDate
	 *            Fecha de presentación.
	 * @param score
	 *            Calificación.
	 * @param totalDays
	 *            Número total de días.
	 * @param repositoryLink
	 *            Enlace al repositorio.
	 */
	public HistoricProjectBean(String title, String description, String tutor1, String tutor2, String tutor3,
			String student1, String student2, String student3, int numStudents, int numTutors, LocalDate assignmentDate,
			LocalDate presentationDate, Double score, int totalDays, String repositoryLink) {
		this.title = title;
		this.description = description;
		this.tutor1 = tutor1;
		this.tutor2 = tutor2;
		this.tutor3 = tutor3;
		this.student1 = student1;
		this.student2 = student2;
		this.student3 = student3;
		this.numStudents = numStudents;
		this.assignmentDate = assignmentDate;
		this.presentationDate = presentationDate;
		this.score = score;
		this.totalDays = totalDays;
		this.repositoryLink = repositoryLink;
		this.numTutors = numTutors;
	}

	/**
	 * Obtiene el número de alumnos del proyecto.
	 * 
	 * @return número de alumno del proyecto.
	 */
	public int getNumStudents() {
		return numStudents;
	}

	/**
	 * Establece el número de alumnos del proyecto.
	 * @param numStudents número de alumnos del proyecto.
	 */
	public void setNumStudents(int numStudents) {
		this.numStudents = numStudents;
	}

	/**
	 * Obtiene la fecha de asignación del proyecto.
	 * @returnla fecha de asignación del proyecto.
	 */
	public LocalDate getAssignmentDate() {
		return assignmentDate;
	}

	/**
	 * Establece la fecha de asignación del proyecto.
	 * @param assignmentDate fecha de asignación del proyecto.
	 */
	public void setAssignmentDate(LocalDate assignmentDate) {
		this.assignmentDate = assignmentDate;
	}

	/**
	 * Obtiene la fecha de presentación del proyecto.
	 * @return fecha de presentación del proyecto.
	 */
	public LocalDate getPresentationDate() {
		return presentationDate;
	}

	/**
	 * Establece la fecha de presentación del proyecto.
	 * @param presentationDate fecha de presentación del proyecto.
	 */
	public void setPresentationDate(LocalDate presentationDate) {
		this.presentationDate = presentationDate;
	}

	/**
	 * Obtiene la calificación del proyecto.
	 * @return calificación del proyecto.
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * Establece la calificación del proyecto.
	 * @param score calificación del proyecto.
	 */
	public void setScore(Double score) {
		this.score = score;
	}

	/**
	 * Obtiene el número total de días del proyecto.
	 * @return número total de días del proyecto.
	 */
	public int getTotalDays() {
		return totalDays;
	}

	/**
	 * Establece el número total de días del proyecto.
	 * @param totalDays número total de días del proyecto.
	 */
	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	/**
	 * Obtiene el enlace al repositorio del proyecto.
	 * @return enlace al repositorio del proyecto.
	 */
	public String getRepositoryLink() {
		return repositoryLink;
	}

	/**
	 * Establece el enlace al repositorio del proyecto.
	 * 
	 * @param repositoryLink
	 *            enlace al repositorio del proyecto.
	 */
	public void setRepositoryLink(String repositoryLink) {
		this.repositoryLink = repositoryLink;
	}

	/**
	 * Obtiene el número de tutores del proyecto.
	 * 
	 * @return número de tutores del proyecto.
	 */
	public int getNumTutors() {
		return numTutors;
	}

	/**
	 * Establece el número de tutores del proyecto.
	 * 
	 * @param numTutors
	 *            número de tutores del proyecto.
	 */
	public void setNumTutors(int numTutors) {
		this.numTutors = numTutors;
	}
}
