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
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class VerseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapter;
    private Activity mContext;
    private ChapterModel mChapterModel;
//    private ArrayList<VerseComponentsModel> mVerseComponentsModels;

    public VerseViewHolder(View itemView, Activity context, ChapterModel chapterModel) {//}, ArrayList<VerseComponentsModel> verseComponentsModels) {
        super(itemView);
        mTvChapter = (TextView) itemView.findViewById(R.id.tv_chapter);

        mContext = context;
        mChapterModel = chapterModel;
//        mVerseComponentsModels = verseComponentsModels;
    }

    public void onBind(int verseNumber) {
        ArrayList<VerseComponentsModel> verseComponentsModels = new ArrayList<>();
        for (VerseComponentsModel model : mChapterModel.getVerseComponentsModels()) {
            if (model.getVerseNumber() == verseNumber) {
                verseComponentsModels.add(model);
            } else if (model.getVerseNumber() > verseNumber) {
                break;
            } else {
                continue;
            }
        }
        addAllContent(verseComponentsModels);

        mTvChapter.setTag(verseNumber);
        mTvChapter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_chapter: {
                int verseNumber = (int) v.getTag();
                Spannable spannable = new SpannableString(mTvChapter.getText());
                UnderlineSpan [] spans = spannable.getSpans(0, spannable.length(), UnderlineSpan.class);
                if (spans.length > 0) {
                    for (UnderlineSpan span : spans) {
                        spannable.removeSpan(span);
                        for (int i=0; i<mChapterModel.getVerseComponentsModels().size(); i++) {
                            if (mChapterModel.getVerseComponentsModels().get(i).getVerseNumber() == verseNumber) {
                                mChapterModel.getVerseComponentsModels().get(i).setSelected(false);
                            } else if (mChapterModel.getVerseComponentsModels().get(i).getVerseNumber() > verseNumber) {
                                break;
                            } else {
                                continue;
                            }
                        }
                    }
                } else {
                    for (int i=0; i<mChapterModel.getVerseComponentsModels().size(); i++) {
                        if (mChapterModel.getVerseComponentsModels().get(i).getVerseNumber() == verseNumber) {
                            mChapterModel.getVerseComponentsModels().get(i).setSelected(true);
                            if (mContext instanceof BookActivity) {
                                ((BookActivity) mContext).showBottomBar();
                            }
                        } else if (mChapterModel.getVerseComponentsModels().get(i).getVerseNumber() > verseNumber) {
                            break;
                        } else {
                            continue;
                        }
                    }
                    spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mTvChapter.setText(spannable);
                break;
            }
        }
    }

    /**
     * call removeAllViews() on linear layout before adding any new views
     * @param verseComponentsModels
     */
    private void addAllContent(ArrayList<VerseComponentsModel> verseComponentsModels) {
        mTvChapter.setText("");

        for (int i=0; i<verseComponentsModels.size(); i++) {

            boolean appendNumber = false;
            VerseComponentsModel verseComponentsModel = verseComponentsModels.get(i);

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
                    mTvChapter.append(Constants.Styling.NEW_LINE);
                    break;
                }
                case Constants.MarkerTypes.PARAGRAPH: {
                    mTvChapter.append(Constants.Styling.NEW_LINE);
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
                    String[] splitString = verseComponentsModel.getText().split(Constants.Styling.SPLIT_SPACE);
                    for (int n = 0; n < splitString.length; n++) {
                        switch (splitString[n]) {
                            case Constants.Markers.MARKER_NEW_PARAGRAPH: {
                                spannableStringBuilder.append(Constants.Styling.NEW_LINE);
                                break;
                            }
                            case Constants.Styling.MARKER_Q: {
                                spannableStringBuilder.append(Constants.Styling.NEW_LINE_WITH_TAB_SPACE);
                                break;
                            }
                            default: {
                                if (splitString[n].startsWith(Constants.Styling.MARKER_Q)) {
                                    String str = splitString[n];
                                    int number = Integer.parseInt(str.replaceAll(Constants.Styling.REGEX_NUMBERS, ""));
                                    spannableStringBuilder.append(Constants.Styling.NEW_LINE);
                                    for (int o = 0; o < number; o++) {
                                        spannableStringBuilder.append(Constants.Styling.TAB_SPACE);
                                    }
                                } else if (splitString[n].startsWith(Constants.Styling.REGEX_ESCAPE)) {
                                    break;
                                } else {
                                    if (appendNumber) {
                                        if (verseComponentsModel.getVerseNumber() == 1) {
                                            if (verseComponentsModel.getVerseNumber() == 1) {
                                                int chapterSize = (int)(22 * mContext.getResources().getDisplayMetrics().scaledDensity);
                                                SpannableString chapterNumberString = new SpannableString(mChapterModel.getChapterNumber() + "");
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
            if (verseComponentsModel.isHighlighted()) {
                spannableStringBuilder.setSpan(new BackgroundColorSpan(0xFFFFFF00), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mTvChapter.append(spannableStringBuilder);
        }
    }

    private void findMinMax() {
        int min = 0;
        int max = mTvChapter.getText().length();
        if (mTvChapter.isFocused()) {
            final int selStart = mTvChapter.getSelectionStart();
            final int selEnd = mTvChapter.getSelectionEnd();

            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }

        String textString = mTvChapter.getText().toString();
        Spannable spanText = Spannable.Factory.getInstance().newSpannable(mTvChapter.getText());
        spanText.setSpan(new BackgroundColorSpan(0xFFFFFF00), min, max, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvChapter.setText(spanText);
    }

    private String findText() {
        int min = 0;
        int max = mTvChapter.getText().length();
        if (mTvChapter.isFocused()) {
            final int selStart = mTvChapter.getSelectionStart();
            final int selEnd = mTvChapter.getSelectionEnd();

            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }

        String textString = mTvChapter.getText().toString();
        return textString.substring(min, max);
    }


}