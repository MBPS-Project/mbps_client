package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.TransferObject;
import ch.uzh.csg.mbps.responseobject.UserAccountObject;

/**
 * This class sends a request to register the user with the inserted
 * information.
 */
public class SignUpRequestTask extends RequestTask<UserAccountObject, TransferObject> {

	public SignUpRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, UserAccountObject input, TransferObject output, Context context) {
		super(input, output,BaseUriHandler.getInstance().getBaseUriSSL() + "/user/create", cro, context);
	}

	@Override
	protected TransferObject responseService(UserAccountObject tro)  throws Exception {
		JSONObject jsonObject = new JSONObject();
		tro.encode(jsonObject);
		return execPost(jsonObject);
	}

}