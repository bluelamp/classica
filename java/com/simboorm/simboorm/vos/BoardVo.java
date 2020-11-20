package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.Categories;

import java.util.Date;

public class BoardVo {
    private final int index;
    private final Categories categories;
    private final int views;
    private final int comments;
    private final Date CreateAt;
    private final String userNickname;
    private final int recommend;
    private final int userRecommend;
    private final String title;
    private final String text;
    private final String status;


    public BoardVo(int index, Categories categories, int views, int comments, Date createAt, String userNickname, int recommend, int userRecommend, String title, String text, String status) {
        this.index = index;
        this.categories = categories;
        this.views = views;
        this.comments = comments;
        CreateAt = createAt;
        this.userNickname = userNickname;
        this.userRecommend = userRecommend;
        this.recommend = recommend;
        this.title = title;
        this.status = status;
        this.text = text;
    }

    public BoardVo(int index, Categories categories, String userNickname) {
        this.index = index;
        this.categories = categories;
        this.views = 0;
        this.comments = 0;
        CreateAt = null;
        this.userNickname = userNickname;
        this.recommend = 0;
        this.userRecommend = 0;
        this.title = null;
        this.status = null;
        this.text = null;
    }

    public BoardVo(Categories categories, String userNickname, String title, String text, String status) {
        this.categories = categories;
        this.userNickname = userNickname;
        this.title = title;
        this.text = text;
        this.status = status;
        this.index = -1;
        this.views = 0;
        this.comments = 0;
        this.CreateAt = null;
        this.recommend = 0;
        this.userRecommend = 0;
    }

    public BoardVo(int index, Categories categories, int views, int comments, Date createAt, String userNickname, int recommend, String title, String status) {
        this.index = index;
        this.categories = categories;
        this.views = views;
        this.comments = comments;
        CreateAt = createAt;
        this.userNickname = userNickname;
        this.recommend = recommend;
        this.userRecommend = 0;
        this.title = title;
        this.status = status;
        this.text = "";                   //일단은 비워둠
    }

    public BoardVo(int index, Categories categories, String userNickname, String title, String text, String status) {
        this.index = index;
        this.categories = categories;
        this.userNickname = userNickname;
        this.title = title;
        this.text = text;
        this.status = status;
        this.views = 0;
        this.comments = 0;
        CreateAt = null;
        this.recommend = 0;
        this.userRecommend = 0;
    }

    public int getIndex() {
        return index;
    }

    public Categories getCategories() {
        return categories;
    }

    public int getViews() {
        return views;
    }

    public int getComments() {
        return comments;
    }

    public Date getCreateAt() {
        return CreateAt;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getRecommend() {
        return recommend;
    }

    public String getStatus() {
        return status;
    }

    public int getUserRecommend() {
        return userRecommend;
    }
}
