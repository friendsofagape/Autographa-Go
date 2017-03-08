package com.bridgeconn.autographago.ormutils;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.models.LanguageModel;
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
                            BookIdModel bookIdModel = new BookIdModel();
                            bookIdModel.setLanguageCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getLanguageCode());
                            bookIdModel.setSection(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getSection());
                            bookIdModel.setVersionCode(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getVersionCode());
                            bookIdModel.setBookId(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookId());
                            bookIdModel.setBookName(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookName());
                            bookIdModel.setBookNumber(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookNumber());
                            bookIdModel.setBookmarkChapterNumber(resultsToReturn.get(i).getVersionModels().get(j).getBookModels().get(k).getBookmarkChapterNumber());
                            Constants.CONTAINER_BOOKS_LIST.add(bookIdModel);
                        }
                    }
                }
            }
        }
        realm.close();
    }
}