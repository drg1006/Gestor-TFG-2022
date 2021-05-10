package ubu.digit.ui.beans;

import java.io.Serializable;

public class ActiveProject extends ProjectBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Curso de asignación.
	 */
	private String courseAssignment="";

	/**
	 * Tutores del proyecto
	 */
	private String tutors;

	/**
	 * Alumnos del proyecto
	 */
	private String students;

	/**
	 * Constructor.
	 * 
	 * @param title
	 *            Título del proyecto.
	 * @param description
	 *            Descripción del proyecto.
	 * @param tutors
	 *            Tutores del proyecto.
	 * @param students
	 *            Alumnos del proyecto.
	 * @param courseAssignment
	 *            Curso de asignación del proyecto.
	 */
	public ActiveProject(String title, String description, String tutors,String students, String courseAssignment) {
		this.title = title;
		this.description = description;
		this.tutors = tutors;
		this.students = students;
		this.courseAssignment = courseAssignment;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param title
	 *            Título del proyecto.
	 * @param description
	 *            Descripción del proyecto.
	 * @param tutor1
	 *            Tutor nº 1 del proyecto.
	 * @param tutor2
	 *            Tutor nº 2 del proyecto.
	 * @param tutor3
	 *            Tutor nº 3 del proyecto.
	 * @param student1
	 *            Alumno nº 1 del proyecto.
	 * @param student2
	 *            Alumno nº 2 del proyecto.
	 * @param student3
	 *            Alumno nº 3 del proyecto.
	 * @param courseAssignment
	 *            Curso de asignación del proyecto.
	 */
	public ActiveProject(String title, String description, String tutor1, String tutor2, String tutor3,
			String student1, String student2, String student3, String courseAssignment) {
		this.title = title;
		this.description = description;
		this.tutor1 = tutor1;
		this.tutor2 = tutor2;
		this.tutor3 = tutor3;
		this.student1 = student1;
		this.student2 = student2;
		this.student3 = student3;
		this.courseAssignment = courseAssignment;
	}

	/**
	 * Obtiene los estudiantes del proyecto.
	 * @return
	 */
	public String getStudents(){
		return this.students;
	}
	
	/**
	 * Establece los estudiantes del proyecto.
	 * @param students
	 */
	public void setStudent(String students) {
		this.students = students;
	}
	
	/**
	 * Devuelve los tutores a cargo del proyecto.
	 * @return
	 */
	public String getTutors(){
		return this.tutors;
	}
	
	/**
	 * Establece los tutores del proyecto
	 * @param tutors
	 */
	public void setTutors(String tutors) {
		this.tutors = tutors;
	}
	
	/**
	 * Obtiene el curso de asignación del proyecto.
	 * 
	 * @return curso de asignación del proyecto
	 */
	public String getCourseAssignment() {
		return courseAssignment;
	}

	/**
	 * Establece el curso de asignación del proyecto.
	 * 
	 * @param courseAssignment
	 *            curso de asignación del proyecto
	 */
	public void setCourseAssignment(String courseAssignment) {
		this.courseAssignment = courseAssignment;
	}
}
