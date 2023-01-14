package ubu.digit.ui.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.vaadin.flow.component.datepicker.DatePicker;

/**
 * Clase que se utilizará para bindear con los formularios de los proyectos.
 * 
 * @author David Renedo Gil
 */
public class FormularioTFG extends Projects implements Serializable {

    private static final long serialVersionUID = 1L;

    private String titulo;
    private String descripcion;
    private String tutor1;
    private String tutor2;
    private String tutor3;
    private String alumno1;
    private String alumno2;
    private String cursoAsignacion;
    private int nota;
    private LocalDate fechaAsignacion;
    private LocalDate fechaPresentacion;
    private String tituloCorto;
    private String repo;

    /**
     * Obtiene el titulo del formulario.
     * 
     * @return students
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Escribe el titulo del formulario.
     * 
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la descripcion tdel formulario.
     * 
     * @return descripcion
     */
    public String getDescripcion() {
        return this.descripcion;
    }
    
    /**
     * Escribe la descripcion del formulario.
     * 
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el tutor1 del formulario.
     * 
     * @return tutor1
     */
    public String getTutor1() {
        return this.tutor1;
    }

    /**
     * Escribe el tutor1 del formulario.
     * 
     */
    public void setTutor1(String tutor1) {
        this.tutor1 = tutor1;
    }

    /**
     * Obtiene el tutor2 del formulario.
     * 
     * @return tutor2
     */
    public String getTutor2() {
        return this.tutor2;
    }

    /**
     * Escribe el tutor2 del formulario.
     * 
     */
    public void setTutor2(String tutor2) {
        this.tutor2 = tutor2;
    }

    /**
     * Obtiene el tutor3 del formulario.
     * 
     * @return tutor3
     */
    public String getTutor3() {
        return this.tutor3;
    }

    /**
     * Escribe el tutor3 del formulario.
     * 
     */
    public void setTutor3(String tutor3) {
        this.tutor3 = tutor3;
    }

    /**
     * Obtiene el alumno1 del formulario.
     * 
     * @return alumno1
     */
    public String getAlumno1() {
        return this.alumno1;
    }

    /**
     * Escribe el alumno1 del formulario.
     * 
     */
    public void setAlumno1(String alumno1) {
        this.alumno1 = alumno1;
    }

    /**
     * Obtiene el alumno2 del formulario.
     * 
     * @return alumno2
     */
    public String getAlumno2() {
        return this.alumno2;
    }

    /**
     * Escribe el alumno2 del formulario.
     * 
     */
    public void setAlumno2(String alumno2) {
        this.alumno2 = alumno2;
    }

    /**
     * Obtiene el cursoAsignacion del formulario.
     * 
     * @return cursoAsignacion
     */
    public String getcursoAsignacion() {
        return this.cursoAsignacion;
    }

    /**
     * Escribe el cursoAsignacion del formulario.
     * 
     */
    public void setCursoAsignacion(String cursoAsignacion) {
        this.cursoAsignacion = cursoAsignacion;
    }
    /**
     * Obtiene la nota del formulario.
     * 
     * @return nota
     */
    public int getNota() {
        return this.nota;
    }

    /**
     * Escribe la nota del formulario.
     * 
     */
    public void setNota(int nota) {
        this.nota = nota;
    }
    /**
     * Obtiene la fecha de asignacion del formulario.
     * 
     * @return cursoAsignacion
     */
    public LocalDate getFechaAsignacion() {
        return this.fechaAsignacion;
    }

    /**
     * Escribe la fecha de asignacion del formulario.
     * 
     */
    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
    /**
     * Obtiene la fecha de presentacion  del formulario.
     * 
     * @return cursoAsignacion
     */
    public LocalDate getFechaPresentacion() {
        return this.fechaPresentacion;
    }

    /**
     * Escribe la fecha de presentacion del formulario.
     * 
     */
    public void FechaPresentacion(LocalDate fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }
    /**
     * Obtiene el repositorio del formulario.
     * 
     * @return cursoAsignacion
     */
    public String getRepo() {
        return this.repo;
    }

    /**
     * Escribe el repositorio del formulario.
     * 
     */
    public void setRepo(String repositorio) {
        this.repo = repositorio;
    }
    /**
     * Obtiene el titulo corto del formulario.
     * 
     * @return cursoAsignacion
     */
    public String getTituloCorto() {
        return this.tituloCorto;
    }

    /**
     * Escribe el titulo corto del formulario.
     * 
     */
    public void setTituloCorto(String tituloCorto) {
        this.tituloCorto = tituloCorto;
    }
    
    
}
