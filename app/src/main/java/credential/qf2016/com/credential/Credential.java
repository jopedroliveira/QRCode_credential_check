package credential.qf2016.com.credential;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by PedroOliveira on 21/04/16.
 */
public class Credential {

    private String code;
    private Integer number;
    private String name;
    private String type;
    private String subtype;
    private String[] permissions;
    private String id;
    private String permissions_s;
    private Calendar[] days;
    private String days_s;
    private Integer active;
    private String lastChangeMeta;


    private Integer area;
    private String area_s;
    private String lastLog;
    private String lastChangeState;

    //private LocalDB db;
    private Context ac;

    public Credential(String code_, Context ac_) {
        code = code_;
        ac = ac_;

    //    db = new LocalDB(ac.getApplicationContext());
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
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

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String getPermissions_s() {
        return permissions_s;
    }

    public void setPermissions_s(String permissions_s) {
        this.permissions_s = permissions_s;
    }

    public Calendar[] getDays() {
        return days;
    }

    public void setDays(Calendar[] days) {
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

    public String getLastChangeMeta() {
        return lastChangeMeta;
    }

    public void setLastChangeMeta(String lastChangeMeta) {
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

    public String getLastChangeState() {
        return lastChangeState;
    }

    public void setLastChangeState(String lastChangeState) {
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

    public boolean checkDay() {
        // funçao aqui interna
        return false;
    }

    public boolean loadDb() {
        this.area = -1;
        this.name = "ze";




        return true;
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

    public void changeArea(int i) {

    }


}


