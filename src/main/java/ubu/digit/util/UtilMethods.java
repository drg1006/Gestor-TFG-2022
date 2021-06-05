package ubu.digit.util;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Response;

import ubu.digit.security.*;
import ubu.digit.webService.WSFunctionAbstract;

/**
 * Clase donde se encuentran los métodos para obtener la respuesta correspondiente a
 * las funciones de webService.
 * 
 * @author Diana Bringas Ochoa
 */
public class UtilMethods {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilMethods.class);

	/**
	 * Se recupera la respuesta del webService en formato JSONArray
	 * @param webService
	 * @param webServiceFunction
	 * @return JSONArray con la respuesta del webService
	 * @throws IOException
	 */
	public static JSONArray getJSONArrayResponse(WebService webService, WSFunctionAbstract webServiceFunction) throws IOException {
		try (Response response = webService.getResponse(webServiceFunction)) {
			String string = response.body()
					.string();

			if (string.startsWith("{")) {
				JSONObject jsonObject = new JSONObject(string);
				LOGGER.error("Error en getJSONArrayResponse al tratar de obtener el JSONArray respuesta del WebService: ");
				throw new IllegalStateException(webServiceFunction + "\n" + jsonObject.optString("exception") + "\n" + jsonObject.optString("message"));
			}
			return new JSONArray(string);
		}
	}

	/**
	 * Se recupera la respuesta del webService en formato JSONObject
	 * @param webService
	 * @param webServiceFunction
	 * @return JSONObject con la respuesta del webService
	 * @throws IOException
	 */
	public static JSONObject getJSONObjectResponse(WebService webService, WSFunctionAbstract webServiceFunction) throws IOException {
		try (Response response = webService.getResponse(webServiceFunction)) {
			JSONObject jsonObject = new JSONObject(new JSONTokener(response.body().byteStream()));
			if (jsonObject.has("exception")) {
				LOGGER.error("Error en getJSONObjectResponde al tratar de obtener el JSONObject respuesta del WebService: ");
				throw new IllegalStateException(webServiceFunction + "\n" + jsonObject.optString("exception") + "\n" + jsonObject.optString("message"));
			}
			return jsonObject;
		}
	}
}
