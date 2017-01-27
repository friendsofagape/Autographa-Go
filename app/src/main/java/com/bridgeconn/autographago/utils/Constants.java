package com.bridgeconn.autographago.utils;

import com.bridgeconn.autographago.models.AllBooksModel;

public class Constants {

    public static final String TAG = "AutographaGo";
    public static final String DUMMY_TAG = "abcd";

    public static final String API_BASE_URL = "https://raw.githubusercontent.com/Bridgeconn/";
    public static final String APPEND_URL_FILE = "Autographa_Repo/master/Bibles/English_ULB/01-GEN.usfm";
    public static final String STORAGE_DIRECTORY = "/appname-external-data-cache/";

    public interface Markers {
        String MARKER_BOOK_NAME = "\\id";
        String MARKER_CHAPTER_NUMBER = "\\c";
        String MARKER_VERSE_NUMBER = "\\v";
        String MARKER_NEW_PARAGRAPH = "\\p";
        String MARKER_SECTION_HEADING = "\\s";
        String MARKER_SECTION_HEADING_ONE = "\\s1";
        String MARKER_SECTION_HEADING_TWO = "\\s2";
        String MARKER_SECTION_HEADING_THREE = "\\s3";
        String MARKER_SECTION_HEADING_FOUR = "\\s4";
        String MARKER_CHUNK = "\\s5";
    }

    public interface Styling {
        String SPLIT_SPACE = "\\s+";
        String NEW_LINE = "\n";
        String NEW_LINE_WITH_TAB_SPACE = "\n    ";
        String MARKER_Q = "\\q";
        String REGEX_NUMBERS = "[^0-9]";
        String TAB_SPACE = "    ";
        String REGEX_ESCAPE = "\\";
        String CHAR_COLON = ":";
    }

    public static final String SHARE_TEXT_TYPE = "text/plain";

    public static final String FILE_PREFIX = "File-";

    public static final AllBooksModel CONTAINER = new AllBooksModel();

    public enum ParagraphMarker implements Comparable<ParagraphMarker> {
        V, P, S5, S4, S3, S2, S1;
    }

    public interface Tabs {
        String BOOK = "BOOK";
        String CHAPTER = "CHAPTER";
        String VERSE = "VERSE";
    }

    public interface TextEditor {
        String BOLD = "BOLD";
        String ITALICS = "ITALICS";
        String UNDERLINE = "UNDERLINE";
    }

    public interface USFMFiles {
        String HOSEA = "28-HOS.usfm";
        String _3_JOHN = "65-3JN.usfm";
        String ZEPHANIAH = "36-ZEP.usfm";
    }

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