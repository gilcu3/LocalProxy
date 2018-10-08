package uci.localproxy.proxycore;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;


import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.WifiProxyChanger;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.ApiNotSupportedException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.NullWifiConfigurationException;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import uci.localproxy.R;
import uci.localproxy.proxycore.core.HttpForwarder;
import uci.localproxy.proxyscreen.ProxyActivity;


public class ProxyService extends Service {
    /*
     * Este es el servicio que inicia el servidor
     * Permanece en el área de notificación
     * */

    public static final String MESSAGE_TAG = "message";

    public static final String SERVICE_RECIVER_NAME = "service-receiver";

    public static final int SERVICE_STARTED_SUCCESSFUL = 0;

    public static final int ERROR_STARTING_SERVICE = 1;

    public static boolean IS_SERVICE_RUNNING = false;

    private String user = "";

    //    private ServerTask s;
    private HttpForwarder proxyThread;
    private boolean set_global_proxy;

    private int NOTIFICATION = 1337;

    private ExecutorService executor;

    @Override
    public void onCreate() {
        executor = Executors.newSingleThreadExecutor();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (proxyThread != null) {
            executor.shutdown();
            proxyThread.halt();
            if (set_global_proxy) {
                Toast.makeText(this, getString(R.string.OnNoProxy), Toast.LENGTH_LONG).show();
                try {
                    WifiProxyChanger.clearProxySettings(this);
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException |
                        NoSuchFieldException | IllegalAccessException | NullWifiConfigurationException | ApiNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        IS_SERVICE_RUNNING = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getExtras() == null) {
//            Log.e(getClass().getName(), "Error starting service");
        }

        user = intent.getStringExtra("user");
        String pass = intent.getStringExtra("pass");
        String server = intent.getStringExtra("server");
        int inputport = Integer.valueOf(intent.getStringExtra("inputport"));
        int outputport = Integer.valueOf(intent.getStringExtra("outputport"));
        set_global_proxy = intent.getBooleanExtra("set_global_proxy", true);
        String bypass = intent.getStringExtra("bypass");
        String domain = intent.getStringExtra("domain");


//        Log.i(getClass().getName(), "Starting for user " + user + ", server " + server + ", input port " + String.valueOf(inputport) + ", output port" + String.valueOf(outputport) + " and bypass string: " + bypass);

        try {
            proxyThread = new HttpForwarder(server, inputport, user, pass, outputport, true, bypass,
                    domain, getApplicationContext());

            executor.execute(proxyThread);
            IS_SERVICE_RUNNING = true;
            notifyit();

            //configuring wifi settings
            try {
                if (set_global_proxy) {
                    Toast.makeText(this, getString(R.string.OnProxy), Toast.LENGTH_LONG).show();
                    WifiProxyChanger.changeWifiStaticProxySettings("127.0.0.1", outputport, this);
                }
            }catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException |
                    NoSuchFieldException | IllegalAccessException | NullWifiConfigurationException | ApiNotSupportedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
//            Intent i = new Intent(SERVICE_RECIVER_NAME);
//            i.putExtra(MESSAGE_TAG, ERROR_STARTING_SERVICE);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }

        //START_REDELIVER_INTENT permite que si el sistema mata el servicio entonces cuando intenta reiniciarlo envia el mismo Intent que se envio para
        //iniciarlo por primera vez
        return START_REDELIVER_INTENT;
    }

    public void notifyit() {
        /*
         * Este método asegura que el servicio permanece en el área de notificación
		 * */

        Intent i = new Intent(this, ProxyActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);


        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher5)
                .setContentText(getApplicationContext().getString(R.string.excuting_proxy_service_notification) + " " + user)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent);


        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
            notification.priority = Notification.PRIORITY_MAX;
        }

        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(NOTIFICATION, notification);
    }

}
