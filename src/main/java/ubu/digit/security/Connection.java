package ubu.digit.security;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.Duration;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Clase conexión
 * 
 * @author Diana Bringas Ochoa
 */
public class Connection {

	private static final OkHttpClient CLIENT;
	private static final CookieManager COOKIES_MANAGER;

	static {
		COOKIES_MANAGER = new CookieManager();
		COOKIES_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(COOKIES_MANAGER);
		CLIENT = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(COOKIES_MANAGER))
				.readTimeout(Duration.ofMinutes(5)).build();
	}

	private Connection() {
	}

	/**
	 * Obtiene la respuesta de la petición 
	 * @param url
	 * @return Response
	 * @throws IOException exception
	 */
	public static Response getResponse(String url) throws IOException {
		return getResponse(new Request.Builder().url(url)
				.build());
	}

	/**
	 * Obtiene la respuesta a la petición
	 * @param request
	 * @return response
	 * @throws IOException I/O exception
	 */
	public static Response getResponse(Request request) throws IOException {
		return CLIENT.newCall(request)
				.execute();
	}

	/**
	 * Devuelve la instancia del cliente
	 * @return la instancia del cliente
	 */
	public static OkHttpClient getClient() {
		return CLIENT;
	}

	/**
	 * Devuelve el cookieManager que almacenan las cookies
	 * @return cookies manager
	 */
	public static CookieManager getCookieManager() {
		return COOKIES_MANAGER;
	}
	
	/**
	 * Limpia las cookies
	 */
	public static void clearCookies() {
		COOKIES_MANAGER.getCookieStore().removeAll();
	}
}
