package com.oneul.oneul;

public class Oneul {
    String oDate, oStart, oEnd, oTitle;

    public Oneul(String oDate, String oStart, String oEnd, String oTitle) {
        this.oDate = oDate;
        this.oStart = oStart;
        this.oEnd = oEnd;
        this.oTitle = oTitle;
    }

    public String getoDate() {
        return oDate;
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

    public void setoDate(String oDate) {
        this.oDate = oDate;
    }

    public void setoStart(String oStart) {
        this.oStart = oStart;
    }

    public void setoEnd(String oEnd) {
        this.oTitle = oEnd;
    }

    public void setoTitle(String oTitle) {
        this.oTitle = oTitle;
    }

    public Oneul() {
    }

}
