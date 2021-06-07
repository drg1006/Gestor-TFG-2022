package ubu.digit.pesistence;

import java.io.IOException;
import org.junit.Test;

import ubu.digit.security.Controller;
/**
 * Clase contenedora de los test para verificar la conexión con el moodle de Ubuvirtual
 * 
 * @author DianaBO
 *
 */
public class WebServiceTest {

	private static final String USERNAME = "prueba@alu.ubu.es";
	private static final String PASSWORD = "password";
	private static final String HOST = "https://ubuvirtual.ubu.es";
	private static Controller CONTROLLER;
	
	/**
	 * Test que verificará que salte el error cuando se intente acceder al
	 * moodle de UbuVirtual con un usuario no existente.
	 * 
	 * @throws IOException 
	 */
	@Test(expected = NoSuchMethodError.class)
	public void loginMoodleTest() throws IOException{
		//Se crea la instancia del controlador
		CONTROLLER = Controller.getInstance();	
		// Se intenta acceder al moodle con el username y el password introducidos
		CONTROLLER.loginMoodleUbuVirtual(HOST, USERNAME, PASSWORD);
	}
}
