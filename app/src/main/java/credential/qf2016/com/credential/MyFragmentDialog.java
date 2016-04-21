package credential.qf2016.com.credential;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;


/**
 * Created by PedroOliveira on 11/04/16.
 */
public class MyFragmentDialog extends Fragment {
    private Button sair;
    private Button entrar;
    private TextView id;
    private TextView name;
    private TextView days;
    private TextView type;
    private TextView subtype;
    private TextView zone;
    private TextView lastlog;
    private TextView atual;
    private int atualL;
    private int caso;
    private Credential c;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.dialog_confirmation, container, false);
        sair = (Button) view.findViewById(R.id.sair);
        entrar = (Button) view.findViewById(R.id.entrar);

        id = (TextView) view.findViewById(R.id.identificationnumber);
        name = (TextView) view.findViewById(R.id.name);
        days = (TextView) view.findViewById(R.id.days);
        type = (TextView) view.findViewById(R.id.type);
        subtype = (TextView) view.findViewById(R.id.subtype);
        zone = (TextView) view.findViewById(R.id.zone);
        lastlog = (TextView) view.findViewById(R.id.lastlog);
        atual = (TextView) view.findViewById(R.id.atual);

        entrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (caso) {
                    case 1:
                        c.changeArea(1);
                        close();
                        break;
                    case 2:
                        dialog("Nao tem premissão para entrar hoje");
                        //((MainReadActivity) getActivity()).v.vibrate(500);

                        // Criar mensagem de erro
                        // set text alert
                        // nao pode entrar nesse dia
                        break;
                    case 3:
                        dialog("Esta credencial já se encontra dentro do parque");
                        // meter registo na bd
                        break;

                }


            }

        });

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (caso) {
                    case 1:
                    case 2:
                        c.changeArea(-1);
                        dialog("Nao passou na entrada!");
                        // avisar que nao passou na entrada
                        // set text alert
                        // timer
                        break;
                    case 3:
                        c.changeArea(-1);
                        close();
                        break;


                }


            }
        });


        return view;
    }

    private void dialog(String errorMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setMessage(errorMessage);
        alertDialog.setCancelable(false);
        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                close();
            }
        });
        alertDialog.show();

    }


    public void close() {
        try {
            ((MainReadActivity) getActivity()).cameraSource.start(((MainReadActivity) getActivity()).cameraView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getView().setVisibility(View.INVISIBLE);

        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        c = ((MainReadActivity) getActivity()).credential;

        id.setText(c.getId());
        name.setText(((MainReadActivity) getActivity()).credential.getName());
        days.setText(((MainReadActivity) getActivity()).credential.getDays_s());
        type.setText(((MainReadActivity) getActivity()).credential.getType());
        subtype.setText(((MainReadActivity) getActivity()).credential.getSubtype());
        zone.setText(((MainReadActivity) getActivity()).credential.getPermissions_s());
        lastlog.setText(((MainReadActivity) getActivity()).credential.getLastLog());
        atual.setText(((MainReadActivity) getActivity()).credential.getArea_s());


        if (((MainReadActivity) getActivity()).credential.getArea() == -1) {
            if (c.checkDay()) {
                caso = 1;
                // Destacar entrar
            } else {
                caso = 2;
                // nao destacar nenhum
            }


        } else {
            caso = 3;
            // destacar sair

        }


        ((MainReadActivity) getActivity()).cameraSource.stop();

    }


    public void onBackPressed() {

        try {
            ((MainReadActivity) getActivity()).cameraSource.start(((MainReadActivity) getActivity()).cameraView.getHolder());
            getView().setVisibility(View.INVISIBLE);
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}