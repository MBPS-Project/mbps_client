package ch.uzh.csg.mbps.client.request;

import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.responseobject.MainRequestObject;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends a request to get the exchange rate BTC to CHF.
 */
public class MainActivityRequestTask extends RequestTask<TransferObject, MainRequestObject> {
	
	public MainActivityRequestTask(IAsyncTaskCompleteListener<MainRequestObject> cro, TransferObject input, MainRequestObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/user/mainActivityRequests", cro, context);
	}

	@Override
	protected MainRequestObject responseService(TransferObject tro) {
		return execGet();
	}
	
}
