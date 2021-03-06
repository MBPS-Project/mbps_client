package ch.uzh.csg.coinblesk.client.request;

import android.content.Context;
import net.minidev.json.JSONObject;
import ch.uzh.csg.coinblesk.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.coinblesk.client.util.Constants;
import ch.uzh.csg.mbps.responseobject.PayOutTransactionObject;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to payout a defined amount of bitcoins to the
 * inserted bitcoin-address.
 */
public class PayOutRequestTask extends RequestTask<PayOutTransactionObject, TransferObject> {
		
	public PayOutRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, PayOutTransactionObject input, TransferObject output, Context context) {
		super(input, output, Constants.BASE_URI_SSL + "/transaction/payOut", cro, context);
	}

	@Override
	protected TransferObject responseService(PayOutTransactionObject tro)  throws Exception {
		JSONObject jsonObject = new JSONObject();
		tro.encode(jsonObject);
		return execPost(jsonObject);
	}

}
