package com.bridgeconn.autographago.models;

/**
 * Created by Admin on 06-01-2017.
 */

public class SearchModel {

    private String bookId;
    private String bookName;
    private int chapterNumber;
    private int verseNumber;
    private String text;

    public SearchModel() {
    }

    public SearchModel(SearchModel searchModel) {
        bookId = searchModel.getBookId();
        bookName = searchModel.getBookName();
        chapterNumber = searchModel.getChapterNumber();
        verseNumber = searchModel.getVerseNumber();
        text = searchModel.getText();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(int verseNumber) {
        this.verseNumber = verseNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}