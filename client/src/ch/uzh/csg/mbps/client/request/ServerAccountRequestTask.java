package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.Constants;
import ch.uzh.csg.mbps.responseobject.ServerAccountObject;
import ch.uzh.csg.mbps.responseobject.ServerAccountTransferObject;

/**
 * This class sends a request to retrieve the server accounts.
 */
public class ServerAccountRequestTask extends RequestTask<ServerAccountObject, ServerAccountTransferObject> {
	
	public ServerAccountRequestTask(IAsyncTaskCompleteListener<ServerAccountTransferObject> cro, ServerAccountObject input, ServerAccountTransferObject output) {
		super(input, output, Constants.BASE_URI_SSL + "/serveraccounts/accounts", cro);
	}

	@Override
	protected ServerAccountTransferObject responseService(ServerAccountObject sao) throws Exception {
		JSONObject jsonObject = new JSONObject();
		sao.encode(jsonObject);
		return execPost(jsonObject);
	}
}