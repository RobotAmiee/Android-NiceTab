package me.amiee.nicetab;


class Badge {
    private boolean mSmall;
    private String mText;

    Badge(String text) {
        mText = text;
        mSmall = false;
    }

    Badge() {
        mSmall = true;
    }

    boolean isSmall() {
        return mSmall;
    }

    void setSmall(boolean small) {
        mSmall = small;
    }

    String getText() {
        return mText;
    }

    void setText(String text) {
        mText = text;
    }
}
