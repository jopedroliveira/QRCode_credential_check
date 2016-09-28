package credential.qf2016.com.credential;


import android.bluetooth.BluetoothAdapter;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by PedroOliveira on 15/04/16.
 */
public class SettingsMenuFragment extends Fragment {


    private TextView dayOfWeek;
    private TextView date;
    private TextView sync;
    private SwitchCompat flashChecker;
    private DrawerLayout mDrawerLayout;
    private Spinner spinnerGate;
    private ActionBarDrawerToggle mDrawerToggle;


    public String doW;
    public String dmonth;
    private View containerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public SettingsMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.nav_menu, container, false);

        dayOfWeek = (TextView) layout.findViewById(R.id.dayOfWeek);
        date = (TextView) layout.findViewById(R.id.date);
        sync = (TextView) layout.findViewById(R.id.syncView);

        flashChecker = (SwitchCompat) layout.findViewById(R.id.flashStatus);
        flashChecker.setTextOn("Flash On");
        flashChecker.setTextOff("Flash Off");

        flashChecker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    buttonView.setText("On");
                    ((MainReadActivity) getActivity()).cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    buttonView.setText("Off");
                    ((MainReadActivity) getActivity()).cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
}
        });

        Button exit = (Button) layout.findViewById(R.id.closeall);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                System.exit(0);
            }
        });

        Button updatenow = (Button) layout.findViewById(R.id.updatenow);
        updatenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainReadActivity) getActivity()).manualSync();
            }
        });

        Button updateall = (Button) layout.findViewById(R.id.restartlocalbd);
        updateall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainReadActivity) getActivity()).totalSync();
            }
        });

        Button settings = (Button) layout.findViewById(R.id.advanced);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainReadActivity) getActivity()).settings();
                mDrawerLayout.closeDrawers();


            }
        });


        spinnerGate = (Spinner) layout.findViewById(R.id.spinnerGates);
        String defaultV = load();
        List<String> myOpt = Arrays.asList(getResources().getStringArray(R.array.gates));
        Log.e("localpreferences", defaultV);
        if (!defaultV.equals("N/A")) {
            int a = myOpt.indexOf(defaultV);
            spinnerGate.setSelection(a);

        } else {
            spinnerGate.setSelection(1);
            save(myOpt.get(1));

        }


        spinnerGate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), spinnerGate.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();


                save(spinnerGate.getSelectedItem().toString());
                ((MainReadActivity) getActivity()).user = spinnerGate.getSelectedItem().toString();

                BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                String deviceName = myDevice.getName();
                ((MainReadActivity) getActivity()).db.setParam("user", spinnerGate.getSelectedItem().toString() + " - " + deviceName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // Log.d("CreLOGGGGGG",((MainReadActivity)getActivity()).db.getParam("sync"));


        return layout;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        manageCalendar();


    }

    private void manageCalendar() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_WEEK);


        switch (month) {
            case 0:
                dmonth = "janeiro";
                break;
            case 1:
                dmonth = "jevereiro";
                break;
            case 2:
                dmonth = "março";
                break;
            case 3:
                dmonth = "abril";
                break;
            case 4:
                dmonth = "maio";
                break;
            case 5:
                dmonth = "junho";
                break;
            case 6:
                dmonth = "julho";
                break;
            case 7:
                dmonth = "agosto";
                break;
            case 8:
                dmonth = "setembro";
                break;
            case 9:
                dmonth = "outubro";
                break;
            case 10:
                dmonth = "novembro";
                break;
            case 11:
                dmonth = "dezembro";
                break;
        }


        switch (day) {
            case 1:
                doW = "Domingo";
                break;
            case 2:
                doW = "Segunda-feira";
                break;
            case 3:
                doW = "Terça-feira";
                break;
            case 4:
                doW = "Quarta-feira";
                break;
            case 5:
                doW = "Quinta-feira";
                break;
            case 6:
                doW = "Sexta-feira";
                break;
            case 7:
                doW = "Sábado";
                break;

        }

        dayOfWeek.setText(doW + ",");
        date.setText(c.get(Calendar.DAY_OF_MONTH) + " de " + dmonth + " de " + c.get(Calendar.YEAR));


    }


    public void setUp(int fragmentID, final DrawerLayout drawerLayout) {

        containerView = getActivity().findViewById(fragmentID);

        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                containerView.bringToFront();


                try {
                    sync.setText(((MainReadActivity) getActivity()).db.getParam("sync"));
                    Date d = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                    String datetime = df.format(d);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }


    public void save(String opt) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Gate", opt);
        editor.commit();
    }

    public String load() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String gate = sharedPreferences.getString("Gate", "N/A");
        return gate;

    }


}
