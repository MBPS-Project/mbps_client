package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.TransactionObject;

/**
 * This class sends a transaction request to the server.
 */
public class TransactionRequestTask extends RequestTask<TransactionObject, TransactionObject> {

	public TransactionRequestTask(IAsyncTaskCompleteListener<TransactionObject> cro, TransactionObject input, TransactionObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/transaction/create", cro, context);
	}

	@Override
	protected TransactionObject responseService(TransactionObject tro)  throws Exception {
		JSONObject jsonObject = new JSONObject();
		tro.encode(jsonObject);
		return execPost(jsonObject);
	}
}
