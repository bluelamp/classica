package com.simboorm.simboorm.daos;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import com.simboorm.simboorm.enums.Categories;
import com.simboorm.simboorm.enums.CommentResponse;
import com.simboorm.simboorm.enums.CommentStatus;
import com.simboorm.simboorm.enums.RecommendResult;
import com.simboorm.simboorm.vos.*;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class BoardDao {
    private final double BOARDS_PER_PAGE = 10.0;
    public int selectCountTotalPage(Connection connection, Categories categories) throws SQLException {
        String query = "" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`boards` WHERE `categorie_key` = ? AND `board_status_key`!='DEL'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categories.name());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return (int)Math.ceil(resultSet.getDouble("count")/BOARDS_PER_PAGE);
                }
            }
        }
        return 1;
    }
    public int selectCountFindTotalPage(Connection connection, Categories categories, String search) throws SQLException {
        String query = "" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`boards` AS `board`\n"+
                "INNER JOIN `classica`.`users` AS `user` ON `user`.`user_index` = `board`.`user_index`\n" +
                "WHERE `categorie_key` = ? AND `board_status_key`!='DEL' AND \n"+
                "(`board`.`board_title` LIKE ? OR `user`.`nickname` LIKE ? OR `board`.`board_content` LIKE ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categories.name());
            preparedStatement.setString(2, search);
            preparedStatement.setString(3, search);
            preparedStatement.setString(4, search);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return (int)Math.ceil(resultSet.getDouble("count")/BOARDS_PER_PAGE);
                }
            }
        }
        return 1;
    }

    public int selectCountBoards(Connection connection, Categories categories, int currentPage) throws SQLException {
        String query = "" +
                "SELECT \n" +
                "COUNT(*) AS `count`\n" +
                "FROM `classica`.`boards` AS `board`\n" +
                "WHERE (`board_status_key` ='OKAY' OR `board_status_key` ='SUSP' OR `board_status_key` = 'EDIT') AND `categorie_key`=?\n" +
                "ORDER BY `board_index` DESC LIMIT ?, ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categories.name());
            preparedStatement.setInt(2, (int)(currentPage-1)/(int)BOARDS_PER_PAGE);
            preparedStatement.setInt(3, (int)BOARDS_PER_PAGE);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count");
            }
        }
    }
    public int selectCountFindBoards(Connection connection, Categories categories, String search, int currentPage) throws SQLException {
        String query = "" +
                "SELECT \n" +
                "COUNT(*) AS `count`\n" +
                "FROM `classica`.`boards` AS `board`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `user`.`user_index` = `board`.`user_index`\n" +
                "WHERE (`board_status_key` ='OKAY' OR `board_status_key` ='SUSP' OR `board_status_key` = 'EDIT') AND `categorie_key`=? AND \n" +
                "(`board`.`board_title` LIKE ? OR `user`.`nickname` LIKE ? OR `board`.`board_content` LIKE ?)\n" +
                "ORDER BY `board_index` DESC LIMIT ?, ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categories.name());
            preparedStatement.setString(2, search);
            preparedStatement.setString(3, search);
            preparedStatement.setString(4, search);
            preparedStatement.setInt(5, (int)(currentPage-1)/(int)BOARDS_PER_PAGE);
            preparedStatement.setInt(6, (int)BOARDS_PER_PAGE);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count");
            }
        }
    }

    public ArrayList<BoardVo> selectBoardList(Connection connection, Categories categories, int currentPage) throws SQLException {
        ArrayList<BoardVo> boardVoArrayList = new ArrayList<>();
        String query = "" +
                "(SELECT \n" +
                "`board_index` AS `boardIndex`, \n" +
                "`board_views` AS `boardViews`,\n" +
                "(SELECT COUNT(*) FROM `classica`.`comments` AS `comment` WHERE `board`.`board_index`= `comment`.`board_index`) AS `boardComments`,\n" +
                "`board_created_at` AS `boardCreatedAt`,\n" +
                "`user`.`nickname` AS `userNickname`,  \n" +
                "(SELECT COUNT(*) FROM `classica`.`board_recommends` AS `recommend` WHERE `recommend`.`board_index` = `board`.`board_index`) AS `boardRecommend`,\n" +
                "`board_title` AS `boardTitle`,\n" +
                "`board_status_key` AS `boardStatus`\n" +
                "FROM `classica`.`boards` AS `board`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `user`.`user_index` = `board`.`user_index`\n" +
                "WHERE `board_status_key` = 'FIX' AND `categorie_key`=?\n" +     //1
                "ORDER BY `board_index` ASC LIMIT 5) UNION\n" +
                "(SELECT \n" +
                "`board_index` AS `boardIndex`, \n" +
                "`board_views` AS `boardViews`,\n" +
                "(SELECT COUNT(*) FROM `classica`.`comments` AS `comment` WHERE `board`.`board_index`= `comment`.`board_index`) AS `boardComments`,\n" +
                "`board_created_at` AS `boardCreatedAt`,\n" +
                "`user`.`nickname` AS `userNickname`,  \n" +
                "(SELECT COUNT(*) FROM `classica`.`board_recommends` AS `recommend` WHERE `recommend`.`board_index` = `board`.`board_index`) AS `boardRecommend`,\n" +
                "`board_title` AS `boardTitle`,\n" +
                "`board_status_key` AS `boardStatus`\n" +
                "FROM `classica`.`boards` AS `board`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `user`.`user_index` = `board`.`user_index`\n" +
                "WHERE (`board_status_key` ='OKAY' OR `board_status_key` ='SUSP' OR `board_status_key` = 'EDIT') AND `categorie_key`=?\n" +   //2
                "ORDER BY `board_index` DESC LIMIT ?, ?)";       //3, 4
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categories.name());
            preparedStatement.setString(2, categories.name());
            preparedStatement.setInt(3, (int)(currentPage-1)*(int)BOARDS_PER_PAGE);
            preparedStatement.setInt(4, (int)BOARDS_PER_PAGE);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    BoardVo boardVo = new BoardVo(
                            resultSet.getInt("boardIndex"),
                            categories,
                            resultSet.getInt("boardViews"),
                            resultSet.getInt("boardComments"),
                            resultSet.getDate("boardCreatedAt"),
                            resultSet.getString("userNickname"),
                            resultSet.getInt("boardRecommend"),
                            resultSet.getString("boardTitle"),
                            resultSet.getString("boardStatus")
                    );
                    if(boardVo != null) {
                        boardVoArrayList.add(boardVo);
                    }
                }
            }
        }
        return boardVoArrayList;
    }

    public ArrayList<BoardVo> selectFindBoardList(Connection connection, Categories categories, String search, int currentPage) throws SQLException {
        ArrayList<BoardVo> boardVoArrayList = new ArrayList<>();
        String query = "" +
                "(SELECT \n" +
                "`board_index` AS `boardIndex`, \n" +
                "`board_views` AS `boardViews`,\n" +
                "(SELECT COUNT(*) FROM `classica`.`comments` AS `comment` WHERE `board`.`board_index`= `comment`.`board_index`) AS `boardComments`,\n" +
                "`board_created_at` AS `boardCreatedAt`,\n" +
                "`user`.`nickname` AS `userNickname`,  \n" +
                "(SELECT COUNT(*) FROM `classica`.`board_recommends` AS `recommend` WHERE `recommend`.`board_index` = `board`.`board_index`) AS `boardRecommend`,\n" +
                "`board_title` AS `boardTitle`,\n" +
                "`board_status_key` AS `boardStatus`\n" +
                "FROM `classica`.`boards` AS `board`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `user`.`user_index` = `board`.`user_index`\n" +
                "WHERE `board_status_key` = 'FIX' AND `categorie_key`=?\n" +     //1
                "ORDER BY `board_index` ASC LIMIT 5) UNION\n" +
                "(SELECT \n" +
                "`board_index` AS `boardIndex`, \n" +
                "`board_views` AS `boardViews`,\n" +
                "(SELECT COUNT(*) FROM `classica`.`comments` AS `comment` WHERE `board`.`board_index`= `comment`.`board_index`) AS `boardComments`,\n" +
                "`board_created_at` AS `boardCreatedAt`,\n" +
                "`user`.`nickname` AS `userNickname`,  \n" +
                "(SELECT COUNT(*) FROM `classica`.`board_recommends` AS `recommend` WHERE `recommend`.`board_index` = `board`.`board_index`) AS `boardRecommend`,\n" +
                "`board_title` AS `boardTitle`,\n" +
                "`board_status_key` AS `boardStatus`\n" +
                "FROM `classica`.`boards` AS `board`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `user`.`user_index` = `board`.`user_index`\n" +
                "WHERE (`board_status_key` ='OKAY' OR `board_status_key` ='SUSP' OR `board_status_key` = 'EDIT') AND `categorie_key`=? AND \n" +
                "(`board`.`board_title` LIKE ? OR `user`.`nickname` LIKE ? OR `board`.`board_content` LIKE ?)\n" +   //2
                "ORDER BY `board_index` DESC LIMIT ?, ?)";       //3, 4
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categories.name());
            preparedStatement.setString(2, categories.name());
            preparedStatement.setString(3, search);
            preparedStatement.setString(4, search);
            preparedStatement.setString(5, search);
            preparedStatement.setInt(6, (int)(currentPage-1)*(int)BOARDS_PER_PAGE);
            preparedStatement.setInt(7, (int)BOARDS_PER_PAGE);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    BoardVo boardVo = new BoardVo(
                            resultSet.getInt("boardIndex"),
                            categories,
                            resultSet.getInt("boardViews"),
                            resultSet.getInt("boardComments"),
                            resultSet.getDate("boardCreatedAt"),
                            resultSet.getString("userNickname"),
                            resultSet.getInt("boardRecommend"),
                            resultSet.getString("boardTitle"),
                            resultSet.getString("boardStatus")
                    );
                    if(boardVo != null) {
                        boardVoArrayList.add(boardVo);
                    }
                }
            }
        }
        return boardVoArrayList;
    }

    public AuthorityVo selectArticleAuth(Connection connection, int ArticleNo) throws SQLException {
        AuthorityVo authorityVo = null;
        String query = "" +
                "SELECT `board_read_level` AS `readLevel`, `board_write_level` AS `writeLevel` FROM `classica`.`boards` WHERE `board_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, ArticleNo);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    authorityVo = new AuthorityVo(
                            resultSet.getInt("readLevel"),
                            resultSet.getInt("writeLevel")
                    );
                }
            }
        }
        return authorityVo;
    }

    public BoardVo selectArticle(Connection connection, int ArticleNo, UserVo userVo) throws SQLException {
        BoardVo boardVo =null;
        String query = "" +
                "SELECT \n" +
                "`board_index` AS `boardIndex`, \n" +
                "`categorie_key` AS `categorieKey`, \n" +
                "`board_views` AS `boardViews`,\n" +
                "(SELECT COUNT(*) FROM `classica`.`comments` AS `comment` WHERE `board`.`board_index`= `comment`.`board_index`) AS `boardComments`,\n" +
                "`board_created_at` AS `boardCreatedAt`,\n" +
                "`user`.`nickname` AS `userNickname`,  \n" +
                "(SELECT COUNT(*) FROM `classica`.`board_recommends` AS `recommend` WHERE `user_index` = ? AND `board_index` = ?) AS `userRecommend`," +
                "(SELECT COUNT(*) FROM `classica`.`board_recommends` AS `recommend` WHERE `recommend`.`board_index` = ?) AS `boardRecommend`,\n" +
                "`board_title` AS `boardTitle`,\n" +
                "`board_content` AS `boardContent`,\n" +
                "`board_status_key` AS `boardStatus`\n" +
                "FROM `classica`.`boards` AS `board`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `user`.`user_index` = `board`.`user_index`\n" +
                "WHERE (`board_status_key` != 'DEL') AND `board_index` = ? LIMIT 1";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userVo.getIndex());
            preparedStatement.setInt(2, ArticleNo);
            preparedStatement.setInt(3, ArticleNo);
            preparedStatement.setInt(4, ArticleNo);
            try(ResultSet resultset = preparedStatement.executeQuery()) {
                while(resultset.next()) {
                    Categories categories;
                    switch (resultset.getString("categorieKey")) {
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

                    boardVo = new BoardVo(
                            resultset.getInt("boardIndex"),
                            categories,
                            resultset.getInt("boardViews"),
                            resultset.getInt("boardComments"),
                            resultset.getDate("boardCreatedAt"),
                            resultset.getString("userNickname"),
                            resultset.getInt("boardRecommend"),
                            resultset.getInt("userRecommend"),
                            resultset.getString("boardTitle"),
                            resultset.getString("boardContent"),
                            resultset.getString("boardStatus")
                    );
                }
            }
        }
        return boardVo;
    }

    public int selectRecommend(Connection connection, RecommendVo recommendVo) throws SQLException {
        String query = "" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`board_recommends` WHERE `board_index`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, recommendVo.getArticleNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count");
            }
        }
    }

    public int selectUserRecommend(Connection connection, RecommendVo recommendVo) throws SQLException {
        String query = "" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`board_recommends` WHERE `user_index`= ? AND `board_index`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, recommendVo.getUserVo().getIndex());
            preparedStatement.setInt(2, recommendVo.getArticleNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count");
            }
        }
    }

    public void insertRecommend(Connection connection, RecommendVo recommendVo) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`board_recommends` (\n" +
                "\t`user_index`,\n" +
                "    `board_index`\n" +
                ") VALUE\n" +
                "(?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, recommendVo.getUserVo().getIndex());
            preparedStatement.setInt(2, recommendVo.getArticleNo());
            preparedStatement.execute();
        }
    }
    public void deleteRecommend(Connection connection, RecommendVo recommendVo) throws SQLException {
        String query = "" +
                "DELETE FROM `classica`.`board_recommends` WHERE `user_index` = ? AND `board_index`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, recommendVo.getUserVo().getIndex());
            preparedStatement.setInt(2, recommendVo.getArticleNo());
            preparedStatement.execute();
        }
    }

    public int LastInsertId(Connection connection) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("" +
                "SELECT LAST_INSERT_ID() AS `lastId`")) {
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                resultSet.next();
                return resultSet.getInt("lastId");
            }
        }
    }

    public int insertArticle(Connection connection, BoardVo boardVo, UserVo userVo) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`boards`\n" +
                "(\n" +
                "    `categorie_key` ,\n" +
                "    `user_index`\t,\n" +
                "    `board_status_key`,\n" +
                "    `board_title`\t,\n" +
                "    `board_content`\t,\n" +
                "    `board_read_level`,\n" +
                "    `board_write_level`\n" +
                ") VALUE (\n" +
                "\t?,\n" +
                "\t?,\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    (SELECT `categorie_read_level` FROM `classica`.`categories` WHERE  `categorie_key` = ?),\n" +
                "    (SELECT `categorie_write_level` FROM `classica`.`categories` WHERE  `categorie_key` = ?)\n" +
                ")";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, boardVo.getCategories().name());
            preparedStatement.setInt(2, userVo.getIndex());
            preparedStatement.setString(3, boardVo.getStatus());
            preparedStatement.setString(4, boardVo.getTitle());
            preparedStatement.setString(5, boardVo.getText());
            preparedStatement.setString(6, boardVo.getCategories().name());
            preparedStatement.setString(7, boardVo.getCategories().name());
            preparedStatement.execute();

            return this.LastInsertId(connection);
        }
    }

    public void updateViews(Connection connection, int ArticleNo) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`boards` SET `board_views` = `board_views` + 1 WHERE `board_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, ArticleNo);
            preparedStatement.execute();
        }
    }

    public void updateArticle(Connection connection, BoardVo boardVo) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`boards` \n" +
                "SET `board_title` = ?, `board_content` = ?, `board_status_key`=?, `board_status_changed_at` = CURRENT_TIMESTAMP WHERE `board_index`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, boardVo.getTitle());
            preparedStatement.setString(2, boardVo.getText());
            preparedStatement.setString(3, boardVo.getStatus().equals("OKAY")? "EDIT" : boardVo.getStatus());
            preparedStatement.setInt(4, boardVo.getIndex());
            preparedStatement.execute();
        }
    }

    public void deleteArticle(Connection connection, int ArticleNo) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`boards` \n" +
                "SET `board_status_key`='DEL', `board_status_changed_at` = CURRENT_TIMESTAMP WHERE `board_index`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, ArticleNo);
            preparedStatement.execute();
        }
    }

    public AuthorityVo selectLevels(Connection connection, Categories categories) throws SQLException {
        AuthorityVo authorityVo = null;
        String query = "" +
                "SELECT `categorie_write_level` AS `writeLevel`, `categorie_read_level` AS `readLevel` FROM `classica`.`categories` WHERE `categorie_key` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categories.name());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    authorityVo = new AuthorityVo(
                            resultSet.getInt("readLevel"),
                            resultSet.getInt("writeLevel")
                    );
                }
            }
        }
        return authorityVo;
    }

    public ArrayList<CommentVo> selectComments(Connection connection, int ArticleNo) throws SQLException {
        ArrayList<CommentVo> commentVoList = new ArrayList<>();
        String query = "" +
                "SELECT \n" +
                "`comment_index` AS `commentIndex`, \n" +
                "`user`.`nickname` AS `userNickname`, \n" +
                "`comment_status_key` AS `commentStatus`, \n" +
                "`comment_status_changed_at` AS `commentStatusChangedAt`,\n" +
                "`comment_created_at` AS `commentCreatedAt`,\n" +
                "(SELECT COUNT(*) AS `count` FROM `classica`.`comment_thumb` WHERE `comment_index`=`comment`.`comment_index` AND `thumb_key`='UP' AND `thumb_status`='OKAY') AS `commentThumbUp`,\n" +
                "(SELECT COUNT(*) AS `count` FROM `classica`.`comment_thumb` WHERE `comment_index`=`comment`.`comment_index` AND `thumb_key`='DOWN' AND `thumb_status`='OKAY') AS `commentThumbDown`,\n" +
                "`comment_reply_index` AS `commentReplyIndex`,\n" +
                "`comment_content` AS `commentContent`\n" +
                "FROM `classica`.`comments` AS `comment`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `comment`.`user_index` = `user`.`user_index`\n" +
                "WHERE `board_index` = ?\n" +
                "ORDER BY `comment_reply_index` ASC";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, ArticleNo);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    CommentStatus commentStatus;
                    switch (resultSet.getString("commentStatus")) {
                        case "OKAY":
                            commentStatus = CommentStatus.OKAY;
                            break;
                        case "SUSP":
                            commentStatus = CommentStatus.SUSP;
                            break;
                        case "DEL":
                            commentStatus = CommentStatus.DEL;
                            break;
                        case "CNGD":
                            commentStatus = CommentStatus.CNGD;
                            break;
                        default:
                            commentStatus = CommentStatus.OKAY;
                            break;
                    }
                    CommentVo commentVo = new CommentVo(
                            resultSet.getInt("commentIndex"),
                            resultSet.getString("userNickname"),
                            resultSet.getString("commentCreatedAt"),
                            commentStatus,
                            resultSet.getString("commentStatusChangedAt"),
                            resultSet.getInt("commentThumbUp"),
                            resultSet.getInt("commentThumbDown"),
                            resultSet.getInt("commentReplyIndex"),
                            resultSet.getString("commentContent")
                    );
                    commentVoList.add(commentVo);
                }
            }
        }
        return commentVoList;
    }

    public int selectUserIndexbyCommentIndex(Connection connection, int commentNo) throws SQLException {
        ArrayList<CommentVo> commentVoList = new ArrayList<>();
        String query = "" +
                "SELECT \n" +
                "\t`user`.`user_index` AS `userIndex`\n" +
                "FROM `classica`.`comments` AS `comment`\n" +
                "INNER JOIN `classica`.`users` AS `user` ON `comment`.`user_index` = `user`.`user_index`\n" +
                "WHERE `comment_index` = ?\n" +
                "ORDER BY `comment_reply_index` ASC, `comment_index` ASC LIMIT 1";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, commentNo);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("userIndex");
                }
            }
        }
        return 0;
    }

    public void insertComment(Connection connection, InsertCommentVo insertCommentVo) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`comments` \n" +
                "(\n" +
                "    `board_index` \t\t\t\t,\n" +
                "    `user_index`\t\t\t\t,\n" +
                "    `comment_content`\t\t\t\n" +
                ")\n" +
                "VALUE\n" +
                "(\n" +
                "?,\n" +
                "?,\n" +
                "?\n" +
                ")";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, insertCommentVo.getArticleNo());
            preparedStatement.setInt(2, insertCommentVo.getUserIndex());
            preparedStatement.setString(3, insertCommentVo.getText());
            preparedStatement.execute();
        }
    }

    public void updateDeleteComment(Connection connection, int commentNo) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`comments` SET `comment_status_key` = 'DEL', `comment_status_changed_at` = CURRENT_TIMESTAMP \n" +
                "WHERE `comment_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, commentNo);
            preparedStatement.execute();
        }
    }

    public boolean isnullReplyIndex(Connection connection, int ReplyIndex) throws SQLException {
        String query = "" +
                "SELECT COUNT(`comment_index`) AS `count` FROM `classica`.`comments` WHERE `comment_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, ReplyIndex);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("count") == 0 ? true: false;
            }

        }
    }

    public void insertReplyComment(Connection connection, InsertCommentVo insertCommentVo) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`comments` (\n" +
                "\t`board_index`\t\t,\n" +
                "\t`user_index`\t\t,\n" +
                "\t`comment_reply_index`,\n" +
                "\t`comment_content`\n" +
                ") \n" +
                "SELECT \n" +
                "?, \n" +
                "?, \n" +
                "?,\n" +
                "? \n" +
                "FROM `classica`.`comments` WHERE EXISTS(\n" +
                "\tSELECT `comment_index` FROM `classica`.`comments` WHERE `comment_index` = ?\n" +
                ") LIMIT 1";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, insertCommentVo.getArticleNo());
            preparedStatement.setInt(2, insertCommentVo.getUserIndex());
            preparedStatement.setInt(3, insertCommentVo.getReplyIndex());
            preparedStatement.setString(4, insertCommentVo.getText());
            preparedStatement.setInt(5, insertCommentVo.getReplyIndex());
            preparedStatement.execute();
        }
    }

    public int selectThumbCount(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`comment_thumb` \n" +
                "WHERE `user_index` = ? AND `comment_index`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(2, thumbCommentVo.getCommentNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }

    public void insertThumb(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "INSERT INTO `classica`.`comment_thumb` (\n" +
                "    `user_index`\t\t,\n" +
                "    `comment_index`\t\t,\n" +
                "    `thumb_key`\t\t\t\n" +
                ") VALUE \n" +
                "(?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(2, thumbCommentVo.getCommentNo());
            preparedStatement.setString(3, thumbCommentVo.getThumbKey());
            preparedStatement.execute();
        }
    }

    public int selectUserThumbUp(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`comment_thumb` \n" +
                "WHERE `user_index`= ? AND `comment_index`= ? AND `thumb_key`='UP'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(2, thumbCommentVo.getCommentNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }
    public int selectThumbUp(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`comment_thumb` \n" +
                "WHERE `comment_index`= ? AND `thumb_key`='UP' AND `thumb_status`='OKAY'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getCommentNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }

    public int selectUserThumbDown(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`comment_thumb` \n" +
                "WHERE `user_index`= ? AND `comment_index`= ? AND `thumb_key`='DOWN'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(2, thumbCommentVo.getCommentNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }
    public int selectThumbDown(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "SELECT COUNT(*) AS `count` FROM `classica`.`comment_thumb` \n" +
                "WHERE `comment_index`= ? AND `thumb_key`='DOWN' AND `thumb_status`='OKAY'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getCommentNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }

    public void deleteThumb(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "UPDATE `classica`.`comment_thumb` SET `thumb_key`=?, `thumb_status`='DEL' WHERE `user_index` = ? AND `comment_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, thumbCommentVo.getThumbKey());
            preparedStatement.setInt(2, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(3, thumbCommentVo.getCommentNo());
            preparedStatement.execute();
        }
    }

    public void updateThumb(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query ="" +
                "UPDATE `classica`.`comment_thumb` SET `thumb_key`=?, `thumb_status`='OKAY'\n" +
                "WHERE `user_index` = ? AND `comment_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, thumbCommentVo.getThumbKey());
            preparedStatement.setInt(2, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(3, thumbCommentVo.getCommentNo());
            preparedStatement.execute();
        }
    }
    public String selectThumbStatus(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query = "" +
                "SELECT `thumb_status` AS `thumbStatus` FROM `classica`.`comment_thumb`\n" +
                "WHERE `user_index` = ? AND `comment_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(2, thumbCommentVo.getCommentNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getString("thumbStatus");
                }
            }
        }
        return null;
    }

    public String selectThumbKey(Connection connection, ThumbCommentVo thumbCommentVo) throws SQLException {
        String query = "" +
                "SELECT `thumb_key` AS `thumbKey` FROM `classica`.`comment_thumb`\n" +
                "WHERE `user_index` = ? AND `comment_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, thumbCommentVo.getUserVo().getIndex());
            preparedStatement.setInt(2, thumbCommentVo.getCommentNo());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getString("thumbKey");
                }
            }
        }
        return null;
    }

    public void insertImage(Connection connection, String imageData) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`images` (\n" +
                "`thumbtail_image_data`,\n" +
                "`image_data`\n" +
                ") VALUE \n" +
                "('', ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, imageData);
            preparedStatement.execute();
        }
    }
    public String selectImage(Connection connection, int id) throws SQLException {
        String imageData = null;
        String query = "SELECT `image_data` AS `imageData` FROM `classica`.`images` WHERE `image_index` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    imageData = resultSet.getString("imageData");
                }
            }
        }
        return imageData;
    }
}
