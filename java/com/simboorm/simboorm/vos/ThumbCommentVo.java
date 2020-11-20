package com.simboorm.simboorm.vos;

public class ThumbCommentVo {
    private final int articleNo;
    private final int commentNo;
    private final UserVo userVo;
    private final String thumbKey;

    public ThumbCommentVo(int articleNo, int commentNo, UserVo userVo, String thumbKey) {
        this.articleNo = articleNo;
        this.commentNo = commentNo;
        this.userVo = userVo;
        this.thumbKey = thumbKey;
    }

    public int getArticleNo() {
        return articleNo;
    }

    public UserVo getUserVo() {
        return userVo;
    }

    public String getThumbKey() {
        return thumbKey;
    }

    public int getCommentNo() {
        return commentNo;
    }
}
