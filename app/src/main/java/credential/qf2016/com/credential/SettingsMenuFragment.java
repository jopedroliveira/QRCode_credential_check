package credential.qf2016.com.credential;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by PedroOliveira on 15/04/16.
 */
public class SettingsMenuFragment extends Fragment {


    private View containerView;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

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

        return layout;
    }


    public void setUp(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }






}
