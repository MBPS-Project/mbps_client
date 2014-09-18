package ch.uzh.csg.mbps.client.request;

import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to get the exchange rate BTC to CHF.
 */
public class ExchangeRateRequestTask extends RequestTask<TransferObject, TransferObject> {
	
	public ExchangeRateRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, TransferObject input, TransferObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/transaction/exchange-rate/", cro, context);
	}

	@Override
	protected TransferObject responseService(TransferObject to) {
		return execGet();
	}
}
