package ubu.digit.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.ErrorMessage;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ubu.digit.security.*;
import ubu.digit.util.Constants;
import ubu.digit.util.UtilMethods;
import ubu.digit.webService.CoreCourseGetUserAdministrationOptions;
import ubu.digit.webService.CoreWebserviceGetSiteInfo;
import ubu.digit.ui.entity.Course;
import ubu.digit.ui.entity.MoodleUser;
import ubu.digit.ui.components.Footer;

/**
 * Vista de inicio de sesión.
 * 
 * @author Diana Bringas Ochoa
 * @author David Renedo Gil
 */
@Route(value = "Login")
@PageTitle("Login ")
public class LoginView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    /**
     * Logger de la clase.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class.getName());

    /**
     * Nombre de la vista.
     */
    public static final String VIEW_NAME = "login";

    /**
     * Login
     */
    private LoginForm login;

    /**
     * Controlador
     */
    private static Controller CONTROLLER;

    /**
     * Url del host de UbuVirtual
     */
    private static final String HOST = "https://ubuvirtual.ubu.es";

    /**
     * Mensaje de error que se mostrará en el login
     * en caso de no poder acceder.
     */
    public static ErrorMessage errorMessage;

    /**
     * Constructor donde se crea el login
     */
    VerticalLayout layout = new VerticalLayout();

    public LoginView() {

        addClassName("login-view");
        setMargin(true);
        setSpacing(true);
        setSizeFull();

        // Se crea la instancia del controlador
        CONTROLLER = Controller.getInstance();

        login = new LoginForm();
        final LoginI18n i18n = createSpanishI18n();
        login.setI18n(i18n);
        login.setForgotPasswordButtonVisible(false);
        login.addLoginListener(e -> {
            login.setEnabled(false);
            LOGGER.info("\nRealizando la autentificación del usuario... ");

            
            Boolean isAutentificarte = CheckData(e.getUsername(), e.getPassword());
            
            //creación usuarios de prueba, la contraseña es independiente
            if(e.getUsername().equals("alumno")){
                isAutentificarte=true;
            }else if(e.getUsername().equals("profesor")) {
                UI.getCurrent().getSession().setAttribute("reports", "true");
                isAutentificarte=true;
            }else if(e.getUsername().equals("administrador")) {
                isAutentificarte=true;
                UI.getCurrent().getSession().setAttribute("update", "true");
            }
            if (!isAutentificarte) {
                LOGGER.info("Usuario no validado ");
                CONTROLLER.setUsername("");
                login.setEnabled(true);
                i18n.getErrorMessage().setTitle(errorMessage.getTitle());
                i18n.getErrorMessage().setMessage(errorMessage.getMessage());
                login.setI18n(i18n);
                login.setError(true);
            } else {
                LOGGER.info("Usuario validado ");
                // Se ha iniciado sesion
                UI.getCurrent().getSession().setAttribute("sesionIniciada", "true");
                CONTROLLER.setUsername(e.getUsername());
                login.setEnabled(true);
                
   
                UI.getCurrent().navigate(InformationView.class);
            }
        });
        layout.add(login);
        Footer footer = new Footer(null);
        layout.add(footer);
        add(layout);
    }

    /**
     * Comprueba si el usuario y la contraseña introducidos en el login existe en
     * moodle de UbuVirtual, si tiene
     * la asignatura correspondiente al TFG y si tiene permisos de actualización
     * (update) en dicha asignatura.
     * 
     * Si todos estos casos se cumplen entonces se retorna true, con lo que se
     * autentificará el usuario y se permitirá
     * ir a la vista de UploadView.
     * 
     * @param username
     *                 Email del usuario
     * @param password
     *                 Contraseña del usuario
     * @return boolean
     *         True, si el usuario y la contraseña están autentificados, posee la
     *         asignatura de TFG y tiene permisos de actualización
     *         False en caso contrario.
     */
    public Boolean CheckData(String username, String password) {
        try {
            CONTROLLER.loginMoodleUbuVirtual(HOST, username, password);

        } catch (IllegalAccessError e) {
            createErrorLogin("Usuario/contraseña inválidos",
                    "Verifique su usuario y contraseña y vuelva a intentarlo.");
            LOGGER.error("Usuario y/o contraseña incorrectos", e);
            login.setError(true);
        } catch (IOException e) {
            LOGGER.error("No se ha podido conectar con el host.", e);
        } catch (JSONException e) {
            createErrorLogin("Usuario/contraseña inválidos",
                    "Verifique su usuario y contraseña y vuelva a intentarlo.");
            LOGGER.error("Usuario y/o contraseña incorrectos", e);
            login.setError(true);
        }

        try {
            String validUsername = UtilMethods
                    .getJSONObjectResponse(CONTROLLER.getWebService(), new CoreWebserviceGetSiteInfo())
                    .getString(Constants.USERNAME);
            PopulateMoodleUser populateMoodleUser = new PopulateMoodleUser(CONTROLLER.getWebService());
            MoodleUser moodleUser = populateMoodleUser.populateMoodleUser(validUsername,
                    CONTROLLER.getUrlHost().toString());

            LOGGER.info("Obteniendo información del usuario: " + moodleUser.getFullName());
            // Guardamos el nombre del tutor que inicia sesión
            UI.getCurrent().getSession().setAttribute("tutorRegistrado", moodleUser.getFullName());
            // Creacion instancia de CreateCourse desde la cual se accedera a los metodos de
            // obtención de los cursos y permisos
            CreateUserCourses createUserCourses = new CreateUserCourses(CONTROLLER.getWebService());

            // Se obtienen los cursos del usuario y se guardan en el usuario (MoodleUser)
            List<Course> userCourses = createUserCourses.getUserCourses(moodleUser.getId());
            moodleUser.setCourses(userCourses);

            // Se comprueba si el usuario tiene la asignatura correspondiente al Trabajo de
            // Fin de Grado.
            // Si no la tiene, se impide inciar seción.
            Course courseTFG = createUserCourses.checkCourseTFG(userCourses);
            if (courseTFG == null) {
                createErrorLogin("Usuario sin acceso",
                        "El usuario no cuenta con la asignatura de Trabajos de Final de Grado");
                return false;
            }

            // Se obtiene los id de los cursos del moodleUser con los que se buscará los
            // permisos del usuario en la asignatura
            Collection<Integer> courseids = moodleUser.getCourses().stream().map(Course::getId)
                    .collect(Collectors.toList());
            LOGGER.info("Cursos del usuario: " + courseids);
            Collection<Integer> idTFG = new ArrayList<Integer>();
            Iterator<Integer> it = courseids.iterator();
            while (it.hasNext()) {
                int id = it.next();
                if (id == 2204 || id == 11707) {
                    idTFG.add(id);
                }
            }
            LOGGER.info("Curso TFG del usuario: ID--> " + idTFG);
            // Obtenemos los permisos
            JSONArray jsonArray;
            jsonArray = UtilMethods.getJSONObjectResponse(CONTROLLER.getWebService(),
                    new CoreCourseGetUserAdministrationOptions(idTFG)).getJSONArray(Constants.COURSES);
            // Guardamos los permisos en las variables de la sesion
            if (createUserCourses.findPermission(jsonArray, courseTFG, "update")) {
                UI.getCurrent().getSession().setAttribute("update", "true");
            }
            ;
            if (createUserCourses.findPermission(jsonArray, courseTFG, "reports")) {
                UI.getCurrent().getSession().setAttribute("reports", "true");
            }
            ;
            //UI.getCurrent().getSession().setAttribute("update", "true");
            
            return true;
        } catch (Exception e) {
            LOGGER.error("Error al recuperar los datos del usuario ", e);
        }
        return false;
    }

    /**
     * Cambiar el idioma de los mensajes que se muestran en el Login
     * a Español
     * 
     * @return i18n LoginI18n
     */
    private LoginI18n createSpanishI18n() {
        final LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Gestor-TFG-2022");
        i18n.getHeader().setDescription("Gestor TFG");
        i18n.getForm().setUsername("Usuario");
        i18n.getForm().setTitle("Iniciar Sesión");
        i18n.getForm().setSubmit("Iniciar Sesión");
        i18n.getForm().setPassword("Contraseña");
        i18n.getForm().setForgotPassword("¿Olvidaste la contraseña?");
        return i18n;
    }

    /**
     * Se crea un mensaje de error en función de los parámetros pasados
     * 
     * @param titleError   titulo del error que se ha producido
     * @param notification mensaje correspondiente al error
     * @return errorMessage mensaje de error
     */
    public static ErrorMessage createErrorLogin(String titleError, String notification) {
        errorMessage = new ErrorMessage();
        errorMessage.setTitle(titleError);
        errorMessage.setMessage(notification);
        return errorMessage;
    }
}
