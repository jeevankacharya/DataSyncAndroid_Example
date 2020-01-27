package com.example.jeevan78.datasynctransfer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by devil on 2/5/2018.
 */

public class CashbackSyncAdapterService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static CashbackSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new CashbackSyncAdapter(getApplicationContext(), true);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}

