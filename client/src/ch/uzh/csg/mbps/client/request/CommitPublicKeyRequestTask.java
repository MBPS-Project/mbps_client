package ch.uzh.csg.mbps.client.request;

import net.minidev.json.JSONObject;
import android.content.Context;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.util.BaseUriHandler;
import ch.uzh.csg.mbps.keys.CustomPublicKey;
import ch.uzh.csg.mbps.responseobject.CustomPublicKeyObject;
import ch.uzh.csg.mbps.responseobject.TransferObject;

/**
 * This class sends the request to commit a {@link CustomPublicKey} for the
 * account of the authenticated user.
 * 
 * @author Jeton Memeti
 * 
 */
public class CommitPublicKeyRequestTask extends RequestTask<CustomPublicKeyObject, TransferObject> {
	
	public CommitPublicKeyRequestTask(IAsyncTaskCompleteListener<TransferObject> cro, CustomPublicKeyObject input, CustomPublicKeyObject output, Context context) {
		super(input, output, BaseUriHandler.getInstance().getBaseUriSSL() + "/user/savePublicKey", cro, context);
	}

	@Override
	protected TransferObject responseService(CustomPublicKeyObject tro) throws Exception {
		JSONObject jsonObject = new JSONObject();
		tro.encode(jsonObject);
		return execPost(jsonObject);
	}

}
