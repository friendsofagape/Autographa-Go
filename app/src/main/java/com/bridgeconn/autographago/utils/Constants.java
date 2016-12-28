package com.bridgeconn.autographago.utils;

import com.bridgeconn.autographago.models.AllLanguagesModel;

/**
 * Created by Admin on 16-12-2016.
 */

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

    public static final AllLanguagesModel CONTAINER = new AllLanguagesModel();

    public enum ParagraphMarker implements Comparable<ParagraphMarker> {
        V, P, S5, S4, S3, S2, S1;
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

}