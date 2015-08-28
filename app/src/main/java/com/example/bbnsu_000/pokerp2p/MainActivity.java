package com.example.bbnsu_000.pokerp2p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WiFiDirectBroadcastReceiver receiver;
    private List peers = new ArrayList();
    private Boolean wifiP2pEnabled;
    private Button discover_button;


    /**
     * Activity life cycle  call back methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        discover_button = (Button) findViewById(R.id.discover_button);

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);



    }
    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(receiver, intentFilter);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
        
        discover_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        // Code for when the discovery initiation is successful goes here.
                        // No services have actually been discovered yet, so this method
                        // can often be left blank.  Code for peer discovery goes in the
                        // onReceive method, detailed below.
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        // Code for when the discovery initiation fails goes here.
                        // Alert the user that something went wrong.
                    }
                });
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * methods called from BroadCast receiver to change the state of the activity according to the intents
     * fired by wifiP2p framework
     *
     */
    //this is called from Broadcast Receiver in WIFI_P2P_PEERS_CHANGED_ACTION
    public void clearPeersList(){
        peers.clear();
    }
    //this is called from Broadcast Receiver in WIFI_P2P_PEERS_CHANGED_ACTION
    public void addPeersList(Collection<WifiP2pDevice> peerList){
        peers.addAll(peerList);
    }
    //this is called from Broadcast Receiver in WIFI_P2P_PEERS_CHANGED_ACTION
    public List<WifiP2pDevice> getPeersList(){
        return peers;
    }
    //this is called from Broadcast Receiver in WIFI_P2P_STATE_CHANGED_ACTION
    public void setIsWifiP2pEnabled(Boolean bool){
        wifiP2pEnabled = bool;
    }
    //this is called from Broadcast Receiver in WIFI_P2P_STATE_CHANGED_ACTION
    public void connectToPeers( List<WifiP2pDevice> peers) {
        // Picking the first device found on the network.
        //TODO:change this code to connect to all the devices available
        WifiP2pDevice device = peers.get(0);
        //create a config obj to pass to mManager.connect()
        WifiP2pConfig config = new WifiP2pConfig();
        //deviceAdress : device MAC address uniquely identifies a Wi-Fi p2p device
        config.deviceAddress = device.deviceAddress;
        //??
        config.wps.setup = WpsInfo.PBC;
        //call the connect method
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}

