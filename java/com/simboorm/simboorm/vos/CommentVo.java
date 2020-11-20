package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.CommentStatus;

public class CommentVo {
    private final int index;
    private final String userNickname;
    private final String createdAt;
    private final CommentStatus commentStatus;
    private final String statusChangedAt;
    private final int thumbUP;
    private final int thumbDown;
    private final int replyIndex;
    private final String content;

    public CommentVo(int index, String userNickname, String createdAt, CommentStatus commentStatus, String statusChangedAt, int thumbUP, int thumbDown, int replyIndex, String content) {
        this.index = index;
        this.userNickname = userNickname;
        this.createdAt = createdAt;
        this.commentStatus = commentStatus;
        this.statusChangedAt = statusChangedAt;
        switch(commentStatus.name()) {
            case "OKAY":
                this.thumbUP = thumbUP;
                this.thumbDown = thumbDown;
                this.content = content;
            break;
            case "SUSP":
                this.thumbUP = thumbUP;
                this.thumbDown = thumbDown;
                this.content = "정지된 댓글 입니다.";
            break;
            case "DEL":
                this.thumbUP = thumbUP;
                this.thumbDown = thumbDown;
                this.content = "삭제된 댓글 입니다.";
            break;
            case "CNGD":
                this.thumbUP = thumbUP;
                this.thumbDown = thumbDown;
                this.content = content+"(수정됨)";
            break;
            default:
                this.thumbUP = thumbUP;
                this.thumbDown = thumbDown;
                this.content = content;
                break;
        }
        this.replyIndex = replyIndex;
    }


    public int getIndex() {
        return index;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public CommentStatus getCommentStatus() {
        return commentStatus;
    }

    public String getStatusChangedAt() {
        return statusChangedAt;
    }

    public int getThumbUP() {
        return thumbUP;
    }

    public int getThumbDown() {
        return thumbDown;
    }

    public int getReplyIndex() {
        return replyIndex;
    }

    public String getContent() {
        return content;
    }
}
