package com.example.bbnsu_000.pokerp2p;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by bbnsu_000 on 8/28/2015.
 */
public class connectionInfo implements WifiP2pManager.ConnectionInfoListener {
    /**
     * get the groupownerAddress
     * find whether this device is group owner or not
     * if it is group owner,create a server thread
     * if it is not a group owner,create a client thread
     * @param info
     */
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        try {
            InetAddress groupOwnerAddress = InetAddress.getByName(info.groupOwnerAddress.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            //this implies that this device is the group owner
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting incoming connections.
        } else if (info.groupFormed) {
            // (The other)This device acts as the client. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.
        }
    }
}
