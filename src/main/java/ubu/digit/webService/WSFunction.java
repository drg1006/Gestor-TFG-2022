package ubu.digit.webService;

import org.json.JSONObject;

/**
 * Funcion WebService interface
 */
public interface WSFunction {
    public WSFunctionEnum getWSFunction();

    public void addToMapParemeters();

    public JSONObject getParameters();
}
