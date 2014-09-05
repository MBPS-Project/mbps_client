package ch.uzh.csg.mbps.client.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import ch.uzh.csg.mbps.client.CurrencyViewHandler;
import ch.uzh.csg.mbps.client.R;
import ch.uzh.csg.mbps.model.AbstractHistory;
import ch.uzh.csg.mbps.model.HistoryPayInTransaction;
import ch.uzh.csg.mbps.model.HistoryPayInTransactionUnverified;
import ch.uzh.csg.mbps.model.HistoryPayOutTransaction;
import ch.uzh.csg.mbps.model.HistoryTransaction;

public class HistoryTransactionFormatter {
	private static  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss", Locale.getDefault());

	/**
	 * Returns a String representation of the HistoryTransaction depending on
	 * its subtype in the proper language.
	 * 
	 * @param tx which shall be formatted
	 * @param context ApplicationContext to access String ressources
	 * @return String representation of HistoryTransaction
	 */
	public static Spanned formatHistoryTransaction(AbstractHistory tx, Context context){
		if(tx instanceof HistoryPayInTransaction) {
			return formatHistoryPayInTransaction((HistoryPayInTransaction) tx, context);
		} else if (tx instanceof HistoryPayInTransactionUnverified) {
			return formatHistoryPayInTransactionUnverified((HistoryPayInTransactionUnverified) tx, context);
		} else if (tx instanceof HistoryPayOutTransaction) {
			return formatHistoryPayOuTransaction((HistoryPayOutTransaction) tx, context);
		} else {
			return formatHistoryNormalTransaction((HistoryTransaction) tx, context);
		}
	}
	
	private static Spanned formatHistoryPayInTransaction(HistoryPayInTransaction tx, Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(tx.getTimestamp()));
		sb.append("<br>");
		sb.append(context.getResources().getString(R.string.history_payIn));
		sb.append("<br>");
		sb.append("<b>"+CurrencyViewHandler.formatBTCAsString(tx.getAmount(), context)+"</b>");
		Spanned text = Html.fromHtml(sb.toString());
		return text;
	}
	
	private static Spanned formatHistoryPayInTransactionUnverified(HistoryPayInTransactionUnverified tx, Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(tx.getTimestamp()));
		sb.append("<br>");
		sb.append(context.getResources().getString(R.string.history_payInUn));
		sb.append("<br>");
		sb.append("<b>"+CurrencyViewHandler.formatBTCAsString(tx.getAmount(), context)+"</b>");
		Spanned text = Html.fromHtml(sb.toString());
		return text;
	}
	
	private static Spanned formatHistoryPayOuTransaction(HistoryPayOutTransaction tx, Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(tx.getTimestamp()));
		sb.append("<br>");
		sb.append(context.getResources().getString(R.string.history_payOut) + " ");
		sb.append("<b>"+CurrencyViewHandler.formatBTCAsString(tx.getAmount(), context)+"</b>");
		sb.append("<br>");
		sb.append(context.getResources().getString(R.string.history_payOutTo) + " ");
		sb.append(tx.getBtcAddress());
		Spanned text = Html.fromHtml(sb.toString());
		return text;
	}
	
	private static Spanned formatHistoryNormalTransaction(HistoryTransaction tx, Context context) {
		String username = ClientController.getUsersUsername();
		String serverName = ClientController.getUsersServer();
		
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(tx.getTimestamp()));
		sb.append("<br>");
		if(!username.equals(tx.getBuyer())){
			sb.append(context.getResources().getString(R.string.history_from) + " ");
			sb.append("<b>"+tx.getBuyer()+"</b>");
			if(!serverName.equals(tx.getBuyerServer()))
				sb.append(" ("+tx.getBuyerServer()+")");
		}
		if(!username.equals(tx.getSeller())){
			sb.append(", " + context.getResources().getString(R.string.history_to) + " ");
			sb.append("<b>"+tx.getSeller()+"</b>");
			if(!serverName.equals(tx.getSellerSever()))
				sb.append(" ("+tx.getSellerSever()+")");
		}
		sb.append("<br>");
		sb.append(context.getResources().getString(R.string.history_amount) + " ");
		sb.append("<b>"+CurrencyViewHandler.formatBTCAsString(tx.getAmount(), context)+"<b>");
		if(tx.getInputCurrency() != null) {
			sb.append(" (" + tx.getInputCurrencyAmount()  + " " + tx.getInputCurrency() + ")");
		}
		return null;
	}
}
