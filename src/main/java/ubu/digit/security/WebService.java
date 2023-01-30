package ubu.digit.security;

import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import ubu.digit.webService.WSFunction;
import ubu.digit.webService.WSFunctionEnum;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio Web
 * 
 * @author Diana Bringas Ochoa
 */
public class WebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebService.class.getName());

    @SuppressWarnings("unused")
    private String hostWithAjax;
    private String sesskey;
    private String token;
    private String privateToken;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private String moodleWSRestFormat = "json";
    private String hostWithWS;

    /**
     * Constructor vacio
     */
    public WebService() {
    }

    /**
     * Intenta conectarse al servicio del moodle(UbuVirtual) para recuperar el token
     * del usuario
     * en la API.
     * 
     * @param host
     *                 host de moodle
     * @param userName
     *                 username de moodle
     * @param password
     *                 contraseña de moodle
     * @throws IOException si no se ha podido conectar al host
     */
    public WebService(String host, String email, String password) throws IOException {
        LOGGER.info("Intentando conectar al servicio del moodle para recuperar el token del usuario");
        String url = host + "/login/token.php";
        RequestBody formBody = new FormBody.Builder().add("username", email)
                .add("password", password)
                .add("service", WSFunctionEnum.MOODLE_MOBILE_APP.toString())
                .build();
        try (Response response = Connection.getResponse(new Request.Builder().url(url)
                .post(formBody)
                .build())) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(response.body().byteStream()));
            if (jsonObject.has("error")) {
                LOGGER.error("Error al intentar recuperar el token del usuario en el moodle de UbuVirtual");
                throw new IllegalAccessError(jsonObject.getString("error"));
            }
            setData(host, jsonObject.getString("token"), jsonObject.optString("privatetoken"));
        }
    }

    /**
     * Se establece el host, el token y el privateToken.
     * 
     * @param host
     * @param token
     * @param privateToken
     */
    public void setData(String host, String token, String privateToken) {
        this.token = token;
        this.privateToken = privateToken;

        this.hostWithWS = host + "/webservice/rest/server.php";
        this.hostWithAjax = host + "/lib/ajax/service.php";
    }

    /**
     * Obtiene la respuesta de una funcion de un servicio web.
     * 
     * @param wsFunction
     * @return respuesta
     * @throws IOException
     */
    public Response getResponse(WSFunction wsFunction) throws IOException {
        LOGGER.info("Obteniendo la respuesta al servicio web");
        wsFunction.addToMapParemeters();
        Builder formBody = new FormBody.Builder();

        formBody.add("wsfunction", wsFunction.getWSFunction()
                .toString());
        formBody.add("moodlewsrestformat", moodleWSRestFormat);
        formBody.add("wstoken", token);

        for (String key : wsFunction.getParameters()
                .keySet()) {
            jsonToQueryParam(formBody, new StringBuilder(key), wsFunction.getParameters().get(key));
        }

        Request.Builder builder = new Request.Builder().url(hostWithWS);
        builder.post(formBody.build());
        return Connection.getResponse(builder.build());
    }

    /**
     * Obtiene el token privado.
     * 
     * @return privateToken
     */
    public String getPrivateToken() {
        return privateToken;
    }

    /**
     * Se establece el token privado.
     * 
     * @param privateToken
     */
    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    /**
     * Obtiene el token.
     * 
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Se establece el token.
     * 
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Obtiene el sesskey.
     * 
     * @return sesskey
     */
    public String getSessKey() {
        return sesskey;
    }

    /**
     * Se establece la sesskey.
     * 
     * @param sesskey
     */
    public void setSesskey(String sesskey) {
        this.sesskey = sesskey;
    }

    /**
     * Método que transforma un json obtenido al obtener la respuesta a una funcion
     * de servicio web en una query.
     * 
     * @param formBodyBuilder
     * @param stringBuilder
     * @param param
     */
    private void jsonToQueryParam(Builder formBodyBuilder, StringBuilder stringBuilder, Object param) {

        if (param instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) param;
            for (String key : jsonObject.keySet()) {
                StringBuilder newStringBuilder = new StringBuilder(stringBuilder);
                newStringBuilder.append("[");
                newStringBuilder.append(key);
                newStringBuilder.append("]");
                jsonToQueryParam(formBodyBuilder, newStringBuilder, jsonObject.get(key));
            }
        } else if (param instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) param;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                StringBuilder newStringBuilder = new StringBuilder(stringBuilder);
                newStringBuilder.append("[");
                newStringBuilder.append(entry.getKey());
                newStringBuilder.append("]");
                jsonToQueryParam(formBodyBuilder, newStringBuilder, entry.getValue());
            }
        } else if (param instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) param;
            Iterator<?> iterator = iterable.iterator();
            for (int i = 0; iterator.hasNext(); ++i) {
                StringBuilder newStringBuilder = new StringBuilder(stringBuilder);
                newStringBuilder.append("[");
                newStringBuilder.append(i);
                newStringBuilder.append("]");
                jsonToQueryParam(formBodyBuilder, newStringBuilder, iterator.next());
            }

        } else {
            formBodyBuilder.add(stringBuilder.toString(), param.toString());
        }
    }
}
