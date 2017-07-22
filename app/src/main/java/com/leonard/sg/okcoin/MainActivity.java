package com.leonard.sg.okcoin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.leonard.sg.okcoin.constant.Constants;
import com.leonard.sg.okcoin.service.robot.SyncAndTradeService;
import com.leonard.sg.okcoin.service.robot.aidl.ISyncAndTradeService;
import com.leonard.sg.okcoin.ui.MainActivityUI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class MainActivity extends ActionBarActivity {

    /*
     * for SyncAndTradeService
     */
    private Intent syncAndTradeServiceIntent;
    private ISyncAndTradeService syncAndTradeService;
    private ScheduledExecutorService mainActivityUIScheduledExecutorService;
    private boolean hasSyncAndTradeServiceBounded = false;

    private ServiceConnection syncAndTradeServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            syncAndTradeService = ISyncAndTradeService.Stub.asInterface(service);

            mainActivityUIScheduledExecutorService = Executors.newScheduledThreadPool(Constants.EXECUTOR_CORE_POOL_SIZE);

            Thread thread = new Thread(new MainActivityUI(MainActivity.this, syncAndTradeService, mainActivityUIScheduledExecutorService));
            thread.start();

            hasSyncAndTradeServiceBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mainActivityUIScheduledExecutorService.shutdownNow();
            syncAndTradeService = null;
            hasSyncAndTradeServiceBounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        syncAndTradeServiceIntent = new Intent(this, SyncAndTradeService.class);

        startService(syncAndTradeServiceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();

        bindService(syncAndTradeServiceIntent, syncAndTradeServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();

        unbindService(syncAndTradeServiceConnection);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (isTaskRoot()) {
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    return true;
                } else {
                    super.onKeyDown(keyCode, event);
                    return false;
                }
            default:
                super.onKeyDown(keyCode, event);
                return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                if (hasSyncAndTradeServiceBounded) {
                    if (MainActivityUI.isUIComponentsInitialized) {
                        return false;
                    } else {
                        Toast.makeText(getApplicationContext(), "UI is initializing", Toast.LENGTH_LONG).show();
                        return true;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Service connection is not ready", Toast.LENGTH_LONG).show();
                    return true;
                }
        }

        return false;
    }
}
