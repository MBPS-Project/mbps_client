package ch.uzh.csg.mbps.client.util;

//TODO mehmet javadoc
/**
 * 
 *
 *
 */
public class BaseUriHandler {

	private static BaseUriHandler baseUriHandler;
	private static String baseUriSSL;
	private static String uri;
	
	private BaseUriHandler(){
		if(baseUriSSL == null){
			baseUriSSL = Constants.URI_SSL + Constants.PATH;
		}
	}
	
	public static BaseUriHandler getInstance(){
		if(baseUriHandler == null)
			baseUriHandler = new BaseUriHandler();
		
		return baseUriHandler;
	}
	
	public String getBaseUriSSL(){
		return baseUriSSL;
	}
	
	private void setBaseUriSSL(String uriSSL){
		baseUriSSL = uriSSL;
	}
	
	public void setDefaultBaseUriSSL(){
		setBaseUriSSL(Constants.URI_SSL + Constants.PATH);
		uri = Constants.URI_SSL;
	}
	
	public void setUri(String url){
		uri = url;
		setBaseUriSSL(url+Constants.PATH);
	}
	
	public  String getUri(){
		return uri;
	}
}
