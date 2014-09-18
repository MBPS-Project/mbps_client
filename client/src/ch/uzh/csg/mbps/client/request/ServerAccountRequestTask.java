package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.ServerAccountTransferObject;
import ch.uzh.csg.mbps.responseobject.ServerAccountsRequestObject;

/**
 * This class sends a request to retrieve the server accounts.
 */
public class ServerAccountRequestTask extends RequestTask<ServerAccountsRequestObject, ServerAccountTransferObject> {
	
	public ServerAccountRequestTask(IAsyncTaskCompleteListener<ServerAccountTransferObject> cro, ServerAccountsRequestObject input, ServerAccountTransferObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/serveraccounts/accounts", cro, context);
	}

	@Override
	protected ServerAccountTransferObject responseService(ServerAccountsRequestObject saro) throws Exception {
		JSONObject jsonObject = new JSONObject();
		saro.encode(jsonObject);
		return execPost(jsonObject);
	}
}