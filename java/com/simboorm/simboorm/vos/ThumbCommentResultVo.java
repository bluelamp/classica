package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.ThumbCommentResult;

public class ThumbCommentResultVo {
    private final int thumbUp;          //이벤트 처리후, 실제 현재 like와
    private final int thumbDown;        //hate 수를 가져온다.
    private final ThumbCommentResult thumbCommentResult;

    public ThumbCommentResultVo(int thumbUp, int thumbDown, ThumbCommentResult thumbCommentResult) {
        this.thumbUp = thumbUp;
        this.thumbDown = thumbDown;
        this.thumbCommentResult = thumbCommentResult;
    }

    public ThumbCommentResultVo(ThumbCommentResult thumbCommentResult) {
        this.thumbUp = 0;
        this.thumbDown = 0;
        this.thumbCommentResult = thumbCommentResult;
    }

    public int getThumbUp() {
        return thumbUp;
    }

    public int getThumbDown() {
        return thumbDown;
    }

    public ThumbCommentResult getThumbCommentResult() {
        return thumbCommentResult;
    }
}
