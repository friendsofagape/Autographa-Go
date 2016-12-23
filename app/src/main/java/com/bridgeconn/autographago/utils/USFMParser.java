package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.util.Log;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VersionModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 16-12-2016.
 */

public class USFMParser {

    private LanguageModel languageModel;
    private List<VersionModel> versionModelList;
    private VersionModel versionModel;
    private List<BookModel> bookModelList;

    private BookModel bookModel;
    private List<ChapterModel> chapterModelList;
    private List<VerseComponentsModel> verseComponentsModelList;

    public USFMParser() {
        languageModel = new LanguageModel();
        versionModelList = new ArrayList<>();
        versionModel = new VersionModel();
        bookModelList = new ArrayList<>();
        bookModel = new BookModel();
        chapterModelList = new ArrayList<>();
        verseComponentsModelList = new ArrayList<>();
    }

    public void parseUSFMFile(Context context, String fileName, boolean fromAssets) {
        BufferedReader reader = null;
        try {
            if (fromAssets) {
                reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            } else {
                reader = new BufferedReader(new FileReader(new File(fileName)));
            }
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                processLine(mLine);
            }
            addComponentsToChapter();
            addChaptersToBook();

            addBookToContainer();

        } catch (IOException e) {
            Log.e(Constants.TAG, "Exception in reading file. " + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "Exception in closing BufferedReader. " + e.toString());
                }
            }
        }
    }

    private void processLine(String line) {

        String[] splitString = line.split("\\s+");
        switch (splitString[0]) {
            case Constants.MARKER_BOOK_NAME: {
                addBook(splitString);
                break;
            }
            case Constants.MARKER_CHAPTER_NUMBER: {
                addChapter(splitString[1]);
                break;
            }
            case Constants.MARKER_NEW_PARAGRAPH: {
                addParagraph(splitString);
                break;
            }
            case Constants.MARKER_VERSE_NUMBER: {
                addVerse(splitString);
                break;
            }
            case Constants.MARKER_SECTION_HEADING: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, splitString);
                break;
            }
            case Constants.MARKER_SECTION_HEADING_ONE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, splitString);
                break;
            }
            case Constants.MARKER_SECTION_HEADING_TWO: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_TWO, splitString);
                break;
            }
            case Constants.MARKER_SECTION_HEADING_THREE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_THREE, splitString);
                break;
            }
            case Constants.MARKER_SECTION_HEADING_FOUR: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_FOUR, splitString);
                break;
            }
            case Constants.MARKER_CHUNK: {
                addChunk();
                break;
            }
            default: {
                seeWhatIsToBeDone();
                // TODO call this also for in between marker like in verse
            }
        }
    }

    private void addBook(String [] splitString) {
        StringBuilder stringBuilder = new StringBuilder("");
        bookModel.setBookId(splitString[1]);
        for (int i=2; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        bookModel.setBookName(stringBuilder.toString());
    }

    private void addChapter(String chapterNumber) {
        addComponentsToChapter();
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setChapterNumber(Integer.parseInt(chapterNumber));
        chapterModel.setChapterId(bookModel.getBookId() + "_" + chapterNumber);
        chapterModelList.add(chapterModel);
    }

    private void addChunk() {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.CHUNK);
        verseComponentsModelList.add(verseComponentsModel);
    }

    private void addSection(String type, String [] splitString) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(type);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModelList.add(verseComponentsModel);
    }

    private void addParagraph(String [] splitString) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.PARAGRAPH);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModelList.add(verseComponentsModel);
    }

    private void addVerse(String [] splitString) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.VERSE);
        verseComponentsModel.setVerseNumber(Integer.parseInt(splitString[1]));
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=2; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        for (int i=verseComponentsModelList.size()-1; i>=0; i--) {
            if (verseComponentsModelList.get(i).getVerseNumber() <= 0) {
                verseComponentsModelList.get(i).setVerseNumber(Integer.parseInt(splitString[1]));
            } else {
                break;
            }
        }
        verseComponentsModelList.add(verseComponentsModel);
    }

    private void addComponentsToChapter() {
        if (chapterModelList.size() > 0) {
            if (verseComponentsModelList.size() > 0) {
                chapterModelList.get(chapterModelList.size() - 1).setVerseComponentsModels(verseComponentsModelList);
                verseComponentsModelList = new ArrayList<>();
            }
        }
    }

    private void addChaptersToBook() {
        bookModel.setChapterModels(chapterModelList);
    }

    private void addBookToContainer() {
        bookModelList.add(bookModel);
        versionModel.setBookModels(bookModelList);
        versionModelList.add(versionModel);
        languageModel.setVersionModels(versionModelList);

        Constants.CONTAINER.getLanguageModelList().add(languageModel);
    }

    private void seeWhatIsToBeDone() {
//        ???
    }

}