package com.wangxin.nfcreader;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by wangxin on 16/6/6.
 */
public class RFIDReadView extends EditText {

    public interface OnReadFinishListener {
        public void onResult(String value);
    }

    private Context mContext = null;
    private boolean isReadFinished = false;
    private OnReadFinishListener mFinishListener = null;

    public RFIDReadView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RFIDReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public RFIDReadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void setOnReadFinishListener(OnReadFinishListener listener) {
        mFinishListener = listener;
    }

    private void init() {
        setInputType(InputType.TYPE_NULL);
        setBackgroundColor(Color.TRANSPARENT);
        setTextColor(Color.TRANSPARENT);
        setCursorVisible(false);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s!=null && s.length()==10) {
                    isReadFinished = true;
                    if (mFinishListener!=null) {
                        mFinishListener.onResult(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isReadFinished) {
                    isReadFinished = false;
                    setText("");
                }
            }
        });
    }

}
