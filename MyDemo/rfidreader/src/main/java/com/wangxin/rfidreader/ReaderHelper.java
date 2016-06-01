package com.wangxin.rfidreader;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AlertDialog;

import com.wangxin.rfidreader.api.ICReaderApi;

/**
 * Reader helper
 * Created by wangxin on 16/6/1.
 */
public class ReaderHelper {

    private Context mContext;
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private ICReaderApi mApi;

    public ReaderHelper(Context context, UsbDevice usbDevice) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mUsbDevice = usbDevice;
        mApi = new ICReaderApi(mUsbDevice, mUsbManager);
    }

    public void readData() {
        String text = new String();
        byte mode1 = (byte) (true ? 0 : 1);
        byte mode2 = (byte) (true ? 0 : 1);
        byte mode = (byte) ((mode1 << 1) | mode2); // reading
        // model
        byte blk_add = (byte) Integer.parseInt("10", 16); // block
        // address
        byte num_blk = (byte) Integer.parseInt("04", 16); // block 01 or 04
        // number
        byte[] snr = getByteArray("FF FF FF FF FF FF"); // key
        byte[] buffer = new byte[16 * num_blk]; // data read

        int result = mApi.API_PCDRead(mode, blk_add, num_blk, snr, buffer);

        text = showStatue(text, result);
        if (result == 0) {
            text = showData(text, snr, "The card number:\n", 0, 4);
            text = showData(text, buffer, "The card data:\n", 0, 16 * num_blk);
        } else
            text = showStatue(text, snr[0]);
        showDialog(text);
    }

    void showDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(text);
        builder.setTitle("Result");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private String showData(String text, byte[] data, String str) {
        String dStr = "";
        for (byte b : data) {
            dStr += String.format("%02x ", b);
        }
        text += (str + dStr.toUpperCase() + '\n');
        return text;
    }

    private String showData(String text, byte[] data, String str, int pos, int len) {
        String dStr = "";
        for (int i = 0; i < len; i++) {
            dStr += String.format("%02x ", data[i + pos]);
        }
        text += (str + dStr.toUpperCase() + '\n');
        return text;
    }

    private byte[] getByteArray(String str) {
        str = str.replaceAll("[^0-9A-F]", "");
        byte[] ans = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            ans[i / 2] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
        }
        return ans;
    }

    private String showStatue(String text, int Code) {
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

}
