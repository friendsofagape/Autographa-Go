package com.bridgeconn.autographago.ui.viewholders;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.SuperscriptSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class ChapterViewHolder extends RecyclerView.ViewHolder {

    private TextView mTvChapter;
    private Activity mContext;
    private ArrayList<ChapterModel> mChapterModels;

    public ChapterViewHolder(View itemView, Activity context, ArrayList<ChapterModel> chapterModels) {
        super(itemView);
        mTvChapter = (TextView) itemView.findViewById(R.id.tv_chapter);

        mContext = context;
        mChapterModels = chapterModels;
    }

    public void onBind(int position) {
        ChapterModel chapterModel = mChapterModels.get(position);
        addAllContent(chapterModel);
    }

    /**
     * call removeAllViews() on linear layout before adding any new views
     * @param chapterModel
     */
    private void addAllContent(ChapterModel chapterModel) {
        mTvChapter.setText("");

        mTvChapter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mTvChapter.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.shareText);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        for (int i=0; i<chapterModel.getVerseComponentsModels().size(); i++) {

            boolean appendNumber = false;
            VerseComponentsModel verseComponentsModel = chapterModel.getVerseComponentsModels().get(i);

            int size = 14;
            switch (verseComponentsModel.getType()) {
                case Constants.MarkerTypes.SECTION_HEADING_ONE: {
                    size = 20;
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_TWO: {
                    size = 18;
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_THREE: {
                    size = 16;
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_FOUR: {
                    size = 14;
                    break;
                }
                case Constants.MarkerTypes.CHUNK: {
                    mTvChapter.append("\n");
                    break;
                }
                case Constants.MarkerTypes.PARAGRAPH: {
                    mTvChapter.append("\n");
                    break;
                }
                case Constants.MarkerTypes.VERSE: {
                    size = 14;
                    appendNumber = true;
                    break;
                }
            }

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

            if (verseComponentsModel.getText() != null) {
                if (!verseComponentsModel.getText().trim().equals("")) {
                    String[] splitString = verseComponentsModel.getText().split("\\s+");
                    for (int n = 0; n < splitString.length; n++) {
                        switch (splitString[n]) {
                            case "\\p": {
                                spannableStringBuilder.append("\n");
                                break;
                            }
                            case "\\q": {
                                spannableStringBuilder.append("\n    ");
                                break;
                            }
                            default: {
                                if (splitString[n].startsWith("\\q")) {
                                    String str = splitString[n];
                                    int number = Integer.parseInt(str.replaceAll("[^0-9]", ""));
                                    spannableStringBuilder.append("\n");
                                    for (int o = 0; o < number; o++) {
                                        spannableStringBuilder.append("    ");
                                    }
                                } else if (splitString[n].startsWith("\\")) {
                                    break;
                                } else {
                                    if (appendNumber) {
                                        if (verseComponentsModel.getVerseNumber() == 1) {
                                            if (verseComponentsModel.getVerseNumber() == 1) {
                                                int chapterSize = (int)(22 * mContext.getResources().getDisplayMetrics().scaledDensity);
                                                SpannableString chapterNumberString = new SpannableString(chapterModel.getChapterNumber() + "");
                                                chapterNumberString.setSpan(new AbsoluteSizeSpan(chapterSize), 0, chapterNumberString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                mTvChapter.append(chapterNumberString);
                                            }
                                        } else {
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                                spannableStringBuilder.append(Html.fromHtml("<sup>" + verseComponentsModel.getVerseNumber() + "</sup>"));
                                            } else {
                                                spannableStringBuilder.append(verseComponentsModel.getVerseNumber() + "", new SuperscriptSpan(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            }
                                        }
                                        appendNumber = false;
                                    }
                                    spannableStringBuilder.append(splitString[n] + " ");
                                }
                                break;
                            }
                        }
                    }
                }
            }

            int px = (int)(size * mContext.getResources().getDisplayMetrics().scaledDensity);
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(px), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mTvChapter.append(spannableStringBuilder);
        }
    }

    public void highLightText() {
        int min = 0;
        int max = mTvChapter.getText().length();
        if (mTvChapter.isFocused()) {
            final int selStart = mTvChapter.getSelectionStart();
            final int selEnd = mTvChapter.getSelectionEnd();

            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }

        String textString = mTvChapter.getText().toString();
        Spannable spanText = Spannable.Factory.getInstance().newSpannable(textString);
        spanText.setSpan(new BackgroundColorSpan(0xFFFFFF00), min, max, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvChapter.setText(spanText);
    }
}