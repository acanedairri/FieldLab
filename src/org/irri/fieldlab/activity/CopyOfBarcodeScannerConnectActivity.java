package org.irri.fieldlab.activity;



import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baracoda.android.baracodamanager.ConnectionState;
import com.baracoda.android.baracodamanager.IBaracodaReaderService;
import com.baracoda.android.baracodamanager.IBaracodaReaderServiceCallback;

public class CopyOfBarcodeScannerConnectActivity extends Activity {

	/**** Baracoda variables ****/
	// Messages received from the Baracoda service and forwarded to the application's handler
	// A new reader has been selected/connected
	private static final int MESSAGE_ON_READER_CHANGED = 1;
	// Reader connected/disconnected or connecting
	private static final int MESSAGE_ON_CONNECTION_STATE_CHANGED = 2;
	// Autoconnect activated/deactivated
	private static final int MESSAGE_ON_AUTOCONNECT_STATE_CHANGED = 3;
	// Data received
	private static final int MESSAGE_ON_DATA_READ = 4;

	// Debugging tag
	private static final String TAG = "BaracodaSDKExample";

	// Activity actions
	private static final int ACTION_REQUEST_SEARCH_DEVICES_RESULT = 1;
	private static final int ACTION_REQUEST_ENABLE_BT_RESULT = 2;

	// Return intent extra for SelectDeviceActivity
	public static String EXTRA_DEVICE = "device";

	// Bluetooth
	private BluetoothAdapter bluetoothAdapter = null;
	private boolean btStackAtStartup = false;

	// Baracoda service to which we connect
	private IBaracodaReaderService baracodaService = null;

	// Initial service state
	private boolean applicationAckAtStartup = false;
	private boolean rawModeAtStartup = false;
	String deviceConnected="";
	private Button btnConnectReader;
	private Button btnFindReader;
	private TextView connectionState;
	private TextView currentDeviceView;

	/********************************************/


	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcodescannerconnect);




		// baracoda

		//**************************************************************************************		
		final Bundle extras = getIntent().getExtras();
		Intent baracodaServiceIntent = new Intent (IBaracodaReaderService.class.getName());
		// Start the service if not already running
		super.startService(baracodaServiceIntent);
		// Bind to the service
		super.bindService(baracodaServiceIntent, this.baracodaServiceConnection, Context.BIND_AUTO_CREATE);

		// Set up the GUI

		// First, the search devices button
		btnFindReader = (Button)this.findViewById(R.id.btnFindReader);
		btnFindReader.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Get Bluetooth adapter
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (bluetoothAdapter == null) {
					// Bluetooth is not supported, display notification and finish this activity
					//Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_LONG).show();
					// TODO: A better way to handle devices that do not support bluetooth connections
					//	        super.finish();
				}

				//Check whether Bluetooth is turned on
				btStackAtStartup = bluetoothAdapter.isEnabled();
				//				if (btStackAtStartup == false) {
				//					startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
				//							SettingsActivity.ACTION_REQUEST_ENABLE_BT_RESULT);
				//				}

				// Also check if bluetooth is enabled here
				if (btStackAtStartup == false) {
					CopyOfBarcodeScannerConnectActivity.this.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
							CopyOfBarcodeScannerConnectActivity.ACTION_REQUEST_ENABLE_BT_RESULT);
				} else {
					// Start the Select Device activity from the BaracodaManager by specifying the right intent
					CopyOfBarcodeScannerConnectActivity.this.startActivityForResult(new Intent("com.baracoda.android.baracodamanager.SelectDeviceActivity"), CopyOfBarcodeScannerConnectActivity.ACTION_REQUEST_SEARCH_DEVICES_RESULT);
				}

			}
		});

		// The current device text field
		BluetoothDevice currentDevice = getCurrentDevice();


		// Connect/disconnect button
		btnConnectReader = (Button)this.findViewById(R.id.btnConnectReader);
		btnConnectReader.setText(R.string.connect);
		btnConnectReader.setEnabled(false);
		// If there is no current device, then disable the button

		//		if(extras.getBoolean("SCANNER_STATUS")){
		//			//connectButton.setEnabled(true);
		//			connectButton.setText("Disconnect");
		//		}
		// Connect / disconnect procedure
		btnConnectReader.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					// If we are connected or attempting to connect, then disconnect
					// WARNING: disconnecting during a connection attempt (ConnectionState.CONNECTING) can take up to 20s
					if(CopyOfBarcodeScannerConnectActivity.this.baracodaService.getConnectionState() != ConnectionState.DISCONNECTED  ) {
						// Disable autoconnect first (so that it does not reconnect the just disconnected reader)
						//SettingsActivity.this.setAutoconnect(false);
						CopyOfBarcodeScannerConnectActivity.this.baracodaService.disconnect();
					}
					// If we are disconnected then connect the current device

					else{
						CopyOfBarcodeScannerConnectActivity.this.baracodaService.disconnect();
						CopyOfBarcodeScannerConnectActivity.this.baracodaService.connect(CopyOfBarcodeScannerConnectActivity.this.getCurrentDevice().getAddress());
					}

				}
				catch(Exception ex) {
					//Toast.makeText(SettingsActivity.this, "Error Connecting" + ex.getMessage(), Toast.LENGTH_LONG).show();
					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
					if (!mBluetoothAdapter.isEnabled()) {

					}else{ 
						mBluetoothAdapter.disable(); 
					} 
					btnConnectReader.setEnabled(false);
				}
			}
		});


		connectionState = (TextView)this.findViewById(R.id.connection_state);
		if(extras.getInt("connectionState")==2){
			connectionState.setText(R.string.connected);
			btnConnectReader.setEnabled(true);
			btnConnectReader.setText("Disconnect");
		}else if(extras.getInt("connectionState")==0){
			connectionState.setText(R.string.disconnected);
		}

		currentDeviceView = (TextView)this.findViewById(R.id.current_device);
		currentDeviceView.setText(extras.getString("connectedDevice"));


	}




	private void init() {

	}



	// baracoda private method

	private BluetoothDevice getCurrentDevice () {

		try {
			if (this.baracodaService == null) {
				return null;
			}
			else {

				String address = this.baracodaService.getAddress();

				if (address == null) {
					return null;
				}
				else {
					return this.bluetoothAdapter.getRemoteDevice(address);
				}
			}

		}
		catch (RemoteException e) {
			return null;
		}
	}

	private void setAutoconnect(boolean autoconnect) {
		try {
			if(autoconnect == this.baracodaService.getAutoConnect()) {
				// No state change, just return
				return;
			}
		}
		catch(RemoteException e) {
			// In case of communication problems with the service, just return
			return;
		}

		//final Button connectButton = (Button)this.findViewById(R.id.button_connect_id);

		try {
			// If autoconnect is active and the current reader is not connected then disable the connect button
			if(autoconnect == true) {
				btnConnectReader.setEnabled(this.baracodaService.getConnectionState() == ConnectionState.CONNECTED);
			}

			this.baracodaService.setAutoConnect(autoconnect);
		}
		catch(RemoteException e) {}
	}

	private final ServiceConnection baracodaServiceConnection = new ServiceConnection () {
		public void onServiceConnected(ComponentName className, IBinder service) {
			CopyOfBarcodeScannerConnectActivity.this.baracodaService = IBaracodaReaderService.Stub.asInterface(service);

			try {
				CopyOfBarcodeScannerConnectActivity.this.baracodaService.registerCallback(CopyOfBarcodeScannerConnectActivity.this.baracodaServiceCallback);

				// We are not using application ack in this example, let's save its state
				applicationAckAtStartup = CopyOfBarcodeScannerConnectActivity.this.baracodaService.getApplicationAck();
				// Disable application ack
				CopyOfBarcodeScannerConnectActivity.this.baracodaService.setApplicationAck(false);

				// We are not using raw mode in this example, let's save its state
				rawModeAtStartup = CopyOfBarcodeScannerConnectActivity.this.baracodaService.getRawMode();
				// Disable raw mode
				CopyOfBarcodeScannerConnectActivity.this.baracodaService.setRawMode(false);
			}
			catch (RemoteException e) {
				// nothing to do here
				Toast.makeText(CopyOfBarcodeScannerConnectActivity.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			CopyOfBarcodeScannerConnectActivity.this.baracodaService = null;
		}
	};

	// Baracoda service callback stub
	private final IBaracodaReaderServiceCallback.Stub baracodaServiceCallback = new IBaracodaReaderServiceCallback.Stub() {
		@Override
		public void onAutoConnectChanged() throws RemoteException {
			// We need to switch to our handler (to avoid threading issues with GUI)
			// Autoconnect state has changed
			CopyOfBarcodeScannerConnectActivity.this.messageHandler.obtainMessage(CopyOfBarcodeScannerConnectActivity.MESSAGE_ON_AUTOCONNECT_STATE_CHANGED,
					0, 0).sendToTarget();
		}

		@Override
		public void onConnectionStateChanged(int connectionState) throws RemoteException {
			// We need to switch to our handler (to avoid threading issues with GUI)
			// Connection state has changed
			CopyOfBarcodeScannerConnectActivity.this.messageHandler.obtainMessage(CopyOfBarcodeScannerConnectActivity.MESSAGE_ON_CONNECTION_STATE_CHANGED,
					connectionState, 0).sendToTarget();

			Toast.makeText(getBaseContext(),"onConnectionStateChanged" , Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onDataRead(int dataType, String dataText) throws RemoteException {
			// We need to switch to our handler (to avoid threading issues with GUI)
			// Data has been received from the reader
			CopyOfBarcodeScannerConnectActivity.this.messageHandler.obtainMessage(
					CopyOfBarcodeScannerConnectActivity.MESSAGE_ON_DATA_READ,
					dataType,
					-1,
					dataText).sendToTarget();
		}

		@Override
		public void onImageRead(byte[] arg0) throws RemoteException {
		}

		@Override
		public void onPrefixChanged() throws RemoteException {
		}

		@Override
		public void onReaderChanged() throws RemoteException {
			// We need to switch to our handler (to avoid threading issues with GUI)
			CopyOfBarcodeScannerConnectActivity.this.messageHandler.obtainMessage(CopyOfBarcodeScannerConnectActivity.MESSAGE_ON_READER_CHANGED).sendToTarget();
		}

		@Override
		public void onSeparatorChanged() throws RemoteException {
		}

		@Override
		public void onSuffixChanged() throws RemoteException {
		}

		@Override
		public void onApplicationAckChanged() throws RemoteException {
		}

		@Override
		public void onRawModeChanged() throws RemoteException {
		}

		@Override
		public void onDataReadWithApplicationAck(int dataType, int id, String dataText) throws RemoteException {
		}

		@Override
		public void onRawDataRead(byte[] buffer) throws RemoteException {
		}
	};

	// This class manages events received from the Baracoda service
	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CopyOfBarcodeScannerConnectActivity.MESSAGE_ON_READER_CHANGED:
				// Current reader has changed, update the text field


				try {
					currentDeviceView.setText(String.format("%s, %s", CopyOfBarcodeScannerConnectActivity.this.baracodaService.getName(),
							CopyOfBarcodeScannerConnectActivity.this.baracodaService.getAddress()));

					// Enable the button and checkbox as now we have an active device
					//final Button connectButton = (Button)SettingsActivity.this.findViewById(R.id.button_connect_id);
					btnConnectReader.setEnabled(true);
					//					final CheckBox autoconnectCheckbox = (CheckBox)SettingsActivity.this.findViewById(R.id.autoconnect_id);
					//					autoconnectCheckbox.setEnabled(true);

				}
				catch(RemoteException ex) {
					currentDeviceView.setText("Error while updating the current reader information.");
				}
				break;

			case CopyOfBarcodeScannerConnectActivity.MESSAGE_ON_CONNECTION_STATE_CHANGED:
				// Connection state has changed, update the text field
				//				final TextView connectionStateView = (TextView)SettingsActivity.this.findViewById(R.id.connection_state);
				//				 connectButton = (Button)SettingsActivity.this.findViewById(R.id.button_connect_id);


				if(msg.arg1 == ConnectionState.CONNECTED) {
					connectionState.setText(R.string.connected);
					// Change the button text to "Disconnect"
					btnConnectReader.setText(R.string.disconnect);

					try {
						// Autoconnect is active, activate the connect button
						if(CopyOfBarcodeScannerConnectActivity.this.baracodaService.getAutoConnect() == true) {
							btnConnectReader.setEnabled(true);
							//							setResult(RESULT_OK, data);
							Toast.makeText(getBaseContext(),"Handler Connected " +currentDeviceView.getText() + "  "+R.string.connected , Toast.LENGTH_SHORT).show();

						}
					}
					catch(RemoteException e) {}
				}
				else if(msg.arg1 == ConnectionState.CONNECTING) {
					connectionState.setText(R.string.connecting);
					btnConnectReader.setText(R.string.disconnect);
					//					Toast.makeText(getBaseContext(),"Handler Connecting "+currentDeviceView.getText() + "  "+R.string.connecting, Toast.LENGTH_SHORT).show();
				}
				else if(msg.arg1 == ConnectionState.DISCONNECTED) {
					connectionState.setText(R.string.disconnected);
					// Change the button text to "Connect"
					btnConnectReader.setText(R.string.connect);

					try {
						// Autoconnect is active, activate the connect button
						if(CopyOfBarcodeScannerConnectActivity.this.baracodaService.getAutoConnect() == true) {
							btnConnectReader.setEnabled(false);
						}
						//						Toast.makeText(getBaseContext(),"Handler Disconnected "+currentDeviceView.getText() + "  "+R.string.disconnected, Toast.LENGTH_SHORT).show();
					}
					catch(RemoteException e) {}
				}
				break;

				//			case SettingsActivity.MESSAGE_ON_AUTOCONNECT_STATE_CHANGED:
				//				//final CheckBox autoconnectCheckbox = (CheckBox)SettingsActivity.this.findViewById(R.id.autoconnect_id);
				//				final Button connectButton2 = (Button)SettingsActivity.this.findViewById(R.id.button_connect_id);
				//
				//				try {
				//					// Let's update the autoconnect checkbox state
				//					autoconnectCheckbox.setChecked(SettingsActivity.this.baracodaService.getAutoConnect());
				//					// If autoconnect has been deactivated, let's enable the connect button
				//					if(SettingsActivity.this.baracodaService.getAutoConnect() == false) {
				//						connectButton2.setEnabled(true);
				//					}
				//				}
				//				catch(RemoteException e) {}
				//
				//				break;

			}


			try {
				int x = CopyOfBarcodeScannerConnectActivity.this.baracodaService.getConnectionState();
				//Toast.makeText(getBaseContext(),"Connection State " + String.valueOf(x) + "  "+msg.arg1, Toast.LENGTH_SHORT).show();
				//				data.putExtra("connectedDevice",currentDeviceView.getText());
				//				data.putExtra("connectionState",DataEntrySettingsActivity.this.baracodaService.getConnectionState());
				//				setResult(RESULT_OK, data);




			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}



	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(CopyOfBarcodeScannerConnectActivity.TAG, "SettingsActivity.onActivityResult");

		try{
		
		switch (requestCode) {
		// Enable Bluetooth activity
		case ACTION_REQUEST_ENABLE_BT_RESULT:
			// Did not succeed to enable, show a message and quit
			if (this.bluetoothAdapter.isEnabled() == false) {
				Toast.makeText(this, R.string.bluetooth_not_enabled, Toast.LENGTH_LONG).show();
				//    					Button connectBlueToothDevice = (Button) super.findViewById(R.id.button_connect_id);
				//    					connectBlueToothDevice.setEnabled(false);
				//    					super.finish();
			} else {
				CopyOfBarcodeScannerConnectActivity.this.startActivityForResult(new Intent("com.baracoda.android.baracodamanager.SelectDeviceActivity"), CopyOfBarcodeScannerConnectActivity.ACTION_REQUEST_SEARCH_DEVICES_RESULT);
			}
			break;

			// Select Device activity
		case CopyOfBarcodeScannerConnectActivity.ACTION_REQUEST_SEARCH_DEVICES_RESULT:
			if (resultCode == Activity.RESULT_OK) {
				// A reader has been selected from the Found devices list
				// Current reader will be disconnected and then we will connect the new one
				try {
					// Get the reader info
					BluetoothDevice device = data.getParcelableExtra(CopyOfBarcodeScannerConnectActivity.EXTRA_DEVICE);

					// If we change the current device, first we will need to disconnect the old one
					if (device.equals(this.getCurrentDevice()) == false) {
						// Disable autoconnect if active
						if(this.baracodaService.getAutoConnect() == true) {
							this.setAutoconnect(false);
						}

						// If there's already a connected reader (or we are connecting to one), disconnect it
						if(this.baracodaService.getConnectionState() != ConnectionState.DISCONNECTED) {
							this.baracodaService.disconnect();
						}

						// Connect the selected found device
						this.baracodaService.connect(device.getAddress());
					}
					// The current device has been selected again
					else if(this.baracodaService.getAutoConnect() == false && this.baracodaService.getConnectionState() == ConnectionState.DISCONNECTED) {
						// Connect the selected found device (only if disconnected and autoconnect not active)
						this.baracodaService.connect(device.getAddress());
					}
				}
				catch (RemoteException e) {
					Toast.makeText(CopyOfBarcodeScannerConnectActivity.this, "Error ACTION_REQUEST_SEARCH_DEVICES_RESULT:" + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
		}catch(Exception e){}
	}
}