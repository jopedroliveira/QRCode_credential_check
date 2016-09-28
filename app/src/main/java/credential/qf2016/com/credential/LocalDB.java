package credential.qf2016.com.credential;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InterfaceAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocalDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 31;
    public static final String DATABASE_NAME = "localDB.db";
    public static Context applicationcontext;

    //RequestQueue queue;

    private JSONObject serverOut;

    //static String URL="http://10.0.2.2:8080/QF2016Credential_WebService";
    //static String URL = "http://192.168.1.176:8080/QF2016Credential_WebService";
    //static String URL="http://194.210.160.1:8080/QF2016Credential_WebService";
    //static String URL="http://joaopmrod.myasustor.com:8080/QF2016Credential_WebService";

    static String URL = "http://46.101.175.138:8080/QF2016Credential_WebService/dbDSfcsf43cwrgerg54YVeyegcer54cEtctV47tV3CT54yve6uyey6eb6Y6RY";
    //static String URL = "http://192.168.0.139:8080/QF2016Credential_WebService";


    Integer dose = 300;

    public LocalDB(Context applicationcontext_) {
        super(applicationcontext_, DATABASE_NAME, null, DATABASE_VERSION);
        applicationcontext = applicationcontext_;


    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE `credentials_meta` (\n" +
                "  `number` INTEGER PRIMARY KEY ASC,\n" +
                "  `code` varchar(45) DEFAULT 0,\n" +
                "  `bi` varchar(45) DEFAULT 0,\n" +
                "  `name` varchar(45) DEFAULT '-',\n" +
                "  `type` varchar(45) DEFAULT '-',\n" +
                "  `subtype` varchar(45) DEFAULT '-',\n" +
                "  `permissions` varchar(900) DEFAULT '-',\n" +
                "  `days` varchar(900) DEFAULT '-',\n" +
                "  `active` int(1) DEFAULT 0,\n" +
                "  `photoPath` STRING DEFAULT 0,\n" +
                "  `lastChange` INTEGER DEFAULT 0);\n";
        database.execSQL(query);

        query = "CREATE TABLE `credentials_state` (\n" +
                "  `number` INTEGER PRIMARY KEY ASC,\n" +
                "  `area` INTEGER DEFAULT '-1',\n" +
                "  `lastLog` varchar(45) DEFAULT '-'," +
                "  `lastChange` INTEGER DEFAULT '0',\n" +
                "  `syncTime` INTEGER DEFAULT 0," +
                "  `changed` INTEGER DEFAULT 0);";

        database.execSQL(query);

        query = "CREATE TABLE `credentials_logs` (\n" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  `timestamp` INTEGER DEFAULT 0,\n" +
                "  `user` INTEGER DEFAULT 0,\n" +
                "  `logType` varchar(45) DEFAULT '-',\n" +
                "  `description` varchar(100) DEFAULT '-',\n" +
                "  `number` INTEGER DEFAULT 0,\n" +
                "  `old_area` INTEGER DEFAULT 0,\n" +
                "  `new_area` INTEGER DEFAULT 0);\n";

        database.execSQL(query);

        query = "CREATE TABLE `credentials_pref` (\n" +
                "  `key`  STRING ,\n" +
                "  `value` STRING DEFAULT 0);\n";

        database.execSQL(query);

        query = String.format("INSERT INTO `credentials_pref` (`key`, `value`) VALUES ('sync_counter', '0'),('last_meta_download','0') ,('last_state_download' , '0'), ('user', 'default'),('sync', '-');\n");
        database.execSQL(query);


        Log.w("Cre", "CRIAR TABELAS");

        query = String.format("INSERT INTO `credentials_meta` (`number`, `code`, `bi`, `name`, `type`, `subtype`, `permissions`, `days`, `active`, `lastChange`) VALUES ('0', '0', '0', 'Teste Local n', 'Teste Local t', 'Teste Local st', 'P', '*', '1', '%d');\n", 0);
        database.execSQL(query);

        query = String.format("INSERT INTO `credentials_state` (`number`, `area`, `lastLog`, `lastChange`, `syncTime`) VALUES ('0', '-1', 'Nova', '%d', '%d');\n", new Date().getTime(), new Date().getTime());
        database.execSQL(query);

    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS credentials_meta";
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS credentials_state";
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS credentials_logs";
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS credentials_pref";
        database.execSQL(query);

        onCreate(database);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    boolean changeArea(Credential credential, int newArea) {
        SQLiteDatabase database = this.getWritableDatabase();
        Boolean success = false;
        try {

            ContentValues values = new ContentValues();
            values.put("area", newArea);
            values.put("lastLog", "XXXX");
            values.put("lastChange", "timestamp");
            success = database.update("credentials_state", values, "where number = " + credential.getNumber(), null) == 1;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return success;

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public long downloadMetaUpdates(long lastDownloadMeta) {
        Log.i("Cre DB", "DownloadOnlineMetaUpdates > " + lastDownloadMeta);
        SQLiteDatabase database;
        Long n = 0l;


        String q = String.format("SELECT * FROM queima.credentials_meta WHERE lastChange>%d AND number > %d ORDER BY number ASC LIMIT %d ;", lastDownloadMeta-1000, n, dose);
        JSONArray ja = queryServer(q);

        while (ja.length() > 0) {
            Log.i("Cre DB", "Download data: " + ja.length());
            //Log.i("Cre DB", "DownloadMeta: " + ja.toString());

            for (int i = 0; i < ja.length(); i++) {
                database = this.getWritableDatabase();
                try {

                    //q=String.format("SELECT * FROM queima.credentials_meta WHERE lastChange>%d ORDER BY lastChange ASC LIMIT 200 ;",lastDownloadMeta);
                    JSONObject jo = ja.getJSONObject(i);

                    ContentValues values = new ContentValues();
                    values.put("number", jo.getLong("number"));
                    values.put("code", jo.getString("code"));
                    values.put("bi", jo.getString("bi"));
                    values.put("name", jo.getString("name"));
                    values.put("type", jo.getString("type"));
                    values.put("subtype", jo.getString("subtype"));
                    values.put("active", jo.getInt("active"));
                    values.put("permissions", jo.getString("permissions"));
                    values.put("days", jo.getString("days"));
                    values.put("photoPath", jo.getString("photopath"));
                    values.put("lastChange", jo.getLong("lastchange"));

                    if (jo.getLong("number") > n) {
                        n = jo.getLong("number");
                    }

                    long a = database.insertWithOnConflict("credentials_meta", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                } catch (JSONException e) {
                    Log.i("Cre DB", "ERRRO");
                    e.printStackTrace();
                } finally {
                    database.close();
                }

            }
            q = String.format("SELECT * FROM queima.credentials_meta WHERE lastChange>%d AND number > %d ORDER BY number ASC LIMIT %d ;", lastDownloadMeta-1000, n, dose);
            ja = queryServer(q);
        }

        database = this.getWritableDatabase();

        /*

        //Print
        Cursor c = database.rawQuery("SELECT * FROM credentials_meta ORDER BY lastChange;", null);
        Log.e("Cre DB", "Meta Elements:" + c.getCount());

        while (c.moveToNext()) {
            String s = "";
            for (int i = 0; i < c.getColumnCount(); i++) {
                s = s + " " + c.getString(i);
            }
            Log.e("Cre DB", s);
        }

        */



        Cursor cc = database.rawQuery("SELECT lastChange FROM credentials_meta ORDER BY lastChange DESC LIMIT 1;", null);
        long lastChange = 0;
        try {
            cc.moveToNext();
            lastChange = cc.getLong(cc.getColumnIndex("lastChange"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.close();
        return lastChange;
    }


    public long downloadStateUpdates(long lastDownloadState) {
        Log.i("Cre DB", "DownloadStateUpdates > " + lastDownloadState);
        SQLiteDatabase database;
        Long n = 0l;
        long lastSync = lastDownloadState;


        String q = String.format("SELECT * FROM queima.credentials_state WHERE syncTime>%d AND number>%d ORDER BY number ASC LIMIT %d;", lastDownloadState-1000, n, dose);
        JSONArray ja = queryServer(q);

        while (ja.length() > 0) {
            Log.i("Cre DB", "DownloadStateUpdates: " + ja.length());

            //Log.i("Cre DB", "DownloadState0: " + ja.toString());

            for (int i = 0; i < ja.length(); i++) {
                database = this.getWritableDatabase();
                try {


                    JSONObject jo = ja.getJSONObject(i);
                    Log.i("Cre DB", "DownloadState: " + jo.toString());

                    ContentValues values = new ContentValues();

                    //Log.d("Cre"," \nOLA??????? 1");
                    values.put("number", jo.getLong("number"));
                    values.put("area", jo.getInt("area"));
                    values.put("lastLog", jo.getString("lastlog"));
                    values.put("lastChange", jo.getLong("lastchange"));
                    values.put("syncTime", jo.getLong("synctime"));
                    values.put("changed", 0);

                    //Log.d("Cre"," \nOLA??????? 2");

                    if (jo.getLong("synctime") > lastSync) {
                        lastSync = jo.getLong("synctime");
                    }

                    //Log.d("Cre"," \nOLA?????? 3");

                    if (jo.getLong("number") > n) {
                        n = jo.getLong("number");
                    }

                    //Log.d("Cre"," \nOLA??????? 4");

                    Cursor c = database.rawQuery(String.format("SELECT lastChange FROM credentials_state WHERE number=%d;", jo.getLong("number")), null);
                    long localT = 0;
                    if (c.moveToNext()) {
                        localT = c.getLong(c.getColumnIndex("lastChange"));
                    }


                    if (jo.getLong("lastchange") > localT) {
                        long a = database.insertWithOnConflict("credentials_state", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        Log.i("Cre DB", "\n\nInsert: " + a);
                    } else {
                        Log.i("Cre DB", "\n\nRefused (older): server:\n" + jo.getLong("lastchange") +"\nlocal\n"+localT);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    database.close();
                }

            }
            q = String.format("SELECT * FROM queima.credentials_state WHERE syncTime>%d AND number>%d ORDER BY number ASC LIMIT %d;", lastDownloadState-1000, n, dose);
            ja = queryServer(q);

        }

        database = this.getWritableDatabase();

        Cursor c = database.rawQuery("SELECT * FROM credentials_state ORDER BY number;", null);

        Log.e("Cre DB", "State Elements: " + c.getCount());

        //Print
        /*
        while (c.moveToNext()) {
            String s = "";
            for (int i = 0; i < c.getColumnCount(); i++) {
                s = s + " " + c.getString(i);
            }
            Log.e("Cre DB", s);
        }

*/


        database.close();

        return lastSync;
    }


    public void updateState() {
        Log.i("Cre DB", "UploadState");
        String q;
        SQLiteDatabase database = this.getWritableDatabase();

        q = String.format("SELECT * FROM credentials_state WHERE changed>0;");
        Cursor r = database.rawQuery(q, null);
        Log.i("Cre DB", "UploadState: " + r.getCount());

        try {
            JSONObject jo = new JSONObject();
            jo.put("rows", convertToJSON(r));
            updateWS(jo);
            Log.i("Cre DB", "Update : " + jo.toString());

            ContentValues values = new ContentValues();
            values.put("changed", 0);
            database.update("credentials_state", values, null, null);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

    }

    public void logT() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO credentials_logs (`timestamp`, `user`, `logType`, `description`, `number`, `old_area`, `new_area`) VALUES ('21432', 'joao', 'teste', 'ola', '1', '1', '2');");
        database.close();
    }

    public void updateLogs() {
        Log.i("Cre DB", "UploadLogs");
        String q;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor r;


        try {
            q = String.format("SELECT * FROM credentials_logs LIMIT 100;");
            r = database.rawQuery(q, null);
            while (r.getCount() > 0) {
                {
                    JSONObject jo = convertToJSON(r).getJSONObject(0);

                    Log.i("Cre DB", "Upload Log  " + r.getCount() + " : " + jo.getDouble("timestamp"));

                    Date d = new Date(jo.getLong("timestamp"));
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                    String datetime = df.format(d);

                    String s = String.format("INSERT INTO `queima`.`credentials_logs` (`timestamp`, `user`, `logType`, `description`, `number`, `old_area`, `new_area`) VALUES ('%s', '%s', '%s', '%s', '%d', '%d', '%d');"
                            , datetime, jo.getString("user"), jo.getString("logtype"), jo.getString("description"), jo.getLong("number"), jo.getInt("old_area"), jo.getInt("new_area"));
                    insertWS(s);
                    Log.i("Cre DB", "Upload Log : ");

                    database.execSQL(String.format("DELETE FROM credentials_logs WHERE `id`=%d;", jo.getLong("id")));

                    r = database.rawQuery(q, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }


    }


    public static JSONArray convertToJSON(Cursor resultSet)
            throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.moveToNext()) {
            int total_rows = resultSet.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {

                if (resultSet.getType(i) == resultSet.FIELD_TYPE_INTEGER) {
                    obj.put(resultSet.getColumnName(i)
                            .toLowerCase(), resultSet.getLong(i));
                } else {
                    obj.put(resultSet.getColumnName(i)
                            .toLowerCase(), resultSet.getString(i));
                }
            }
            jsonArray.put(obj);
            //System.out.println(obj.toString());

        }
        return jsonArray;
    }

//////////////////////////////////////////////////////
    // Communication

    public JSONArray queryServer(String q) {
        String s = null;
        try {
            s = downloadUrl(q);
            //Log.e("Cre db", s);
            JSONObject jo = new JSONObject(s);
            //Log.w("db",jo.toString());
            return jo.getJSONArray("rows");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String downloadUrl(String q) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.

        try {
            String url_ = (URL + "/query?query=" + q).replace(" ", "%20");
            Log.d("Cre",url_);
            URL url = new URL(url_);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(40000 /* milliseconds */);
            conn.setConnectTimeout(30000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("db", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String insertWS(String q) throws IOException {
        InputStream is = null;


        try {
            URL url = new URL(URL + "/insert");
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("insert", q);
            Log.e("ALARM", q);

            StringBuilder postData = new StringBuilder();

            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    private String updateWS(JSONObject jo) throws IOException {
        InputStream is = null;


        try {
            URL url = new URL(URL + "/updateStates");
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("update", jo.toString());

            StringBuilder postData = new StringBuilder();

            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    // Reads an InputStream and converts it to a String.
    public String readIt0(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);

        return new String(buffer);
    }


    public String readIt(InputStream inputStream) throws IOException {


        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1)

        {
            buf.write((byte) result);
            result = bis.read();
        }
        // Log.w("Cre TESTE",buf.toString());

        return buf.toString();
    }


    public void log(String user, String logType, String description, long number, int old_area, int new_area) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(String.format("INSERT INTO credentials_logs (`timestamp`, `user`, `logType`, `description`, `number`, `old_area`, `new_area`) VALUES ('%d', '%s', '%s', '%s', '%d', '%d', '%d');",new Date().getTime(),user,logType,description,number,old_area,new_area  ));
        database.close();
        Log.d("CreLogDB", user+" - "+logType+" : "+description);
    }


    String getParam(String k) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = String.format("SELECT value FROM credentials_pref WHERE `key`='%s';", k);
        Cursor c = database.rawQuery(query, null);

        String a = "";

        try {
            JSONArray ja = convertToJSON(c);
            a = ja.getJSONObject(0).getString("value");
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.close();
        return a;

    }

    public void setParam(String k, String v) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = String.format(" UPDATE credentials_pref SET `value`='%s' WHERE `key`='%s';", v, k);
        database.execSQL(query);
        database.close();

    }

}
