package com.example.jeevan78.datasynctransfer;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by devil on 2/5/2018.
 */

public class CashbackAccountService extends Service {

    private static final String ACCOUNT_TYPE = "com.zoftino.sync";
    public static final String ACCOUNT_NAME = "zoftino_sync";
    private CashbackAuthenticator mAuthenticator;

    public static Account GetAccount() {
        final String accountName = ACCOUNT_NAME;
        return new Account(accountName, ACCOUNT_TYPE);
    }

    @Override
    public void onCreate() {
        mAuthenticator = new CashbackAuthenticator(this);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
