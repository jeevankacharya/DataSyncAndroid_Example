package com.example.jeevan78.datasynctransfer;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by devil on 2/5/2018.
 */

public class CashbackSyncAdapterRunActivity    extends AppCompatActivity {

    public static final String AUTHORITY = "com.zoftino.sync.cashback";

    public static final String ACCOUNT_TYPE = "com.zoftino.sync";

    public static final String ACCOUNT = "cashbacksync";

    public static final int SYNC_INTERVAL = 25000;

    private Account mAccount;
    private ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syncadapter_run);

        mAccount = CreateSyncAccount(this);

        mContentResolver = getContentResolver();

        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);

    }

    public void runSyncAdapter(View v) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(mAccount, AUTHORITY, bundle);
    }

    public Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(ACCOUNT_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 31);
        }
        Account accounts[] = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
                return newAccount;
            } else {
                Log.i("sync activity", "error creating account");
            }
        } else {
            return accounts[0];
        }
        return null;
    }
}