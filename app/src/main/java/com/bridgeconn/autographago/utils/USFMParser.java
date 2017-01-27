package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.util.Log;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import io.realm.RealmList;

public class USFMParser {

    private LanguageModel languageModel;
    private RealmList<VersionModel> versionModelList;
    private VersionModel versionModel;
    private RealmList<BookModel> bookModelList;

    private BookModel bookModel;
    private RealmList<ChapterModel> chapterModelList;
    private RealmList<VerseComponentsModel> verseComponentsModelList;

    public USFMParser() {
        languageModel = new LanguageModel();
        versionModelList = new RealmList<>();
        versionModel = new VersionModel();
        bookModelList = new RealmList<>();
        bookModel = new BookModel();
        chapterModelList = new RealmList<>();
        verseComponentsModelList = new RealmList<>();
    }

    /**
     * Parse method that reads the file content line by line
     * @param context context of the application
     * @param fileName name of the usfm file to be parsed
     * @param fromAssets true if the file is stored in bundled assets or false for device storage
     */
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
                processLine(context, mLine);
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

    /**
     * Parse the line with corresponding marker at the first position
     * @param line each line in the file
     */
    private void processLine(Context context, String line) {
        line = line.trim();
        String[] splitString = line.split(Constants.Styling.SPLIT_SPACE);
        switch (splitString[0]) {
            case Constants.Markers.MARKER_BOOK_NAME: {
                addBook(context, splitString);
                break;
            }
            case Constants.Markers.MARKER_CHAPTER_NUMBER: {
                addChapter(splitString[1]);
                break;
            }
            case Constants.Markers.MARKER_NEW_PARAGRAPH: {
                addParagraph(splitString);
                break;
            }
            case Constants.Markers.MARKER_VERSE_NUMBER: {
                addVerse(splitString);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, Constants.ParagraphMarker.S1, splitString);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_ONE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, Constants.ParagraphMarker.S1, splitString);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_TWO: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_TWO, Constants.ParagraphMarker.S2, splitString);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_THREE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_THREE, Constants.ParagraphMarker.S3, splitString);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_FOUR: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_FOUR, Constants.ParagraphMarker.S4, splitString);
                break;
            }
            case Constants.Markers.MARKER_CHUNK: {
                addChunk();
                break;
            }
            case "": {
                break;
            }
            default: {
                if (splitString.length == 1) {
                    // add this to the next coming verse
                    addFormattingToNextVerse(line);
                } else {
                    // add this to the last verse
                    addFormattingToLastVerse(line);
                }
            }
        }
    }

    /**
     * This adds the book name and book abbreviation to the model
     * @param splitString the line containing book marker
     */
    private void addBook(Context context, String [] splitString) {
        bookModel.setBookId(splitString[1]);
        bookModel.setBookName(UtilFunctions.getBookNameFromMapping(context, splitString[1]));
    }

    /**
     * This adds the chapter number and id (bookName_chapterNumber) to the model
     * @param chapterNumber the number of the chapter parsed from the line
     */
    private void addChapter(String chapterNumber) {
        addComponentsToChapter();
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setChapterNumber(Integer.parseInt(chapterNumber));
        chapterModel.setChapterId(bookModel.getBookId() + "_" + chapterNumber);
        chapterModelList.add(chapterModel);
    }

    /**
     * This adds the chunk \\s5 marker of the line to the component model and add it to the list of component models
     */
    private void addChunk() {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.CHUNK);
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.S5);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the section marker of the line to the component model and add it to the list of component models
     * @param type the section marker type
     * @param splitString the line for section heading text
     */
    private void addSection(String type, Constants.ParagraphMarker marker, String [] splitString) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(type);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(marker);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the new paragraph marker of the line to the component model and add it to the list of component models
     * @param splitString the string for any text that is in the line
     */
    private void addParagraph(String [] splitString) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.PARAGRAPH);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.P);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the verse marker of the line to the component model and add it to the list of component models.
     * This also adds this verse's number to all previous models that do not have a verse number
     * @param splitString the line for text of the verse
     */
    private void addVerse(String [] splitString) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        if (chapterModelList.size() > 0) { // dont really need this check, but still
            verseComponentsModel.setChapterId(bookModel.getBookId() + "_" + chapterModelList.get(chapterModelList.size() - 1).getChapterNumber());
        }
        StringBuilder stringBuilder = new StringBuilder("");

        // check for all components that need not be added to list and mut be appended to the next verse, and remove from the list
        for (Iterator<VerseComponentsModel> iterator = verseComponentsModelList.iterator(); iterator.hasNext(); ) {
            VerseComponentsModel model = iterator.next();
            if (!model.isAdded()) {
                stringBuilder.append(model.getText());
                iterator.remove();
            }
        }

        verseComponentsModel.setType(Constants.MarkerTypes.VERSE);
        verseComponentsModel.setVerseNumber(Integer.parseInt(splitString[1]));

        for (int i=2; i<splitString.length; i++) {
            // whenever a new marker comes in between a line, append a new line marker before that
//            if (splitString[i].startsWith("\\")) {
//                stringBuilder.append("\n");
//            }
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());

        for (int i=verseComponentsModelList.size()-1; i>=0; i--) {
            if (verseComponentsModelList.get(i).getVerseNumber() > 0 ) {
                break;
            }
            verseComponentsModelList.get(i).setVerseNumber(Integer.parseInt(splitString[1]));
            if (chapterModelList.size() > 0) { // dont really need this check, but still
                verseComponentsModelList.get(i).setChapterId(bookModel.getBookId() + "_" + chapterModelList.get(chapterModelList.size() - 1).getChapterNumber());
            }
        }
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.V);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * When a new chapter marker comes, this adds all verse components to the last chapter,
     * and then reinitialize the verse components list
     */
    private void addComponentsToChapter() {
        if (chapterModelList.size() > 0) {
            if (verseComponentsModelList.size() > 0) {
                for (VerseComponentsModel model : verseComponentsModelList) {
                    if (model.getVerseNumber() > 0) {
                        chapterModelList.get(chapterModelList.size() - 1).getVerseComponentsModels().add(model);
                    }
                }
                for (Iterator<VerseComponentsModel> iterator = verseComponentsModelList.iterator(); iterator.hasNext(); ) {
                    VerseComponentsModel model = iterator.next();
                    if (model.getVerseNumber() > 0) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * At the end of the book, this adds all the chapters to the book model
     */
    private void addChaptersToBook() {
        bookModel.setChapterModels(chapterModelList);
    }

    /**
     * At the end, add the book to the CONTAINER
     */
    private void addBookToContainer() {
        bookModelList.add(bookModel);
        versionModel.setBookModels(bookModelList);
        versionModelList.add(versionModel);
        languageModel.setVersionModels(versionModelList);

        new AutographaRepository<BookModel>().add(bookModel);
    }

    /**
     * Check for line level markers like \q and inline \p and others for formatting and add them to the last verse's text.
     * @param line the text to be appended to the verse's text
     */
    private void addFormattingToLastVerse(String line) {
        if (verseComponentsModelList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("");
            stringBuilder.append(verseComponentsModelList.get(verseComponentsModelList.size() - 1).getText());
            stringBuilder.append(Constants.Styling.NEW_LINE);
            stringBuilder.append(line);
            verseComponentsModelList.get(verseComponentsModelList.size() - 1).setText(stringBuilder.toString());
        }
    }

    /**
     * Check for line level markers that do not contain any text after the marker, create new component for each line,
     * and whenever new verse marker comes, append these verses before the text of the verse and then remove these from the list.
     * @param line the marker text
     */
    private void addFormattingToNextVerse(String line) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setText(line + " ");
        verseComponentsModel.setAdded(false);
        verseComponentsModelList.add(verseComponentsModel);
    }

}