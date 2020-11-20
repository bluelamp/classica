package com.simboorm.simboorm.services;

import com.simboorm.simboorm.daos.BoardDao;
import com.simboorm.simboorm.enums.*;
import com.simboorm.simboorm.vos.*;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class BoardService {
    private final DataSource dataSource;
    private final BoardDao boardDao;

    @Autowired
    public BoardService(DataSource dataSource, BoardDao boardDao) {
        this.dataSource = dataSource;
        this.boardDao = boardDao;
    }

    public int getTotalPage(Categories categories) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.boardDao.selectCountTotalPage(connection, categories);
        }
    }
    public int getFindTotalPage(Categories categories, String search) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.boardDao.selectCountFindTotalPage(connection, categories, search);
        }
    }
    public int getBoardListCount(Categories categories, int currentPage) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.boardDao.selectCountBoards(connection, categories, currentPage);
        }
    }
    public int getFindBoardListCount(Categories categories, String search, int currentPage) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.boardDao.selectCountFindBoards(connection, categories, search,  currentPage);
        }
    }

    public ArrayList<BoardVo> getBoardList(Categories categories, int currentPage) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.boardDao.selectBoardList(connection, categories, currentPage);
        }
    }
    public ArrayList<BoardVo> getFindBoardList(Categories categories, String search, int currentPage) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.boardDao.selectFindBoardList(connection, categories, search, currentPage);
        }
    }

    // 게시글 읽기를 위해서
    public ArticleResultResponseVo getArticle(int articleNo, UserVo userVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            AuthorityVo authorityVo = this.boardDao.selectArticleAuth(connection, articleNo);
            BoardVo boardVo = this.boardDao.selectArticle(connection, articleNo, userVo);
            if (authorityVo.getReadLevel() < userVo.getLevel() &&
                    !boardVo.getUserNickname().equals(userVo.getNickname()) &&
                    !userVo.isAdmin()) {
                return new ArticleResultResponseVo(BoardResponseResult.NO_PERMISSION);
            } else {
                this.boardDao.updateViews(connection, articleNo);
                ArrayList<CommentVo> commentVoList = this.boardDao.selectComments(connection, articleNo);

                if (boardVo != null) {
                    if(boardVo.getStatus().equals("SUSP") && userVo.getNickname().equals(boardVo.getUserNickname())) {
                        return new ArticleResultResponseVo(boardVo.getText(),
                                BoardResponseResult.SUSPEND_ARTICLE,
                                articleNo,
                                boardVo.getTitle(),
                                boardVo.getCreateAt(),
                                boardVo.getUserNickname(),
                                boardVo.getViews(),
                                boardVo.getRecommend(),
                                boardVo.getUserRecommend(),
                                commentVoList,
                                authorityVo,
                                boardVo.getStatus()
                        );
                    } else {
                        if(boardVo.getStatus().equals("SUSP")) {
                            return new ArticleResultResponseVo("정지(비공개)된 게시글 입니다.",
                                    BoardResponseResult.SUSPEND_ARTICLE,
                                    articleNo,
                                    "게시자에 의해 정지(비공개)된 글입니다.",
                                    boardVo.getCreateAt(),
                                    boardVo.getUserNickname(),
                                    boardVo.getViews(),
                                    boardVo.getRecommend(),
                                    boardVo.getUserRecommend(),
                                    null,
                                    authorityVo,
                                    boardVo.getStatus()
                            );
                        } else {
                            return new ArticleResultResponseVo(boardVo.getText(),
                                    BoardResponseResult.RESPONSE_SUCCESS,
                                    articleNo,
                                    boardVo.getTitle(),
                                    boardVo.getCreateAt(),
                                    boardVo.getUserNickname(),
                                    boardVo.getViews(),
                                    boardVo.getRecommend(),
                                    boardVo.getUserRecommend(),
                                    commentVoList,
                                    authorityVo,
                                    boardVo.getStatus()
                            );
                        }
                    }
                } else
                    return new ArticleResultResponseVo(BoardResponseResult.NONE_DATA);
            }
        }
    }

    // 게시글 쓰기
    public ArticleResultResponseVo writeArticle(BoardVo boardVo, UserVo userVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            AuthorityVo authorityVo = this.boardDao.selectLevels(connection, boardVo.getCategories());
            if(authorityVo.getWriteLevel() < userVo.getLevel()) {
                return new ArticleResultResponseVo(BoardResponseResult.NO_PERMISSION);
            } else {
                int articleNo = this.boardDao.insertArticle(connection, boardVo, userVo);
                return this.getArticle(articleNo, userVo);
            }
        }
    }

    public RecommendResultVo recommendArticle(RecommendVo recommendVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            AuthorityVo authorityVo = this.boardDao.selectArticleAuth(connection, recommendVo.getArticleNo());
            if(recommendVo.getUserVo().getIndex() == 9) {
                return new RecommendResultVo(RecommendResult.NO_PERMISSION);
            } else {
                if(this.boardDao.selectUserRecommend(connection, recommendVo) == 0) {
                    this.boardDao.insertRecommend(connection, recommendVo);
                } else {
                    this.boardDao.deleteRecommend(connection, recommendVo);
                }
                return new RecommendResultVo(
                        this.boardDao.selectRecommend(connection, recommendVo),
                        this.boardDao.selectUserRecommend(connection, recommendVo),
                        RecommendResult.RECOMMEND_SUCCESS
                );
            }
        }
    }

    public ThumbCommentResultVo thumbUpDownComment(ThumbCommentVo thumbCommentVo) throws SQLException {
        ThumbCommentResultVo thumbCommentResultVo = null;
        try(Connection connection = this.dataSource.getConnection()) {
            AuthorityVo authorityVo = this.boardDao.selectArticleAuth(connection, thumbCommentVo.getArticleNo());
            if(thumbCommentVo.getUserVo().getIndex() ==  9) {
                return new ThumbCommentResultVo(ThumbCommentResult.NO_PERMISSION);
            } else {
                if(this.boardDao.selectThumbCount(connection, thumbCommentVo) == 0) {
                    this.boardDao.insertThumb(connection, thumbCommentVo);
                } else {
                    if(this.boardDao.selectThumbStatus(connection, thumbCommentVo).equals("DEL") ||
                            !this.boardDao.selectThumbKey(connection, thumbCommentVo).equals(thumbCommentVo.getThumbKey())
                    ) {
                        this.boardDao.updateThumb(connection, thumbCommentVo);
                    } else {

                        this.boardDao.deleteThumb(connection, thumbCommentVo);
                    }
                }
            }
            thumbCommentResultVo = new ThumbCommentResultVo(this.boardDao.selectThumbUp(connection, thumbCommentVo),
                    this.boardDao.selectThumbDown(connection, thumbCommentVo),
                    ThumbCommentResult.RESPONSE_SUCCESS);
        }
        return thumbCommentResultVo;
    }

    public String getThumbComment(ThumbCommentVo thumbCommentVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            if(this.boardDao.selectThumbCount(connection, thumbCommentVo) > 0) {
                if(!this.boardDao.selectThumbStatus(connection, thumbCommentVo).equals("DEL")) {
                    return boardDao.selectThumbKey(connection, thumbCommentVo);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }


    public ArticleResultResponseVo editArticle(BoardVo boardVo, UserVo userVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            AuthorityVo authorityVo = this.boardDao.selectLevels(connection, boardVo.getCategories());
            if(authorityVo.getWriteLevel() < userVo.getLevel()) {
                return new ArticleResultResponseVo(BoardResponseResult.NO_PERMISSION);
            } else {
                if(userVo.getNickname().equals(boardVo.getUserNickname()) || userVo.isAdmin()) {
                    this.boardDao.updateArticle(connection, boardVo);
                    return this.getArticle(boardVo.getIndex(),userVo);
                } else {
                    return new ArticleResultResponseVo(BoardResponseResult.NO_PERMISSION);
                }
            }
        }
    }
    public ArticleResultResponseVo deleteArticle(BoardVo boardVo, UserVo userVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            AuthorityVo authorityVo = this.boardDao.selectLevels(connection, boardVo.getCategories());
            if(authorityVo.getWriteLevel() < userVo.getLevel()) {
                return new ArticleResultResponseVo(BoardResponseResult.NO_PERMISSION);
            } else {
                if(userVo.getNickname().equals(boardVo.getUserNickname()) || userVo.isAdmin()) {
                    this.boardDao.deleteArticle(connection,boardVo.getIndex());
                    return new ArticleResultResponseVo(BoardResponseResult.RESPONSE_SUCCESS);
                } else {
                    return new ArticleResultResponseVo(BoardResponseResult.NO_PERMISSION);
                }
            }
        }
    }

    public AuthorityVo getAuthority(Categories categories) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.boardDao.selectLevels(connection, categories);
        }
    }

    //댓글 달기
    public CommentResultResponseVo insertComment(InsertCommentVo insertCommentVo, UserVo userVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            if(insertCommentVo.getReplyIndex() == -1) {
                this.boardDao.insertComment(connection, insertCommentVo);
            } else {
                if(this.boardDao.isnullReplyIndex(connection, insertCommentVo.getReplyIndex())) {
                    this.boardDao.insertComment(connection, insertCommentVo);
                } else {
                    this.boardDao.insertReplyComment(connection, insertCommentVo);
                }
            }
            ArrayList<CommentVo> commentVoList = this.boardDao.selectComments(connection, insertCommentVo.getArticleNo());
            return new CommentResultResponseVo(CommentResponse.COMMENT_SUCCESS, commentVoList);
        }
    }

    //댓글 삭제
    public CommentResultResponseVo deleteComment(InsertCommentVo insertCommentVo, int commentNo, UserVo userVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            if(this.boardDao.selectUserIndexbyCommentIndex(connection, commentNo) == userVo.getIndex() || userVo.isAdmin()) {
                this.boardDao.updateDeleteComment(connection, commentNo);
                ArrayList<CommentVo> commentVoList = this.boardDao.selectComments(connection, insertCommentVo.getArticleNo());
                return new CommentResultResponseVo(CommentResponse.COMMENT_SUCCESS
                        , commentVoList);
            } else {
                return new CommentResultResponseVo(CommentResponse.NOT_PERMISSION);
            }
        }
    }


    public int uploadImage(String imageData) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            this.boardDao.insertImage(connection, imageData);
            return this.boardDao.LastInsertId(connection);
        }
    }

    public byte[] downloadImage(int id) throws SQLException, IOException {
        try(Connection connection = this.dataSource.getConnection()) {
            String imageData = this.boardDao.selectImage(connection, id).split(",")[1];
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(imageData);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
