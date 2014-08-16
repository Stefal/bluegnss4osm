/*
 * Copyright 2014, Hiroshi Miura <miurahr@linux.com>
 * Copyright 2014, BlueGnss4OSM Project
 *
 * This file is part of BlueGnss4OSM.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it. If not, see <http://www.gnu.org/licenses/>.
 */

package org.da_cha.android.bluegnss;

import org.da_cha.android.bluegnss.GnssProviderService;
import org.da_cha.android.bluegnss.GnssStatus;
import org.da_cha.android.bluegnss.R;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.util.Log;


public class StatusFragment extends Fragment {

    private final static String LOG_TAG = "BlueGNSS";

    private GnssProviderService mService = null;
    boolean mIsBound;
    boolean mIsRegistered;
    private final GnssUpdateReceiver mGnssUpdateReceiver = new GnssUpdateReceiver();

    private View myView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       myView = inflater.inflate(R.layout.status_fragment, container, false);
       return myView;
    }

    /*
     * Service communications
     */
    private void CheckIfServiceIsRunning() {
        if (GnssProviderService.isRunning()) {
            doBindService();
            doRegisterReceiver();
        }
    }
    private void doRegisterReceiver(){
        IntentFilter filter = new IntentFilter(GnssProviderService.NOTIFY_UPDATE);
        getActivity().registerReceiver(mGnssUpdateReceiver, filter);
        mIsRegistered = true;
    }
    private void doUnregisterReceiver(){
        if (mIsRegistered) {
            getActivity().unregisterReceiver(mGnssUpdateReceiver);
        }
    }
    private void doBindService() {
        Context appContext = getActivity().getApplicationContext();
        getActivity().bindService(new Intent(appContext, GnssProviderService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    private void doUnbindService() {
        if (mIsBound) {
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        CheckIfServiceIsRunning();
    }
    @Override
    public void onPause() {
        doUnregisterReceiver();
        doUnbindService();
        super.onPause();
    }
    /*
     * For bind to ProviderService
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = (GnssProviderService)((GnssProviderService.GnssProviderServiceBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    /*
     * for reciever
     */
     private class GnssUpdateReceiver extends BroadcastReceiver {
        private GnssStatus status;
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("notification");
            if (GnssProviderService.NOTIFY_UPDATE_GPS_STATUS.equals(message)){
               status = mService.getGnssStatus();
               update_rador_view(bundle);
            }
        }
        private void update_rador_view(Bundle bundle){
            int numNbSat = status.getNbSat();
            int numSat = status.getNumSatellites();
        }
    }
}
// vim: tabstop=4 expandtab shiftwidth=4 softtabstop=4