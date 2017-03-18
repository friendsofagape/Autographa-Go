package com.bridgeconn.autographago.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.ui.customviews.BounceInterpolator;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

public class HintsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivContinueReading, ivHistory, ivSearch, ivNotes, ivBookmarks, ivHighlights, ivSettings;
    private TextView tvContinueReading, tvHistory, tvSearch, tvNotes, tvBookmarks, tvHighlights, tvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hints);

        UtilFunctions.applyReadingMode();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ivContinueReading = (ImageView) findViewById(R.id.iv_continue_reading);
        ivHistory = (ImageView) findViewById(R.id.iv_history);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        ivNotes = (ImageView) findViewById(R.id.iv_notes);
        ivBookmarks = (ImageView) findViewById(R.id.iv_bookmark);
        ivHighlights = (ImageView) findViewById(R.id.iv_highlight);
        ivSettings = (ImageView) findViewById(R.id.iv_settings);

        tvContinueReading = (TextView) findViewById(R.id.tv_continue_reading);
        tvHistory = (TextView) findViewById(R.id.tv_history);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        tvNotes = (TextView) findViewById(R.id.tv_notes);
        tvBookmarks = (TextView) findViewById(R.id.tv_bookmark);
        tvHighlights = (TextView) findViewById(R.id.tv_highlight);
        tvSettings = (TextView) findViewById(R.id.tv_settings);

        ivContinueReading.setOnClickListener(this);
        ivHistory.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        ivNotes.setOnClickListener(this);
        ivBookmarks.setOnClickListener(this);
        ivHighlights.setOnClickListener(this);
        ivSettings.setOnClickListener(this);

        Toast.makeText(this, "Click on any icon to see hint", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_continue_reading: {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.5, 20);
                myAnim.setInterpolator(interpolator);

                ivContinueReading.startAnimation(myAnim);
                tvContinueReading.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.iv_history: {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.5, 20);
                myAnim.setInterpolator(interpolator);

                ivHistory.startAnimation(myAnim);
                tvHistory.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.iv_search: {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.5, 20);
                myAnim.setInterpolator(interpolator);

                ivSearch.startAnimation(myAnim);
                tvSearch.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.iv_notes: {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.5, 20);
                myAnim.setInterpolator(interpolator);

                ivNotes.startAnimation(myAnim);
                tvNotes.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.iv_bookmark: {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.5, 20);
                myAnim.setInterpolator(interpolator);

                ivBookmarks.startAnimation(myAnim);
                tvBookmarks.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.iv_highlight: {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.5, 20);
                myAnim.setInterpolator(interpolator);

                ivHighlights.startAnimation(myAnim);
                tvHighlights.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.iv_settings: {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                BounceInterpolator interpolator = new BounceInterpolator(0.5, 20);
                myAnim.setInterpolator(interpolator);

                ivSettings.startAnimation(myAnim);
                tvSettings.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
