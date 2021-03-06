package ch.uzh.csg.coinblesk.client.request;

import android.content.Context;
import net.minidev.json.JSONObject;
import ch.uzh.csg.coinblesk.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.coinblesk.client.util.Constants;
import ch.uzh.csg.mbps.responseobject.TransferObject;
import ch.uzh.csg.mbps.responseobject.UserAccountObject;

/**
 * This class sends a request to register the user with the inserted
 * information.
 */
public class SignUpRequestTask extends RequestTask<UserAccountObject, TransferObject> {

	public SignUpRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, UserAccountObject input, TransferObject output, Context context) {
		super(input, output, Constants.BASE_URI_SSL + "/user/create", cro, context);
	}

	@Override
	protected TransferObject responseService(UserAccountObject tro)  throws Exception {
		JSONObject jsonObject = new JSONObject();
		tro.encode(jsonObject);
		return execPost(jsonObject);
	}

}