package usbserial.util;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import usbserial.driver.UsbSerialPort;

import java.io.IOException;

/**
 * 看一下数据需要怎么维护呢?
 * 
 * BB,7E单字节校验的误区还是有可能出现那样的情况,那么需要的是数据的长度(8|24)
 * 
 * 现在可以简单一点,
 * 
 * @author Hasee
 *
 */
public class SerialInputOutputManager implements Runnable {

	private static final String TAG = "Serial";
	private static final boolean DEBUG = true;
	private static final int READ_WAIT_MILLIS = 100;
	private final UsbSerialPort mDriver;
	byte recieveData[] = new byte[1024];
	public byte bufferData[];
	public LocalBroadcastManager broadcastManager;
	final String ACTION_RFID_EPC = "hyt.shopcarrfid.ACTION_RFID_EPC";
	public byte[] inventory = { (byte) 0xbb, 0x00, 0x22, 0x00, 0x00, 0x22, 0x7e };

	private enum State {
		STOPPED, RUNNING, STOPPING
	}

	private State mState = State.STOPPED;

	public SerialInputOutputManager(UsbSerialPort driver) {
		mDriver = driver;
	}

	public synchronized void stop() {
		if (getState() == State.RUNNING) {
			Log.i(TAG, "Stop requested");
			mState = State.STOPPING;
		}
	}

	private synchronized State getState() {
		return mState;
	}

	@Override
	public void run() {
		synchronized (this) {
			if (getState() != State.STOPPED) {
				throw new IllegalStateException("Already running.");
			}
			mState = State.RUNNING;
		}
		Log.i(TAG, "Running ..");

		while (true) {
			if (getState() != State.RUNNING) {
				Log.i(TAG, "Stopping mState=" + getState());
				break;
			}
			// 简单一点命令都可以固定下来,这里只是把数据放在缓存里面,然后之后才会写入到串口
			try {
				mDriver.write(inventory, READ_WAIT_MILLIS);
				Thread.sleep(400);
				step();
			} catch (Exception e) {
				Log.i(TAG, "Run ending due to exception: " + e.getMessage(), e);

			}
		}

	}

	private void step() throws IOException {
		int len = mDriver.read(recieveData, READ_WAIT_MILLIS);
		if (len > 0) {
			if (broadcastManager != null) {
				final byte[] data = new byte[len];
				System.arraycopy(recieveData, 0, data, 0, len);
				if (DEBUG) {
					Log.i(TAG, "R:" + byte2HexStr(recieveData, len));
				}
				bufferData = copyBytes2(bufferData, data);
				byte[] chidData = parseData();
				while (chidData != null) {
					chidData = parseData();
				}
			}
		}
	}

	private byte[] copyBytes(byte[] buffer1, byte[] buffer2, int start, int end) {
		if (buffer2 == null) {
			return buffer1;
		}
		// 位置需要确定,不然会出现一些问题
		if (buffer1 == null && buffer2 != null) {
			int length2 = end - start;
			byte[] buffer3 = new byte[length2];
			System.arraycopy(buffer2, start, buffer3, 0, length2);
			return buffer3;
		}
		int length1 = buffer1.length, length2 = end - start;
		byte[] buffer3 = new byte[length1 + length2];
		System.arraycopy(buffer1, 0, buffer3, 0, length1);
		System.arraycopy(buffer2, start, buffer3, length1, length2);
		return buffer3;
	}

	private byte[] copyBytes2(byte[] buffer1, byte[] buffer2) {
		if (buffer2 == null) {
			return buffer1;
		}
		// 位置需要确定,不然会出现一些问题
		if (buffer1 == null && buffer2 != null) {
			return buffer2;
		}
		int length1 = buffer1.length, length2 = buffer2.length;
		byte[] buffer3 = new byte[length1 + length2];
		System.arraycopy(buffer1, 0, buffer3, 0, length1);
		System.arraycopy(buffer2, 0, buffer3, length1, length2);
		return buffer3;
	}

	private String byte2HexStr(byte[] buffer, int end) {
		StringBuilder sb = new StringBuilder("{");
		for (int index = 0; index < end; index++) {
			int res = buffer[index] & 0xff;
			// if (res > 127) {
			// sb.append("(byte)");
			// }
			// sb.append("0x");
			if (res < 16) {
				sb.append("0").append(Integer.toHexString(res)).append(",");
			} else {
				sb.append(Integer.toHexString(res)).append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private byte[] parseData() {
		if (bufferData != null) {
			int length = bufferData.length;
			if (length >= 8) {
				int start = -1;
				int end = -1;
				for (int index = 0; index < length; index++) {
					if ((bufferData[index] & 0xff) == 0xbb && start == -1) {
						start = index;
					} else if ((bufferData[index] & 0xff) == 0x7e) {
						end = index;
						if (end - start == 7 || end - start >= 23) {
							break;
						}
					}
				}
				if (DEBUG) {
					// Log.i(TAG, "start:" + start + ",end:" + end + ",length:"
					// + length + ",cha:" + (end - start));

				}
				if (end - start == 23 || end - start == 7) {
					int chidLength = end - start + 1;
					byte chidData[] = new byte[chidLength];
					System.arraycopy(bufferData, start, chidData, 0, chidLength);
					if (end == bufferData.length - 1) {
						bufferData = null;

					} else {
						bufferData = copyBytes(null, bufferData, end + 1, bufferData.length);

					}
					sendEpc(chidData);
					return chidData;
				} else {
					// 如果中间还出现过
					if (end == bufferData.length - 1) {
						bufferData = null;
					} else {
						bufferData = copyBytes(null, bufferData, end + 1, bufferData.length);
					}
				}
			}
		}
		return null;
	}

	private void sendEpc(byte[] data) {
//		if (broadcastManager != null) {
//			RfidMode rfidMode = RfidProtocal.parseRfidData(data);
//			InventoryMode inventoryMode = RfidProtocal.parseInventory(rfidMode);
//			Intent intent = new Intent(ACTION_RFID_EPC);
//			intent.putExtra("inventoryMode", inventoryMode);
//			broadcastManager.sendBroadcast(intent);
//		}
	}
}
