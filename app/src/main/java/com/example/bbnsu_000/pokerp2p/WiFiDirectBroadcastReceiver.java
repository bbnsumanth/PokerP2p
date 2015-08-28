package com.example.bbnsu_000.pokerp2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bbnsu_000 on 8/28/2015.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity activity;
    private WifiP2pManager.ConnectionInfoListener mConnectionListener;
    /**
     * constructor
     * @param manager
     * @param channel
     * @param activity
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       Activity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.activity = (MainActivity) activity;
        //this conectionIfo class implements WifiP2pManager.ConnectionInfoListener
        this.mConnectionListener =  new connectionInfo();
    }

    /**
     * onRecive call back method,which receives intents broadcasted.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        /**
         * This intent is fired when the Wifi P2P mode is enabled
         * this is the best place to check the Wifi P2P status and notify the activity
         */
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            Toast.makeText(activity, "wifip2penabled",
                    Toast.LENGTH_SHORT).show();
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
               // activity.setIsWifiP2pEnabled(true);
            } else {
               // activity.setIsWifiP2pEnabled(false);
            }
            /**
             * this intent is fired when new peers are discovered
             * we can request the list of available peers here using requestPeers()
             * this method takes in PeerListListner
             * this method is async and the output is retured in onPeersAvaialble callback
             * in the onPeers available method we receive the peer Device List,which can be used to
             * connect to from this device
             *
             */
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Toast.makeText(activity, "peers discovered",
                    Toast.LENGTH_SHORT).show();

            if (mManager != null) {
                mManager.requestPeers(mChannel,new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList) {
                        Toast.makeText(activity, "peers list available",
                                Toast.LENGTH_SHORT).show();
                        // Out with the old, in with the new.
                        // all this alteration of state is in activity
                        activity.clearPeersList();
                        activity.addPeersList(peerList.getDeviceList());
                        List<WifiP2pDevice> peers = activity.getPeersList();
                        if(peers.size() != 0){
                            activity.connectToPeers(peers);
                        }else{
                            // Log.d(WiFiDirectActivity.TAG, "No devices found");
                            System.out.println("no devices found");
                            return;
                        }
                        // If an AdapterView is backed by this data, notify it
                        // of the change.  For instance, if you have a ListView of available
                        // peers, trigger an update.
                        // ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                });
            }

            /**
             * this intent is fired when a connection is made or cancelled between peers
             * if the connection is made,we can request the connection info by calling
             * requestConnectionInfo(channel,connectionListner)
             * this is async method and the requested info is returned in onConnectionInfoAvailable
             * callback method of connectionListener
             */
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }
            //check whether we are connected or not
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                Toast.makeText(activity, "wifip2p is connected ",
                        Toast.LENGTH_SHORT).show();
                // We are connected with the other device, request connection
                // info to find group owner IP
                mManager.requestConnectionInfo(mChannel,mConnectionListener);
                //this method is returned immediately with void,actual info is received
                // in callback method of the listener
            }
            /**
             *
             */
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
