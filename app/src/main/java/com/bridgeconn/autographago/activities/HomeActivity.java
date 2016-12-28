package com.bridgeconn.autographago.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.USFMParser;

/**
 * Created by Admin on 18-12-2016.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    //    private Button unzipButton;
    private LinearLayout linearLayoutBook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        if(Intent.ACTION_VIEW.equals(intent.getAction())){
            String filePath = intent.getData().getPath();

            USFMParser usfmParser = new USFMParser();
            Toast.makeText(getApplicationContext(), "path= " + filePath, Toast.LENGTH_SHORT).show();
            usfmParser.parseUSFMFile(this, filePath, false);
        }

//        unzipButton = (Button) findViewById(R.id.bt_unzip);
//        unzipButton.setOnClickListener(this);

        USFMParser usfmParser = new USFMParser();
//        usfmParser.parseUSFMFile(this, "65-3JN.usfm", true);
        usfmParser.parseUSFMFile(this, "36-ZEP.usfm", true);

        linearLayoutBook = (LinearLayout) findViewById(R.id.layout_book);

        addContent();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.bt_unzip: {
//                String filePath = Environment.getExternalStorageDirectory()+"/Download/zipfolder.zip";
//                UnzipUtil.unzipFile(new File(filePath), getApplicationContext());
//                break;
//            }
//        }
    }

    private void addContent() {
        for (int i=0; i< Constants.CONTAINER.getLanguageModelList().size(); i++) {
            LanguageModel languageModel = Constants.CONTAINER.getLanguageModelList().get(i);
            for (int j=0; j<languageModel.getVersionModels().size(); j++) {
                VersionModel versionModel = languageModel.getVersionModels().get(j);
                for (int k=0; k<versionModel.getBookModels().size(); k++) {
                    BookModel bookModel = versionModel.getBookModels().get(k);
                    TextView textViewBook = new TextView(this);
                    textViewBook.setAllCaps(true);
                    textViewBook.setTextSize(24);
                    textViewBook.setText(bookModel.getBookId() + " " + bookModel.getBookName());
                    linearLayoutBook.addView(textViewBook);
                    for (int l=0; l<bookModel.getChapterModels().size(); l++) {
                        ChapterModel chapterModel = bookModel.getChapterModels().get(l);
                        TextView textViewChapter = new TextView(this);
                        textViewChapter.setTextSize(22);
                        textViewChapter.setText("" + chapterModel.getChapterNumber());
                        linearLayoutBook.addView(textViewChapter);
                        TextView textViewVerse = new TextView(this);
                        for (int m=0; m<chapterModel.getVerseComponentsModels().size(); m++) {
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
                                                    textViewVerse.append(Html.fromHtml("<sup>" + verseComponentsModel.getVerseNumber() + "</sup>"));
                                                }
                                                appendNumber = false;
                                                textViewVerse.append(splitString[n] + " ");
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        linearLayoutBook.addView(textViewVerse);
                    }
                }
            }
        }
    }

}