package com.bridgeconn.autographago.models;

import java.util.ArrayList;
import java.util.Comparator;

public class BookIdModel {

    private String bookId;
    private String languageCode;
    private String versionCode;
    private String bookName;
    private String section;
    private int bookNumber;
    private boolean selected;
    private int bookmarkChapterNumber;
    private ArrayList<Integer> bookmarksList = new ArrayList<>();

    public BookIdModel(BookIdModel model) {
        bookId = model.getBookId();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
        bookName = model.getBookName();
        section = model.getSection();
        bookNumber = model.getBookNumber();
        selected = model.isSelected();
        bookmarkChapterNumber = model.getBookmarkChapterNumber();
        bookmarksList = model.getBookmarksList();
    }

    public BookIdModel() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getBookmarkChapterNumber() {
        return bookmarkChapterNumber;
    }

    public void setBookmarkChapterNumber(int bookmarkChapterNumber) {
        this.bookmarkChapterNumber = bookmarkChapterNumber;
    }

    public ArrayList<Integer> getBookmarksList() {
        return bookmarksList;
    }

    public void setBookmarksList(ArrayList<Integer> bookmarksList) {
        this.bookmarksList = bookmarksList;
    }

    public static class BookNumberComparator implements Comparator<BookIdModel> {

        @Override
        public int compare(BookIdModel obj1, BookIdModel obj2) {
            return (obj1.getBookNumber() < obj2.getBookNumber()) ? -1
                    : (obj1.getBookNumber() > obj2.getBookNumber()) ? 1 : 0;
        }
    }
}
