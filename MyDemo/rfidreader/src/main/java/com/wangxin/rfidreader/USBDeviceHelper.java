package com.wangxin.rfidreader;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * USBDeviceHelper
 * Created by wangxin on 16/6/1.
 */
public class USBDeviceHelper {

    private static final int VENDOR_ID = 2303;
    private static final int PRODUCT_ID = 9;

    public static UsbDevice checkUsbDevice(Context ctx) {
        UsbManager usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<Map.Entry<String, UsbDevice>> it = deviceList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UsbDevice> map = it.next();
            String key = map.getKey();
            UsbDevice usbDevice = map.getValue();
            if (usbDevice.getVendorId() == VENDOR_ID && usbDevice.getProductId() == PRODUCT_ID) {
                return usbDevice;
            }
            System.out.println("key->" + key);
            System.out.println(usbDevice);
        }

        return null;
    }

    public static Pair<UsbEndpoint, UsbDeviceConnection> connectUsbDevice(Context ctx, UsbDevice usbDevice) {

        if (usbDevice == null) {
            return null;
        }
        UsbManager usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        UsbInterface intf = usbDevice.getInterface(0);
        UsbEndpoint endpoint = intf.getEndpoint(0);
        UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
        connection.claimInterface(intf, true);

        return new Pair<>(endpoint, connection);
    }
}
