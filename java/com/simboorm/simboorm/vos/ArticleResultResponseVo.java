package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.BoardResponseResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ArticleResultResponseVo {
    private final String contents;
    private final BoardResponseResult boardResponseResult;
    private final int index;
    private final String title;
    private final Date createAt;
    private final String userNickname;
    private final int views;
    private final int recommend;
    private final int userRecommend;
    private final JSONArray comments;
    private final JSONObject authority;
    private final String status;


    public ArticleResultResponseVo(String contents, BoardResponseResult boardResponseResult, int index, String title, Date createAt, String userNickname, int views, int recommend, int userRecommend, ArrayList<CommentVo> commentVos, AuthorityVo authority, String status) {
        this.contents = contents;
        this.index = index;
        this.title = title;
        this.createAt = createAt;
        this.views = views;
        this.recommend = recommend;
        this.userRecommend = userRecommend;
        this.boardResponseResult = boardResponseResult;
        this.userNickname = userNickname;
        this.status = status;

        JSONObject authObject = new JSONObject();
        authObject.put("readLevel", authority.getReadLevel());
        authObject.put("writeLevel", authority.getWriteLevel());
        this.authority = authObject;

        if(commentVos != null) {
            if(!commentVos.isEmpty()) {
                JSONArray array = new JSONArray();
                Iterator iterator = commentVos.iterator();

                while(iterator.hasNext()) {
                    JSONObject object = new JSONObject();
                    CommentVo comment = (CommentVo)iterator.next();
                    object.put("index", comment.getIndex());
                    object.put("Nickname", comment.getUserNickname());
                    object.put("CreatedAt", comment.getCreatedAt());
                    object.put("StatusChangedAt", comment.getStatusChangedAt());
                    object.put("ThumbUp", comment.getThumbUP());
                    object.put("ThumbDown", comment.getThumbDown());
                    object.put("ReplyIndex", comment.getReplyIndex());
                    object.put("Content", comment.getContent());

                    array.put(object);
                }
                this.comments = array;
            } else {
                this.comments = null;
            }
        }else {
            this.comments = null;
        }
    }

    public ArticleResultResponseVo(BoardResponseResult boardResponseResult) {
        this.contents = "";
        this.boardResponseResult = boardResponseResult;
        this.comments = null;
        this.authority = null;
        this.index = -1;
        this.title = null;
        this.createAt = null;
        this.userNickname = null;
        this.views = 0;
        this.recommend = 0;
        this.userRecommend =0;
        this.status = null;
    }

    public String getContents() {
        return contents;
    }

    public BoardResponseResult getBoardResponseResult() {
        return boardResponseResult;
    }

    public JSONArray getComments() {
        return comments;
    }

    public JSONObject getAuthority() {
        return authority;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public int getViews() {
        return views;
    }

    public int getRecommend() {
        return recommend;
    }

    public int getUserRecommend() {
        return userRecommend;
    }

    public String getStatus() {
        return status;
    }
}
