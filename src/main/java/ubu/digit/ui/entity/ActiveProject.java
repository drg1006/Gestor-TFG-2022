package ubu.digit.ui.entity;

import java.io.Serializable;

/**
 * Clase para almacenar los proyectos activos.
 * 
 * @author Diana Bringas Ochoa
 */
public class ActiveProject extends Projects implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Curso de asignación.
	 */
	private String courseAssignment="";

	/**
	 * Tutores del proyecto.
	 */
	private String tutors;

	/**
	 * Alumnos del proyecto.
	 */
	private String students;

	private String status;
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
	public ActiveProject(String title, String description, String tutors,String students, String courseAssignment, String status) {
		this.title = title;
		this.description = description;
		this.tutors = tutors;
		this.students = students;
		this.courseAssignment = courseAssignment;
		this.status=status;
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
			String student1, String student2, String student3, String courseAssignment, String status) {
		this.title = title;
		this.description = description;
		this.tutor1 = tutor1;
		this.tutor2 = tutor2;
		this.tutor3 = tutor3;
		this.student1 = student1;
		this.student2 = student2;
		this.student3 = student3;
		this.courseAssignment = courseAssignment;
		this.status=status;
	}
	/**
	 * Constructior vacio, se utiliza para crear un proyecto sin tener asignaciones de manera inicial.
	 */
	public ActiveProject() {
        // TODO Auto-generated constructor stub
    }

    /**
	 * Obtiene los estudiantes del proyecto.
	 * @return students
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
	 * 
	 * @return tutors
	 */
	public String getTutors(){
		return this.tutors;
	}
	
	/**
	 * Establece los tutores del proyecto.
	 * 
	 * @param tutors
	 */
	public void setTutors(String tutors) {
		this.tutors = tutors;
	}
	
	/**
	 * Obtiene el curso de asignación del proyecto.
	 * 
	 * @return courseAssignment 
	 * 			Curso de asignación del proyecto
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
	/**
     * Obtiene el estado.
     * 
     * @return status
     *          Estado del TFG
     */
    public String getStatus() {
        return this.status;
    }
    
    /**
     * Establece estado del TFG.
     * 
     * @param status
     *            estado del TFG
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
}
