package ch.uzh.csg.mbps.client.util;

import java.math.BigDecimal;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import ch.uzh.csg.mbps.client.CurrencyViewHandler;
import ch.uzh.csg.mbps.client.R;
import ch.uzh.csg.mbps.model.ServerAccount;

public class ServerAccountsTransactionFormatter {
	
	/**
	 * Returns a String representation of server accounts.
	 * @param account
	 * @param context
	 * @return
	 */
	public static Spanned formatServerTransaction(ServerAccount account, Context context){
		BigDecimal amount = account.getBalanceLimit().subtract(account.getActiveBalance().abs());
		String trust = getDescription(account.getTrustLevel(), context);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<b>"+account.getUrl()+"</b>");
		sb.append("<br>" + context.getResources().getString(R.string.relation_trust) + " ");
		sb.append("<b>"+trust+"</b>");
		sb.append("<br>" + context.getResources().getString(R.string.relation_active) + " ");
		sb.append("<b>" + CurrencyViewHandler.formatBTCAsString(amount, context)+"</b>");
		sb.append("<br>" + context.getResources().getString(R.string.relation_user_limit) + " ");
		sb.append("<b>"+CurrencyViewHandler.formatBTCAsString(account.getUserBalanceLimit(), context)+"</b>");
		
		Spanned text = Html.fromHtml(sb.toString());
		return text;
	}
	
	private static String getDescription(int level, Context context){
		switch(level){
		case 0:
			return context.getResources().getString(R.string.relation_no_trust);
		case 1:
			return context.getResources().getString(R.string.relation_hyprid);
		case 2:
			return context.getResources().getString(R.string.relation_high);
		default:
			return context.getResources().getString(R.string.relation_default); 
		}
	}
	
}
