package credential.qf2016.com.credential;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
    private TextView alert;
    ImageView iv;
    WebView web;
    private int atualL;
    private int caso;
    private Credential c;
    public Toolbar toolbar;
    String currentDate;


    private boolean syncAfter = true;


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
        alert = (TextView) view.findViewById(R.id.alert);
        iv = (ImageView) view.findViewById(R.id.imageView);


        entrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LocalDB db = ((MainReadActivity) getActivity()).db;
                String user =  db.getParam("user");
                switch (caso) {
                    case 1:
                        db.log(user, "Entrada", "", c.getNumber(), c.getArea(), 1);
                        c.changeArea(1, user);
                        close();
                        break;
                    case 2:
                        db.log(user, "Erro2", "Dia nao autorizado (Pressão botao entrada)", c.getNumber(), c.getArea(), c.getArea());
                        dialog("DIA NÃO AUTORIZADO");

                        //volta ao menu


                        // Criar mensagem de erro
                        // nao pode entrar nesse dia

                        break;
                    case 3:
                        db.log(user, "Erro2", "Entrada dupla", c.getNumber(), c.getArea(), c.getArea());
                        dialog("ENTRADA NÃO AUTORIZADA \n \n Credencial já dentro do parque:\n\n" + c.getLastLog());
                        if (syncAfter)
                            ((MainReadActivity) getActivity()).manualSync();

                        close();
                        //volta ao menu
                        // meter registo na bd
                        break;
                    case 4:
                        db.log(user, "Erro2", "Credencial desactivada (Pressão botao entrada)", c.getNumber(), c.getArea(), c.getArea());
                        dialog("CREDENCIAL DESACTIVADA");

                        //volta ao menu
                        break;

                }

                if (syncAfter)
                    ((MainReadActivity) getActivity()).manualSync();


            }

        });

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDB db = ((MainReadActivity) getActivity()).db;
                String user = db.getParam("user");
                switch (caso) {
                    case 1:
                        c.changeArea(-1, user);
                        dialog("Esta credencial não foi validada à entrada. Atencão!");
                        db.log(user, "Aviso", "Credencial nao foi validada a entrada", c.getNumber(), c.getArea(), c.getArea());
                        break;
                    case 2:
                    case 4:
                        db.log(user, "Saida Forcada", "Entrada barrada. Saida forcada.", c.getNumber(), c.getArea(), -1);
                        //Não fazer nada.. Vai embora e é asism que deve ser. Não esta autorizado a entrar
                        c.changeArea(-1, user);
                        close();
                        break;
                    case 3:
                        c.changeArea(-1, user);
                        db.log(user, "Saida", "", c.getNumber(), c.getArea(), -1);
                        close();
                        break;
                }

                if (syncAfter)
                    ((MainReadActivity) getActivity()).manualSync();


            }
        });


        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainReadActivity) getActivity()).cameraSource.stop();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm");
        try {
            Date d1 = df.parse("10:30");
            cal.set(Calendar.HOUR_OF_DAY, d1.getHours());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Date dt = new Date();  // current time
        int month = dt.getMonth();     // gets the current month
        int hours = dt.getHours();

        Log.d("CreDay", cal.toString());
        int cday = cal.get(Calendar.DAY_OF_MONTH) ;
        Log.d("CreDay","Mudar dia1  "+ cal.get(Calendar.HOUR));


        Log.d("CreDay","Mudar dia1  "+ hours);


        if (hours < 10) {
            cday = cday - 1;
            Log.d("CreDay","Mudar dia2 "+ cal.get(Calendar.HOUR));
            Log.d("CreDay","Mudar dia2 "+ cal.get(Calendar.HOUR_OF_DAY));
        }
        //currentDate = cday + "-" + (Integer.valueOf(cal.get(Calendar.MONTH)+1)) + "-" + cal.get(Calendar.YEAR);

        currentDate = String.format("%d-%02d-%d", cday, Integer.valueOf(cal.get(Calendar.MONTH) + 1), cal.get(Calendar.YEAR));

        c = ((MainReadActivity) getActivity()).credential;

        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);



        ((MainReadActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainReadActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainReadActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainReadActivity) getActivity()).setTitle("Queima das Fitas 2016");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (((MainReadActivity) getActivity()).db).log((((MainReadActivity) getActivity()).user),"Back", "", c.getNumber(), c.getArea(), c.getArea());
                ((MainReadActivity) getActivity()).manualSync();
                close();}
        });

        id.setText(c.getId());
        name.setText(((MainReadActivity) getActivity()).credential.getName());
        days.setText(((MainReadActivity) getActivity()).credential.getDays_s());
        type.setText(((MainReadActivity) getActivity()).credential.getType());
        subtype.setText(((MainReadActivity) getActivity()).credential.getSubtype());
        zone.setText(((MainReadActivity) getActivity()).credential.getPermissions_s());
        lastlog.setText(((MainReadActivity) getActivity()).credential.getLastLog());
        atual.setText(((MainReadActivity) getActivity()).credential.getArea_s());


        entrar.setBackgroundColor(getResources().getColor(R.color.entrarDesvan));
        sair.setBackgroundColor(getResources().getColor(R.color.sairDesvan));
        entrar.setTextColor(getResources().getColor(R.color.realblack));
        sair.setTextColor(getResources().getColor(R.color.realblack));
        alert.setVisibility(View.GONE);

        LocalDB db = ((MainReadActivity) getActivity()).db;
        String user = db.getParam("user");

        String url = c.getPhotoPath();


        //Picasso.with( ((MainReadActivity) getActivity())).load(url).into(iv);
        Picasso.with(((MainReadActivity) getActivity())).load(url).centerCrop().fit().into(iv);

        Log.d("CreD","ddddddd");
        if (c.getActive() == 1) {
            if (c.getArea() == -1) {
                if (c.checkDay(currentDate)) {
                    caso = 1;
                    //destacar entrar
                    entrar.setBackgroundColor(getResources().getColor(R.color.entrarRealcar));


                } else {
                    db.log(user, "Erro", "Dia nao autorizado (Lido)", c.getNumber(), c.getArea(), c.getArea());
                    alert.setText("DIA NÃO AUTORIZADO");
                    alert.setVisibility(View.VISIBLE);
                    sair.setBackgroundColor(getResources().getColor(R.color.sairRealcar));


                    AlertDialog.Builder adb = new AlertDialog.Builder(getView().getContext());
                    AlertDialog ad = adb.create();
                    ad.setMessage("DIA NÃO AUTORIZADO");
                    ad.setCancelable(false);

                    ad.setButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
                    //ad.show();
                    caso = 2;

                    // nao destacar nenhum
                }


            } else {
                caso = 3;
                // destacar sair
                sair.setBackgroundColor(getResources().getColor(R.color.sairRealcar));


            }
        } else {
            db.log(user, "Erro2", "Credencial desactivada (Lido)", c.getNumber(), c.getArea(), c.getArea());
            alert.setText("CREDENCIAL DESACTIVADA");
            alert.setVisibility(View.VISIBLE);
            sair.setBackgroundColor(getResources().getColor(R.color.sairRealcar));

            //Mostar mensagem vermelha
            // nao destacar nenhum


            caso = 4;
        }


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


    private void dialog(String errorMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setMessage(errorMessage);
        alertDialog.setCancelable(false);
        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (caso == 1) {
                    close();
                }
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
        counter();
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }


}