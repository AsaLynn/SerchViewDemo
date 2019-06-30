package com.zxn.serchview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 用于键盘搜索的一个控件.
 * Created by zxn on 2019/4/23.
 */
public class SerchView extends RelativeLayout implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {
    protected EditText etSerch;
    protected ImageView ivClear;
    private Drawable mSerchBackgroundDrawable;
    private Drawable mSerchIconDrawable;
    private String mSerchHint;
    private Drawable mClearIconDrawable;

    public SerchView(Context context) {
        this(context, null);
    }

    public SerchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SerchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttributeSet(attrs);
        refreshView();
    }

    private void refreshView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            etSerch.setBackground(mSerchBackgroundDrawable);
        } else {
            etSerch.setBackgroundDrawable(mSerchBackgroundDrawable);
        }
        if (null != mSerchIconDrawable) {
            mSerchIconDrawable.setBounds(0, 0, mSerchIconDrawable.getMinimumWidth(), mSerchIconDrawable.getMinimumHeight());
            etSerch.setCompoundDrawables(mSerchIconDrawable, null, null, null);
        }
        if (!TextUtils.isEmpty(mSerchHint)) {
            etSerch.setHint(mSerchHint);
        }
        if (null != mClearIconDrawable) {
            mClearIconDrawable.setBounds(0, 0, mClearIconDrawable.getMinimumWidth(), mClearIconDrawable.getMinimumHeight());
            ivClear.setImageDrawable(mClearIconDrawable);
        }

    }

    private void initAttributeSet(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray
                = getContext()
                .obtainStyledAttributes(attrs, R.styleable.SerchView);
        if (typedArray == null) {
            return;
        }
        mSerchBackgroundDrawable = typedArray.getDrawable(R.styleable.SerchView_serchBackground);
        if (null == mSerchBackgroundDrawable) {
            mSerchBackgroundDrawable = new ColorDrawable(getResources().getColor(android.R.color.white));
        }
        mSerchIconDrawable = typedArray.getDrawable(R.styleable.SerchView_serchIcon);
        mSerchHint = typedArray.getString(R.styleable.SerchView_serchHint);
        mClearIconDrawable = typedArray.getDrawable(R.styleable.SerchView_clearIcon);
        typedArray.recycle();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_clear) {
            etSerch.getEditableText().clear();
            ivClear.setVisibility(View.GONE);
            if (null != mOnSerchListener) {
                mOnSerchListener.onClearSerch();
            }
            hideSoftKeyboard();
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_serche_view, this);
        etSerch = (EditText) findViewById(R.id.et_serch);
        ivClear = (ImageView) findViewById(R.id.iv_clear);
        ivClear.setOnClickListener(SerchView.this);
        ivClear.setVisibility(View.GONE);
        etSerch.addTextChangedListener(this);
        etSerch.setOnEditorActionListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (ivClear.getVisibility() != View.VISIBLE) {//不可以见,变为可见
            if (!TextUtils.isEmpty(s)) {
                ivClear.setVisibility(View.VISIBLE);
            }
            ivClear.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
        } else {
            //可见,变为不可见
            if (TextUtils.isEmpty(s)) {
                ivClear.setVisibility(View.GONE);
            }
        }
    }

    private String TAG = this.getClass().getSimpleName();

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            Log.i(TAG, "onEditorAction: IME_ACTION_SEARCH");
            if (null != mOnSerchListener && !TextUtils.isEmpty(etSerch.getText())) {
                mOnSerchListener.onSerch(etSerch.getText().toString());
            }
            hideSoftKeyboard();
            return true;
        }
        return false;
    }

    /**
     * Interface definition for a callback to be invoked when a view is Serched.
     */
    public interface OnSerchListener {
        /**
         * Called when a view has been Serched.
         *
         * @param text the result.
         */
        void onSerch(String text);

        void onClearSerch();
    }

    /**
     * Listener used to dispatch serch events.
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    private OnSerchListener mOnSerchListener;

    public void setOnSerchListener(OnSerchListener listener) {
        this.mOnSerchListener = listener;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etSerch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public EditText getSerchEditText() {
        return etSerch;
    }

    public ImageView getClearView() {
        return ivClear;
    }

    /**
     * EditorInfo.TYPE_CLASS_NUMBER
     *
     * @attr ref android.R.styleable#TextView_inputType
     * @see android.text.InputType
     */
    public void setInputType(int type) {
        etSerch.setInputType(type);
    }

}
