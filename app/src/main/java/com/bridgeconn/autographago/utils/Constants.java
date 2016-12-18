package com.bridgeconn.autographago.utils;

import com.bridgeconn.autographago.models.AllBooksModel;

/**
 * Created by Admin on 16-12-2016.
 */

public class Constants {

    public static final String TAG = "AutographaGo";

    public static final String MARKER_BOOK_NAME = "\\id";
    public static final String MARKER_CHAPTER_NUMBER = "\\c";
    public static final String MARKER_VERSE_NUMBER = "\\v";
    public static final String MARKER_NEW_PARAGRAPH = "\\p";
    public static final String MARKER_LINE_BREAK = "\\b";
    public static final String MARKER_POETRY = "\\q";
    public static final String MARKER_POETRY_WITH_INDENT = "\\\\q*";

    public static final AllBooksModel CONTAINER = new AllBooksModel();
}
