package com.bridgeconn.autographago.ui.viewholders;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.activities.SelectLanguageAndVersionActivity;
import com.bridgeconn.autographago.ui.fragments.LanguageFragment;
import com.bridgeconn.autographago.utils.SharedPrefs;

import java.util.ArrayList;

public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapterNumber;
    private View mView;
    private ImageView mDelete;
    private Fragment mFragment;
    private ArrayList<LanguageModel> mLanguageModelArrayList;

    public LanguageViewHolder(View itemView, Fragment fragment, ArrayList<LanguageModel> languageModelArrayList) {
        super(itemView);
        mView = itemView;
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_book_name);
        mDelete = (ImageView) itemView.findViewById(R.id.delete);

        mFragment = fragment;
        mLanguageModelArrayList = languageModelArrayList;
    }

    public void onBind(final int position) {
        LanguageModel languageModel = mLanguageModelArrayList.get(position);

        switch (SharedPrefs.getReadingMode()) {
            case Day: {
                if (languageModel.isSelected()) {
                    mView.setBackgroundColor(Color.LTGRAY);
                } else {
                    mView.setBackgroundColor(Color.WHITE);
                }
                break;
            }
            case Night: {
                if (languageModel.isSelected()) {
                    mView.setBackgroundColor(Color.DKGRAY);
                } else {
                    mView.setBackgroundColor(Color.BLACK);
                }
                break;
            }
        }

        mTvChapterNumber.setText(languageModel.getLanguageName());
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);

        if (languageModel.getLanguageCode().equalsIgnoreCase("ENG")) {
            mDelete.setVisibility(View.GONE);
        } else {
            mDelete.setVisibility(View.VISIBLE);
        }
        mDelete.setTag(position);
        mDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectLanguageAndVersionActivity) {
                    ((LanguageFragment) mFragment).setSelected(position);
                    ((SelectLanguageAndVersionActivity) mFragment.getActivity()).openVersionPage(mLanguageModelArrayList.get(position).getLanguageCode());
                }
                break;
            }
            case R.id.delete: {
                int position = (int) v.getTag();
                showDiscardDialog(position);
                break;
            }
        }
    }

    private void yesDelete(int position) {
        new AutographaRepository<LanguageModel>().remove(new AllSpecifications.LanguageModelByCode(mLanguageModelArrayList.get(position).getLanguageCode()));

        mLanguageModelArrayList.remove(position);
        ((SelectLanguageAndVersionActivity) mFragment.getActivity()).removeLanguage(position);
    }

    private void showDiscardDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getActivity());
        builder.setTitle(mFragment.getContext().getString(R.string.delete_bible));
        builder.setMessage(mFragment.getContext().getString(R.string.delete_message_language));

        String positiveText = mFragment.getContext().getString(R.string.yes);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yesDelete(position);
                    }
                });

        String negativeText = mFragment.getContext().getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}