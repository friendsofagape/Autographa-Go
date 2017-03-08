package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.util.Log;

import com.bridgeconn.autographago.models.AllLanguagesModel;
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
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class USFMParser {

    private AllLanguagesModel allLanguagesModel;
    private List<LanguageModel> languageModelList;

    private LanguageModel languageModel;
    private RealmList<VersionModel> versionModelList;
    private VersionModel versionModel;
    private RealmList<BookModel> bookModelList;

    private BookModel bookModel;
    private RealmList<ChapterModel> chapterModelList;
    private RealmList<VerseComponentsModel> verseComponentsModelList;

    private boolean languageExist = false, versionExist = false, bookExist = false;
    private ArrayList<LanguageModel> languageResults;
    private int versionPosition, bookPosition;

    public USFMParser() {
        allLanguagesModel = new AllLanguagesModel();
        languageModelList = new ArrayList<>();

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
    public boolean parseUSFMFile(Context context, String fileName, boolean fromAssets, String languageName, String versionCode, String versionName) {
        BufferedReader reader = null;
        try {
            if (fromAssets) {
                reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            } else {
                reader = new BufferedReader(new FileReader(new File(fileName)));
            }
            String mLine;
//            try {
                while ((mLine = reader.readLine()) != null) {
                    if (!processLine(context, mLine, languageName, versionCode, versionName)) {
                        return true;
                    }
                }
                addComponentsToChapter();
                addChaptersToBook();

                addBookToContainer(languageName, versionCode, versionName);

                return true;
//            } catch (Exception e) {
//                Log.e(Constants.TAG, "Exception in processing lines. So skipping this file" + e.toString());
//            }
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
        return false;
    }

    /**
     * Parse the line with corresponding marker at the first position
     * @param line each line in the file
     */
    private boolean processLine(Context context, String line, String languageName, String versionCode, String versionName) {
//        line = line.trim();
        String[] splitString = line.split(Constants.Styling.SPLIT_SPACE);
        if (splitString.length == 0) {
            return true;
        }
        switch (splitString[0]) {
            case Constants.Markers.MARKER_BOOK_NAME: {
                if (!addBook(context, splitString, languageName, versionCode, versionName)) {
                    Log.i(Constants.DUMMY_TAG, "SKIP BOOK");
                    return false;
                }
                break;
            }
            case Constants.Markers.MARKER_CHAPTER_NUMBER: {
                addChapter(splitString[1], languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_NEW_PARAGRAPH: {
                addParagraph(splitString, languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_VERSE_NUMBER: {
                addVerse(splitString, languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, Constants.ParagraphMarker.S1, splitString, languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_ONE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_ONE, Constants.ParagraphMarker.S1, splitString, languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_TWO: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_TWO, Constants.ParagraphMarker.S2, splitString, languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_THREE: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_THREE, Constants.ParagraphMarker.S3, splitString, languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_SECTION_HEADING_FOUR: {
                addSection(Constants.MarkerTypes.SECTION_HEADING_FOUR, Constants.ParagraphMarker.S4, splitString, languageName, versionCode);
                break;
            }
            case Constants.Markers.MARKER_CHUNK: {
                addChunk(languageName, versionCode);
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
    private boolean addBook(Context context, String [] splitString, String languageName, String versionCode, String versionName) {

        bookModel.setBookId(splitString[1]);
        bookModel.setBookName(UtilFunctions.getBookNameFromMapping(context, splitString[1]));
        bookModel.setSection(UtilFunctions.getBookSectionFromMapping(context, splitString[1]));
        bookModel.setBookNumber(UtilFunctions.getBookNumberFromMapping(context, splitString[1]));
        bookModel.setLanguageCode(UtilFunctions.getLanguageCodeFromName(languageName));
        bookModel.setVersionCode(versionCode);

        languageResults = query(new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (int i=0; i<languageResults.size(); i++) {
            if (languageResults.get(i).getLanguageName().equals(languageName)) {
                languageExist = true;
                versionPosition = i;
                for (int j=0; j<languageResults.get(i).getVersionModels().size(); j++) {
                    if (languageResults.get(i).getVersionModels().get(j).getVersionCode().equals(versionCode)) {
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
    private void addChapter(String chapterNumber, String languageName, String versionCode) {
        addComponentsToChapter();
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setChapterNumber(Integer.parseInt(chapterNumber));
        chapterModel.setChapterId(bookModel.getBookId() + "_" + chapterNumber);
        chapterModel.setLanguageCode(UtilFunctions.getLanguageCodeFromName(languageName));
        chapterModel.setVersionCode(versionCode);
        chapterModelList.add(chapterModel);
    }

    /**
     * This adds the chunk \\s5 marker of the line to the component model and add it to the list of component models
     */
    private void addChunk(String languageName, String versionCode) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.CHUNK);
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.S5);
        verseComponentsModel.setLanguageCode(UtilFunctions.getLanguageCodeFromName(languageName));
        verseComponentsModel.setVersionCode(versionCode);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the section marker of the line to the component model and add it to the list of component models
     * @param type the section marker type
     * @param splitString the line for section heading text
     */
    private void addSection(String type, Constants.ParagraphMarker marker, String [] splitString, String languageName, String versionCode) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(type);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(marker);
        verseComponentsModel.setLanguageCode(UtilFunctions.getLanguageCodeFromName(languageName));
        verseComponentsModel.setVersionCode(versionCode);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the new paragraph marker of the line to the component model and add it to the list of component models
     * @param splitString the string for any text that is in the line
     */
    private void addParagraph(String [] splitString, String languageName, String versionCode) {
        VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
        verseComponentsModel.setType(Constants.MarkerTypes.PARAGRAPH);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i=1; i<splitString.length; i++) {
            stringBuilder.append(splitString[i] + " ");
        }
        verseComponentsModel.setText(stringBuilder.toString());
        verseComponentsModel.setAdded(true);
        verseComponentsModel.setMarker(Constants.ParagraphMarker.P);
        verseComponentsModel.setLanguageCode(UtilFunctions.getLanguageCodeFromName(languageName));
        verseComponentsModel.setVersionCode(versionCode);
        verseComponentsModelList.add(verseComponentsModel);
    }

    /**
     * This adds the verse marker of the line to the component model and add it to the list of component models.
     * This also adds this verse's number to all previous models that do not have a verse number
     * @param splitString the line for text of the verse
     */
    private void addVerse(String [] splitString, String languageName, String versionCode) {
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
        verseComponentsModel.setLanguageCode(UtilFunctions.getLanguageCodeFromName(languageName));
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
                    if (i==0) {
                        if (verseComponentsModelList.get(i).getVerseNumber() != null) {
                            size++;
                        }
                    } else {
                        if (verseComponentsModelList.get(i).getVerseNumber().equals(
                                verseComponentsModelList.get(i-1).getVerseNumber())) {
                            continue;
                        } else {
                            if (verseComponentsModelList.get(i).getVerseNumber() != null) {
                                size++;
                            }
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

    private Realm realm;

    private ArrayList<LanguageModel> query(Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {

        realm = Realm.getDefaultInstance();

        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);
        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();
        for (LanguageModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }

        ArrayList<LanguageModel> languageResults = new ArrayList<>();
        for (LanguageModel languageModel1 : resultsToReturn) {
            LanguageModel lModel = new LanguageModel();
            lModel.setLanguageCode(languageModel1.getLanguageCode());
            lModel.setLanguageName(languageModel1.getLanguageName());
            for (VersionModel versionModel1 : languageModel1.getVersionModels()) {
                VersionModel vModel = new VersionModel();
                vModel.setVersionCode(versionModel1.getVersionCode());
                vModel.setVersionName(versionModel1.getVersionName());
                for (BookModel bookModel1 : versionModel1.getBookModels()) {
                    BookModel bModel = new BookModel();
                    bModel.setBookId(bookModel1.getBookId());
                    bModel.setBookmarkChapterNumber(bookModel1.getBookmarkChapterNumber());
                    bModel.setBookName(bookModel1.getBookName());
                    bModel.setBookNumber(bookModel1.getBookNumber());
                    bModel.setSection(bookModel1.getSection());
                    bModel.setLanguageCode(bookModel1.getLanguageCode());
                    bModel.setVersionCode(bookModel1.getVersionCode());
                    for (ChapterModel chapterModel1 : bookModel1.getChapterModels()) {
                        ChapterModel cModel = new ChapterModel();
                        cModel.setChapterNumber(chapterModel1.getChapterNumber());
                        cModel.setChapterId(chapterModel1.getChapterId());
                        cModel.setLanguageCode(chapterModel1.getLanguageCode());
                        cModel.setVersionCode(chapterModel1.getVersionCode());
                        for (VerseComponentsModel verseComponentsModel1 : chapterModel1.getVerseComponentsModels()) {
                            VerseComponentsModel vcModel = new VerseComponentsModel();
                            vcModel.setLanguageCode(verseComponentsModel1.getLanguageCode());
                            vcModel.setVersionCode(verseComponentsModel1.getVersionCode());
                            vcModel.setText(verseComponentsModel1.getText());
                            vcModel.setVerseNumber(verseComponentsModel1.getVerseNumber());
                            vcModel.setChapterId(verseComponentsModel1.getChapterId());
                            vcModel.setType(verseComponentsModel1.getType());
                            vcModel.setHighlighted(verseComponentsModel1.isHighlighted());
                            cModel.getVerseComponentsModels().add(vcModel);
                        }
                        bModel.getChapterModels().add(cModel);
                    }
                    vModel.getBookModels().add(bModel);
                }
                lModel.getVersionModels().add(vModel);
            }
            languageResults.add(lModel);
        }

        realm.close();

        return languageResults;
    }

    /**
     * At the end, add the book to the database
     */
    private void addBookToContainer(String languageName, String versionCode, String versionName) {

        versionModel.setVersionCode(versionCode);
        versionModel.setVersionName(versionName);
        versionModel.getBookModels().add(bookModel);

        if (!languageExist) {
            // add new all
            Log.i(Constants.DUMMY_TAG, "adding new language - " + languageName);
            languageModel.getVersionModels().add(versionModel);
            languageModel.setLanguageName(languageName);
            languageModel.setLanguageCode(UtilFunctions.getLanguageCodeFromName(languageName));
            new AutographaRepository<LanguageModel>().add(languageModel);
            return;
        }

        if (!versionExist) {
            // add new version in same language
            Log.i(Constants.DUMMY_TAG, "adding new version - " + versionModel.getVersionCode());
            LanguageModel newLanguageModel = new LanguageModel(languageResults.get(versionPosition));
            newLanguageModel.getVersionModels().add(versionModel);
            new AutographaRepository<LanguageModel>().update(newLanguageModel);
            return;
        }

        if (!bookExist) {
            // add new book in same version and language
            Log.i(Constants.DUMMY_TAG, "adding new book - " + bookModel.getBookId());
            LanguageModel newLanguageModel = new LanguageModel(languageResults.get(versionPosition));
            newLanguageModel.getVersionModels().get(bookPosition).getBookModels().add(bookModel);
            new AutographaRepository<LanguageModel>().update(newLanguageModel);
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