package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class ChapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private LinearLayout mChapterLayout;
    private Context mContext;
    private ArrayList<ChapterModel> mChapterModels;

    public ChapterViewHolder(View itemView, Context context, ArrayList<ChapterModel> chapterModels) {
        super(itemView);
        mChapterLayout = (LinearLayout) itemView.findViewById(R.id.layout_chapter);

        mContext = context;
        mChapterModels = chapterModels;
    }

    public void onBind(int position) {
        ChapterModel chapterModel = mChapterModels.get(position);
        addAllContent(chapterModel);
//        addContent(chapterModel);
    }

    /**
     * call removeAllViews() on linear layout before adding any new views
     * @param chapterModel
     */
    private void addAllContent(ChapterModel chapterModel) {
        mChapterLayout.removeAllViews();
        TextView textViewChapter = new TextView(mContext);
        textViewChapter.setTextSize(22);
        textViewChapter.setText(chapterModel.getChapterNumber() + "");
        for (int i=0; i<chapterModel.getVerseComponentsModels().size(); i++) {
            TextView textViewVerse = new TextView(mContext);
            boolean appendNumber = false;
            VerseComponentsModel verseComponentsModel = chapterModel.getVerseComponentsModels().get(i);
            switch (verseComponentsModel.getType()) {
                case Constants.MarkerTypes.SECTION_HEADING_ONE: {
                    textViewVerse.setTextSize(20);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_TWO: {
                    textViewVerse.setTextSize(18);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_THREE: {
                    textViewVerse.setTextSize(16);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_FOUR: {
                    textViewVerse.setTextSize(14);
                    break;
                }
                case Constants.MarkerTypes.CHUNK: {
                    break;
                }
                case Constants.MarkerTypes.PARAGRAPH: {
//                    textViewVerse.append("\n");
                    break;
                }
                case Constants.MarkerTypes.VERSE: {
                    textViewVerse.setTextSize(12);
                    appendNumber = true;
                    break;
                }
            }

//            String s= "Hello Everyone";
//            SpannableString ss1=  new SpannableString(s);
//            ss1.setSpan(new RelativeSizeSpan(2f), 0,5, 0); // set size
//            ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color
//            TextView tv= new TextView(mContext);
//            tv.setText(ss1);
//            mChapterLayout.addView(tv);
//
//            String [] splitS = s.split(" ");
//            SpannableString ss2 = new SpannableString(splitS[0]);
//            ss2.setSpan(new AbsoluteSizeSpan(20), 0, splitS[0].length() - 1, 0);
//            ss2.setSpan(new ForegroundColorSpan(Color.BLUE), 0, splitS[0].length() - 1, 0);
//            TextView tv1 = new TextView(mContext);
//            tv1.setText(ss2);
//            mChapterLayout.addView(tv1);

            if (verseComponentsModel.getText() != null) {
                if (!verseComponentsModel.getText().trim().equals("")) {
                    String[] splitString = verseComponentsModel.getText().split("\\s+");
                    for (int n = 0; n < splitString.length; n++) {
                        switch (splitString[n]) {
                            case "\\p": {
//                                textViewVerse.append("\n");
                                break;
                            }
                            case "\\q": {
                                textViewVerse.append("\n    ");
                                break;
                            }
                            default: {
                                if (splitString[n].startsWith("\\q")) {
                                    String str = splitString[n];
                                    int number = Integer.parseInt(str.replaceAll("[^0-9]", ""));
                                    textViewVerse.append("\n");
                                    for (int o = 0; o < number; o++) {
                                        textViewVerse.append("    ");
                                    }
                                } else {
                                    if (appendNumber) {
                                        if (verseComponentsModel.getVerseNumber() == 1) {
                                            mChapterLayout.addView(textViewChapter);
                                        } else {
                                            textViewVerse.append(Html.fromHtml("<sup>" + verseComponentsModel.getVerseNumber() + "</sup>"));
                                        }
                                        appendNumber = false;
                                    }
                                    textViewVerse.append(splitString[n] + " ");
                                }
                                break;
                            }
                        }
                    }
                }
            }
            mChapterLayout.addView(textViewVerse);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // TODO fix this
    private void addContent(ChapterModel chapterModel) {
        TextView textViewChapter = new TextView(mContext);
        textViewChapter.setTextSize(22);
        for (int m=0; m<chapterModel.getVerseComponentsModels().size(); m++) {
            TextView textViewVerse = new TextView(mContext);
            boolean appendNumber = false;
            VerseComponentsModel verseComponentsModel = chapterModel.getVerseComponentsModels().get(m);
            switch (verseComponentsModel.getType()) {
                case Constants.MarkerTypes.SECTION_HEADING_ONE: {
                    textViewVerse.setTextSize(20);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_TWO: {
                    textViewVerse.setTextSize(18);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_THREE: {
                    textViewVerse.setTextSize(16);
                    break;
                }
                case Constants.MarkerTypes.SECTION_HEADING_FOUR: {
                    textViewVerse.setTextSize(14);
                    break;
                }
                case Constants.MarkerTypes.CHUNK: {
                    break;
                }
                case Constants.MarkerTypes.PARAGRAPH: {
                    textViewVerse.append("\n");
                    break;
                }
                case Constants.MarkerTypes.VERSE: {
                    textViewVerse.setTextSize(12);
                    appendNumber = true;
                    break;
                }
            }
            if (verseComponentsModel.getText() != null) {
                String[] splitString = verseComponentsModel.getText().split("\\s+");
                for (int n = 0; n < splitString.length; n++) {
                    switch (splitString[n]) {
                        case "\\p": {
                            textViewVerse.append("\n");
                            break;
                        }
                        case "\\q": {
                            textViewVerse.append("\n    ");
                            break;
                        }
                        default: {
                            if (splitString[n].startsWith("\\q")) {
                                String str=splitString[n];
                                int number = Integer.parseInt(str.replaceAll("[^0-9]", ""));
                                textViewVerse.append("\n");
                                for (int o = 0; o < number; o++) {
                                    textViewVerse.append("    ");
                                }
                            } else {
                                if (appendNumber) {
                                    if (verseComponentsModel.getVerseNumber() == 1) {
                                        textViewChapter.setText("" + chapterModel.getChapterNumber());
                                        mChapterLayout.addView(textViewChapter);
                                    } else {
                                        textViewVerse.append(Html.fromHtml("<sup>" + verseComponentsModel.getVerseNumber() + "</sup>"));
                                    }
                                    appendNumber = false;
                                }
                                textViewVerse.append(splitString[n] + " ");
                            }
                            break;
                        }
                    }
                }
            }
            mChapterLayout.addView(textViewVerse);
        }
    }
}