package demo.ipc.alaske.com.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Author: zhaocheng
 * Date: 2016-06-03
 * Time: 16:08
 * Name:IRemoteService
 * Introduction:
 */
public class IRemoteService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    private IBinder mIBinder = new IMyAidlInterface.Stub() {
        @Override
        public int add(int num1, int num2) throws RemoteException {
             return num1+num2;
        }
    };

}
