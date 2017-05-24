package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.util.Log;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class USFMParser {

    private LanguageModel languageModel;
    private VersionModel versionModel;

    private BookModel bookModel;
    private RealmList<ChapterModel> chapterModelList;
    private RealmList<VerseComponentsModel> verseComponentsModelList;

    private boolean languageExist = false, versionExist = false, bookExist = false;
    private ArrayList<LanguageModel> languageResults = new ArrayList<>();
    private int versionPosition, bookPosition;

    private Realm realm;

    public USFMParser() {
        languageModel = new LanguageModel();
        versionModel = new VersionModel();
        bookModel = new BookModel();
        chapterModelList = new RealmList<>();
        verseComponentsModelList = new RealmList<>();

        realm = Realm.getDefaultInstance();
    }

    /**
     * Parse method that reads the file content line by line
     * @param context context of the application
     * @param fileName name of the usfm file to be parsed
     * @param fromAssets true if the file is stored in bundled assets or false for device storage
     */
    public boolean parseUSFMFile(Context context, String fileName, boolean fromAssets,
                                 String languageName, String languageCode, String versionCode,
                                 String versionName, String source, String license, int year) {
        BufferedReader reader = null;
        try {
            if (fromAssets) {
                reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            } else {
                reader = new BufferedReader(new FileReader(new File(fileName)));
            }
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (!processLine(context, mLine, languageName, languageCode, versionCode)) {
                    realm.close();
                    return false;
                }
            }
            addComponentsToChapter();
            addChaptersToBook();

            addBookToContainer(languageName, languageCode, versionCode, versionName, source, license, year);
            realm.close();
            realm = null;
            return true;
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Exception in reading file. " + fileName + "So skipping this file" + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "Exception in closing BufferedReader. " + e.toString());
                }
            }
        }
        realm.close();
        realm = null;
        return false;
    }

    /**
     * Parse the line with corresponding marker at the first position
     * @param line each line in the file
     */
    private boolean processLine(Context context, String line, String languageName, String languageCode, String versionCode) {
//        line = line.trim();
        String[] splitString = line.split(Constants.Styling.SPLIT_SPACE);
        if (splitString.length == 0) {
            return true;
        }
        switch (splitString[0]) {
            case Constants.Markers.MARKER_BOOK_NAME: {
                if (!addBook(context, splitString, languageName, languageCode, versionCode)) {
                    Log.i(Constants.LOG_TAG, "Skip book, already exist in db, " + splitString[1] + "  lan=" + languageName + versionCode);
                    return false;
                }
                break;
            }
            case Constants.Markers.MARKER_CHAPTER_NUMBER: {
                addChapter(splitString[1], languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_NEW_PARAGRAPH: {
                addParagraph(splitString, languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_VERSE_NUMBER: {
                addVerse(splitString, languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, Constants.ParagraphMarker.S1, splitString, languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_ONE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, Constants.ParagraphMarker.S1, splitString, languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_TWO: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_TWO, Constants.ParagraphMarker.S2, splitString, languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_THREE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_THREE, Constants.ParagraphMarker.S3, splitString, languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_FOUR: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_FOUR, Constants.ParagraphMarker.S4, splitString, languageName, languageCode, versionCode);
                break;
            }
            case Constants.Markers.MARKER_CHUNK: {
                addChunk(languageName, languageCode, versionCode);
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
        return true;
    }

    /**
     * This adds the book name and book abbreviation to the model
     * @param splitString the line containing book marker
     */
    private boolean addBook(Context context, String [] splitString, String languageName, String languageCode, String versionCode) {

        bookModel.setBookId(splitString[1]);
        bookModel.setBookPrimaryId(languageCode + "_" + versionCode + "_" + splitString[1]);
        bookModel.setBookName(UtilFunctions.getBookNameFromMapping(context, splitString[1]));
        bookModel.setSection(UtilFunctions.getBookSectionFromMapping(context, splitString[1]));
        bookModel.setBookNumber(UtilFunctions.getBookNumberFromMapping(context, splitString[1]));
        bookModel.setLanguageCode(languageCode);
        bookModel.setVersionCode(versionCode);

        languageResults = query(new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (int i=0; i<languageResults.size(); i++) {
            if (languageResults.get(i).getLanguageName().equalsIgnoreCase(languageName)) {
                languageExist = true;
                versionPosition = i;
                for (int j=0; j<languageResults.get(i).getVersionModels().size(); j++) {
                    if (languageResults.get(i).getVersionModels().get(j).getVersionCode().equalsIgnoreCase(versionCode)) {
                        versionExist = true;
                        bookPosition = j;
                        for (int k=0; k<languageResults.get(i).getVersionModels().get(j).getBookModels().size(); k++) {
                            if (languageResults.get(i).getVersionModels().get(j).getBookModels().get(k)
                                    .getBookId().equals(bookModel.getBookId())) {
                                bookExist = true;
                                // book already present in db, do nothing
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * This adds the chapter number and id (bookName_chapterNumber) to the model
     * @param chapterNumber the number of the chapter parsed from the line
     */
    private void addChapter(String chapterNumber, String languageName, String languageCode, String versionCode) {
        addComponentsToChapter();
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setChapterNumber(Integer.parseInt(chapterNumber));
        chapterModel.setChapterId(languageCode + "_" + versionCode + "_" + bookModel.getBookId() + "_" + chapterNumber);
        chapterModel.setLanguageCode(languageCode);
        chapterModel.setVersionCode(versionCode);
        chapterModelList.add(chapterModel);
    }

    /**
     * This adds the chunk \\s5 marker of the line to the component model and add it to the list of component models
     */
    private void addChunk(String languageName, String languageCode, String versionCode) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.CHUNK);
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.S5);
        verseComponentsModel.setLanguageCode(languageCode);
        verseComponentsModel.setVersionCode(versionCode);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the section marker of the line to the component model and add it to the list of component models
     * @param type the section marker type
     * @param splitString the line for section heading text
     */
    private void addSection(String type, Constants.ParagraphMarker marker, String [] splitString, String languageName, String languageCode, String versionCode) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(type);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(marker);
        verseComponentsModel.setLanguageCode(languageCode);
        verseComponentsModel.setVersionCode(versionCode);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the new paragraph marker of the line to the component model and add it to the list of component models
     * @param splitString the string for any text that is in the line
     */
    private void addParagraph(String [] splitString, String languageName, String languageCode, String versionCode) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.PARAGRAPH);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.P);
        verseComponentsModel.setLanguageCode(languageCode);
        verseComponentsModel.setVersionCode(versionCode);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the verse marker of the line to the component model and add it to the list of component models.
     * This also adds this verse's number to all previous models that do not have a verse number
     * @param splitString the line for text of the verse
     */
    private void addVerse(String [] splitString, String languageName, String languageCode, String versionCode) {
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
        String verseNum = splitString[1];
        String intString = verseNum.replaceAll(Constants.Styling.REGEX_NOT_NUMBERS, "");
        String notIntString = verseNum.replaceAll(Constants.Styling.REGEX_NUMBERS, "");
        if (intString.equals("")) {
            throw new NumberFormatException();
        }
        // FIXED PLAY STORE CRASH, NUMBER FORMAT EXCEPTION WITH STRING AS 10.
        if (!(notIntString.equals("") || notIntString.equals("-"))) {
            throw new NumberFormatException();
        }
        verseComponentsModel.setVerseNumber(splitString[1]);

        for (int i=2; i<splitString.length; i++) {
            // whenever a new marker comes in between a line, append a new line marker before that
//            if (splitString[i].startsWith("\\")) {
//                stringBuilder.append("\n");
//            }
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());

        for (int i=verseComponentsModelList.size()-1; i>=0; i--) {
            if (verseComponentsModelList.get(i).getVerseNumber() != null) {//.equals("") ) {
                break;
            }
            verseComponentsModelList.get(i).setVerseNumber(splitString[1]);
            if (chapterModelList.size() > 0) { // dont really need this check, but still
                verseComponentsModelList.get(i).setChapterId(bookModel.getBookId() + "_" + chapterModelList.get(chapterModelList.size() - 1).getChapterNumber());
            }
        }
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.V);
        verseComponentsModel.setLanguageCode(languageCode);
        verseComponentsModel.setVersionCode(versionCode);
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
                    if (model.getVerseNumber() != null) {//.equals("")) {
                        chapterModelList.get(chapterModelList.size() - 1).getVerseComponentsModels().add(model);
                    }
                }
                int size = 0;
                for (int i=0; i<verseComponentsModelList.size(); i++) {
                    if (verseComponentsModelList.get(i).getVerseNumber() != null) {
                        if (i == 0 || !verseComponentsModelList.get(i).getVerseNumber().equals(
                                verseComponentsModelList.get(i - 1).getVerseNumber())) {
                            size++;
                        } else {
                            continue;
                        }
                    }
                }
                chapterModelList.get(chapterModelList.size() - 1).setNumberOfVerses(size);
                for (Iterator<VerseComponentsModel> iterator = verseComponentsModelList.iterator(); iterator.hasNext(); ) {
                    VerseComponentsModel model = iterator.next();
                    if (model.getVerseNumber() != null) {//.equals("")) {
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

    private ArrayList<LanguageModel> query(Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {

        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);
        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();
        for (LanguageModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    /**
     * At the end, add the book to the database
     */
    private void addBookToContainer(String languageName, String languageCode, String versionCode, String versionName,
                                    String source, String license, int year) {

        versionModel.setVersionCode(versionCode);
        versionModel.setVersionName(versionName);
        versionModel.setLanguageCode(languageCode);
        versionModel.setSource(source);
        versionModel.setLicense(license);
        versionModel.setYear(year);

        versionModel.setVersionId(languageCode + "_" + versionCode);
        versionModel.getBookModels().add(bookModel);

        for (LanguageModel languageModel : languageResults) {
            if (languageModel.getLanguageCode().equalsIgnoreCase("ENG")) {
                for (VersionModel versionModel : languageModel.getVersionModels()) {
                    if (versionModel.getVersionCode().equalsIgnoreCase(Constants.VersionCodes.ULB)) {
                        for (BookModel
                                bookModel1 : versionModel.getBookModels()) {
                            if (bookModel1.getBookId().equals(bookModel.getBookId())) {

                                for (ChapterModel chapterModel1 : bookModel1.getChapterModels()) {
                                    for (VerseComponentsModel verseComponentsModel1 : chapterModel1.getVerseComponentsModels()) {

                                        for (ChapterModel chapterModel : bookModel.getChapterModels()) {
                                            if (chapterModel.getChapterNumber() == chapterModel1.getChapterNumber()) {
                                                for (VerseComponentsModel verseComponentsModel : chapterModel.getVerseComponentsModels()) {
                                                    if (verseComponentsModel.getVerseNumber().equals(verseComponentsModel1.getVerseNumber())) {
                                                        verseComponentsModel.setHighlighted(verseComponentsModel1.isHighlighted());
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }

        if (!languageExist) {
            // add new all
            languageModel.getVersionModels().add(versionModel);
            languageModel.setLanguageName(languageName);
            languageModel.setLanguageCode(languageCode);
            new AutographaRepository<LanguageModel>().add(languageModel);
            return;
        }

        if (!versionExist) {
            // add new version in same language
            new AutographaRepository<LanguageModel>().updateLanguageWithVersion(realm, new LanguageModel(languageResults.get(versionPosition)), versionModel);
            return;
        }

        if (!bookExist) {
            // add new book in same version and language
            new AutographaRepository<LanguageModel>().updateLanguageWithBook(realm, new LanguageModel(languageResults.get(versionPosition)), bookPosition, bookModel);
            return;
        }
    }

    /**
     * Check for line level markers like \q and inline \p and others for formatting and add them to the last verse's text.
     * @param line the text to be appended to the verse's text
     */
    private void addFormattingToLastVerse(String line) {
        if (verseComponentsModelList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("");
            stringBuilder.append(verseComponentsModelList.get(verseComponentsModelList.size() - 1).getText());
            stringBuilder.append(" " + Constants.Styling.NEW_LINE);
            stringBuilder.append(" " + line + " ");
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
        verseComponentsModel.setText(" " + line + " ");
        verseComponentsModel.setAdded(false);
        verseComponentsModelList.add(verseComponentsModel);
    }

}