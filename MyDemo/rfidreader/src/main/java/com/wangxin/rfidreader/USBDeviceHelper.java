package com.wangxin.rfidreader;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.util.Pair;

import com.wangxin.rfidreader.api.ICReaderApi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * USBDeviceHelper
 * Created by wangxin on 16/6/1.
 */
public class USBDeviceHelper {

    private static final String TAG = USBDeviceHelper.class.getSimpleName();

    private static final int VENDOR_ID = 65535;
    private static final int PRODUCT_ID = 53;

    public static UsbDevice checkUsbDevice(Context ctx) {
        UsbManager usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceMap = usbManager.getDeviceList();
        Set<String> keySet = deviceMap.keySet();
        for (String key:keySet) {
            UsbDevice device = deviceMap.get(key);
            System.out.println("key->" + key);
            System.out.println(device);

            if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID) {

                int intfCount = device.getInterfaceCount();
                for (int i=0;i<intfCount;i++) {
                    UsbInterface usbInterface = device.getInterface(i);
                    System.out.println("interface:" + usbInterface.toString());
                    int endPointCount = usbInterface.getEndpointCount();
                    for (int j=0;j<endPointCount;j++) {
                        UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                        System.out.println("endPoint:" + endpoint.getDirection() + ", Usb out:" + UsbConstants.USB_DIR_OUT);
                    }
                }

                return device;
            }
        }

        return null;
    }

    public static Pair<UsbEndpoint, UsbDeviceConnection> connectUsbDevice(Context ctx, UsbDevice usbDevice) {

        if (usbDevice == null) {
            return null;
        }
        UsbManager usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        int intfCount = usbDevice.getInterfaceCount();
        Log.d(TAG, "interface count:" + intfCount);
        if (intfCount>0) {
            UsbInterface intf = usbDevice.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(0);
            UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
            connection.claimInterface(intf, true);

            return new Pair<>(endpoint, connection);
        }

        return null;

    }

    public static void closeUsbDeviceConnection(UsbDeviceConnection connection) {
        if (connection!=null) {
            connection.close();
        }
    }

    /******************************以下是设备商提供的API的测试方法**************************************/

    public static void readDataUseOfficialAPI(Context ctx, UsbDevice device) {
        String text = new String();
        UsbManager manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        ICReaderApi api = new ICReaderApi(device, manager);
//        byte mode1 = (byte) (true ? 0 : 1);
//        byte mode2 = (byte) (true ? 0 : 1);
        byte mode1 = (byte) 0;
        byte mode2 = (byte) 1;
        byte mode = (byte) ((mode1 << 1) | mode2); // reading
        // model
        byte blk_add = (byte) Integer.parseInt("10", 16); // block
        // address
//        byte num_blk = (byte) Integer.parseInt(true?"04":"01", 16); // block
        byte num_blk = (byte) Integer.parseInt("04", 16); // block

        // number
        byte[] snr = getByteArray("FF FF FF FF FF FF"); // key
        byte[] buffer = new byte[16 * num_blk]; // data read

        int result = api.API_PCDRead(mode, blk_add, num_blk, snr, buffer);

        text = showStatue(text, result);
        if (result == 0) {
            text = showData(text, snr, "The card number:\n", 0, 4);
            text = showData(text, buffer, "The card data:\n", 0, 16 * num_blk);
        } else {
            text = showStatue(text, snr[0]);
        }
        System.out.println("text->" + text);
//        showDialog(text);
    }

    private static byte[] getByteArray(String str) {
        str = str.replaceAll("[^0-9A-F]", "");
        byte[] ans = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            ans[i / 2] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
        }
        return ans;
    }

    private static String showStatue(String text, int Code) {
        String msg = null;
        switch (Code) {
            case 0x00:
                msg = "Command succeed.....";
                break;
            case 0x01:
                msg = "Command failed.....";
                break;
            case 0x02:
                msg = "Checksum error.....";
                break;
            case 0x03:
                msg = "Not selected COM port.....";
                break;
            case 0x04:
                msg = "Reply time out.....";
                break;
            case 0x05:
                msg = "Check sequence error.....";
                break;
            case 0x07:
                msg = "Check sum error.....";
                break;
            case 0x0A:
                msg = "The parameter value out of range.....";
                break;
            case 0x80:
                msg = "Command OK.....";
                break;
            case 0x81:
                msg = "Command FAILURE.....";
                break;
            case 0x82:
                msg = "Reader reply time out error.....";
                break;
            case 0x83:
                msg = "The card does not exist.....";
                break;
            case 0x84:
                msg = "The data is error.....";
                break;
            case 0x85:
                msg = "Reader received unknown command.....";
                break;
            case 0x87:
                msg = "Error.....";
                break;
            case 0x89:
                msg = "The parameter of the command or the format of the command error.....";
                break;
            case 0x8A:
                msg = "Some error appear in the card InitVal process.....";
                break;
            case 0x8B:
                msg = "Get the wrong snr during anticollison loop.....";
                break;
            case 0x8C:
                msg = "The authentication failure.....";
                break;
            case 0x8F:
                msg = "Reader received unknown command.....";
                break;
            case 0x90:
                msg = "The card do not support this command.....";
                break;
            case 0x91:
                msg = "The foarmat of the command error.....";
                break;
            case 0x92:
                msg = "Do not support option mode.....";
                break;
            case 0x93:
                msg = "The block do not exist.....";
                break;
            case 0x94:
                msg = "The object have been locked.....";
                break;
            case 0x95:
                msg = "The lock operation do not success.....";
                break;
            case 0x96:
                msg = "The operation do not success.....";
                break;
        }
        msg += '\n';
        text += msg;
        return text;
    }

    private static String showData(String text, byte[] data, String str, int pos, int len) {
        String dStr = "";
        for (int i = 0; i < len; i++) {
            dStr += String.format("%02x ", data[i + pos]);
        }
        text += (str + dStr.toUpperCase() + '\n');
        return text;
    }
}
