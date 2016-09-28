package credential.qf2016.com.credential;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by PedroOliveira on 21/04/16.
 */
public class Credential {

    private String code;
    private Long number;
    private String name;
    private String type;
    private String subtype;
    private String permissions;
    private String id;
    private String permissions_s;
    private String days;
    private String days_s;
    private Integer active;
    private Integer lastChangeMeta;


    private Integer area;
    private String area_s;
    private String lastLog;
    private Integer lastChangeState;
    private Integer syncTimeMeta;


    private String photoPath;

    private LocalDB db;
    private Context ac;

    public Credential(String code_, LocalDB db_) {
        code = code_;
        db = db_;

        //    db = new LocalDB(ac.getApplicationContext());
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getPermissions_s() {
        return permissions_s;
    }

    public void setPermissions_s(String permissions_s) {
        this.permissions_s = permissions_s;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDays_s() {
        return days_s;
    }

    public void setDays_s(String days_s) {
        this.days_s = days_s;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getLastChangeMeta() {
        return lastChangeMeta;
    }

    public void setLastChangeMeta(Integer lastChangeMeta) {
        this.lastChangeMeta = lastChangeMeta;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public String getLastLog() {
        return lastLog;
    }

    public void setLastLog(String lastLog) {
        this.lastLog = lastLog;
    }

    public Integer getLastChangeState() {
        return lastChangeState;
    }

    public void setLastChangeState(Integer lastChangeState) {
        this.lastChangeState = lastChangeState;
    }

 /*   public LocalDB getDb() {
        return db;
    }

    public void setDb(LocalDB db) {
        this.db = db;
    }*/

    public Context getAc() {
        return ac;
    }

    public void setAc(Context ac) {
        this.ac = ac;
    }


    public String toString() {
        String str = number + "\n" + name + "\n" + type + "\n" + subtype + "\n" + permissions + "\n" + days + "\n" + area + "\n";
        return str;
    }

    public boolean validate() {
        //return true se é uma credencial válida pelo checksum ou outra cena qualquer
        return true;
    }

    public boolean checkDay(String dayTest) {



        Log.e("CreDay"," Dia de Teste: "+dayTest + " "+ days.indexOf(dayTest) );

        Boolean b = days.indexOf(dayTest) != -1 || days.indexOf("*") != -1;

        Log.e("CreDay","resultado: "+b);
        // funçao aqui interna
        return b;
    }

    public boolean loadDb2() {
        this.area = -1;
        this.area_s = "fora";
        this.name = "ze";


        return true;
    }

    boolean loadDb() {
        SQLiteDatabase database2 = db.getWritableDatabase();
        Log.i("CreCre", "getCredential");
        try {


            String selectQuery = String.format("SELECT  * FROM credentials_meta WHERE code='%s'", code);
            Log.e("Cre", selectQuery);
            Cursor cursor = database2.rawQuery(selectQuery, null);

            try {
                JSONObject jo = LocalDB.convertToJSON(cursor).getJSONObject(0);
                Log.w("CreCre", jo.toString());
                code = jo.getString("code");
                number = jo.getLong("number");
                name = jo.getString("name");
                type = jo.getString("type");
                subtype = jo.getString("subtype");
                permissions_s = jo.getString("permissions");
                permissions = permissions_s;
                days = jo.getString("days");
                photoPath = "http://credenciais.queimadasfitascoimbra.pt/" + jo.getString("photopath");

                String[] sa = days.split(";");
                days_s = "";
                try {
                    for (int i = 0; i < sa.length; i++) {
                        String[] pa = sa[i].split("-");
                        days_s = days_s + String.format(" %d/%d ;", Integer.valueOf(pa[0]), Integer.valueOf(pa[1]));
                    }
                } catch (Exception e) {
                    days_s = days;
                }

                active = jo.getInt("active");
                id = jo.getString("bi");
                lastChangeMeta = jo.getInt("lastchange");

            } catch (Exception e) {

                throw new Exception("Meta Not Found");


            }

            selectQuery = String.format("SELECT  * FROM credentials_state WHERE number='%s'", number);
            Log.e("Cre", selectQuery);
            cursor = database2.rawQuery(selectQuery, null);

            try {
                JSONObject jo = LocalDB.convertToJSON(cursor).getJSONObject(0);
                Log.w("CreCre", jo.toString());
                area = jo.getInt("area");

                if (area == -1) {
                    area_s = "Exterior";
                } else if (area == 1) {
                    area_s = "Parque";
                } else {
                    area_s = "Area Desconhecida";
                }


                lastLog = jo.getString("lastlog");
                lastChangeState = jo.getInt("lastchange");
                syncTimeMeta = jo.getInt("synctime");

            } catch (Exception e) {
                throw new Exception("State Not Found");

            }
            database2.close();
            return true;


        } catch (Exception e) {
            database2.close();
            e.printStackTrace();
            return false;
        } /*finally {

        }*/



    }

    boolean checkPermission() {
        // funçao aqui interna
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea_s() {
        return area_s;
    }

    public void setArea_s(String area_s) {
        this.area_s = area_s;
    }


    void changeArea(int newArea, String user) {

        SQLiteDatabase database = db.getWritableDatabase();

        // Mensagem

        String message;

        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String datetime = df.format(d);


        if (newArea == 1) {
            message = datetime + " : Entrada - Porta " + user;
        } else if (newArea == -1) {
            message = datetime + " : Saida - Porta " + user;
        } else {
            message = "Erro - Movimento desconhecido!";
        }

        Log.i("CreCre", "Chnge Area: " + area + " to " + newArea);
        try {
            ContentValues values = new ContentValues();
            values.put("number", number);
            values.put("area", newArea);
            values.put("lastLog", message);
            values.put("lastChange", new Date().getTime());
            values.put("changed", 1);
            long a = database.insertWithOnConflict("credentials_state", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.i("Cre LocalDB", "Insert Local: " + a);

            //db.log(user, "Area Change", message, number, area, newArea);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        //loadDB(db);
    }


}


