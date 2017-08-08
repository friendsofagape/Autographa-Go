package com.bridgeconn.autographago.ui.viewholders;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.activities.SelectLanguageAndVersionActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapterNumber;
    private View mView;
    private ImageView mDelete;
    private Fragment mFragment;
    private ArrayList<VersionModel> mVersionModelArrayList;
    private boolean mSelectBook;

    public VersionViewHolder(View itemView, Fragment fragment, ArrayList<VersionModel> versionModelArrayList, boolean selectBook) {
        super(itemView);
        mView = itemView;
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_book_name);
        mDelete = (ImageView) itemView.findViewById(R.id.delete);

        mFragment = fragment;
        mVersionModelArrayList = versionModelArrayList;
        mSelectBook = selectBook;
    }

    public void onBind(final int position) {
        VersionModel versionModel = mVersionModelArrayList.get(position);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (versionModel.isSelected()) {
//                mTvChapterNumber.setBackgroundColor(mFragment.getResources().getColor(R.color.black_40, null));
                mView.setBackgroundColor(Color.LTGRAY);
            } else {
                mView.setBackgroundColor(Color.WHITE);
            }
//        }
        mTvChapterNumber.setText(versionModel.getVersionCode() + "  " + versionModel.getVersionName());
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);

        if (((SelectLanguageAndVersionActivity) mFragment.getActivity()).getSelectedLanguageCode().equalsIgnoreCase("ENG")) {
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

                Intent output = new Intent();
                output.putExtra(Constants.Keys.LANGUAGE_CODE, ((SelectLanguageAndVersionActivity) mFragment.getActivity()).getSelectedLanguageCode());
                output.putExtra(Constants.Keys.VERSION_CODE, mVersionModelArrayList.get(position).getVersionCode());
                output.putExtra(Constants.Keys.LANGUAGE_NAME, ((SelectLanguageAndVersionActivity) mFragment.getActivity()).getSelectedLanguageName());
                output.putExtra(Constants.Keys.BOOK_ID, ((SelectLanguageAndVersionActivity) mFragment.getActivity()).getBookId());
                output.putExtra(Constants.Keys.CHAPTER_NO, ((SelectLanguageAndVersionActivity) mFragment.getActivity()).getChapterNumber());
                output.putExtra(Constants.Keys.VERSE_NO, ((SelectLanguageAndVersionActivity) mFragment.getActivity()).getVerseNumber());

                mFragment.getActivity().setResult(RESULT_OK, output);
                mFragment.getActivity().finish();

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

        new AutographaRepository<VersionModel>().remove(new AllSpecifications.VersionModelByCode
                (((SelectLanguageAndVersionActivity) mFragment.getActivity()).getSelectedLanguageCode(),
                        mVersionModelArrayList.get(position).getVersionCode()));

        mVersionModelArrayList.remove(position);
        ((SelectLanguageAndVersionActivity) mFragment.getActivity()).removeVersion(position);

        if (mVersionModelArrayList.size() == 0) {
            new AutographaRepository<LanguageModel>().remove(new AllSpecifications.LanguageModelByCode(((SelectLanguageAndVersionActivity) mFragment.getActivity()).getSelectedLanguageCode()));

            ((SelectLanguageAndVersionActivity) mFragment.getActivity()).removeLanguageWithVersion();
        } else {
            ((SelectLanguageAndVersionActivity) mFragment.getActivity()).setSelectedLanguage();
        }
    }


    private void showDiscardDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getActivity());
        builder.setTitle(mFragment.getContext().getString(R.string.delete_bible));
        builder.setMessage(mFragment.getContext().getString(R.string.delete_message_version));

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