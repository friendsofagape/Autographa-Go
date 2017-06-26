package com.bridgeconn.autographago.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.ui.activities.SelectLanguageAndVersionActivity;
import com.bridgeconn.autographago.ui.adapters.LanguageVersionAdapter;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class LanguageFragment extends Fragment implements SelectLanguageAndVersionActivity.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private ArrayList<LanguageModel> languageModelArrayList = new ArrayList<>();
    private LanguageVersionAdapter mAdapter;
    private boolean mSelectBook;

    @Override
    public void onItemClick(String languageCode) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectBook = getArguments().getBoolean(Constants.Keys.SELECT_BOOK);
        languageModelArrayList = ((SelectLanguageAndVersionActivity) getActivity()).getLanguageModelArrayList();
        if (languageModelArrayList.size() > 0) {
            setSelected(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_books);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new LanguageVersionAdapter(this, languageModelArrayList, null, mSelectBook);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void setSelected(int position) {
        for (int i = 0; i< languageModelArrayList.size(); i++) {
            languageModelArrayList.get(i).setSelected(false);
        }
        if (position >= 0) {
            languageModelArrayList.get(position).setSelected(true);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public String getSelectedLanguageCode() {
        for (int i = 0; i< languageModelArrayList.size(); i++) {
            if (languageModelArrayList.get(i).isSelected()) {
                return languageModelArrayList.get(i).getLanguageCode();
            }
        }
        return null;
    }

    public String getSelectedLanguageName() {
        for (int i = 0; i< languageModelArrayList.size(); i++) {
            if (languageModelArrayList.get(i).isSelected()) {
                return languageModelArrayList.get(i).getLanguageName();
            }
        }
        return null;
    }

    public void removeItem(int position) {
        mAdapter.notifyItemRemoved(position);
    }

    public void notifyNewLanguageAdded() {
        mAdapter.notifyDataSetChanged();
    }
}