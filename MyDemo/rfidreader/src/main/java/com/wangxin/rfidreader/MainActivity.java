package com.wangxin.rfidreader;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tv_content)
    TextView mContentView;

    UsbManager usbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @OnClick(R.id.btn_get_devices)
    public void getDeviceList() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<Map.Entry<String, UsbDevice>> it = deviceList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UsbDevice> map = it.next();
            String key = map.getKey();
            UsbDevice usbDevice = map.getValue();
            System.out.println("key->" + key);
            System.out.println(usbDevice);

            mContentView.append("---------------------------------------------\n");
            mContentView.append("key->" + key + "\n");
            mContentView.append(usbDevice+"\n");
            mContentView.append("---------------------------------------------\n");
        }
    }


}
