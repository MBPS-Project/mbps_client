package ch.uzh.csg.mbps.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import ch.uzh.csg.mbps.client.request.ServerAccountRequestTask;
import ch.uzh.csg.mbps.client.util.ClientController;
import ch.uzh.csg.mbps.client.util.ServerAccountsTransactionFormatter;
import ch.uzh.csg.mbps.client.util.TimeHandler;
import ch.uzh.csg.mbps.model.ServerAccount;
import ch.uzh.csg.mbps.responseobject.ServerAccountTransferObject;
import ch.uzh.csg.mbps.responseobject.ServerAccountsRequestObject;

public class ServerRelationActivity extends AbstractAsyncActivity {
	private ServerAccountTransferObject sato;
	private int urlResultsPerPage;
	private int urlPage = 0;
	
	private MenuItem menuWarning;
	private MenuItem offlineMode;
	private MenuItem sessionCountdownMenuItem;
	private MenuItem sessionRefreshMenuItem;
	private TextView sessionCountdown;
	private CountDownTimer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_relation);
		
		setScreenOrientation();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public void onResume(){
    	super.onResume();
    	invalidateOptionsMenu();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		initializeMenuItems(menu);
		invalidateOptionsMenu();
		return true;
	}

	protected void initializeMenuItems(Menu menu){
		menuWarning = menu.findItem(R.id.action_warning);
		offlineMode = menu.findItem(R.id.menu_offlineMode);
		TextView offlineModeTV = (TextView) offlineMode.getActionView();
		offlineModeTV.setText(getResources().getString(R.string.menu_offlineModeText));
		
		menuWarning.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				return false;
			}
		});

		//setup timer
		sessionCountdownMenuItem = menu.findItem(R.id.menu_session_countdown);
		sessionCountdown = (TextView) sessionCountdownMenuItem.getActionView();
		sessionRefreshMenuItem = menu.findItem(R.id.menu_refresh_session);
		sessionRefreshMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				return false;
			}
		});
	}
	
	@Override
	public void invalidateOptionsMenu() {
		if(menuWarning != null){
			if(ClientController.isOnline()) {
				menuWarning.setVisible(false);
				offlineMode.setVisible(false);
				sessionCountdownMenuItem.setVisible(true);
				sessionRefreshMenuItem.setVisible(true);
			} else {
				menuWarning.setVisible(true);
				offlineMode.setVisible(true);
				sessionCountdownMenuItem.setVisible(false);
				sessionRefreshMenuItem.setVisible(false);
			}
		}
	}
	
	protected void startTimer(long duration, long interval) {
		if(timer != null){
			timer.cancel();
		}
		timer = new CountDownTimer(duration, interval) {

			@Override
			public void onFinish() {
				//Session Timeout is already handled by TimeHandler
			}

			@Override
			public void onTick(long millisecondsLeft) {
				int secondsLeft = (int) Math.round((millisecondsLeft / (double) 1000));
				sessionCountdown.setText(getResources().getString(R.string.menu_sessionCountdown) + " " + TimeHandler.getInstance().formatCountdown(secondsLeft));
			}
		};

		timer.start();
	}
	
	/**
	 * Makes a server call to retrieve server account items. The items are loaded
	 * only per page (based on the page size defined on the server). If a
	 * parameter is negative, the given transaction type is not retrieved.
	 */
	private void launchServerAccounts(int urlPage) {
		if (ClientController.isOnline()) {
			showLoadingProgressDialog();
			
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.relation_linearLayout);
			if (linearLayout.getChildCount() > 0)
				linearLayout.removeAllViews();
			
			this.urlPage = urlPage;

			ServerAccountsRequestObject request = new ServerAccountsRequestObject();
			request.setUrlPage(urlPage);
			
			ServerAccountRequestTask getServerAccounts = new ServerAccountRequestTask(new IAsyncTaskCompleteListener<ServerAccountTransferObject>() {
				@Override
				public void onTaskComplete(ServerAccountTransferObject response) {
					dismissProgressDialog();
					
					if(response.isSuccessful()) {
						if(ClientController.isOnline()){
							startTimer(TimeHandler.getInstance().getRemainingTime(), 1000);
						}
						
						sato = response;
						if (sato != null) {
							write();
						}
					} else {
						sato = null;
						if (response.getMessage() == null) {
							reload(getIntent());
							invalidateOptionsMenu();
						}
					}
				}

			}, request, new ServerAccountTransferObject(), getApplicationContext());
			getServerAccounts.execute();
		}
	}
	
	private void write() {
		ArrayList<ServerAccount> accounts = new ArrayList<ServerAccount>();
		
		if (sato == null)
			return;

		Collections.sort(accounts, new CustomComparator());
		createViews(accounts);
	}
	
	private void createViews(ArrayList<ServerAccount> accounts) {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.relation_linearLayout);
		
		if (accounts.size() == 0) {
			TextView tv = new TextView(getApplicationContext());
			tv.setGravity(Gravity.LEFT);
            tv.setText(R.string.relation_activity_no_text);
            tv.setTextColor(Color.BLACK);
			linearLayout.addView(tv);
			return;
		}
		for(int i = accounts.size()-1; i >= 0; i--){
			TextView tv = new TextView(getApplicationContext());
			tv.setGravity(Gravity.LEFT);
			tv.setText(ServerAccountsTransactionFormatter.formatHistoryTransaction(accounts.get(i), getApplicationContext()));
            tv.setPadding(0, 5, 0, 5);
            tv.setTextColor(Color.BLACK);
            if(i % 2 == 0){
				tv.setBackgroundColor(Color.LTGRAY);
			}
			linearLayout.addView(tv);
			//TB: add a horizontal separator
			View ruler = new View(getApplicationContext()); 
			ruler.setBackgroundColor(Color.DKGRAY);
			linearLayout.addView(ruler);
		}
		
		createNavigationButtons(linearLayout);
	}

	private void createNavigationButtons(LinearLayout linearLayout) {
		LinearLayout innerLayout = new LinearLayout(getApplicationContext());
		innerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		innerLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutParams params = new LayoutParams(120, LayoutParams.WRAP_CONTENT);
		params.weight = 1.0f;
		params.gravity = Gravity.LEFT;
		params.setMargins(30, 0, 0, 15);
		createPreviousButton(innerLayout, params);
		
        LayoutParams params2 = new LayoutParams(120, LayoutParams.WRAP_CONTENT);
        params2.weight = 1.0f;
		params2.gravity = Gravity.RIGHT;
		params.setMargins(0, 0, 30, 15);
		createNextButton(innerLayout, params2);
		
        linearLayout.addView(innerLayout);
	}

	private void createPreviousButton(LinearLayout innerLayout, LayoutParams params) {
		Button previous = new Button(getApplicationContext());
        previous.setText("<< Previous");
        previous.setTextColor(Color.BLACK);
        previous.setBackgroundResource(android.R.drawable.btn_default);
        previous.setLayoutParams(params);
        previous.setTextColor(Color.BLACK);
        previous.setBackgroundColor(Color.LTGRAY);

    	if (urlPage == 0) {
			previous.setEnabled(false);
			previous.setVisibility(View.INVISIBLE);
		} else {
			previous.setVisibility(View.VISIBLE);
			previous.setEnabled(true);
			previous.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					launchServerAccounts(urlPage-1);				}
			});
		}
        innerLayout.addView(previous);
	}
	
	private void createNextButton(LinearLayout innerLayout, LayoutParams params2) {
		Button next = new Button(getApplicationContext());
        next.setText("Next >>");
        next.setTextColor(Color.BLACK);
        next.setBackgroundResource(android.R.drawable.btn_default);
        next.setLayoutParams(params2);
        next.setTextColor(Color.BLACK);
        next.setBackgroundColor(Color.LTGRAY);
    
 		if (((double) sato.getNumberOfSA()) / urlResultsPerPage > urlPage+1) {
			next.setEnabled(true);
			next.setVisibility(View.VISIBLE);
			next.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					launchServerAccounts(urlPage+1);
				}
			});
		} else {
			next.setEnabled(false);
			next.setVisibility(View.INVISIBLE);
		}
        innerLayout.addView(next);
	}

	private class CustomComparator implements Comparator<ServerAccount> {
	    public int compare(ServerAccount o1, ServerAccount o2) {
	        return o1.getUrl().compareTo(o2.getUrl());
	    }
	}
}