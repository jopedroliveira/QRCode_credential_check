package credential.qf2016.com.credential;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import java.io.IOException;

/**
 * Created by PedroOliveira on 27/09/16.
 */
public class Passcode extends Fragment {

    private Toolbar toolbar;
    private String insertedPin;
    private EditText n4;
    private EditText n3;
    private EditText n2;
    private EditText n1;
    Button confirm;
    final int codeCode = 1887;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_acess, container, false);


        confirm = (Button) view.findViewById(R.id.confirmpin);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainReadActivity) getActivity()).cameraSource.stop();

        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        ((MainReadActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainReadActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainReadActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainReadActivity) getActivity()).setTitle("Queima das Fitas 2016");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainReadActivity) getActivity()).manualSync();
                close();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n1 = (EditText) getActivity().findViewById(R.id.first);
                n2 = (EditText) getActivity().findViewById(R.id.second);
                n3 = (EditText) getActivity().findViewById(R.id.third);
                n4 = (EditText) getActivity().findViewById(R.id.fourth);

                String nn1 = n1.getText().toString();
                String nn2 = n2.getText().toString();
                String nn3 = n3.getText().toString();
                String nn4 = n4.getText().toString();
                insertedPin = "" + nn1 + nn2 + nn3 + nn4;

                if (!TextUtils.isEmpty(insertedPin)) {
                    if (Integer.parseInt(insertedPin) == codeCode) {
                        proceed();
                    } else {
                        repeat("Código errado");
                    }

                } else {
                    repeat("Inserir código");
                }
            }
        });
    }


    private void proceed() {
        AdvancedSettings newf = new AdvancedSettings();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_layout_main, newf);
        transaction.addToBackStack("tag");
        transaction.commit();
    }


    private void repeat(String errorMessage) {


        AlertDialog.Builder adb = new AlertDialog.Builder((MainReadActivity)getActivity());
        adb.setTitle("Erro")
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }

                });


        AlertDialog alertDialog = adb.create();

        alertDialog.show();

        n1.getText().clear();
        n2.getText().clear();
        n3.getText().clear();
        n4.getText().clear();

    }


    private void counter() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ((MainReadActivity) getActivity()).suspend();
            }
        };
        ((MainReadActivity) getActivity()).mainHandler.postDelayed(r, ((MainReadActivity) getActivity()).timer);
    }

    public void close() {
        try {
            ((MainReadActivity) getActivity()).cameraSource.start(((MainReadActivity) getActivity()).cameraView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getView().setVisibility(View.INVISIBLE);
        counter();
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

}
