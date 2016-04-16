package credential.qf2016.com.credential;


import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;


public class MainReadActivity extends AppCompatActivity {


    public SurfaceView cameraView;
    private TextView barcodeInfo;
    public BarcodeDetector barcodeDetector;
    public CameraSource cameraSource;
    Vibrator v;

    public String textInfo;
    public DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_read);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);


//        SettingsMenuFragment settingsMenuFragment = (SettingsMenuFragment)
        getSupportFragmentManager().findFragmentById(R.id.drawer_layout_main);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);


        barcodeInfo = (TextView) findViewById(R.id.code_info);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();


        cameraSource = new CameraSource.Builder(this, barcodeDetector).build();


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


                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {


                            v.vibrate(500);
                            textInfo = barcodes.valueAt(0).displayValue;

                            MyFragmentDialog newf = new MyFragmentDialog();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, newf);

                            transaction.addToBackStack("tag");
                            transaction.commit();

                        }
                    });
                }
            }

        });


    }

    public void onBackPressed() {
        // do nothing
    }


}




