package com.wangxin.nfcreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements RFIDReadView.OnReadFinishListener{

    public static final String TAG = "NfcDemo";

    private RFIDReadView mRFIDReadView;
    private TextView mContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRFIDReadView = (RFIDReadView) findViewById(R.id.read_view);
        mRFIDReadView.setOnReadFinishListener(this);

        mContentView = (TextView) findViewById(R.id.content_view);
    }

    @Override
    public void onResult(String value) {
        mContentView.append(value);
        mContentView.append("\n");
    }
}
