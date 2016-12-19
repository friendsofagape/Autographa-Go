package com.bridgeconn.autographago.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.PoetryModel;
import com.bridgeconn.autographago.models.VerseModel;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.USFMParser;
import com.bridgeconn.autographago.utils.UnzipUtil;

import java.io.File;

/**
 * Created by Admin on 18-12-2016.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button unzipButton;
    private TextView bookView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        if(Intent.ACTION_VIEW.equals(intent.getAction())){
            String filePath = intent.getData().getPath();

            USFMParser usfmParser = new USFMParser();
            Toast.makeText(getApplicationContext(), "path= " + filePath, Toast.LENGTH_SHORT).show();
            usfmParser.parseUSFMFile(this, filePath);
        }

        unzipButton = (Button) findViewById(R.id.bt_unzip);
        unzipButton.setOnClickListener(this);

        USFMParser usfmParser = new USFMParser();
        usfmParser.parseUSFMFileFromAssets(this, "65-3JN.usfm");

        bookView = (TextView) findViewById(R.id.tv_book);

        addContentToView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_unzip: {
                String filePath = Environment.getExternalStorageDirectory()+"/Download/zipfolder.zip";
                UnzipUtil.unzipFile(new File(filePath), getApplicationContext());
                break;
            }
        }
    }

    private void addContentToView() {
        for (int i=0; i<Constants.CONTAINER.getBookModels().size(); i++) {
            BookModel bookModel = Constants.CONTAINER.getBookModels().get(i);

            bookView.append("\n");
            bookView.append(bookModel.getBookAbbreviation() + "\n");
            bookView.append(bookModel.getBookName() + "\n");

            for (int j=0; j<bookModel.getChapterModels().size(); j++) {
                ChapterModel chapterModel = bookModel.getChapterModels().get(j);

                bookView.append(chapterModel.getChapterNumber() + "" + "\n");

                for (int k=0; k<chapterModel.getVerseModels().size(); k++) {
                    VerseModel verseModel = chapterModel.getVerseModels().get(k);

                    bookView.append(verseModel.getVerseNumber() + " ");
                    bookView.append(verseModel.getVerseText() + "\n");

                    for (int l=0; l<verseModel.getPoetryModels().size(); l++) {
                        PoetryModel poetryModel = verseModel.getPoetryModels().get(l);

                        bookView.append(poetryModel.getPoetryIndentLevel() + " ");
                        bookView.append(poetryModel.getPoetry());
                    }
                }
            }
        }
    }

}
