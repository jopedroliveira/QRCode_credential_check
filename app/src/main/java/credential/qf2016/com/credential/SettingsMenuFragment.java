package credential.qf2016.com.credential;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by PedroOliveira on 15/04/16.
 */
public class SettingsMenuFragment extends Fragment {


    private TextView dayOfWeek;
    private TextView date;
    private SwitchCompat flashChecker;
    private DrawerLayout mDrawerLayout;
    private Spinner spinnerGate;
    private DrawerLayout.DrawerListener mDrawerToggle;

    private String doW;
    private String dmonth;


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

        flashChecker = (SwitchCompat) layout.findViewById(R.id.flashStatus);
        flashChecker.setTextOn("Flash On");
        flashChecker.setTextOff("Flash Off");
        flashChecker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    buttonView.setText("On");
                    Toast.makeText(getActivity(), "On", Toast.LENGTH_SHORT).show();
                } else {
                    buttonView.setText("Off");
                    Toast.makeText(getActivity(), "Off", Toast.LENGTH_SHORT).show();
                }

            }
        });


        spinnerGate = (Spinner) layout.findViewById(R.id.spinnerGates);

        spinnerGate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), spinnerGate.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        manageCalendar();


        return layout;
    }

    private void manageCalendar() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case 1:
                dmonth = "janeiro";
                break;
            case 2:
                dmonth = "jevereiro";
                break;
            case 3:
                dmonth = "março";
                break;
            case 4:
                dmonth = "abril";
                break;
            case 5:
                dmonth = "maio";
                break;
            case 6:
                dmonth = "junho";
                break;
            case 7:
                dmonth = "julho";
                break;
            case 8:
                dmonth = "agosto";
                break;
            case 9:
                dmonth = "setembro";
                break;
            case 10:
                dmonth = "outubro";
                break;
            case 11:
                dmonth = "novembro";
                break;
            case 12:
                dmonth = "dezembro";
                break;
        }

        int day = c.get(Calendar.DAY_OF_WEEK);
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


    public void setUp(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }


}
