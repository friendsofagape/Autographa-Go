package com.bridgeconn.autographago.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class NotesStyleModel extends RealmObject implements Parcelable {

    int style;
    int start;
    int end;

    public NotesStyleModel(NotesStyleModel model) {
        style = model.getStyle();
        start = model.getStart();
        end = model.getEnd();
    }

    public NotesStyleModel() {
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.style);
        dest.writeInt(this.start);
        dest.writeInt(this.end);
    }

    protected NotesStyleModel(Parcel in) {
        this.style = in.readInt();
        this.start = in.readInt();
        this.end = in.readInt();
    }

    public static final Creator<NotesStyleModel> CREATOR = new Creator<NotesStyleModel>() {
        @Override
        public NotesStyleModel createFromParcel(Parcel source) {
            return new NotesStyleModel(source);
        }

        @Override
        public NotesStyleModel[] newArray(int size) {
            return new NotesStyleModel[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NotesStyleModel &&
                this.style == ((NotesStyleModel) obj).style &&
                this.start == ((NotesStyleModel) obj).start &&
                this.end == ((NotesStyleModel) obj).end;
    }

    @Override
    public int hashCode() {
        return (this.getStyle()+ " " + this.getStart()+ " " + this.getEnd()).hashCode();
    }
}
