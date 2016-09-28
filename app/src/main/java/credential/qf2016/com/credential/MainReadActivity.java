package credential.qf2016.com.credential;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;

import android.content.DialogInterface;
import android.hardware.Camera;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainReadActivity extends AppCompatActivity {


    public static final String ACCOUNT_TYPE = "com.example";
    public static final String ACCOUNT = "dummyaccount2";
    public static final long SYNC_INTERVAL = 10L;
    public SurfaceView cameraView;
    public BarcodeDetector barcodeDetector;
    public CameraSource cameraSource;
    public CameraSource.Builder cameraSourceB;
    public String textInfo;
    public Credential credential;
    public Vibrator v;
    public boolean proceed = false;
    public int flash = 0;


    public LocalDB db;
    Handler mainHandler;
    int timer;
    String user;
    private TextView barcodeInfo;
    private Account newAccount;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_read);

        // Sync vars
        db = new LocalDB(getApplicationContext());
        user = db.getParam("user");
        timer = 30000;

        // Sync setup
        newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);

        ContentResolver.setIsSyncable(newAccount, StubProvider.AUTHORITY, 1);
        ContentResolver.setMasterSyncAutomatically(true);
        ContentResolver.setSyncAutomatically(newAccount, StubProvider.AUTHORITY, true);
        ContentResolver.addPeriodicSync(newAccount, StubProvider.AUTHORITY, new Bundle(), SYNC_INTERVAL);

        // Sync control
        Log.e("CreSync", "ContentResolver set periodic sync");

        // Sync
        manualSync();


        // Activity setup

        SettingsMenuFragment settingsMenuFragment = (SettingsMenuFragment) getSupportFragmentManager().findFragmentById(R.id.naviation_drawer_personal_menu);
        settingsMenuFragment.setUp(R.id.naviation_drawer_personal_menu, (DrawerLayout) findViewById(R.id.drawer_layout_main));


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcodeInfo = (TextView) findViewById(R.id.code_info);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSourceB = new CameraSource.Builder(this, barcodeDetector).setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        cameraSource = cameraSourceB.build();
        mainHandler = new Handler(Looper.getMainLooper());
        Runnable r = new Runnable() {

            @Override
            public void run() {
                suspend();
            }
        };
        mainHandler.postDelayed(r, timer);


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                                         @Override
                                         public void release() {
                                         }

                                         @Override
                                         public void receiveDetections(Detector.Detections<Barcode> detections) {
                                             final SparseArray<Barcode> barcodes = detections.getDetectedItems();


                                             if (barcodes.size() != 0) {

                                                 mainHandler.removeCallbacksAndMessages(null);
                                                 v.vibrate(500);

                                                 // QRCode decode
                                                 credential = new Credential(barcodes.valueAt(0).displayValue, db);

                                                 // Credential validation
                                                 if (credential.validate()) {
                                                     if (credential.loadDb()) {
                                                         // Security interface setup
                                                         MyFragmentDialog newf = new MyFragmentDialog();
                                                         FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                         transaction.replace(R.id.drawer_layout_main, newf);
                                                         transaction.addToBackStack("tag");
                                                         transaction.commit();
                                                     } else {
                                                         user = db.getParam("user");
                                                         db.log(user, "NotFound", credential.getCode(), 0, 0, 0);

                                                         manualSync();


                                                         runOnUiThread(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 cameraSource.stop();
                                                                 new AlertDialog.Builder(MainReadActivity.this)
                                                                         .setTitle("Credencial não válida")
                                                                         .setMessage("Aguardar alguns minutos. Caso persista dirigir-se à credenciação.")
                                                                         .setCancelable(false)
                                                                         .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                             public void onClick(DialogInterface dialog, int id) {
                                                                                 try {
                                                                                     cameraSource.start(cameraView.getHolder());
                                                                                 } catch (IOException e) {
                                                                                     e.printStackTrace();
                                                                                 }
                                                                                 Runnable r = new Runnable() {
                                                                                     @Override
                                                                                     public void run() {
                                                                                         suspend();
                                                                                     }
                                                                                 };
                                                                                 mainHandler.postDelayed(r, timer);
                                                                             }
                                                                         }).create().show();
                                                             }
                                                         });
                                                     }

                                                 } else {
                                                     user = db.getParam("user");
                                                     db.log(user, "Fake", credential.getCode(), 0, 0, 0);
                                                     runOnUiThread(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             cameraSource.stop();
                                                             new AlertDialog.Builder(MainReadActivity.this)
                                                                     .setTitle("Credencial falsificada")
                                                                     .setMessage("Esta credencial foi falsificada! Avisar credenciação")
                                                                     .setCancelable(false)
                                                                     .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                         public void onClick(DialogInterface dialog, int id) {
                                                                             try {
                                                                                 cameraSource.start(cameraView.getHolder());
                                                                             } catch (IOException e) {
                                                                                 e.printStackTrace();
                                                                             }
                                                                             Runnable r = new Runnable() {
                                                                                 @Override
                                                                                 public void run() {
                                                                                     suspend();
                                                                                 }
                                                                             };
                                                                             mainHandler.postDelayed(r, timer);
                                                                         }
                                                                     }).create().show();
                                                         }
                                                     });
                                                 }
                                             }
                                         }
                                     }
        );
    }


    public void manualSync() {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(newAccount, StubProvider.AUTHORITY, settingsBundle);
        Log.e("CreSync", "Manual Sync Requested");
    }

    public void totalSync() {
        db.onUpgrade(db.getWritableDatabase(), 3, 6);
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        db.setParam("user", user + " - " + deviceName);
        manualSync();

    }


    public void suspend() {
        cameraSource.stop();


        AlertDialog.Builder adb = new AlertDialog.Builder(MainReadActivity.this);
        adb.setTitle("Atividade suspensa")
                .setMessage("Clicar 'OK' para continuar")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            cameraSource.start(cameraView.getHolder());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Runnable r = new Runnable() {

                            @Override
                            public void run() {
                                suspend();
                            }
                        };

                        mainHandler.postDelayed(r, timer);
                    }

                });


        AlertDialog alertDialog = adb.create();

        alertDialog.show();

    }

    public void settings() {
        mainHandler.removeCallbacksAndMessages(null);
        Fragment_Passcode newf = new Fragment_Passcode();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_layout_main, newf);
        transaction.addToBackStack("tag");
        transaction.commit();
    }


}


