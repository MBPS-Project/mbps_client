package ch.uzh.csg.mbps.client.util;

//TODO mehmet javadoc
/**
 * 
 *
 *
 */
public class BaseUriHandler {

	private static BaseUriHandler baseUriHandler;
	private static String baseUri;
	private static String baseUriSSL;
	
	private BaseUriHandler(){
		if(baseUri == null){
			baseUri = Constants.BASE_URI;
		}
		
		if(baseUriSSL == null){
			baseUriSSL = Constants.BASE_URI_SSL;
		}
	}
	
	public static BaseUriHandler getInstance(){
		if(baseUriHandler == null)
			baseUriHandler = new BaseUriHandler();
		
		return baseUriHandler;
	}
	
	public String getBaseUri(){
		return baseUri;
	}
	
	public String getBaseUriSSL(){
		return baseUriSSL;
	}
	
	public void setBaseUri(String uri){
		baseUri = uri;
	}
	
	public void setBaseUriSSL(String uriSSL){
		baseUriSSL = uriSSL;
	}
	
	public void setDefaultBaseUri(){
		baseUri = Constants.BASE_URI;
	}
	
	public void setDefaultBaseUriSSL(){
		baseUriSSL = Constants.BASE_URI_SSL;
	}
}
