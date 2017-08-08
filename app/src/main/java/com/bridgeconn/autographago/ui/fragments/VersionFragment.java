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
        setSelected(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLanguageCode = ((SelectLanguageAndVersionActivity) getActivity()).getSelectedLanguageCode();
        if (mLanguageCode == null) {
            getActivity().finish();
            return;
        }
        ((SelectLanguageAndVersionActivity) getActivity()).setVersionModelArrayList(mLanguageCode);

        mSelectBook = getArguments().getBoolean(Constants.Keys.SELECT_BOOK);
        String versionCode = getArguments().getString(Constants.Keys.VERSION_CODE);

        mVersionModels = ((SelectLanguageAndVersionActivity) getActivity()).getVersionModelArrayList();

        if (mVersionModels.size() > 0) {
            setSelected(0);
            if (versionCode != null) {
                for (int i=0; i<mVersionModels.size(); i++) {
                    if (mVersionModels.get(i).getVersionCode().equals(versionCode)) {
                        setSelected(i);
                        break;
                    }
                }
            }
        }
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

    public void setSelected(int position) {
        if (mVersionModels.size() == 0) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            return;
        }
        for (int i = 0; i< mVersionModels.size(); i++) {
            mVersionModels.get(i).setSelected(false);
        }
        if (position >= 0) {
            mVersionModels.get(position).setSelected(true);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }
}