package com.ujuzicode.mobilebluetothprinter.printer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WorkService extends Service {

	public static WorkThread workThread = null;
	private static List<Handler> targetsHandler = new ArrayList<Handler>(5);

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Handler mHandler = new MHandler(this);
		workThread = new WorkThread(mHandler);
		workThread.start();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Message msg = Message.obtain();
		msg.what = Global.MSG_ALLTHREAD_READY;
		notifyHandlers(msg);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		workThread.disconnectBt();
		workThread.disconnectBle();
		workThread.disconnectNet();
		workThread.disconnectUsb();
		workThread.quit();
		workThread = null;

	}

	static class MHandler extends Handler {

		WeakReference<WorkService> mService;

		MHandler(WorkService service) {
			mService = new WeakReference<WorkService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			notifyHandlers(msg);
		}
	}

	/**
	 * 
	 * @param handler
	 */
	public static void addHandler(Handler handler) {
		if (!targetsHandler.contains(handler)) {
			targetsHandler.add(handler);
		}
	}

	/**
	 * 
	 * @param handler
	 */
	public static void delHandler(Handler handler) {
		if (targetsHandler.contains(handler)) {
			targetsHandler.remove(handler);
		}
	}

	/**
	 * 
	 * @param msg
	 */
	public static void notifyHandlers(Message msg) {
		for (int i = 0; i < targetsHandler.size(); i++) {
			Message message = Message.obtain(msg);
			targetsHandler.get(i).sendMessage(message);
		}
	}

}
