package laanayabdrzak.ma.rxconnectivitystatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by abderrazak on 13/04/2016.
 * BeenReactiveStateNetwork is an Android library
 * listening network connection state with RxJava Observables.
 * It can be easily used with RxAndroid.
 */
public class RxConnectivityState {

    private boolean checkInternet = false;
    private UtilityNetworkState status = UtilityNetworkState.UNKNOWN;

    /**
     * Enables Internet connection check.
     * When it's called WIFI_CONNECTED_HAS_INTERNET and WIFI_CONNECTED_HAS_NO_INTERNET statuses
     * can be emitted by observeConnectivity(context) method. When it isn't called
     * only WIFI_CONNECTED can by emitted by observeConnectivity(context) method.
     *
     * @return ReactiveNetwork object
     */
    public RxConnectivityState enableInternetCheck() {
        checkInternet = true;
        return this;
    }

    /**
     * Observes UtilityNetworkState,
     * which can be WIFI_CONNECTED, MOBILE_CONNECTED or OFFLINE
     *
     * @param context Context of the activity or an application
     * @return RxJava Observable with UtilityNetworkState
     */
    public Observable<UtilityNetworkState> observeConnectivity(final Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        return Observable.create(new Observable.OnSubscribe<UtilityNetworkState>() {
            @Override public void call(final Subscriber<? super UtilityNetworkState> subscriber) {
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override public void onReceive(Context context, Intent intent) {
                        final UtilityNetworkState newStatus = getConnectivityStatus(context, checkInternet);

                        // we need to perform check below,
                        // because after going off-line, onReceive() is called twice
                        if (newStatus != status) {
                            status = newStatus;
                            subscriber.onNext(newStatus);
                        }
                    }
                };

                context.registerReceiver(receiver, filter);

                subscriber.add(unsubscribeInUiThread(new Action0() {
                    @Override public void call() {
                        context.unregisterReceiver(receiver);
                    }
                }));
            }
        }).defaultIfEmpty(UtilityNetworkState.OFFLINE);
    }

    public UtilityNetworkState getConnectivityStatus(final Context context,
                                                     final boolean checkInternet) {
        final String service = Context.CONNECTIVITY_SERVICE;
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return UtilityNetworkState.OFFLINE;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            if (checkInternet) {
                return getWifiInternetStatus(networkInfo);
            } else {
                return UtilityNetworkState.WIFI_CONNECTED;
            }
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return UtilityNetworkState.MOBILE_CONNECTED;
        }

        return UtilityNetworkState.OFFLINE;
    }

    private UtilityNetworkState getWifiInternetStatus(final NetworkInfo networkInfo) {
        if (networkInfo.isConnected()) {
            return UtilityNetworkState.WIFI_CONNECTED_NETWORK_AVAILABLE;
        } else {
            return UtilityNetworkState.WIFI_CONNECTED_NETWORK_NOT_AVAILABLE;
        }
    }

    private Subscription unsubscribeInUiThread(final Action0 unsubscribe) {
        return Subscriptions.create(new Action0() {

            @Override public void call() {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    unsubscribe.call();
                } else {
                    final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
                    inner.schedule(new Action0() {
                        @Override public void call() {
                            unsubscribe.call();
                            inner.unsubscribe();
                        }
                    });
                }
            }
        });
    }

    public static void safelyUnsubscribeToRxConnectivityState(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

}
