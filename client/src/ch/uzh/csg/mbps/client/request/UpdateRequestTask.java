package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.TransferObject;
import ch.uzh.csg.mbps.responseobject.UserAccountObject;

/**
 * This class sends an update request. The update requests are changing the email
 * address or defining a new password.
 */
public class UpdateRequestTask extends RequestTask<UserAccountObject, TransferObject> {	
	
	public UpdateRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, UserAccountObject input, TransferObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/user/update", cro, context);
	}

	@Override
	protected TransferObject responseService(UserAccountObject muao)  throws Exception {		
		JSONObject jsonObject = new JSONObject();
		muao.encode(jsonObject);
		return execPost(jsonObject);
	}

}
