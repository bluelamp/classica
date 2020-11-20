package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.RecommendResult;

public class RecommendVo {
    private final int ArticleNo;
    private final UserVo userVo;

    public RecommendVo(int articleNo, UserVo userVo) {
        ArticleNo = articleNo;
        this.userVo = userVo;
    }

    public int getArticleNo() {
        return ArticleNo;
    }

    public UserVo getUserVo() {
        return userVo;
    }
}
