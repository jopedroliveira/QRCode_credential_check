package credential.qf2016.com.credential;

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
    private TextView info;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.dialog_confirmation, container, false);
        sair = (Button) view.findViewById(R.id.sair);
        entrar = (Button) view.findViewById(R.id.entrar);

        info = (TextView) view.findViewById(R.id.identificationnumber);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    ((MainReadActivity) getActivity()).cameraSource.start(((MainReadActivity) getActivity()).cameraView.getHolder());
                    getView().setVisibility(View.INVISIBLE);
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }





            }

        });

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ((MainReadActivity) getActivity()).cameraSource.start(((MainReadActivity) getActivity()).cameraView.getHolder());
                    getView().setVisibility(View.INVISIBLE);
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


        return view;
    }


    public void close(){
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        info = (TextView) getView().findViewById(R.id.identificationnumber);
        info.setText(((MainReadActivity) getActivity()).textInfo.toString());

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