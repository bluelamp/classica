package com.simboorm.simboorm.controllers;

import com.simboorm.simboorm.Converter;
import com.simboorm.simboorm.enums.BoardResponseResult;
import com.simboorm.simboorm.enums.Categories;
import com.simboorm.simboorm.enums.CommentResponse;
import com.simboorm.simboorm.enums.ThumbCommentResult;
import com.simboorm.simboorm.services.BoardService;
import com.simboorm.simboorm.utility.Variable;
import com.simboorm.simboorm.vos.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping(value="/board/api")
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @RequestMapping(value="read-article", method= RequestMethod.POST)
    public void readArticle(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(name="articleNo", defaultValue = "1") String articleNo,
                            @RequestParam(name="categorie", defaultValue = "FREE") String categorie) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            Categories categories;
            switch(categorie) {
                case "FREE":
                    categories = Categories.FREE;
                    break;
                case "NOTI":
                    categories = Categories.NOTI;
                    break;
                case "QNA":
                    categories = Categories.QNA;
                    break;
                default:
                    categories = Categories.FREE;
            }
            UserVo userVo = Variable.getUser(session);
            ArticleResultResponseVo articleResultResponseVo = this.boardService.getArticle(Integer.parseInt(articleNo), userVo);

            JSONArray commentArray = new JSONArray();
            if(articleResultResponseVo.getComments() != null) {
                for (int i = 0; i < articleResultResponseVo.getComments().length(); i++) {
                    JSONObject commentObject = new JSONObject();
                    int commentNo = (int) articleResultResponseVo.getComments().getJSONObject(i).get("index");
                    ThumbCommentVo thumbCommentVo = new ThumbCommentVo(
                            Integer.parseInt(articleNo),
                            commentNo,
                            userVo,
                            null
                    );
                    String thumbKey = this.boardService.getThumbComment(thumbCommentVo);
                    if (thumbKey != null) {
                        commentObject.put("commentIndex", commentNo);
                        commentObject.put("thumbKey", thumbKey);
                        commentArray.put(commentObject);
                    } else {
                        commentObject.put("commentIndex", commentNo);
                        commentObject.put("thumbKey", "null");
                        commentArray.put(commentObject);
                    }
                }
            }

            JSONObject objects = new JSONObject();

            objects.put("response", articleResultResponseVo.getBoardResponseResult());
            objects.put("index", articleResultResponseVo.getIndex());
            objects.put("title", articleResultResponseVo.getTitle());
            objects.put("createAt", articleResultResponseVo.getCreateAt());
            objects.put("views", articleResultResponseVo.getViews());
            objects.put("recommend", articleResultResponseVo.getRecommend());
            objects.put("userRecommend", articleResultResponseVo.getUserRecommend());  // 1이면 해당 유저가 좋아요 눌러놓은 상태, 0이면 안누름.
            objects.put("userNickname", articleResultResponseVo.getUserNickname());
            objects.put("contents", articleResultResponseVo.getContents());
            objects.put("status", articleResultResponseVo.getStatus());
            objects.put("comments", articleResultResponseVo.getComments());
            objects.put("authority", articleResultResponseVo.getAuthority());
            objects.put("isAdmin", userVo.isAdmin());

            objects.put("thumbInfo", commentArray);

            out.print(objects);

        } catch (NumberFormatException ignored) {}
    }

    @RequestMapping(value="write-article", method= RequestMethod.POST)
    public void writeArticle(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(name="categorie", defaultValue = "FREE") String categorie,
                            @RequestParam(name="statusKey", defaultValue = "OKAY") String statusKey,
                            @RequestParam(name="title", defaultValue = "1") String title,
                            @RequestParam(name="content", defaultValue = "") String content
    ) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
            Categories categories;
            switch(categorie) {
                case "FREE":
                    categories = Categories.FREE;
                    break;
                case "NOTI":
                    categories = Categories.NOTI;
                    break;
                case "QNA":
                    categories = Categories.QNA;
                    break;
                default:
                    categories = Categories.FREE;
            }
            UserVo userVo = Variable.getUser(session);
            BoardVo boardVo = new BoardVo(categories, userVo.getNickname(), title, content, statusKey);
            ArticleResultResponseVo articleResultResponseVo =  this.boardService.writeArticle(boardVo, userVo);

            JSONObject object = new JSONObject();
            object.put("response", articleResultResponseVo.getBoardResponseResult());
            object.put("articleNo", articleResultResponseVo.getIndex());
            out.print(object);
    }

    @RequestMapping(value="edit-article", method= RequestMethod.POST)
    public void editArticle(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(name="categorie", defaultValue = "FREE") String categorie,
                             @RequestParam(name="articleNo", defaultValue = "0") String articleNo,
                             @RequestParam(name="statusKey", defaultValue = "OKAY") String statusKey,
                             @RequestParam(name="title", defaultValue = "1") String title,
                             @RequestParam(name="content", defaultValue = "") String content
    ) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
            Categories categories;
            switch(categorie) {
                case "FREE":
                    categories = Categories.FREE;
                    break;
                case "NOTI":
                    categories = Categories.NOTI;
                    break;
                case "QNA":
                    categories = Categories.QNA;
                    break;
                default:
                    categories = Categories.FREE;
            }
            UserVo userVo = Variable.getUser(session);
            try {
                BoardVo boardVo = new BoardVo(Integer.parseInt(articleNo), categories, userVo.getNickname(), title, content, statusKey);

                ArticleResultResponseVo articleResultResponseVo = this.boardService.editArticle(boardVo, userVo);

                JSONObject object = new JSONObject();
                object.put("response", articleResultResponseVo.getBoardResponseResult());
                object.put("articleNo", articleNo);
                out.print(object);
            } catch(NumberFormatException ignored) {
                JSONObject object = new JSONObject();
                object.put("response", BoardResponseResult.RESPONSE_FAILURE);
                out.print(object);
            }
    }

    @RequestMapping(value="recommend-article", method= RequestMethod.POST)
    public void recommendArticle(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(name="articleNo", defaultValue = "0") String articleNo) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        UserVo userVo = Variable.getUser(session);
        try {
            RecommendVo recommendVo = new RecommendVo(Integer.parseInt(articleNo), userVo);
            RecommendResultVo recommendResultVo = this.boardService.recommendArticle(recommendVo);

            JSONObject object = new JSONObject();
            object.put("response", recommendResultVo.getRecommendResult());
            object.put("recommend", recommendResultVo.getRecommend());
            object.put("userRecommend", recommendResultVo.getUserRecommend());
            out.print(object);
        } catch (NumberFormatException ignored) {
            JSONObject object = new JSONObject();
            object.put("response", BoardResponseResult.RESPONSE_FAILURE);
            out.print(object);
        }
    }

    @RequestMapping(value="delete-article", method= RequestMethod.POST)
    public void deleteArticle(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(name="articleNo", defaultValue = "") String articleNo,
                              @RequestParam(name="categorie", defaultValue = "FREE") String categorie
                              ) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            Categories categories;
            switch(categorie) {
                case "FREE":
                    categories = Categories.FREE;
                    break;
                case "NOTI":
                    categories = Categories.NOTI;
                    break;
                case "QNA":
                    categories = Categories.QNA;
                    break;
                default:
                    categories = Categories.FREE;
            }
            UserVo userVo = Variable.getUser(session);

            BoardVo boardVo = new BoardVo(Integer.parseInt(articleNo), categories, userVo.getNickname());
            ArticleResultResponseVo articleResultResponseVo = this.boardService.deleteArticle(boardVo, userVo);
            out.print(articleResultResponseVo.getBoardResponseResult());
        }catch (NumberFormatException ignored) {}
    }


    @RequestMapping(value="comment", method= RequestMethod.POST)
    public void writeComment(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(name="articleNo", defaultValue = "1") String articleNo,
                             @RequestParam(name="text", defaultValue = "") String text) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            UserVo userVo = Variable.getUser(session);
            InsertCommentVo insertCommentVo = new InsertCommentVo(Integer.parseInt(articleNo), userVo.getIndex(), text);
            CommentResultResponseVo commentResultResponseVo = this.boardService.insertComment(insertCommentVo, userVo);
            JSONObject object = new JSONObject();
            object.put("response", commentResultResponseVo.getCommentResponse());
            object.put("comments", commentResultResponseVo.getComments());
            out.print(object);
        } catch(NumberFormatException ignored) {
            JSONObject object = new JSONObject();
            object.put("response", CommentResponse.COMMENT_FAILURE);
            out.print(object);
        }
    }

    @RequestMapping(value="delete-comment", method= RequestMethod.POST)
    public void deleteComment(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(name="articleNo", defaultValue = "1") String articleNo,
                              @RequestParam(name="commentNo", defaultValue = "1") String commentNo
                             ) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            UserVo userVo = Variable.getUser(session);
            InsertCommentVo insertCommentVo = new InsertCommentVo(Integer.parseInt(articleNo), userVo.getIndex(), "");
            CommentResultResponseVo commentResultResponseVo = this.boardService.deleteComment(insertCommentVo, Integer.parseInt(commentNo), userVo);
            JSONObject object = new JSONObject();
            object.put("response", commentResultResponseVo.getCommentResponse());
            object.put("articleNo", articleNo);
            object.put("comments", commentResultResponseVo.getComments());
            out.print(object);
        } catch(NumberFormatException ignored) {
            JSONObject object = new JSONObject();
            object.put("response", CommentResponse.COMMENT_FAILURE);
            out.print(object);
        }
    }

    @RequestMapping(value="reply", method= RequestMethod.POST)
    public void writeReplyComment(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(name="articleNo", defaultValue = "1") String articleNo,
                              @RequestParam(name="replyIndex", defaultValue = "9") String replyIndex,
                             @RequestParam(name="text", defaultValue = "") String text) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            UserVo userVo = Variable.getUser(session);
            InsertCommentVo insertCommentVo = new InsertCommentVo(Integer.parseInt(articleNo), userVo.getIndex(), Integer.parseInt(replyIndex), text);
            CommentResultResponseVo commentResultResponseVo = this.boardService.insertComment(insertCommentVo, userVo);
            JSONObject object = new JSONObject();
            object.put("response", commentResultResponseVo.getCommentResponse());
            object.put("comments", commentResultResponseVo.getComments());
            out.print(object);
        } catch(NumberFormatException ignored) {
            JSONObject object = new JSONObject();
            object.put("response", CommentResponse.COMMENT_FAILURE);
            out.print(object);
        }
    }

    @RequestMapping(value="thumb", method = RequestMethod.POST)
    public void thumbUpdate(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(name="articleNo", defaultValue = "0") String articleNo,
                            @RequestParam(name="commentNo", defaultValue = "0") String commentNo,
                            @RequestParam(name="thumbKey", defaultValue = "") String thumbKey) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            UserVo userVo = Variable.getUser(session);
            ThumbCommentVo thumbCommentVo = new ThumbCommentVo(
                    Integer.parseInt(articleNo),
                    Integer.parseInt(commentNo),
                    userVo,
                    thumbKey
                    );
            ThumbCommentResultVo thumbCommentResultVo = this.boardService.thumbUpDownComment(thumbCommentVo);

            ArticleResultResponseVo articleResultResponseVo = this.boardService.getArticle(Integer.parseInt(articleNo), userVo);
            JSONArray commentArray = new JSONArray();
            if(articleResultResponseVo.getComments() != null) {
                for (int i = 0; i < articleResultResponseVo.getComments().length(); i++) {
                    JSONObject commentObject = new JSONObject();
                    int commentIndex = (int) articleResultResponseVo.getComments().getJSONObject(i).get("index");
                    ThumbCommentVo innerThumbCommentVo = new ThumbCommentVo(
                            Integer.parseInt(articleNo),
                            commentIndex,
                            userVo,
                            null
                    );
                    String innerThumbKey = this.boardService.getThumbComment(innerThumbCommentVo);
                    commentObject.put("commentIndex", commentIndex);
                    if (innerThumbKey != null) {
                        commentObject.put("thumbKey", innerThumbKey);
                    } else {
                        commentObject.put("thumbKey", "null");
                    }
                    commentArray.put(commentObject);
                }
            }

            JSONObject object = new JSONObject();
            object.put("response", thumbCommentResultVo.getThumbCommentResult());
            object.put("thumbUp", thumbCommentResultVo.getThumbUp());
            object.put("thumbDown", thumbCommentResultVo.getThumbDown());

            object.put("thumbInfo", commentArray);

            out.print(object);
        } catch (NumberFormatException ignored) {
            JSONObject object = new JSONObject();
            object.put("response", ThumbCommentResult.RESPONSE_FAILURE);
            out.print(object);
        }
    }

//    이미지 업로드 및 업로드 경로 DB 저장
    @RequestMapping(
            value = "upload_image",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadImage(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(name = "upload") MultipartFile image,
                            @RequestParam(name = "ckCsrfToken", defaultValue = "") String token) throws
            SQLException, IOException {
        String imageData = Converter.imageToString(image);
        int index = this.boardService.uploadImage(imageData);
        JSONObject jsonResponse = new JSONObject();
        if (index > 0) {
            jsonResponse.put("uploaded", true);
            jsonResponse.put("url", String.format("/download_image?id=%d", index));
        } else {
            jsonResponse.put("uploaded", false);
        }
        response.getWriter().print(jsonResponse.toString());
    }
}
