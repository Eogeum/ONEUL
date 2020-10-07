package com.oneul.oneul;

import androidx.annotation.Nullable;

public class Oneul {
    int oNo;
    String oDate, oStart, oEnd, oTitle, oMemo;
    byte[] pPhoto;

    public Oneul(int oNo, String oDate, String oStart, @Nullable String oEnd, String oTitle, String oMemo,
                 @Nullable byte[] pPhoto) {
        this.oNo = oNo;
        this.oDate = oDate;
        this.oStart = oStart;
        this.oEnd = oEnd;
        this.oTitle = oTitle;
        this.oMemo = oMemo;
        this.pPhoto = pPhoto;
    }

    public int getoNo() {
        return oNo;
    }

    public String getoDate() {
        return oDate;
    }

    public String getoStart() {
        return oStart;
    }

    public String getoTitle() {
        return oTitle;
    }

    public String getoMemo() {
        return oMemo;
    }
}