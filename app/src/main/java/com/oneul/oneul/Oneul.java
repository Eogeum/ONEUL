package com.oneul.oneul;

public class Oneul {
    int oNo;
    String oDate, oStart, oEnd, oTitle, oMemo;

    public Oneul(int oNo, String oStart, String oTitle) {
        this.oNo = oNo;
        this.oStart = oStart;
        this.oTitle = oTitle;
    }

    public Oneul(String oDate, String oStart, String oEnd, String oTitle, String oMemo) {
        this.oDate = oDate;
        this.oStart = oStart;
        this.oEnd = oEnd;
        this.oTitle = oTitle;
        this.oMemo = oMemo;
    }


    public Oneul() {
    }

    public int getoNo() {
        return oNo;
    }

    public String getoStart() {
        return oStart;
    }

    public void setoStart(String oStart) {
        this.oStart = oStart;
    }

    public String getoEnd() {
        return oEnd;
    }

    public void setoEnd(String oEnd) {
        this.oTitle = oEnd;
    }

    public String getoTitle() {
        return oTitle;
    }

    public void setoTitle(String oTitle) {
        this.oTitle = oTitle;
    }

    public String getoMemo() {
        return oMemo;
    }

    public void setoMemo(String oMemo) {
        this.oMemo = oMemo;
    }

    public void setoDate(String oDate) {
        this.oDate = oDate;
    }
}
