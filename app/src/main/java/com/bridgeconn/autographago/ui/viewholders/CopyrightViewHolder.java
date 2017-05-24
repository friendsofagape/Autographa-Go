package com.bridgeconn.autographago.ui.viewholders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.utils.Constants;

public class CopyrightViewHolder extends RecyclerView.ViewHolder {

    private TextView mTvCopyright;
    private Activity mContext;
    private Constants.FontSize mFontSize;
    private String mVersionName, mLicense;
    private int mYear;

    public CopyrightViewHolder(View itemView, Activity context, Constants.FontSize fontSize, String versionName, String license, int year) {
        super(itemView);
        mTvCopyright = (TextView) itemView.findViewById(R.id.copyright_text);

        mContext = context;
        mFontSize = fontSize;
        mVersionName = versionName;
        mLicense = license;
        mYear = year;
    }

    public void onBind() {
        int textSize = getTextSize(10);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        spannableStringBuilder.append(mContext.getString(R.string.copyright, mYear, mVersionName, mLicense));

        int px = (int) (textSize * mContext.getResources().getDisplayMetrics().scaledDensity);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(px), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvCopyright.append(spannableStringBuilder);
    }

    private int getTextSize(int prevSize) {
        switch (mFontSize) {
            case XSmall: {
                return (prevSize - 4);
            }
            case Small: {
                return (prevSize - 2);
            }
            case Medium: {
                return prevSize;
            }
            case Large: {
                return (prevSize + 4);
            }
            case XLarge: {
                return (prevSize + 12);
            }
        }
        return prevSize;
    }
}