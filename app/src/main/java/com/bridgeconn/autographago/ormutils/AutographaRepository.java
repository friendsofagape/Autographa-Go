package com.bridgeconn.autographago.ormutils;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class AutographaRepository<T extends RealmObject> implements Repository<T> {

    @Override
    public void add(final T item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(item);
            }
        });

        realm.close();
    }

    @UiThread
    @Override
    public void add(final T item, final RepositoryRequestListener listener) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(item);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (listener != null) {
                    listener.onError(error);
                }
                realm.close();
            }
        });
    }


    @UiThread
    @Override
    public void add(final Iterable<T> items, final RepositoryRequestListener listener) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(items);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (listener != null) {
                    listener.onError(error);
                }
                realm.close();
            }
        });
    }

    @WorkerThread
    @Override
    public void add(final Iterable<T> items) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(items);
            }
        });

        realm.close();
    }

    @Override
    public void update(T item) {
        add(item);
    }

    @Override
    public void remove(Specification<T> specification) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<T> realmResults = specification.generateResults(realm);
        realm.beginTransaction();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public ArrayList<T> query(Specification<T> specification, Mapper<T, T> mapper) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<T> realmResults = specification.generateResults(realm);

        ArrayList<T> resultsToReturn = new ArrayList<>();

        for (T result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }

        realm.close();

        return resultsToReturn;
    }

    @Override
    public int count(Specification<T> specification) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<T> realmResults = specification.generateResults(realm);
        int size = realmResults.size();
        realm.close();
        return size;
    }

    @Override
    public void addToNewContainer(String language, String version) {

        Constants.CONTAINER.getBookModelList().clear();

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<LanguageModel> realmResults = new AllSpecifications.AllLanguages().generateResults(realm);

        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();

        for (LanguageModel languageModel : realmResults) {
            resultsToReturn.add(new AllMappers.LanguageMapper().map(languageModel));
        }

        for (int i=0; i<resultsToReturn.size(); i++) {
            if (resultsToReturn.get(i).getLanguageCode().equals(language)) {
                for (int j = 0; j < resultsToReturn.get(i).getVersionModels().size(); j++) {
                    if (resultsToReturn.get(i).getVersionModels().get(j).getVersionCode().equals(version)) {
                        for (int k = 0; k < resultsToReturn.get(i).getVersionModels().get(j).getBookModels().size(); k++) {
                            BookModel bookModel = new BookModel();
                            bookModel.setBookNumber(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookNumber());
                            bookModel.setSection(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getSection());
                            bookModel.setBookmarkChapterNumber(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookmarkChapterNumber());
                            bookModel.setBookId(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookId());
                            bookModel.setBookName(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookName());
                            bookModel.setLanguageCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getLanguageCode());
                            bookModel.setVersionCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getVersionCode());
                            for (int l = 0; l < resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().size(); l++) {
                                ChapterModel chapterModel = new ChapterModel();
                                chapterModel.setChapterId(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getChapterId());
                                chapterModel.setChapterNumber(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getChapterNumber());
                                chapterModel.setLanguageCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getLanguageCode());
                                chapterModel.setVersionCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVersionCode());
                                for (int m = 0; m < resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().size(); m++) {
                                    VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
                                    verseComponentsModel.setLanguageCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().get(m).getLanguageCode());
                                    verseComponentsModel.setVersionCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().get(m).getVersionCode());
                                    verseComponentsModel.setChapterId(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().get(m).getChapterId());
                                    verseComponentsModel.setType(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().get(m).getType());
                                    verseComponentsModel.setHighlighted(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().get(m).isHighlighted());
                                    verseComponentsModel.setText(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().get(m).getText());
                                    verseComponentsModel.setVerseNumber(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getChapterModels().get(l).getVerseComponentsModels().get(m).getVerseNumber());
                                    chapterModel.getVerseComponentsModels().add(verseComponentsModel);
                                }
                                bookModel.getChapterModels().add(chapterModel);
                            }
                            Constants.CONTAINER.getBookModelList().add(bookModel);
                        }
                    }
                }
            }
        }
        realm.close();
    }
}