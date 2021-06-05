package ubu.digit.webService;

import org.json.JSONObject;

/**
 * Clase abstracta de las funciones de webService.
 */
public abstract class WSFunctionAbstract implements WSFunction {
	
	private WSFunctionEnum webserviceFunctions;
	protected JSONObject parameters;
	
	public WSFunctionAbstract(WSFunctionEnum webserviceFunctions) {
		this.webserviceFunctions = webserviceFunctions;
		parameters = new JSONObject();
	}
	
	@Override
	public WSFunctionEnum getWSFunction() {
		return webserviceFunctions;
	}

	@Override
	public JSONObject getParameters() {
		return parameters;
	}	
	
	@Override
	public String toString() {
		return webserviceFunctions.toString();
	}
	
	@Override
	public void addToMapParemeters() {
	}
}
