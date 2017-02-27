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
import com.bridgeconn.autographago.utils.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class VerseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapter;
    private Activity mContext;
    private ArrayList<ChapterModel> mChapterModels;
    private Constants.FontSize mFontSize;

    public VerseViewHolder(View itemView, Activity context, ArrayList<ChapterModel> chapterModels, Constants.FontSize fontSize) {
        super(itemView);
        mTvChapter = (TextView) itemView.findViewById(R.id.tv_chapter);

        mContext = context;
        mChapterModels = chapterModels;
        mFontSize = fontSize;
    }

    private int findChapterNumber(int position) {
        int size = 0;
        for (int i=0; i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (j==0) {
                    if (size == position) {
                        return i+1;
                    }
                    size++;
                } else {
                    if (mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber().equals(
                            mChapterModels.get(i).getVerseComponentsModels().get(j-1).getVerseNumber())) {
                        continue;
                    } else {
                        if (size == position) {
                            return i+1;
                        }
                        size++;
                    }
                }
            }
        }
        return 0;
    }

    private String findVerseNumber(int position) {
        int size = 0;
        for (int i=0; i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (j==0) {
                    if (size == position) {
                        return mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber();
                    }
                    size++;
                } else {
                    if (mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber().equals(
                            mChapterModels.get(i).getVerseComponentsModels().get(j-1).getVerseNumber())) {
                        continue;
                    } else {
                        if (size == position) {
                            return mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber();
                        }
                        size++;
                    }
                }
            }
        }
        return null;
    }

    public void onBind(int position) {
        int chapterNumber = findChapterNumber(position);
        if (chapterNumber < 1) {
            return;
        }
        ChapterModel mChapterModel = mChapterModels.get(chapterNumber-1);

        ArrayList<VerseComponentsModel> verseComponentsModels = new ArrayList<>();

        List<String> verseNumberList = new ArrayList<>();
        for (int i=0; i<mChapterModel.getVerseComponentsModels().size(); i++) {
            if (i==0) {
                verseNumberList.add(mChapterModel.getVerseComponentsModels().get(i).getVerseNumber());
            } else {
                if (mChapterModel.getVerseComponentsModels().get(i).getVerseNumber().equals(verseNumberList.get(verseNumberList.size() - 1))) {
                    continue;
                } else {
                    verseNumberList.add(mChapterModel.getVerseComponentsModels().get(i).getVerseNumber());
                }
            }
        }

        String verseNumber = findVerseNumber(position);
        if (verseNumber == null) {
            return;
        }
        int verNumberOne = Integer.parseInt(verseNumber.split("-")[0]);
        for (int i=0; i<mChapterModel.getVerseComponentsModels().size(); i++) {
            VerseComponentsModel model = mChapterModel.getVerseComponentsModels().get(i);
            int verseNumberStringOne = Integer.parseInt(model.getVerseNumber().split("-")[0]);
            if (verseNumberStringOne == verNumberOne) {
                verseComponentsModels.add(model);
            } else if (verseNumberStringOne > verNumberOne) {
                break;
            } else {
                continue;
            }
        }
        addAllContent(verseComponentsModels, chapterNumber);

        mTvChapter.setTag(position);
        mTvChapter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_chapter: {
                int position = (int) v.getTag();

                String verseNumber = findVerseNumber(position);
                String verseNo = verseNumber;
                int verNumberOne = Integer.parseInt(verseNo.split("-")[0]);

                Spannable spannable = new SpannableString(mTvChapter.getText());
                UnderlineSpan [] spans = spannable.getSpans(0, spannable.length(), UnderlineSpan.class);
                if (spans.length > 0) {
                    for (UnderlineSpan span : spans) {
                        spannable.removeSpan(span);
                        ChapterModel mChapterModel = mChapterModels.get(findChapterNumber(position) - 1);
                        for (int i=0; i<mChapterModel.getVerseComponentsModels().size(); i++) {

                            VerseComponentsModel model = mChapterModel.getVerseComponentsModels().get(i);
                            int verseNumberStringOne = Integer.parseInt(model.getVerseNumber().split("-")[0]);

                            if (verseNumberStringOne == verNumberOne) {
                                mChapterModel.getVerseComponentsModels().get(i).setSelected(false);
                            } else if (verseNumberStringOne > verNumberOne) {
                                break;
                            } else {
                                continue;
                            }
                        }
                    }
                } else {
                    ChapterModel mChapterModel = mChapterModels.get(findChapterNumber(position) - 1);
                    for (int i=0; i<mChapterModel.getVerseComponentsModels().size(); i++) {

                        VerseComponentsModel model = mChapterModel.getVerseComponentsModels().get(i);
                        int verseNumberStringOne = Integer.parseInt(model.getVerseNumber().split("-")[0]);

                        if (verseNumberStringOne == verNumberOne) {
                            mChapterModel.getVerseComponentsModels().get(i).setSelected(true);
                            if (mContext instanceof BookActivity) {
                                ((BookActivity) mContext).showBottomBar();
                            }
                        } else if (verseNumberStringOne > verNumberOne) {
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
    private void addAllContent(ArrayList<VerseComponentsModel> verseComponentsModels, int chapterNumber) {
        mTvChapter.setText("");

        for (int i=0; i<verseComponentsModels.size(); i++) {

            boolean appendNumber = false;
            VerseComponentsModel verseComponentsModel = verseComponentsModels.get(i);

            int textSize = getTextSize(14);
            switch (verseComponentsModel.getType()) {
                case Constants.MarkerTypes.SECTION_HEADING_ONE: {
                    textSize = getTextSize(20);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_TWO: {
                    textSize = getTextSize(18);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_THREE: {
                    textSize = getTextSize(16);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_FOUR: {
                    textSize = getTextSize(14);
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
                    textSize = getTextSize(14);
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
                                        String [] verseNumberSplit = verseComponentsModel.getVerseNumber().split("-");
                                        if (Integer.parseInt(verseNumberSplit[0]) == 1) {
                                            int chapterSize = (int)(22 * mContext.getResources().getDisplayMetrics().scaledDensity);
                                            SpannableString chapterNumberString = new SpannableString(chapterNumber + "");
                                            chapterNumberString.setSpan(new AbsoluteSizeSpan(chapterSize), 0, chapterNumberString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            mTvChapter.append(chapterNumberString);
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

            int px = (int)(textSize * mContext.getResources().getDisplayMetrics().scaledDensity);
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(px), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (verseComponentsModel.isHighlighted()) {
                switch (SharedPrefs.getReadingMode()) {
                    case Day: {
                        spannableStringBuilder.setSpan(new BackgroundColorSpan(0xFFFFFF00), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                    case Night: {
                        spannableStringBuilder.setSpan(new BackgroundColorSpan(0x77FFA500), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                }
            }
            mTvChapter.append(spannableStringBuilder);
        }
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
                return (prevSize + 2);
            }
            case XLarge: {
                return (prevSize + 4);
            }
        }
        return prevSize;
    }
}