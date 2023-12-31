package com.batsworks.simplewebview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.batsworks.simplewebview.brodcast.InternetBrodcast;
import com.batsworks.simplewebview.brodcast.NotificationReceiver;
import com.batsworks.simplewebview.config.internet.CheckInternet;
import com.batsworks.simplewebview.fragments.Admin;
import com.batsworks.simplewebview.fragments.Home;
import com.batsworks.simplewebview.fragments.TimeCard;
import com.batsworks.simplewebview.fragments.Youtube;
import com.batsworks.simplewebview.observable.IntObservable;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.Gravity.BOTTOM;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET,
            Manifest.permission.POST_NOTIFICATIONS};
    private static final int PERMISSION_CODE = 1234;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton actionButton;
    private IntObservable intObservable;
    private BroadcastReceiver notificationReceiver, internetReceiver;
    int doubleClick = 0;
    private int time = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        askPermissions();

        IntentFilter filter = new IntentFilter("actionpause");
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, filter);
        setContentView(R.layout.activity_main);
        initComponents();
        checkingInternetStatus();
        observerInternet();
        btnClick();
    }

    private void initComponents() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
        bottomAppBar = findViewById(R.id.bottom_bar);
        actionButton = findViewById(R.id.float_btn);
        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnItemSelectedListener(navListener);
        intObservable = new IntObservable();
    }

    private void observerInternet() {
        CheckInternet.Status status = CheckInternet.networkInfo(this);
        final Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> handler.post(() -> {
            boolean unavaliable = status.equals(CheckInternet.Status.UNAVALIABLE);
            bottomAppBar.setVisibility(unavaliable ? View.INVISIBLE : View.VISIBLE);
            actionButton.setClickable(!unavaliable);
        }));
    }

    private void btnClick() {
        Handler handler = new Handler(Looper.getMainLooper());
        actionButton.setOnClickListener(click -> {
            doubleClick++;
            handler.postDelayed(() -> {
                if (doubleClick == 1) {
                    showDialog();
                    doubleClick = 0;
                }
            }, 500);
        });
    }

    private void checkingInternetStatus() {
        internetReceiver = new InternetBrodcast();
        registerReceiver(internetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        switch (item.getItemId()) {
            case R.id.placeholder:
                Handler handler = new Handler(Looper.getMainLooper());
                doubleClick++;
                handler.postDelayed(() -> {
                    if (doubleClick == 1) {
                        showDialog();
                        doubleClick = 0;
                    }
                }, 500);
                break;
            case R.id.nav_home:
                replaceFragment(new Home());
                break;
            case R.id.nav_admin:
                replaceFragment(new Admin());
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

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        dialog.show();
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnimation;
        dialog.getWindow().setGravity(BOTTOM);

        AppCompatButton downloadLayout = dialog.findViewById(R.id.download_action);
        AppCompatButton pictureLayout = dialog.findViewById(R.id.pip_action);
        AppCompatButton hideAndShowMenu = dialog.findViewById(R.id.hide_menu_btns);

        hideAndShowMenu.setOnClickListener(click -> {
            bottomAppBar.setVisibility(bottomAppBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            dialog.dismiss();
        });
        pictureLayout.setOnClickListener(click -> {
            intObservable.setChange(true);
            dialog.dismiss();
        });
        downloadLayout.setOnClickListener(click -> startActivity(new Intent(this, YoutubeDownload.class)));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN) {
                if (time == 2) {
                    actionButton.setVisibility(actionButton.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                    time = 1;
                } else {
                    time++;
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
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

    @Deprecated
    private void hideStatusBar() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void sharedPreference(String accepted) {
        SharedPreferences prefs = getSharedPreferences("have_aceepted", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("accepted", accepted);
        edit.apply();
    }

    private void askPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                permissionsNeeded.add(permission);
            }
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSION_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkingInternetStatus();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        SharedPreferences prefs = getSharedPreferences("have_aceepted", MODE_PRIVATE);
        if (requestCode == PERMISSION_CODE) {
            HashMap<String, Integer> permissionResult = new HashMap<>();
            int deniedCode = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permissionResult.put(permissions[i], grantResults[i]);
                    deniedCode++;
                }
            }

            if (deniedCode != 0) {
                for (Map.Entry<String, Integer> entry : permissionResult.entrySet()) {
                    String name = entry.getKey();
                    int result = entry.getValue();

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, name)) {
                        new AlertDialog.Builder(this).setTitle("Permissions")
                                .setCancelable(true).setMessage("Is important to allow all")
                                .setPositiveButton("accept", (dialog, which) -> {
                                    dialog.dismiss();
                                    askPermissions();
                                }).setNegativeButton("cancel", ((dialog, which) -> {
                                    dialog.dismiss();
                                })).create().show();
                    } else {
                        String haveAccepted = prefs.getString("accepted", "");
                        if (!haveAccepted.trim().equals("") || haveAccepted.startsWith("true")) {
                            new AlertDialog.Builder(this).setTitle("Permission denied")
                                    .setCancelable(true).setMessage("Permission denied")
                                    .setMessage("Allow all permissions at [Settings]>[Permissions]")
                                    .setPositiveButton("Go to Settings", ((dialog, which) -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    })).setNegativeButton("no", ((dialog, which) -> {
                                        sharedPreference("false");
                                        dialog.dismiss();
                                    })).create().show();
                        }
                        break;
                    }
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}