package com.bridgeconn.autographago.utils;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;

import java.util.ArrayList;

public class Constants {

    public static final String LOG_TAG = "TagAutographaGo";

    public static final String API_BASE_URL = "https://raw.githubusercontent.com/friendsofagape/Autographa_Repo/master/Bibles/";
    public static final String META_DATA_FILE_NAME = "package.json";
    public static final String USFM_ZIP_FILE_NAME = "Archive.zip";

    public static final String STORAGE_DIRECTORY = "/autographago-external-data-cache/";

    public interface VersionCodes {
        String UDB = "UDB";
        String ULB = "ULB";
    }

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
        String REGEX_NOT_NUMBERS = "[^0-9]";
        String REGEX_NUMBERS = "[0-9]";
        String TAB_SPACE = "    ";
        String REGEX_ESCAPE = "\\";
        String CHAR_COLON = ":";
    }

    public static final String SHARE_TEXT_TYPE = "text/plain";

    public static final ArrayList<BookIdModel> CONTAINER_BOOKS_LIST = new ArrayList<>();

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
        String BULLETS = "BULLETS";
    }

    public interface TextEditorStyles {
        int BOLD = 1;
        int ITALICS = 2;
        int UNDERLINE = 3;
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
        String BOOK_ID = "bookId";
        String CHAPTER_NO = "chapter_number";
        String VERSE_NO = "verse_number";
        String VERSE_MODELS = "verse_models";
        String SELECT_VERSE_FOR_NOTE = "select_verse_for_note";
        String OPEN_BOOK = "open_book";
        String VERSE_NOTE_MODEL = "verse_note_model";
        String NOTES_MODEL = "notes_model";

        String RECREATE_NEEDED = "recreate_needed";

        String LANGUAGE_CODE = "language_code";
        String LANGUAGE_NAME = "language_name";
        String VERSION_CODE = "version_code";
        String VERSION_NAME = "version_name";
        String FILE_PATH = "file_path";

        String TIMESTAMP = "timestamp";
        String REFRESH_CONTAINER = "refresh_container";

        String TEXT = "text";
        String IMPORT_BIBLE = "import_bible";
        String NOTES_TIMESTAMPS = "notes_timestamps";

        String SOURCE = "source";
        String LICENSE = "license";
        String YEAR = "year";
    }

    public interface RequestCodes {
        int EDIT_NOTES = 0;
        int SETTINGS = 1;
        int PERMISSION_STORAGE = 2;
        int APP_SETTINGS_STORAGE = 3;
        int CHANGE_BOOK = 4;
    }

    public interface PrefKeys {
        String LAST_READ_BOOK_ID = "last_read_book";
        String LAST_READ_CHAPTER = "last_read_chapter";
        String LAST_READ_VERSE = "last_read_verse";

        String LAST_READ = "last_read";

        String FONT_SIZE = "font_size";
        String READING_MODE = "reading_mode";

        String LAST_OPEN_LANGUAGE_CODE = "last_open_language_code";
        String LAST_OPEN_LANGUAGE_NAME = "last_open_language_name";
        String LAST_OPEN_VERSION_CODE = "last_open_version_code";

        String DOWNLOAD_ID_ = "download_id_";
        String LANGUAGE_NAME = "language_name";
        String LANGUAGE_CODE = "language_code";
        String VERSION_NAME = "version_name";
        String VERSION_CODE = "version_code";
        String TIMESTAMP = "timestamp";
        String TIMESTAMP_ = "timestamp_";

        String FIRST_LAUNCH = "first_launch";

        String SOURCE = "source";
        String LICENSE = "license";
        String YEAR = "year";
    }

    public enum ReadingMode {
        Day(0, "Day"),
        Night(1, "Night");

        private int value;
        private String title;

        public int getValue() {
            return value;
        }

        public String getTitle() {
            return title;
        }

        ReadingMode(int value, String title) {
            this.value = value;
            this.title = title;
        }
    }

    public enum FontSize {
        XSmall(R.style.FontSize_XSmall, "XSmall"),
        Small(R.style.FontSize_Small, "Small"),
        Medium(R.style.FontSize_Medium, "Medium"),
        Large(R.style.FontSize_Large, "Large"),
        XLarge(R.style.FontSize_XLarge, "XLarge");

        private int resId;
        private String title;

        public int getResId() {
            return resId;
        }

        public String getTitle() {
            return title;
        }

        FontSize(int resId, String title) {
            this.resId = resId;
            this.title = title;
        }
    }

    public interface ACTION {
        String START_UNZIP = "com.bridgeconn.autographago.action.startunzip";
        String START_PARSE = "com.bridgeconn.autographago.action.startparse";
        String PARSE_COMPLETE = "com.bridgeconn.autographago.action.parsedone";

        String ADD_TO_HISTORY = "com.bridgeconn.autographago.action.addtohistory";
        String UPDATE_SEARCH_HISTORY = "com.bridgeconn.autographago.action.updatesearchhistory";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}