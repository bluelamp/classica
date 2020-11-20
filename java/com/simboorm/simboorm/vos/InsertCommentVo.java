package com.simboorm.simboorm.vos;

public class InsertCommentVo {
    private final int articleNo;
    private final int userIndex;
    private final int replyIndex;
    private final String text;

    public InsertCommentVo(int articleNo, int userIndex, int replyIndex, String text) {
        this.articleNo = articleNo;
        this.userIndex = userIndex;
        this.replyIndex = replyIndex;
        this.text = text;
    }

    public InsertCommentVo(int articleNo, int userIndex, String text) {
        this.articleNo = articleNo;
        this.userIndex = userIndex;
        this.text = text;
        this.replyIndex = -1;
    }

    public int getArticleNo() {
        return articleNo;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public String getText() {
        return text;
    }

    public int getReplyIndex() {
        return replyIndex;
    }
}
