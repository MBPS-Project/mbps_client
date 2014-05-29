package ch.uzh.csg.mbps.client.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SignedObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import ch.uzh.csg.mbps.client.AbstractAsyncActivity;
import ch.uzh.csg.mbps.client.CurrencyViewHandler;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.MainActivity;
import ch.uzh.csg.mbps.client.R;
import ch.uzh.csg.mbps.client.request.ExchangeRateRequestTask;
import ch.uzh.csg.mbps.client.request.RequestTask;
import ch.uzh.csg.mbps.client.request.TransactionRequestTask;
import ch.uzh.csg.mbps.client.util.ClientController;
import ch.uzh.csg.mbps.client.util.Constants;
import ch.uzh.csg.mbps.client.util.CurrencyFormatter;
import ch.uzh.csg.mbps.model.Transaction;
import ch.uzh.csg.mbps.responseobject.CreateTransactionTransferObject;
import ch.uzh.csg.mbps.responseobject.CustomResponseObject;
import ch.uzh.csg.mbps.responseobject.CustomResponseObject.Type;

/**
 * This is the UI to receive a payment - i.e. to be the seller in a transaction or to actively send bitcoins by NFC.
 */
public class SendPaymentActivity extends AbstractAsyncActivity implements IAsyncTaskCompleteListener<CustomResponseObject> {
	private MenuItem menuWarning;
	private String[] currencies = { "Swiss", "Bitcoins" };
	private int arr_images[] = { R.drawable.ic_swiss_currency_light, R.drawable.ic_bitcoin_currency_light };
	protected CalculatorDialog calculatorDialogFragment;
	protected static BigDecimal amountBTC = BigDecimal.ZERO;
	protected static BigDecimal inputUnitValue = BigDecimal.ZERO;
	private BigDecimal exchangeRate;
	private EditText sendAmount;
	private TextView descriptionOfInputUnit;
	private SharedPreferences settings;
	AnimationDrawable nfcActivityAnimation;
	private Button sendButton;
	private EditText receiverUsernameEditText;
	
	protected static final String INPUT_UNIT_CHF = "CHF";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_payment);
		setScreenOrientation();
		
		Constants.inputUnit = INPUT_UNIT_CHF;
		getActionBar().setDisplayHomeAsUpEnabled(true);
		exchangeRate = BigDecimal.ZERO;
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		

		launchRequest();
		setUpGui();
		refreshCurrencyTextViews();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(ch.uzh.csg.mbps.client.R.menu.send_payment, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menuWarning = menu.findItem(R.id.action_warning);
		invalidateOptionsMenu();
		return true;
	}

	@Override
	public void invalidateOptionsMenu() {
		if (menuWarning != null) {
			if (ClientController.isOnline()) {
				menuWarning.setVisible(false);
			} else {
				menuWarning.setVisible(true);
			}
		}
	}

	protected void launchRequest() {
		if (ClientController.isOnline()) {
			showLoadingProgressDialog();
			RequestTask getExchangeRate = new ExchangeRateRequestTask(this);
			getExchangeRate.execute();
		}
	}
	
	protected void launchTransactionRequest(CreateTransactionTransferObject ctto) {
		if (ClientController.isOnline()) {
			showLoadingProgressDialog();
			RequestTask transactionRequest = new TransactionRequestTask(this, ctto);
			transactionRequest.execute();
		}
	}
	
	
	public void onTaskComplete(CustomResponseObject response) {
		CurrencyViewHandler.clearTextView((TextView) findViewById(R.id.sendPayment_exchangeRate));	
		if (response.isSuccessful()) {
			if(response.getType().equals(Type.EXCHANGE_RATE)){
				exchangeRate = new BigDecimal(response.getMessage());
				CurrencyViewHandler.setExchangeRateView(exchangeRate, (TextView) findViewById(R.id.sendPayment_exchangeRate));
				CurrencyViewHandler.setBTC((TextView) findViewById(R.id.sendPayment_balance), ClientController.getUser().getBalance(), getBaseContext());
				TextView balanceTv = (TextView) findViewById(R.id.sendPayment_balance);
				balanceTv.append(" (" + CurrencyViewHandler.amountInCHF(exchangeRate, ClientController.getUser().getBalance()) + ")");
			}
			else if (response.getType().equals(Type.OTHER)){
				//TODO simon: show successful payment notification
			}
		} else if(response.getMessage().equals(Constants.REST_CLIENT_ERROR)){
			displayResponse(getResources().getString(R.string.no_connection_server));
			finish();
			launchActivity(this, MainActivity.class);
		}
		else{
			displayResponse(response.getMessage());
		}
		dismissProgressDialog();
	}

	private void refreshCurrencyTextViews() {
		amountBTC = BigDecimal.ZERO;
		if (Constants.inputUnit.equals(INPUT_UNIT_CHF)) {
			try {
				BigDecimal amountChf = CurrencyFormatter.getBigDecimalChf(sendAmount.getText().toString());
				inputUnitValue = amountChf;
				amountBTC = CurrencyViewHandler.getBitcoinExchangeValue(exchangeRate, amountChf);
				CurrencyViewHandler.setBTC((TextView) findViewById(R.id.sendPayment_CHFinBTC), amountBTC, getApplicationContext());
			} catch (NumberFormatException e) {
				CurrencyViewHandler.setBTC((TextView) findViewById(R.id.sendPayment_CHFinBTC), BigDecimal.ZERO, getApplicationContext());
			}
		} else {
			try{
				BigDecimal tempBTC = CurrencyFormatter.getBigDecimalBtc(sendAmount.getText().toString());
				inputUnitValue = BigDecimal.ZERO;
				amountBTC = CurrencyViewHandler.getBitcoinsRespectingUnit(tempBTC, getApplicationContext());
				CurrencyViewHandler.setToCHF((TextView) findViewById(R.id.sendPayment_CHFinBTC), exchangeRate, amountBTC);
			} catch(NumberFormatException e) {
				CurrencyViewHandler.setToCHF((TextView) findViewById(R.id.sendPayment_CHFinBTC), exchangeRate, amountBTC);
			}
		}
		
		//Check if the user defined a fee on the received amount of bitcoin.
		if(settings.getBoolean("include_fee", false)){
			String percentageStr = settings.getString("fee_amount", "pref_fee_amount");
			try{
				double percentage = 1 + ((double) Integer.valueOf(percentageStr))/100;
				amountBTC = amountBTC.multiply(new BigDecimal(percentage)).setScale(Constants.SCALE_BTC, RoundingMode.HALF_UP) ;
				CurrencyViewHandler.setBTC((TextView) findViewById(R.id.sendPayment_BTCIncFee), amountBTC, getApplicationContext());
				((TextView)findViewById(R.id.sendPayment_BTCIncFeeText)).setText("(" + getResources().getString(R.string.receivePayment_fee) + " " +  percentageStr +"%)");				
			} catch(NumberFormatException e){
				CurrencyViewHandler.setBTC((TextView) findViewById(R.id.sendPayment_BTCIncFee), BigDecimal.ZERO, getApplicationContext());
			}
		}
	}

	private void openCalculatorDialog() {
		calculatorDialogFragment = new CalculatorDialog(this);
		calculatorDialogFragment.show();
		calculatorDialogFragment.setOnDismissListener(new OnDismissListener() {
			
			public void onDismiss(DialogInterface dialog) {
				sendAmount.setText(Constants.inputValueCalculator.toString());
				refreshCurrencyTextViews();
			}
		});
	}

	private class MyAdapter extends ArrayAdapter<String> {
		
		public MyAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.spinner_currency, parent, false);
			TextView label = (TextView) row.findViewById(R.id.textView_currency);
			label.setText(currencies[position]);
			
			ImageView icon = (ImageView) row.findViewById(R.id.image_currency);
			icon.setImageResource(arr_images[position]);

			return row;
		}
	}
	
	private OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			if (pos == 0)
				Constants.inputUnit = INPUT_UNIT_CHF;
			else
				Constants.inputUnit = CurrencyViewHandler.getBitcoinUnit(getApplicationContext());
			
			descriptionOfInputUnit.setText(Constants.inputUnit);
			refreshCurrencyTextViews();
		}

		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	};
	
	private void setUpGui(){
		sendAmount = (EditText) findViewById(R.id.sendPayment_amountText);
		sendAmount.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				openCalculatorDialog();			
			}
		});
		sendAmount.setFocusable(false);
		
		descriptionOfInputUnit = (TextView)findViewById(R.id.sendPayment_enterAmountIn);
		
		Spinner spinner = (Spinner) findViewById(R.id.sendPayment_currencySpinner);
		spinner.setAdapter(new MyAdapter(this, R.layout.spinner_currency, currencies));
		spinner.setOnItemSelectedListener(spinnerListener);
		spinner.setSelection(0);
		
		receiverUsernameEditText = (EditText)findViewById(R.id.sendPayment_enterReceiver);
		
		sendButton = (Button)findViewById(R.id.sendPayment_sendButton);
		sendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				createTransaction();
			}
		});
	}
	
	public void createTransaction(){
		//TODO simon: refactor after jeton updated transaction handling
		Transaction transaction = new Transaction();
		transaction.setAmount(amountBTC);
		transaction.setAmountInputCurrency(inputUnitValue);
		transaction.setBuyerUsername(ClientController.getUser().getUsername());
		//TODO: simon: get receiver-username from edittext or addressbook
		String receiverUsername = receiverUsernameEditText.getText().toString();
		
		transaction.setSellerUsername(receiverUsername);
		transaction.setInputCurrency(Constants.inputUnit);
		SignedObject signedTransactionBuyer;
		try {
			displayResponse("button clicked");
			//TODO simon: adapt TransactionHandler etc
			signedTransactionBuyer = TransactionHandler.signPayment(transaction);
			CreateTransactionTransferObject ctto = new CreateTransactionTransferObject(null, signedTransactionBuyer);
			launchTransactionRequest(ctto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}