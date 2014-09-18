package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to reset the password. The user gets a link to
 * reset the password by email if the request was successful.
 */
public class PasswordResetRequestTask extends RequestTask<TransferObject, TransferObject> {
	
	public PasswordResetRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, TransferObject input, TransferObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/user/resetPasswordRequest", cro, context);
	}

	@Override
	protected TransferObject responseService(TransferObject tro) throws Exception {
		JSONObject jsonObject = new JSONObject();
		tro.encode(jsonObject);
		return execResetPassword(jsonObject);
	}

}
