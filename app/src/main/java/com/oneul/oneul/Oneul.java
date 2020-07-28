package com.oneul.oneul;

public class Oneul {
    int oNo;
    String oDate, oStart, oEnd, oTitle, oMemo;

    public Oneul(int oNo, String oStart, String oTitle, String oMemo) {
        this.oNo = oNo;
        this.oStart = oStart;
        this.oTitle = oTitle;
        this.oMemo = oMemo;
    }

    public Oneul(String oDate, String oStart, String oEnd, String oTitle, String oMemo) {
        this.oDate = oDate;
        this.oStart = oStart;
        this.oEnd = oEnd;
        this.oTitle = oTitle;
        this.oMemo = oMemo;
    }

    public int getoNo() {
        return oNo;
    }

    public String getoStart() {
        return oStart;
    }

    public String getoEnd() {
        return oEnd;
    }

    public String getoTitle() {
        return oTitle;
    }

    public String getoMemo() {
        return oMemo;
    }
}
