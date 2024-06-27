package hello;

import java.util.HashMap;

/**
 * @author Jacob
 *
 */
public class Response
{
	HashMap map;
	
	public Response() {
		map = new HashMap();
	}

	protected String result;
	public String getResult()
	{
		return result;
	}
	public void setResult(String s)
	{
		result = s;
	}
	
	 

	public HashMap getMap()
	{
		return map;
	}

}
