package com.bridgeconn.autographago.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ui.activities.SelectLanguageAndVersionActivity;
import com.bridgeconn.autographago.ui.adapters.LanguageVersionAdapter;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class VersionFragment extends Fragment implements SelectLanguageAndVersionActivity.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private LanguageVersionAdapter mAdapter;
    private String mLanguageCode;
    private boolean mSelectBook;
    private ArrayList<VersionModel> mVersionModels = new ArrayList<>();

    @Override
    public void onItemClick(String languageCode) {
        mLanguageCode = languageCode;
        ((SelectLanguageAndVersionActivity) getActivity()).setVersionModelArrayList(mLanguageCode);
        mVersionModels = ((SelectLanguageAndVersionActivity) getActivity()).getVersionModelArrayList();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLanguageCode = ((SelectLanguageAndVersionActivity) getActivity()).getLanguageModelArrayList().get(0).getLanguageCode();
        ((SelectLanguageAndVersionActivity) getActivity()).setVersionModelArrayList(mLanguageCode);

        mSelectBook = getArguments().getBoolean(Constants.Keys.SELECT_BOOK);

        mVersionModels = ((SelectLanguageAndVersionActivity) getActivity()).getVersionModelArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_books);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new LanguageVersionAdapter(this, null, mVersionModels, mSelectBook);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void removeItem(int position) {
        mAdapter.notifyItemRemoved(position);
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }
}