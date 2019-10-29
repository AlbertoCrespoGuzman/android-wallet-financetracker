package ta.na.mao.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Date;

import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Blocker;
import ta.na.mao.utils.services.NetworkSchedulerServiceOld;

public class ConnectivityReceiver extends BroadcastReceiver {

    public ConnectivityReceiverListener mConnectivityReceiverListener;

    public ConnectivityReceiver(){}
    public ConnectivityReceiver(ConnectivityReceiverListener listener) {
        mConnectivityReceiverListener = listener;
        Log.e("ConnectivityReceiver","mConnectivityReceiverListener  is null?? "
                + (mConnectivityReceiverListener == null));
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("onReceive","ConnectivityReceiverListener onReceive !!!");
        if(mConnectivityReceiverListener != null) {
            mConnectivityReceiverListener.onNetworkConnectionChanged(isConnected(context));
        }else{
            if(isConnected(context)){
                DatabaseManager db = new DatabaseManager(context);
                if(db.findBlocker().isBlocked()){
                    Log.e("Timer", "is blocked!");
                    NetworkSchedulerServiceOld.checkBlocker(context);

                }else{
                    Log.e("Timer", "NOT Blocked, doing job");
                    Blocker blocker = db.findBlocker();
                    blocker.setBlocked(true);
                    blocker.setLast_update(new Date());
                    db.saveBlocker(blocker);

                    NetworkSchedulerServiceOld.updateData(context);

                }
            }
        }

    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}