package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.util.Log;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.PoetryModel;
import com.bridgeconn.autographago.models.VerseModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.realm.RealmList;

/**
 * Created by Admin on 16-12-2016.
 */

public class USFMParser {

    private BookModel bookModel;
    private RealmList<ChapterModel> chapterModels;
    private RealmList<VerseModel> verseModels;
    private VerseModel verseModel;
    private PoetryModel poetryModel;
    private RealmList<PoetryModel> poetryModels;

    public USFMParser() {
        bookModel = new BookModel();
        chapterModels = new RealmList<>();
        verseModels = new RealmList<>();
        verseModel = new VerseModel();

        poetryModel = new PoetryModel();
        poetryModels = new RealmList<>();
    }

    public void parseUSFMFile(Context context, String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(fileName)));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                processLine(mLine);
            }
            addVersesToChapter();
            addChaptersToBook();

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

    public void parseUSFMFileFromAssets(Context context, String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                processLine(mLine);
            }
            addVersesToChapter();
            addChaptersToBook();

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
        if (splitString.length > 0) {
            String marker = splitString[0];
            if (marker.equals(Constants.MARKER_BOOK_NAME)) {
                addBook(splitString);
            } else if (marker.equals(Constants.MARKER_CHAPTER_NUMBER)) {
                addChapter(splitString[1]);
            } else if (marker.equals(Constants.MARKER_VERSE_NUMBER)) {
                addVerse(splitString);
            } else if (marker.equals(Constants.MARKER_NEW_PARAGRAPH)) {
                verseModel.setParagraphStart(true);
            } else if (marker.equals(Constants.MARKER_POETRY)) {
                int indentLevel = 1;
            } else if (marker.matches(Constants.MARKER_POETRY_WITH_INDENT)) {
                String number = marker.substring(2);
                int indentLevel =  Integer.parseInt(number);
            }
        }
    }

    private void addBook(String [] splitString) {
        StringBuilder stringBuilder = new StringBuilder("");
        bookModel.setBookAbbreviation(splitString[1]);
        for (int i=2; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        bookModel.setBookName(stringBuilder.toString());
    }

    private void addChapter(String chapterNumber) {
        addVersesToChapter();
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setChapterNumber(Long.parseLong(chapterNumber));
        chapterModels.add(chapterModel);
    }

    private void addVerse(String [] splitString) {
        StringBuilder stringBuilder = new StringBuilder("");
        verseModel.setVerseNumber(Long.parseLong(splitString[1]));
        for (int i=2; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseModel.setVerseText(stringBuilder.toString());
        verseModels.add(verseModel);

        verseModel = new VerseModel();
    }

    private void addVersesToChapter() {
        if (chapterModels.size() == 0) {
            return;
        }
        chapterModels.get(chapterModels.size()-1).setVerseModels(verseModels);
        verseModels = new RealmList<>();
    }

    private void addChaptersToBook() {
        bookModel.setChapterModels(chapterModels);
        addBookToContainer();
    }

    private void addBookToContainer() {
        Constants.CONTAINER.getBookModels().add(bookModel);
    }

}