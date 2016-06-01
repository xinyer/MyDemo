package com.wangxin.rfidreader;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_get_devices)
    public void getDeviceList() {
        System.out.println("Thread name:" + Thread.currentThread().getName());
        UsbDevice device = USBDeviceHelper.checkUsbDevice(this);
        mContentView.setText("" + device);
    }

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_READ_OK:
                    byte[] array = msg.getData().getByteArray(KEY_BYTE_DATA);
                    String s = getText(array);
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
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
        UsbDevice device = USBDeviceHelper.checkUsbDevice(this);
        if (device != null) {
            Pair<UsbEndpoint, UsbDeviceConnection> pair = USBDeviceHelper.connectUsbDevice(this, device);
            if (pair != null) {
                new TransferRunnable(pair.first, pair.second).start();
            } else {
                Log.d(TAG, "Usb device connection is fail.");
            }
        } else {
            Log.d(TAG, "Usb device is null.");
        }
    }

    private static int TIMEOUT = 2000;

    class TransferRunnable extends Thread {

        UsbEndpoint endpoint;
        UsbDeviceConnection connection;
        private byte[] bytes = new byte[64];

        TransferRunnable(UsbEndpoint endpoint, UsbDeviceConnection connection) {
            this.endpoint = endpoint;
            this.connection = connection;
        }

        @Override
        public void run() {
            System.out.println("-----run-----");
            System.out.println("Thread name:" + Thread.currentThread().getName());
            connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
            if (isByteArrayEmpty(bytes)) {
                System.out.println("-----bytes is empty.-----");
                //mHandler.postDelayed(this, 1000);
                mHandler.sendEmptyMessage(MSG_READ_TIME_OUT);
            } else {
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_READ_OK;
                Bundle data = new Bundle();
                byte[] tmpBytes = new byte[64];
                System.arraycopy(bytes, 0, tmpBytes, 0, bytes.length);
                data.putByteArray(KEY_BYTE_DATA, tmpBytes);
                msg.setData(data);
                mHandler.sendMessage(msg);

                bytes = null;
                bytes = new byte[64];
            }
        }
    }

    private boolean isByteArrayEmpty(byte[] bytes) {
        if (bytes == null) return true;

        for (byte b : bytes) {
            if (b != 0) {
                return false;
            }
        }

        return true;
    }

    private String getText(byte[] bytes) {
        String dStr = "";
        for (int i = 0; i < bytes.length; i++) {
            dStr += String.format("%02x ", bytes[i]);
        }
        return dStr.toUpperCase();
    }

}
