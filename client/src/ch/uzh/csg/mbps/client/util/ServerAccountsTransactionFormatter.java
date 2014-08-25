package ch.uzh.csg.mbps.client.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import ch.uzh.csg.mbps.client.CurrencyViewHandler;
import ch.uzh.csg.mbps.client.R;
import ch.uzh.csg.mbps.model.ServerAccount;

public class ServerAccountsTransactionFormatter {
	private static  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss", Locale.getDefault());

	/**
	 * Returns a String representation of server accounts.
	 * @param account
	 * @param context
	 * @return
	 */
	public static String formatHistoryTransaction(ServerAccount account, Context context){
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(account.getUrl()));
		sb.append(" " + context.getResources().getString(R.string.relation_trust) + " ");
		sb.append(account.getTrustLevel());
		sb.append(", " + context.getResources().getString(R.string.relation_active) + " ");
		sb.append(CurrencyViewHandler.formatBTCAsString(account.getActiveBalance(), context));
		sb.append(", " + context.getResources().getString(R.string.relation_limit) + " ");
		sb.append(CurrencyViewHandler.formatBTCAsString(account.getBalanceLimit(), context));
		
		return sb.toString();
	}
	
}
