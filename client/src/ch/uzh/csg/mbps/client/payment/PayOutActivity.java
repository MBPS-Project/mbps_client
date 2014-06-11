package ch.uzh.csg.mbps.client.payment;

import java.math.BigDecimal;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ch.uzh.csg.mbps.client.AbstractAsyncActivity;
import ch.uzh.csg.mbps.client.CurrencyViewHandler;
import ch.uzh.csg.mbps.client.IAsyncTaskCompleteListener;
import ch.uzh.csg.mbps.client.R;
import ch.uzh.csg.mbps.client.model.PayOutTransaction;
import ch.uzh.csg.mbps.client.request.ExchangeRateRequestTask;
import ch.uzh.csg.mbps.client.request.PayOutRequestTask;
import ch.uzh.csg.mbps.client.request.RequestTask;
import ch.uzh.csg.mbps.client.util.ClientController;
import ch.uzh.csg.mbps.client.util.Constants;
import ch.uzh.csg.mbps.client.util.CurrencyFormatter;
import ch.uzh.csg.mbps.responseobject.CustomResponseObject;
import ch.uzh.csg.mbps.responseobject.CustomResponseObject.Type;

/**
 * This class is a view to send bitcoins from the system to the inserted
 * bitcoin-address.
 */
public class PayOutActivity extends AbstractAsyncActivity implements IAsyncTaskCompleteListener<CustomResponseObject> {
	public static BigDecimal exchangeRate;
	private BigDecimal payOutAmount;
	private MenuItem menuWarning;
	private Button acceptBtn;
	private Button allBtn;
	private Button scanQRButton;
	private EditText payoutAmountEditText;
	private EditText payoutAddress;
	
	private TextView btcBalance;
	private TextView chfBalance;
	private TextView chfAmount;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_out);
		setScreenOrientation();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		btcBalance = (TextView) findViewById(R.id.payOut_Balance);
		CurrencyViewHandler.setBTC(btcBalance, ClientController.getUser().getBalance(), getApplicationContext());
		chfBalance = (TextView) findViewById(R.id.payOut_BalanceCHF);
		CurrencyViewHandler.clearTextView(chfBalance);
		
		chfAmount = (TextView) findViewById(R.id.payOut_AmountCHF);
		
		acceptBtn = (Button) findViewById(R.id.payOut_payOutButton);
		allBtn = (Button) findViewById(R.id.payout_ButtonAll);
		payoutAmountEditText = (EditText) findViewById(R.id.payOut_Amount);
		payoutAddress = (EditText) findViewById(R.id.payOut_Address);
		scanQRButton = (Button) findViewById(R.id.payout_ButtonScanQR );
		
		initClickListener();
		
		checkOnlineModeAndProceed();
	}

    @Override
    public void onResume(){
    	super.onResume();
    	checkOnlineModeAndProceed();
    	invalidateOptionsMenu();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pay_out, menu);
		return true;
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
    
    private void initClickListener() {

		acceptBtn.setOnClickListener(new View.OnClickListener() {
	  		public void onClick(View v) {
				if (ClientController.isOnline()) {
					launchPayOutRequest();
				}
	  		}
	  	});  
		
		scanQRButton.setOnClickListener(new View.OnClickListener() {
	  		public void onClick(View v) {
	  			scanQRAction();
	  		}

	  	});  
		
	  	allBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BigDecimal amount = CurrencyViewHandler.getBTCAmountInDefinedUnit(ClientController.getUser().getBalance(), getApplicationContext());
				payoutAmountEditText.setText(CurrencyFormatter.formatBTC(amount));
				payOutAmount = ClientController.getUser().getBalance();
			}
		});  
		
	  	payoutAmountEditText.addTextChangedListener(new TextWatcher() {
	  		/*
	  		 * Listener to update the inserted amount of bitcoin
	  		 * and the exchange rate to swiss currency. 
	  		 */
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					BigDecimal tempBTC = CurrencyFormatter.getBigDecimalBtc(payoutAmountEditText.getText().toString());
					payOutAmount = CurrencyViewHandler.getBitcoinsRespectingUnit(tempBTC, getApplicationContext());
					
					CurrencyViewHandler.setToCHF(chfAmount, exchangeRate, payOutAmount);
				} catch (NumberFormatException e) {
					CurrencyViewHandler.setToCHF(chfAmount, BigDecimal.ZERO, payOutAmount);
				}
			}

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {	
			}
		});	  	
	}
    
    
	private void scanQRAction() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}
    
	private void launchPayOutRequest() {
		if (!payoutAmountEditText.getText().toString().isEmpty() && !payoutAddress.getText().toString().isEmpty()) {
			showLoadingProgressDialog();
			BigDecimal tempBTC = CurrencyFormatter.getBigDecimalBtc(payoutAmountEditText.getText().toString());
			payOutAmount = CurrencyViewHandler.getBitcoinsRespectingUnit(tempBTC, getApplicationContext());
			
			PayOutTransaction pot = new PayOutTransaction(ClientController.getUser().getId(), payOutAmount, payoutAddress.getText().toString());
			RequestTask payOut = new PayOutRequestTask(this, pot);
			payOut.execute();
		} else {
			displayResponse(getResources().getString(R.string.fill_necessary_fields));
		}
	}
    
	private void checkOnlineModeAndProceed() {
		if (ClientController.isOnline()) {
			launchExchangeRateRequest();
		} else {
			acceptBtn.setEnabled(false);
		}
	}
	
	private void launchExchangeRateRequest(){
		showLoadingProgressDialog();
		RequestTask getExchangeRate = new ExchangeRateRequestTask(this);
		getExchangeRate.execute();
	}
	
	public void onTaskComplete(CustomResponseObject response) {
		if (response.isSuccessful()) {
			if (response.getType() == Type.EXCHANGE_RATE) {
				exchangeRate = new BigDecimal(response.getMessage());
				CurrencyViewHandler.setExchangeRateView(exchangeRate, (TextView) findViewById(R.id.payout_exchangeRate));
				CurrencyViewHandler.setToCHF(chfBalance, exchangeRate, ClientController.getUser().getBalance());
			} else {
				showDialog("Pay out", getResources().getIdentifier("ic_payment_succeeded", "drawable", getPackageName()),response.getMessage());
				try {
					ClientController.setUserBalance(ClientController.getUser().getBalance().subtract(payOutAmount));
				} catch (Exception e) {
					//TODO jeton: handle exception!
				}
				CurrencyViewHandler.setBTC(btcBalance, ClientController.getUser().getBalance(), getApplicationContext());
				CurrencyViewHandler.setToCHF(chfBalance, exchangeRate, ClientController.getUser().getBalance());
				chfAmount.setText("");
				payoutAmountEditText.setText("");
			}
		} else if (response.getMessage().equals(Constants.REST_CLIENT_ERROR)) {
			exchangeRate = BigDecimal.ZERO;
			reload(getIntent());
			invalidateOptionsMenu();
		} else {
			exchangeRate = BigDecimal.ZERO;
			displayResponse(response.getMessage());
			chfBalance.setText("");
		}
		dismissProgressDialog();
	}
	
	/**
	 * Catches Result from Barcode-Scanner and sets PayOut address.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
			String result = scanResult.getContents();
			try {
				if (result.substring(0, 8).equalsIgnoreCase("bitcoin:")){
					String btcAddress = result.substring(8, 42);
					payoutAddress.setText(btcAddress);
				}
			} catch (Exception e) {
				displayResponse(getResources().getString(R.string.payOut_NoQRCode));
			}
		  }
		}
}
