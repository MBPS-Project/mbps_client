package ch.uzh.csg.mbps.client.request;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.servercomm.CookieHandler;
import ch.uzh.csg.mbps.client.servercomm.CustomRestTemplate;
import ch.uzh.csg.mbps.client.util.Constants;
import ch.uzh.csg.mbps.responseobject.CustomResponseObject;

/**
 * This class sends a request to retrieve the server accounts.
 */
public class ServerAccountRequestTask extends RequestTask {
	private static final String URL_PAGE_STRING = "urlPage";
	
	public ServerAccountRequestTask(IAsyncTaskCompleteListener<CustomResponseObject> cro, int urlPage) {
		this.callback = cro;
		setParamteres(urlPage);
	}
	
	private void setParamteres(int urlPage) {
		this.url = UriComponentsBuilder.fromUriString(Constants.BASE_URI_SSL)
			    .path("/serveraccounts/accounts")
			    .queryParam(URL_PAGE_STRING, urlPage)
			    .build()
			    .toUriString();
	}
	
	@Override
	protected CustomResponseObject responseService(CustomRestTemplate restTemplate) {
		@SuppressWarnings("rawtypes")
		HttpEntity requestEntity = CookieHandler.getAuthHeader();
		return restTemplate.exchange(url, HttpMethod.GET, requestEntity);
	}
}