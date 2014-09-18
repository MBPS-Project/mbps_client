package ch.uzh.csg.mbps.client.request;

import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to sign out the authenticated user.
 */
public class SignOutRequestTask extends RequestTask<TransferObject, TransferObject> {
	
	public SignOutRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, TransferObject input, TransferObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/spring_security_logout", cro, context);
	}

	@Override
	protected TransferObject responseService(TransferObject tro)  throws Exception {
		return execLogout();
	}
	
}