package com.bridgeconn.autographago.ormutils;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
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
    public void addToContainer() {

        Constants.CONTAINER.getBookModelList().clear();

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<BookModel> realmResults = new AllSpecifications.AllBooks().generateResults(realm);

        ArrayList<BookModel> resultsToReturn = new ArrayList<>();

        for (BookModel result : realmResults) {
            resultsToReturn.add(new AllMappers.BookMapper().map(result));
        }

        for (int i=0; i<resultsToReturn.size(); i++) {
            BookModel bookModel = new BookModel();
            bookModel.setBookId(resultsToReturn.get(i).getBookId());
            bookModel.setBookName(resultsToReturn.get(i).getBookName());
            bookModel.setBookmarkChapterNumber(resultsToReturn.get(i).getBookmarkChapterNumber());
            for (int j=0; j<resultsToReturn.get(i).getChapterModels().size(); j++) {
                ChapterModel chapterModel = new ChapterModel();
                chapterModel.setChapterId(resultsToReturn.get(i).getChapterModels().get(j).getChapterId());
                chapterModel.setChapterNumber(resultsToReturn.get(i).getChapterModels().get(j).getChapterNumber());
                for (int k=0; k<resultsToReturn.get(i).getChapterModels().get(j).getVerseComponentsModels().size(); k++) {
                    VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
                    verseComponentsModel.setChapterId(resultsToReturn.get(i).getChapterModels().get(j).getChapterId());
                    verseComponentsModel.setType(resultsToReturn.get(i).getChapterModels().get(j).getVerseComponentsModels().get(k).getType());
                    verseComponentsModel.setVerseNumber(resultsToReturn.get(i).getChapterModels().get(j).getVerseComponentsModels().get(k).getVerseNumber());
                    verseComponentsModel.setText(resultsToReturn.get(i).getChapterModels().get(j).getVerseComponentsModels().get(k).getText());
                    verseComponentsModel.setHighlighted(resultsToReturn.get(i).getChapterModels().get(j).getVerseComponentsModels().get(k).isHighlighted());
                    chapterModel.getVerseComponentsModels().add(verseComponentsModel);
                }
                bookModel.getChapterModels().add(chapterModel);
            }
            Constants.CONTAINER.getBookModelList().add(bookModel);
        }

        realm.close();
    }
}