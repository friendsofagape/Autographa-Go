package com.bridgeconn.autographago.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class ExpandableHistoryModel extends ExpandableGroup<SearchModel> {

    public ExpandableHistoryModel(String title, List<SearchModel> items) {
        super(title, items);
    }

}
