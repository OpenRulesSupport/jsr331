package hello;

import java.util.HashMap;

public class Response {
	public Response() {
	}

	protected String result;

	public String getResult() {
		return result;
	}

	public void setResult(String s) {
		result = s;
	}

	HashMap map = new HashMap();

	public HashMap getMap() {
		return map;
	}

}
