package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.BoardResponseResult;
import com.simboorm.simboorm.enums.CommentResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class CommentResultResponseVo {
    private final CommentResponse commentResponse;
    private final JSONArray comments;

    public CommentResultResponseVo(CommentResponse commentResponse, ArrayList<CommentVo> commentVos) {
        this.commentResponse = commentResponse;

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
    }

    public CommentResultResponseVo(CommentResponse commentResponse) {
        this.commentResponse = commentResponse;
        this.comments = null;
    }

    public CommentResponse getCommentResponse() {
        return commentResponse;
    }

    public JSONArray getComments() {
        return comments;
    }
}
