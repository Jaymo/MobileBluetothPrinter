package com.ujuzicode.mobilebluetothprinter.printer;

import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lvrenyang.io.BLEPrinting;
import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.NETPrinting;
import com.lvrenyang.io.Pos;
import com.lvrenyang.io.USBPrinting;

public class WorkThread extends Thread {

	private static final String TAG = "WorkThread";
	private static Handler workHandler = null;
	private static Looper mLooper = null;
	static Handler targetHandler = null;
	private static volatile boolean threadInitOK = false;
	private static boolean isConnecting = false;
	private static BTPrinting bt = null;
	private static BLEPrinting ble = null;
	private static NETPrinting net = null;
	private static USBPrinting usb = null;
	private static Pos pos = new Pos();

	WorkThread(Handler handler) {
		threadInitOK = false;
		targetHandler = handler;
		if (usb == null)
            usb = new USBPrinting();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			if (ble == null)
				ble = new BLEPrinting();
		}
		if (bt == null)
			bt = new BTPrinting();
		if (net == null)
			net = new NETPrinting();
	}

	@Override
	public void start() {
		super.start();
		while (!threadInitOK);

	}

	@Override
	public void run() {
		Looper.prepare();
		mLooper = Looper.myLooper();
		if (null == mLooper)
			Log.v(TAG, "mLooper is null pointer");
		else
			Log.v(TAG, "mLooper is valid");
		workHandler = new WorkHandler();
		threadInitOK = true;
		Looper.loop();
	}

	private static class WorkHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case Global.MSG_WORKTHREAD_HANDLER_CONNECTBT: {
				isConnecting = true;

				pos.Set(bt);

				String BTAddress = (String) msg.obj;
				boolean result = bt.Open(BTAddress);

				Message smsg = targetHandler
						.obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT);
				smsg.arg1 = result ? 1 : 0;
				targetHandler.sendMessage(smsg);

				isConnecting = false;
				break;
			}

			case Global.MSG_WORKTHREAD_HANDLER_CONNECTBLE: {
				isConnecting = true;

				pos.Set(ble);

				String BTAddress = (String) msg.obj;
				boolean result = ble.Open(BTAddress);

				Message smsg = targetHandler
						.obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTBLERESULT);
				smsg.arg1 = result ? 1 : 0;
				targetHandler.sendMessage(smsg);

				isConnecting = false;
				break;
			}

			case Global.MSG_WORKTHREAD_HANDLER_CONNECTNET: {
				isConnecting = true;

				pos.Set(net);

				int PortNumber = msg.arg1;
				String IPAddress = (String) msg.obj;
				boolean result = net.Open(IPAddress, PortNumber);

				Message smsg = targetHandler
						.obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTNETRESULT);
				smsg.arg1 = result ? 1 : 0;
				targetHandler.sendMessage(smsg);

				isConnecting = false;
				break;
			}

			case Global.MSG_WORKTHREAD_HANDLER_CONNECTUSB: {
				isConnecting = true;

				pos.Set(usb);

				UsbManager manager = (UsbManager) msg.obj;
				Bundle data = msg.getData();
				UsbDevice device = data.getParcelable(Global.PARCE1);

				boolean result = usb.Open(manager, device);
				Message smsg = targetHandler
						.obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTUSBRESULT);
				smsg.arg1 = result ? 1 : 0;
				targetHandler.sendMessage(smsg);

				isConnecting = false;
				break;
			}

			case Global.CMD_WRITE: {
				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);
				int offset = data.getInt(Global.INTPARA1);
				int count = data.getInt(Global.INTPARA2);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_WRITERESULT);
				if (pos.IO.Write(buffer, offset, count) == count) {
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			// USB写入自带流控制，不需要读取状态。
			case Global.CMD_POS_WRITE: {
				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);
				int offset = data.getInt(Global.INTPARA1);
				int count = data.getInt(Global.INTPARA2);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_WRITERESULT);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				if (result) {
					if (pos.IO.Write(buffer, offset, count) == count)
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETKEY: {
				Bundle data = msg.getData();
				byte[] key = data.getByteArray(Global.BYTESPARA1);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETKEYRESULT);

				if (result) {
					if (key != null) {
						pos.POS_SetKey(key);
					}
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_CHECKKEY: {
				Bundle data = msg.getData();
				byte[] key = data.getByteArray(Global.BYTESPARA1);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_CHECKKEYRESULT);
				if (result) {
					if (pos.POS_CheckKey(key))
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}
			case Global.CMD_POS_PRINTPICTURE: {
				Bundle data = msg.getData();
				Bitmap mBitmap = (Bitmap) data.getParcelable(Global.PARCE1);
				int nWidth = data.getInt(Global.INTPARA1);
				int nMode = data.getInt(Global.INTPARA2);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_PRINTPICTURERESULT);
				if (result) {
					if (mBitmap != null) {
						pos.POS_PrintPicture(mBitmap, nWidth, nMode);
					}

					result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

					if (result)
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_PRINTBWPICTURE: {
				Bundle data = msg.getData();
				// Bitmap mBitmap = (Bitmap) data.get(Global.OBJECT1);
				Bitmap mBitmap = (Bitmap) data.getParcelable(Global.PARCE1);
				int nWidth = data.getInt(Global.INTPARA1);
				int nMode = data.getInt(Global.INTPARA2);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_PRINTPICTURERESULT);
				if (result) {
					if (mBitmap != null) {
						pos.POS_PrintBWPic(mBitmap, nWidth, nMode);
					}

					result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

					if (result)
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SALIGN: {
				Bundle data = msg.getData();
				int align = data.getInt(Global.INTPARA1);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SALIGNRESULT);
				if (result) {
					pos.POS_S_Align(align);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETLINEHEIGHT: {
				Bundle data = msg.getData();
				int nHeight = data.getInt(Global.INTPARA1);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETLINEHEIGHTRESULT);
				if (result) {
					pos.POS_SetLineHeight(nHeight);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETRIGHTSPACE: {
				Bundle data = msg.getData();
				int nDistance = data.getInt(Global.INTPARA1);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETRIGHTSPACERESULT);
				if (result) {
					pos.POS_SetRightSpacing(nDistance);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_STEXTOUT: {
				Bundle data = msg.getData();
				String pszString = data.getString(Global.STRPARA1);
				String encoding = data.getString(Global.STRPARA2);
				int nOrgx = data.getInt(Global.INTPARA1);
				int nWidthTimes = data.getInt(Global.INTPARA2);
				int nHeightTimes = data.getInt(Global.INTPARA3);
				int nFontType = data.getInt(Global.INTPARA4);
				int nFontStyle = data.getInt(Global.INTPARA5);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_STEXTOUTRESULT);
				if (result) {
					if (pszString != null) {
						pos.POS_S_TextOut(pszString, encoding, nOrgx, nWidthTimes,
                                nHeightTimes, nFontType, nFontStyle);
					}
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETCHARSETANDCODEPAGE: {
				Bundle data = msg.getData();
				int nCharSet = data.getInt(Global.INTPARA1);
				int nCodePage = data.getInt(Global.INTPARA2);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETCHARSETANDCODEPAGERESULT);
				if (result) {
					pos.POS_SetCharSetAndCodePage(nCharSet, nCodePage);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETBARCODE: {
				Bundle data = msg.getData();
				String strBarcode = data.getString(Global.STRPARA1);
				int nOrgx = data.getInt(Global.INTPARA1);
				int nType = data.getInt(Global.INTPARA2);
				int nWidthX = data.getInt(Global.INTPARA3);
				int nHeight = data.getInt(Global.INTPARA4);
				int nHriFontType = data.getInt(Global.INTPARA5);
				int nHriFontPosition = data.getInt(Global.INTPARA6);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETBARCODERESULT);
				if (result) {
					pos.POS_S_SetBarcode(strBarcode, nOrgx, nType, nWidthX,
							nHeight, nHriFontType, nHriFontPosition);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETQRCODE: {
				Bundle data = msg.getData();
				String strQrcode = data.getString(Global.STRPARA1);
				int nWidthX = data.getInt(Global.INTPARA1);
				int nVersion = data.getInt(Global.INTPARA2);
				int necl = data.getInt(Global.INTPARA3);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETQRCODERESULT);
				if (result) {
					pos.POS_S_SetQRcode(strQrcode, nWidthX, nVersion, necl);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_EPSON_SETQRCODE: {
				Bundle data = msg.getData();
				String strQrcode = data.getString(Global.STRPARA1);
				int nWidthX = data.getInt(Global.INTPARA1);
				int nVersion = data.getInt(Global.INTPARA2);
				int necl = data.getInt(Global.INTPARA3);

				boolean result = false;

				result = USBPrinting.class.getName().equals(pos.IO.getClass().getName()) || pos.POS_QueryOnline(1000);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_EPSON_SETQRCODERESULT);
				if (result) {
					pos.POS_EPSON_SetQRCode(strQrcode, nWidthX, nVersion, necl);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_EMBEDDED_SEND_TO_UART: {
				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);
				int offset = data.getInt(Global.INTPARA1);
				int count = data.getInt(Global.INTPARA2);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_EMBEDDED_SEND_TO_UART_RESULT);
				if (pos.EMBEDDED_WriteToUart(buffer, offset, count)) {
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}
			}

		}

	}

	public void quit() {
		try {
			if (null != mLooper) {
				mLooper.quit();
				mLooper = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectBt() {
		try {
			bt.Close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectBle() {
		try {
			ble.Close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectNet() {
		try {
			net.Close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectUsb() {
		try {
			usb.Close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connectBle(String BTAddress) {
		if ((null != workHandler) && (null != mLooper)) {
			Message msg = workHandler
					.obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTBLE);
			msg.obj = BTAddress;
			workHandler.sendMessage(msg);
		} else {
			if (null == workHandler)
				Log.v(TAG, "workHandler is null pointer");

			if (null == mLooper)
				Log.v(TAG, "mLooper is null pointer");
		}
	}

	public void connectBt(String BTAddress) {
		if ((null != workHandler) && (null != mLooper)) {
			Message msg = workHandler
					.obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTBT);
			msg.obj = BTAddress;
			workHandler.sendMessage(msg);
		} else {
			if (null == workHandler)
				Log.v(TAG, "workHandler is null pointer");

			if (null == mLooper)
				Log.v(TAG, "mLooper is null pointer");
		}
	}

	public void connectNet(String IPAddress, int PortNumber) {
		if ((null != workHandler) && (null != mLooper)) {
			Message msg = workHandler
					.obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTNET);
			msg.arg1 = PortNumber;
			msg.obj = IPAddress;
			workHandler.sendMessage(msg);
		} else {
			if (null == workHandler)
				Log.v("WorkThread connectNet", "workHandler is null pointer");

			if (null == mLooper)
				Log.v("WorkThread connectNet", "mLooper is null pointer");
		}
	}

	public void connectUsb(UsbManager manager, UsbDevice device) {
		if ((null != workHandler) && (null != mLooper)) {
			Message msg = workHandler
					.obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTUSB);
			msg.obj = manager;
			Bundle data = new Bundle();
			data.putParcelable(Global.PARCE1, device);
			msg.setData(data);
			workHandler.sendMessage(msg);
		} else {
			if (null == workHandler)
				Log.v("WorkThread connectUsb", "workHandler is null pointer");

			if (null == mLooper)
				Log.v("WorkThread connectUsb", "mLooper is null pointer");

		}
	}

	public boolean isConnecting() {
		return isConnecting;
	}

	public boolean isConnected() {
		return bt.IsOpened() || net.IsOpened() || usb.IsOpened() || ble.IsOpened();
	}

	/**
	 * 
	 * @param cmd
	 */
	public void handleCmd(int cmd, Bundle data) {
		if ((null != workHandler) && (null != mLooper)) {
			Message msg = workHandler.obtainMessage(cmd);
			msg.setData(data);
			workHandler.sendMessage(msg);
		} else {
			if (null == workHandler)
				Log.v(TAG, "workHandler is null pointer");

			if (null == mLooper)
				Log.v(TAG, "mLooper is null pointer");
		}
	}
}
