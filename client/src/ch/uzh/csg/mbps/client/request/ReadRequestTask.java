package ch.uzh.csg.mbps.client.request;

import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.Constants;
import ch.uzh.csg.mbps.responseobject.ReadRequestObject;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to retrieve the user account information.
 */
public class ReadRequestTask extends RequestTask<TransferObject, ReadRequestObject> {
		
	public ReadRequestTask(IAsyncTaskCompleteListener<ReadRequestObject> cro, TransferObject input, ReadRequestObject output, Context context) {
		super(input, output, Constants.BASE_URI_SSL + "/user/afterLogin", cro, context);
	}

	@Override
	protected ReadRequestObject responseService(TransferObject to) {
		return execGet();
	}

}
