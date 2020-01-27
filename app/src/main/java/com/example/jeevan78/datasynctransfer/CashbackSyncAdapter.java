package com.example.jeevan78.datasynctransfer;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by devil on 2/5/2018.
 */

public class CashbackSyncAdapter extends AbstractThreadedSyncAdapter {




    private static final String CASHBACK_TABLE = "cashback_t";

    public static final Uri AUTHORITY_URI = Uri.parse("content://com.zoftino.sync.cashback");

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CASHBACK_TABLE);

    private static final String CASHBACK_URL = "http://testcashback.cashback/";

    private final ContentResolver mContentResolver;

    private static final String[] PROJECTION = new String[]{
            "_id",
            "STORE",
            "CASHBACK"
    };
    public CashbackSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public CashbackSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        try {
            final URL location = new URL(CASHBACK_URL);
            InputStream stream = null;

            try {
                Log.i("cashback sync adaper", "onPerformSync running");

                // stream = downloadUrl(location);
                // String feedData = readInput(stream);

                //syn adapter can be run without downloading using test data
                String feedData = getTestData();

                Map<Long, CashbackEntity> cashbackFeed = processCashbackFeed(feedData);

                addUpdateDeleteLocalData(cashbackFeed, syncResult);

            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    public void addUpdateDeleteLocalData(Map<Long, CashbackEntity> cashbackData, final SyncResult syncResult)
            throws Exception {

        final ContentResolver contentResolver = getContext().getContentResolver();
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        Cursor c = contentResolver.query(CONTENT_URI, PROJECTION, null, null, null);

        long id;
        String store;
        String cashback;

        if (c != null) {
            while (c.moveToNext()) {
                syncResult.stats.numEntries++;
                id = c.getLong(0);
                store = c.getString(1);
                cashback = c.getString(2);

                CashbackEntity cashbackExist = cashbackData.get(id);
                if (cashbackExist != null) {

                    cashbackData.remove(id);

                    Uri existingUri = CONTENT_URI.buildUpon()
                            .appendPath(Long.toString(id)).build();
                    //update record as local data is different
                    if ((cashbackExist.getStore() != null && !cashbackExist.getStore().equals(store)) ||
                            (cashbackExist.getCashback() != null && !cashbackExist.getCashback().equals(cashback))) {

                        batch.add(ContentProviderOperation.newUpdate(existingUri)
                                .withValue("STORE", cashbackExist.getStore())
                                .withValue("CASHBACK", cashbackExist.getCashback())
                                .build());
                        syncResult.stats.numUpdates++;
                    }
                } else {
                    //delete local record as it does not exist in server
                    Uri deleteUri = CONTENT_URI.buildUpon()
                            .appendPath(Long.toString(id)).build();
                    batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                    syncResult.stats.numDeletes++;
                }
            }
            c.close();
        }

        // Add New records
        for (CashbackEntity ce : cashbackData.values()) {

            batch.add(ContentProviderOperation.newInsert(CONTENT_URI)
                    .withValue("_id", ce.id)
                    .withValue("STORE", ce.getStore())
                    .withValue("CASHBACK", ce.getCashback())
                    .build());
            syncResult.stats.numInserts++;

        }


        mContentResolver.applyBatch("com.zoftino.sync.cashback", batch);

    }

    private Map<Long, CashbackEntity> processCashbackFeed(String feed) {

        Map<Long, CashbackEntity> cashbackFeed = new HashMap<Long, CashbackEntity>();

        String[] cbrecs = feed.split(",");
        for (String cbrec : cbrecs) {
            String[] cbdata = cbrec.split("\\|");

            CashbackEntity cbe = new CashbackEntity();
            cbe.setId(Long.valueOf(cbdata[0]));
            cbe.setStore(cbdata[1]);
            cbe.setCashback(cbdata[2]);

            cashbackFeed.put(cbe.getId(), cbe);

        }
        Log.i("cashback sync adaper", "total recs in map " + cashbackFeed.keySet().size());
        return cashbackFeed;
    }

    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();
        return conn.getInputStream();
    }

    private String readInput(InputStream is) {
        String str = "";
        StringBuffer buf = new StringBuffer();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + ",");
                }
            }
        } catch (Exception e) {

        } finally {
            try {
                is.close();
            } catch (Throwable ignore) {
            }
        }
        return buf.toString();
    }

    private String getTestData() {
        String cashbackFedd = "1|fashion store|Upto 2% cashback,";
        cashbackFedd = cashbackFedd + "2|shoes store|Upto 21% cashback,";
        cashbackFedd = cashbackFedd + "3|electronics store|Upto 12% cashback,";
        cashbackFedd = cashbackFedd + "4|travel store|Upto 24% cashback,";
        cashbackFedd = cashbackFedd + "6|mobiles store|Upto 32% cashback,";
        cashbackFedd = cashbackFedd + "7|blah store|Upto 52% cashback,";
        cashbackFedd = cashbackFedd + "8|xtz store|Upto 23% cashback";
        cashbackFedd = cashbackFedd + "9|xyz store|Upto 23% cashback";
        return cashbackFedd;
    }
}
