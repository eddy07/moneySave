package com.parse.app.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.parse.app.R;

public class FloatLabeledEditText extends FrameLayout {

    private static final int DEFAULT_PADDING_LEFT= 2;

    private TextView mHintTextView;
    private EditText mEditText;

    private Context mContext;

        public FloatLabeledEditText(Context context) {
        super(context);
        mContext = context;
    }

    public FloatLabeledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setAttributes(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FloatLabeledEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        mHintTextView = new TextView(mContext);

        final TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.FloatLabeledEditText);

        final int padding = a.getDimensionPixelSize(R.styleable.FloatLabeledEditText_fletPadding, 0);
        final int defaultPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PADDING_LEFT, getResources().getDisplayMetrics());
        final int paddingLeft = a.getDimensionPixelSize(R.styleable.FloatLabeledEditText_fletPaddingLeft, defaultPadding);
        final int paddingTop = a.getDimensionPixelSize(R.styleable.FloatLabeledEditText_fletPaddingTop, 0);
        final int paddingRight = a.getDimensionPixelSize(R.styleable.FloatLabeledEditText_fletPaddingRight, 0);
        final int paddingBottom = a.getDimensionPixelSize(R.styleable.FloatLabeledEditText_fletPaddingBottom, 0);
        Drawable background = a.getDrawable(R.styleable.FloatLabeledEditText_fletBackground);

        if (padding != 0) {
            mHintTextView.setPadding(padding, padding, padding, padding);
        } else {
            mHintTextView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        if (background != null) {
            setHintBackground(background);
        }

        mHintTextView.setTextAppearance(mContext, a.getResourceId(R.styleable.FloatLabeledEditText_fletTextAppearance, android.R.style.TextAppearance_Small));
        mHintTextView.setTextColor(getResources().getColor(R.color.textColorFloat));
        setFont(mHintTextView);
        //Start hidden
        mHintTextView.setVisibility(INVISIBLE);
        AnimatorProxy.wrap(mHintTextView).setAlpha(0);

        addView(mHintTextView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        a.recycle();
    }
    public void setFont(TextView tv) {
        Typeface tf = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
    }
    @SuppressLint("NewApi")
    private void setHintBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mHintTextView.setBackground(background);
        } else {
            mHintTextView.setBackgroundDrawable(background);
        }
    }

    @Override
    public final void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            if (mEditText != null) {
                throw new IllegalArgumentException("Can only have one Edittext subview");
            }

            final LayoutParams lp = new LayoutParams(params);
            lp.gravity = Gravity.BOTTOM;
            lp.topMargin = (int) (mHintTextView.getTextSize() + mHintTextView.getPaddingBottom() + mHintTextView.getPaddingTop());
            params = lp;

            setEditText((EditText) child);
        }

        super.addView(child, index, params);
    }

    private void setEditText(EditText editText) {
        mEditText = editText;

        mEditText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				setShowHint(!TextUtils.isEmpty(s));
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}

        });

        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View arg0, boolean gotFocus) {
				// TODO Auto-generated method stub
				onFocusChanged(gotFocus);
			}
           
        });

        mHintTextView.setText(mEditText.getHint());

        if(!TextUtils.isEmpty(mEditText.getText())){
            mHintTextView.setVisibility(VISIBLE);

        }
    }

    private void onFocusChanged(boolean gotFocus) {
        if (gotFocus && mHintTextView.getVisibility() == VISIBLE) {
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 0.33f, 1f).start();
        } else if (mHintTextView.getVisibility() == VISIBLE) {
            AnimatorProxy.wrap(mHintTextView).setAlpha(1f);  //Need this for compat reasons
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 1f, 0.33f).start();
        }
    }

    private void setShowHint(final boolean show) {
        AnimatorSet animation = null;
        if ((mHintTextView.getVisibility() == VISIBLE) && !show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mHintTextView, "translationY", 0, mHintTextView.getHeight() / 8);
            ObjectAnimator fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 1, 0);
            animation.playTogether(move, fade);
        } else if ((mHintTextView.getVisibility() != VISIBLE) && show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mHintTextView, "translationY", mHintTextView.getHeight() / 8, 0);
            ObjectAnimator fade;
            if (mEditText.isFocused()) {
                fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 0, 1);
            } else {
                fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 0, 0.33f);
            }
            animation.playTogether(move, fade);
        }

        if (animation != null) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mHintTextView.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHintTextView.setVisibility(show ? VISIBLE : INVISIBLE);
                    AnimatorProxy.wrap(mHintTextView).setAlpha(show ? 1 : 0);
                }
            });
            animation.start();
        }
    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setHint(String hint) {
        mEditText.setHint(hint);
        mHintTextView.setText(hint);
    }

    public CharSequence getHint() {
        return mHintTextView.getHint();
    }

}
