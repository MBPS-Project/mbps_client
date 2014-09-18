package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.PayOutRulesTransferObject;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to store the defined payout rules.
 */
public class PayOutRuleRequestTask extends RequestTask<PayOutRulesTransferObject, TransferObject> {

	public PayOutRuleRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, PayOutRulesTransferObject input, TransferObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/rules/create", cro, context);
	}

	@Override
	protected TransferObject responseService(PayOutRulesTransferObject obj) throws Exception {
		JSONObject jsonObject = new JSONObject();
		obj.encode(jsonObject);
		return execPost(jsonObject);
	}
}
