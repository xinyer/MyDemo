package com.wangxin.rfidreader;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MSG_READ_OK = 1000;
    private static final int MSG_READ_TIME_OUT = 1001;

    private static final String KEY_BYTE_DATA = "key_byte_data";

    @BindView(R.id.tv_content)
    TextView mContentView;

    private UsbEndpoint mEndpoint;
    private UsbDeviceConnection mUsbDeviceConnection;
    private byte[] bytes = new byte[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        USBDeviceHelper.closeUsbDeviceConnection(mUsbDeviceConnection);
    }

    @OnClick(R.id.btn_get_devices)
    public void getDeviceList() {
        Log.d(TAG, "getDeviceList|Thread name:" + Thread.currentThread().getName());
        UsbDevice device = USBDeviceHelper.checkUsbDevice(this);
        mContentView.setText("" + device);
    }

    @OnClick(R.id.btn_official_read_data)
    public void readData() {
        UsbDevice device = USBDeviceHelper.checkUsbDevice(this);
        if (device!=null) {
            USBDeviceHelper.readDataUseOfficialAPI(this, device);
        }
    }

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_READ_OK:
                    byte[] array = msg.getData().getByteArray(KEY_BYTE_DATA);
                    String s = getText(array);
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    mContentView.append(s+"\n");
                    break;

                case MSG_READ_TIME_OUT:
                    communicate();
                    break;
            }
            return true;
        }
    };


    private Handler mHandler = new Handler(mCallback);

    @OnClick(R.id.btn_communicate)
    public void communicate() {
        if (mEndpoint!=null && mUsbDeviceConnection!=null) {
            new TransferRunnable().start();
        } else {
            UsbDevice device = USBDeviceHelper.checkUsbDevice(this);
            if (device != null) {
                Pair<UsbEndpoint, UsbDeviceConnection> pair = USBDeviceHelper.connectUsbDevice(this, device);
                if (pair != null) {
                    mEndpoint = pair.first;
                    mUsbDeviceConnection = pair.second;
                    new TransferRunnable().start();
                } else {
                    Log.d(TAG, "communicate|Usb device connection is fail.");
                }
            } else {
                Log.d(TAG, "communicate|Usb device is null.");
            }
        }
    }

    private static int TIMEOUT = 0;

    class TransferRunnable extends Thread {

        @Override
        public void run() {
            Log.d(TAG, "-----run-----");
            Log.d(TAG, "Thread name:" + Thread.currentThread().getName());
            Arrays.fill(bytes, (byte) 0);
            int readRet = mUsbDeviceConnection.bulkTransfer(mEndpoint, bytes, bytes.length, TIMEOUT);
            if (readRet<0) {
                Log.d(TAG, "-----bytes is empty.-----");
                mHandler.sendEmptyMessage(MSG_READ_TIME_OUT);
            } else {
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_READ_OK;
                Bundle data = new Bundle();
                byte[] tmpBytes = new byte[16];
                System.arraycopy(bytes, 0, tmpBytes, 0, bytes.length);
                data.putByteArray(KEY_BYTE_DATA, tmpBytes);
                msg.setData(data);
                mHandler.sendMessage(msg);

                Arrays.fill(bytes, (byte)0);
            }
        }

        private void readData(UsbEndpoint outEndpoint, UsbDeviceConnection connection) {
            int outMax = outEndpoint.getMaxPacketSize();
            //int inMax = inEndpoint.getMaxPacketSize();
            ByteBuffer byteBuffer = ByteBuffer.allocate(outMax);
            UsbRequest usbRequest = new UsbRequest();
            usbRequest.initialize(connection, outEndpoint);
            usbRequest.queue(byteBuffer, outMax);
            if(connection.requestWait() == usbRequest){
                byte[] retData = byteBuffer.array();
                for(Byte byte1 : retData){
                    System.err.println(byte1);
                }
            }
        }
    }

    private String getText(byte[] bytes) {
        String dStr = "";
        for (int i = 0; i < bytes.length; i++) {
            dStr += String.format("%02x ", bytes[i]);
        }
        return dStr.toUpperCase();
    }


}
