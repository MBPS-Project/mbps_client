package ch.uzh.csg.mbps.client.request;

import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to delete the defined payout rules.
 */
public class PayOutRuleResetRequestTask extends RequestTask<TransferObject, TransferObject> {
	
	public PayOutRuleResetRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, TransferObject input, TransferObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/rules/reset", cro, context);
	}

	@Override
	protected TransferObject responseService(TransferObject to) {
		return execGet();
	}

}
