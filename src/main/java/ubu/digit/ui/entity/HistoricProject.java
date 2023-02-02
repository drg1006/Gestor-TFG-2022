package ubu.digit.ui.entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Clase para almacenar un proyecto histórico.
 * 
 * @author Javier de la Fuente Barrios
 * @author Diana Bringas Ochoa
 */
public class HistoricProject extends Projects implements Serializable {

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
     * Ranking de percentiles de las notas
     */
    private String rankingPercentile;

    /**
     * Ranking total de las notas
     */
    private int rankingTotal;

    /**
     * Ranking según el curso academico (1 de septiembre - 30 junio)
     */
    private int rankingCurse;

    private String tutors;
    
    private String course;


    /**
     * Constructor vacío sin parámetros (convención JavaBean).
     */
    public HistoricProject() {
    }

    /**
     * Constructor.
     * 
     * @param title
     *                         Título del proyecto.
     * @param description
     *                         Descripción del proyecto.
     * @param tutor1
     *                         Primer tutor del proyecto.
     * @param tutor2
     *                         Segundo tutor del proyecto.
     * @param tutor3
     *                         Tercer tutor del proyecto.
     * @param student1
     *                         Primer alumno del proyecto.
     * @param student2
     *                         Segundo alumno del proyecto.
     * @param student3
     *                         Tercer alumno del proyecto.
     * @param numStudents
     *                         Número de alumnos.
     * @param numTutors
     *                         Número de tutores.
     * @param assignmentDate
     *                         Fecha de asignación.
     * @param presentationDate
     *                         Fecha de presentación.
     * @param score
     *                         Calificación.
     * @param totalDays
     *                         Número total de días.
     * @param repositoryLink
     *                         Enlace al repositorio.
     */
    public HistoricProject(String title, String tutors, int numStudents, LocalDate assignmentDate,
            LocalDate presentationDate, String rankingPercentile, int rankingTotal, int rankingCurse) {
        this.title = title;
        this.tutors = tutors;
        this.numStudents = numStudents;
        this.assignmentDate = assignmentDate;
        this.presentationDate = presentationDate;
        this.rankingPercentile = rankingPercentile;
        this.rankingTotal = rankingTotal;
        this.rankingCurse = rankingCurse;
    }

    /**
     * Constructor.
     * 
     * @param title
     *                         Título del proyecto.
     * @param description
     *                         Descripción del proyecto.
     * @param tutor1
     *                         Primer tutor del proyecto.
     * @param tutor2
     *                         Segundo tutor del proyecto.
     * @param tutor3
     *                         Tercer tutor del proyecto.
     * @param student1
     *                         Primer alumno del proyecto.
     * @param student2
     *                         Segundo alumno del proyecto.
     * @param student3
     *                         Tercer alumno del proyecto.
     * @param numStudents
     *                         Número de alumnos.
     * @param numTutors
     *                         Número de tutores.
     * @param assignmentDate
     *                         Fecha de asignación.
     * @param presentationDate
     *                         Fecha de presentación.
     * @param score
     *                         Calificación.
     * @param totalDays
     *                         Número total de días.
     * @param repositoryLink
     *                         Enlace al repositorio.
     */
    public HistoricProject(String title, String description, String tutor1, String tutor2, String tutor3,
            String student1, String student2, String student3, int numStudents, int numTutors, LocalDate assignmentDate,
            LocalDate presentationDate, Double score, int totalDays, String repositoryLink, String rankingPercentile,
            int rankingTotal, int rankingCurse) {
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
        this.rankingPercentile = rankingPercentile;
        this.rankingTotal = rankingTotal;
        this.rankingCurse = rankingCurse;
        this.totalDays = totalDays;
        this.repositoryLink = repositoryLink;
        this.numTutors = numTutors;
    }

    /**
     * Devuelve los tutores a cargo del proyecto.
     * 
     * @return
     */
    public String getTutors() {
        return this.tutors;
    }

    /**
     * Establece los tutores del proyecto
     * 
     * @param tutors
     */
    public void setTutors(String tutors) {
        this.tutors = tutors;
    }

    /**
     * Obtiene el ranking basado en percentiles de la nota
     * del proyecto
     * 
     * @return ranking de la nota del proyecto
     */
    public String getRankingPercentile() {
        return rankingPercentile;
    }

    /**
     * Establece el ranking basado en percentiles del proyecto
     * 
     * @param ranking clasificación de las nota del proyecto
     *                respecto al resto de proyectos
     */
    public void setRankingPercentile(String rankingPercentile) {
        this.rankingPercentile = rankingPercentile;
    }

    /**
     * Obtiene el ranking según el curso academico
     * 
     * @return ranking de la nota del proyecto por cursos
     */
    public int getRankingCurse() {
        return rankingCurse;
    }

    /**
     * Establece el ranking por cursos (año academico)
     * 
     * @param ranking clasificación de las nota del proyecto
     *                respecto al resto de proyectos según el año academico
     */
    public void setRankingCurse(int rankingCurse) {
        this.rankingCurse = rankingCurse;
    }

    /**
     * Obtiene el ranking total de la nota del proyecto.
     * 
     * @return ranking de la nota del proyecto
     */
    public int getRankingTotal() {
        return rankingTotal;
    }

    /**
     * Establece el rankingtotal de la nota del proyecto.
     * 
     * @param ranking clasificación de las nota del proyecto
     */
    public void setRankingTotal(int rankingTotal) {
        this.rankingTotal = rankingTotal;
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
     * 
     * @param numStudents número de alumnos del proyecto.
     */
    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    /**
     * Obtiene la fecha de asignación del proyecto.
     * 
     * @returnla fecha de asignación del proyecto.
     */
    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    /**
     * Establece la fecha de asignación del proyecto.
     * 
     * @param assignmentDate fecha de asignación del proyecto.
     */
    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    /**
     * Obtiene la fecha de presentación del proyecto.
     * 
     * @return fecha de presentación del proyecto.
     */
    public LocalDate getPresentationDate() {
        return presentationDate;
    }

    /**
     * Establece la fecha de presentación del proyecto.
     * 
     * @param presentationDate fecha de presentación del proyecto.
     */
    public void setPresentationDate(LocalDate presentationDate) {
        this.presentationDate = presentationDate;
    }

    /**
     * Obtiene la calificación del proyecto.
     * 
     * @return calificación del proyecto.
     */
    public Double getScore() {
        return score;
    }

    /**
     * Establece la calificación del proyecto.
     * 
     * @param score calificación del proyecto.
     */
    public void setScore(Double score) {
        this.score = score;
    }

    /**
     * Obtiene el número total de días del proyecto.
     * 
     * @return número total de días del proyecto.
     */
    public int getTotalDays() {
        return totalDays;
    }

    /**
     * Establece el número total de días del proyecto.
     * 
     * @param totalDays número total de días del proyecto.
     */
    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    /**
     * Obtiene el enlace al repositorio del proyecto.
     * 
     * @return enlace al repositorio del proyecto.
     */
    public String getRepositoryLink() {
        return repositoryLink;
    }

    /**
     * Establece el enlace al repositorio del proyecto.
     * 
     * @param repositoryLink
     *                       enlace al repositorio del proyecto.
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
     *                  número de tutores del proyecto.
     */
    public void setNumTutors(int numTutors) {
        this.numTutors = numTutors;
    }
    /**
     * Obtiene el número de tutores del proyecto.
     * 
     * @return número de tutores del proyecto.
     */
    public String getCourse() {
        return course;
    }

    /**
     * Establece el número de tutores del proyecto.
     * 
     * @param numTutors
     *                  número de tutores del proyecto.
     */
    public void setCourse(String course) {
        this.course = course;
    }
}