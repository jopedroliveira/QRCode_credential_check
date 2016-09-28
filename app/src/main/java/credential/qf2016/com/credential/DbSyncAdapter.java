package credential.qf2016.com.credential;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by joaop on 12/04/2016.
 */

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class DbSyncAdapter extends AbstractThreadedSyncAdapter {

    private SharedPreferences sPreferences;
    LocalDB db;
    Context context_=null;

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public DbSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        context_=context;
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        sPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        db=new LocalDB(context);
        onCreate();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public DbSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        context_=context;
        mContentResolver = context.getContentResolver();
        sPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        db=new LocalDB(context);
        onCreate();

    }

    private void onCreate(){


    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */


    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        long a=-1;

        try {

        a= Long.valueOf(db.getParam("sync_counter"));
        Log.i("CreSync", String.format("SYNC -- %d",a));
        db.setParam("sync_counter",String.valueOf(a+1) );


        long lastDownloadMeta = Long.valueOf(db.getParam("last_meta_download"));
        long lastDownload = Long.valueOf(db.getParam("last_state_download"));

        //lastDownloadMeta = 0;
        // lastDownload = 0;



            lastDownloadMeta = db.downloadMetaUpdates(lastDownloadMeta);
            Log.e("CreSync", "lastDownloadMeta: " + lastDownloadMeta);
            lastDownload = db.downloadStateUpdates(lastDownload);
            Log.e("CreSync", "lastDownload: " + lastDownload);
            db.setParam("last_meta_download",String.valueOf(lastDownloadMeta));
            db.setParam("last_state_download",String.valueOf(lastDownload));

            db.updateState();

            db.updateLogs();

            db.log(db.getParam("user"),"Sync","Sync OK "+a,0,0,0);

            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            String time = df.format(d);

            db.setParam("sync",time+ " Sync OK "+a);
            Log.e("Cre",db.getParam("sync"));

        }
        catch (Exception e){
            e.printStackTrace();
            //db.log(db.getParam("user"),"Sync Error","Sync Error "+a +" : "+e.toString().replace("'","*"),-1,0,0);
            db.log(db.getParam("user"),"Sync Error","Sync Error "+a +" : ",-1,0,0);
            Date d = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            String time = df.format(d);

            db.setParam("sync", time+" Sync Error "+a);

        }





    }


}
