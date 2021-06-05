package ubu.digit.webService;

import java.util.Collection;
import java.util.Collections;

/**
 * Clase que contiene la información acerca de los usuario obtenida 
 * empleando la funcion de web service CORE_USER_GET_USERS_BY_FIELD.
 * 
 */
public class CoreUserGetUsersByField extends WSFunctionAbstract {

	public CoreUserGetUsersByField() {
		super(WSFunctionEnum.CORE_USER_GET_USERS_BY_FIELD);
	}

	/**
	 * Establece el id del usuario.
	 * 
	 * @param id user id
	 */
	public void setId(int id) {
		setIds(Collections.singleton(id));
	}

	/**
	 * Establece los ids del usuario .
	 * 
	 * @param ids
	 */
	public void setIds(Collection<Integer> ids) {
		setField("id", ids);
	}

	/**
	 * Establece el id number. 
	 * 
	 * @param idnumber
	 */
	public void setIdnumber(int idnumber) {
		setIdnumbers(Collections.singleton(idnumber));
	}

	/**
	 * Establece los id numbers.
	 * 
	 * @param idnumbers 
	 */
	public void setIdnumbers(Collection<Integer> idnumbers) {
		setField("idnumber", idnumbers);
	}

	/**
	 *  Establace el nombre del usuario.
	 *  
	 * @param username
	 */
	public void setUsername(String username) {
		setUsernames(Collections.singleton(username));
	}
	/**
	 * Establece los nombres.
	 * 
	 * @param usernames
	 */
	public void setUsernames(Collection<String> usernames) {
		setField("username", usernames);
	}

	/**
	 * Establece el email.
	 * 
	 * @param email email
	 */
	public void setEmail(String email) {
		setEmails(Collections.singleton(email));
	}
	/**
	 * Establece los emails.
	 * 
	 * @param emails
	 */
	public void setEmails(Collection<String> emails) {
		setField("email", emails);
	}

	private void setField(String field, Collection<?> values) {
		parameters.put("field", field);
		parameters.put("values", values);

	}
}
