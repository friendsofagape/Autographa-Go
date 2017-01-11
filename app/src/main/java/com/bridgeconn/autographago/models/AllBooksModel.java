package com.bridgeconn.autographago.models;

import java.util.ArrayList;
import java.util.List;

public class AllBooksModel {

    private List<BookModel> bookModelList = new ArrayList<>();

    public AllBooksModel(AllBooksModel model) {
        bookModelList = model.getBookModelList();
    }

    public AllBooksModel() {
    }

    public List<BookModel> getBookModelList() {
        return bookModelList;
    }

    public void setBookModelList(List<BookModel> bookModelList) {
        this.bookModelList = bookModelList;
    }
}