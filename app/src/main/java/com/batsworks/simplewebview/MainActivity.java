package com.batsworks.simplewebview;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.batsworks.simplewebview.fragments.BatsWorksAdmin;
import com.batsworks.simplewebview.fragments.TimeCard;
import com.batsworks.simplewebview.fragments.Youtube;
import com.batsworks.simplewebview.observable.IntObservable;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton actionButton;
    private IntObservable intObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_main);
        initComponents();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
        btnClick();
    }

    private void initComponents() {
        bottomAppBar = findViewById(R.id.bottom_bar);
        actionButton = findViewById(R.id.float_btn);
        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(navListener);
        intObservable = new IntObservable();
    }

    private void btnClick() {
        actionButton.setOnClickListener(click -> intObservable.setChange(true));
        actionButton.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(new Home());
                break;
            case R.id.nav_admin:
                replaceFragment(new BatsWorksAdmin());
                break;
            case R.id.nav_timecard:
                replaceFragment(new TimeCard());
                break;
            case R.id.nav_youtube:
                replaceFragment(new Youtube(intObservable));
                break;
        }
        return true;
    };

    private void replaceFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomAppBar.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
        } else {
            bottomAppBar.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}