package im.adamant.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import im.adamant.android.R;
import im.adamant.android.dagger.EncryptKeyPairServiceModule;
import im.adamant.android.helpers.NotificationHelper;
import im.adamant.android.interactors.SettingsInteractor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static im.adamant.android.Constants.ADAMANT_SYSTEM_NOTIFICATION_CHANNEL_ID;

public class EncryptKeyPairService extends Service {
    public static final String VALUE_KEY = "value";
    private static final int NOTIFICATION_ID = 34242;

    @Inject
    SettingsInteractor settingsInteractor;

    @Named(EncryptKeyPairServiceModule.NAME)
    @Inject
    CompositeDisposable subscriptions;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.adamant_system_notification_channel);
            channelId = NotificationHelper.createNotificationChannel(ADAMANT_SYSTEM_NOTIFICATION_CHANNEL_ID, channelName, this);
        }

        String title = getString(R.string.app_name);
        String text = getString(R.string.encrypt_keypair_notification_message);

        startForeground(NOTIFICATION_ID, NotificationHelper.buildServiceNotification(channelId, this, title, text));
        if (intent.getExtras() != null){
            boolean value = intent.getExtras().getBoolean(VALUE_KEY, false);
            saveKeyPair(value);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }

    private void saveKeyPair(boolean value) {
        WeakReference<Service> thisReference = new WeakReference<>(this);
        Disposable subscribe = settingsInteractor
                .saveKeypair(value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> stopForeground(thisReference),
                    (error) -> stopForeground(thisReference)
                );
        subscriptions.add(subscribe);
    }

    private static void stopForeground(WeakReference<Service> serviceWeakReference) {
        Service service = serviceWeakReference.get();
        if (service != null) {
            service.stopForeground(true);
            service.stopSelf();
        }
    }
}
