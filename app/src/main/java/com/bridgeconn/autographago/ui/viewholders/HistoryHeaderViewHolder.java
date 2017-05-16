package com.bridgeconn.autographago.ui.viewholders;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class HistoryHeaderViewHolder extends GroupViewHolder {

    private TextView timeTitle;
    private ImageView arrow;

    public HistoryHeaderViewHolder(View itemView) {
        super(itemView);
        timeTitle = (TextView) itemView.findViewById(R.id.item_header);
        arrow = (ImageView) itemView.findViewById(R.id.item_header_arrow);
    }

    public void setTimeTitle(ExpandableGroup group) {
        timeTitle.setText(group.getTitle());
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
