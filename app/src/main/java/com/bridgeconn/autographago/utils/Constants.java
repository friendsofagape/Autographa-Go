package com.bridgeconn.autographago.utils;

import com.bridgeconn.autographago.models.AllBooksModel;

public class Constants {

    public static final String TAG = "AutographaGo";

    public static final String MARKER_BOOK_NAME = "\\id";
    public static final String MARKER_CHAPTER_NUMBER = "\\c";

    public static final String MARKER_VERSE_NUMBER = "\\v";

    public static final String MARKER_NEW_PARAGRAPH = "\\p";

    public static final String MARKER_SECTION_HEADING = "\\s";
    public static final String MARKER_SECTION_HEADING_ONE = "\\s1";
    public static final String MARKER_SECTION_HEADING_TWO = "\\s2";
    public static final String MARKER_SECTION_HEADING_THREE = "\\s3";
    public static final String MARKER_SECTION_HEADING_FOUR = "\\s4";

    public static final String MARKER_CHUNK = "\\s5";

    public static final AllBooksModel CONTAINER = new AllBooksModel();

    public enum ParagraphMarker implements Comparable<ParagraphMarker> {
        V, P, S5, S4, S3, S2, S1;
    }

    public static final String TAB_BOOK = "BOOK";
    public static final String TAB_CHAPTER = "CHAPTER";
    public static final String TAB_VERSE = "VERSE";

    public interface MarkerTypes {
        String SECTION_HEADING_ONE = "s1";
        String SECTION_HEADING_TWO = "s2";
        String SECTION_HEADING_THREE = "s3";
        String SECTION_HEADING_FOUR = "s4";
        String CHUNK = "s5";
        String PARAGRAPH = "p";
        String VERSE = "v";
    }

    public interface Keys {
        String POSITION = "position";
        String TAB_NAME = "tabName";
        String BOOK_ID = "bookId";
        String SCREEN_MODE = "screenMode";
        String CHAPTER_NO = "chapter_number";
        String VERSE_NO = "verse_number";
        String VERSE_MODELS = "verse_models";
        String VERSE_SET = "verse_set";
        String SELECT_VERSE_FOR_NOTE = "select_verse_for_note";
        String OPEN_BOOK = "open_book";
    }

    public interface RequestCodes {
        int EDIT_NOTES = 0;
    }

    public interface PrefKeys {
        String DEFAULT_HIGHLIGHT_COLOR = "default_highlight_color";

    }
}