package com.oneul.oneul;

import androidx.annotation.Nullable;

public class Oneul {
    int oNo;
    String oStart, oEnd, oTitle, oMemo;
    byte[] pPhoto;

    public Oneul(int oNo, String oStart, @Nullable String oEnd, String oTitle, String oMemo,
                 @Nullable byte[] pPhoto) {
        this.oNo = oNo;
        this.oStart = oStart;
        this.oEnd = oEnd;
        this.oTitle = oTitle;
        this.oMemo = oMemo;
        this.pPhoto = pPhoto;
    }

    public int getoNo() {
        return oNo;
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