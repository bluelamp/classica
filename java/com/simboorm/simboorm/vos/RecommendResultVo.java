package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.RecommendResult;

public class RecommendResultVo {
    private final int recommend;
    private final int userRecommend;
    private final RecommendResult recommendResult;

    public RecommendResultVo(int recommend, int userRecommend, RecommendResult recommendResult) {
        this.recommend = recommend;
        this.userRecommend = userRecommend;
        this.recommendResult = recommendResult;
    }

    public RecommendResultVo(RecommendResult recommendResult) {
        this.recommendResult = recommendResult;
        this.recommend = 0;
        this.userRecommend =0;
    }

    public int getRecommend() {
        return recommend;
    }

    public int getUserRecommend() {
        return userRecommend;
    }

    public RecommendResult getRecommendResult() {
        return recommendResult;
    }
}
