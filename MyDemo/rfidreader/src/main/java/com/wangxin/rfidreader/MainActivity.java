package com.wangxin.rfidreader;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int VENDOR_ID = 2303;
    private static final int PRODUCT_ID = 9;

    private static final int MSG_READ_OK = 1000;
    private static final int MSG_READ_TIME_OUT = 1001;

    private static final String KEY_BYTE_DATA = "key_byte_data";

    @BindView(R.id.tv_content)
    TextView mContentView;

    UsbManager mUsbManager;
    UsbDevice mUsbDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @OnClick(R.id.btn_get_devices)
    public void getDeviceList() {
        System.out.println("Thread name:"+Thread.currentThread().getName());
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<Map.Entry<String, UsbDevice>> it = deviceList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UsbDevice> map = it.next();
            String key = map.getKey();
            UsbDevice usbDevice = map.getValue();
            if (usbDevice.getVendorId() == VENDOR_ID && usbDevice.getProductId() == PRODUCT_ID) {
                mUsbDevice = usbDevice;
            }
            System.out.println("key->" + key);
            System.out.println(usbDevice);

            mContentView.append("---------------------------------------------\n");
            mContentView.append("key->" + key + "\n");
            mContentView.append(usbDevice+"\n");
            mContentView.append("---------------------------------------------\n");
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
                    break;

                case MSG_READ_TIME_OUT:
                    communicate();
                    break;
            }
            return true;
        }
    };


    private Handler mHandler = new Handler(mCallback);


    private static int TIMEOUT = 2000;
    private boolean forceClaim = true;

    @OnClick(R.id.btn_communicate)
    public void communicate() {
        getDeviceList();

        if (mUsbDevice == null) {
            return;
        }

        UsbInterface intf = mUsbDevice.getInterface(0);
        UsbEndpoint endpoint = intf.getEndpoint(0);
        UsbDeviceConnection connection = mUsbManager.openDevice(mUsbDevice);
        connection.claimInterface(intf, forceClaim);

        new Thread(new TransferRunnable(endpoint, connection)).start();
    }

    class TransferRunnable implements Runnable {

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
            System.out.println("Thread name:"+Thread.currentThread().getName());
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

        boolean isByteArrayEmpty(byte[] bytes) {
            if (bytes==null) return true;

            for (byte b:bytes) {
                if (b!=0) {
                    return false;
                }
            }

            return true;
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
