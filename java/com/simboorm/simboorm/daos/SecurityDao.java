package com.simboorm.simboorm.daos;

import com.simboorm.simboorm.vos.AttemptLoginVo;
import com.simboorm.simboorm.vos.BlockipVo;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class SecurityDao {
    public void insertAttemptips(Connection connection, AttemptLoginVo attemptLoginVo) throws SQLException {
        String query = "" +
                "INSERT INTO `security`.`attempts_login`(\n" +
                "\t`attempt_ip`,\n" +
                "    `attempt_email`,\n" +
                "    `attempt_password`,\n" +
                "    `attempt_login_result`\n" +
                ") VALUE (\n" +
                "?,\n" +
                "?,\n" +
                "?,\n" +
                "?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, attemptLoginVo.getIp());
            preparedStatement.setString(2, attemptLoginVo.getEmail());
            preparedStatement.setString(3, attemptLoginVo.getPassword());
            preparedStatement.setString(4, attemptLoginVo.getLoginResult());
            preparedStatement.execute();
        }
    }

    // 1분 안에 해당 ip로 로그인 시도 횟수 가져오기
    public int selectAttemptCountbyip(Connection connection, String ip)  throws SQLException {
        String query = "" +
                "SELECT \n" +
                "COUNT(`attempt_index`) AS `count`\n" +
                "FROM `security`.`attempts_login`\n" +
                "WHERE `attempts_login`.`attempt_ip`=? \n" +
                "AND `attempt_created_at` > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MINUTE) \n" +
                "AND `attempt_login_result` = 'LOGIN_FAILURE'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, ip);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }

    //해당 email로 로그인 시도 횟수 가져오기
    public int selectAttemptCountbyemail(Connection connection, String email)  throws SQLException {
        String query = "" +
                "SELECT \n" +
                "COUNT(`attempt_index`) AS `count`\n" +
                "FROM `security`.`attempts_login`\n" +
                "WHERE `attempt_email` = ?\n" +
                "AND `attempt_login_result` = 'LOGIN_FAILURE'\n" +
                "AND (SELECT `attempt_created_at` \n" +
                "\t\tFROM `security`.`attempts_login` \n" +
                "\t\tWHERE `attempt_email` = ? \n" +
                "        AND `attempt_login_result` = 'LOGIN_SUCCESS'\n" +
                "        ORDER BY `attempt_index` DESC LIMIT 1) < `attempt_created_at`";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, email);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    if(resultSet.getInt("count") > 0) {
                        return resultSet.getInt("count");
                    } else {
                        return this.selectSubAttemptCountbyemail(connection, email);
                    }
                }
            }
        }
        return 0;
    }

    public int selectSubAttemptCountbyemail(Connection connection, String email)  throws SQLException {
        String query = "" +
                "SELECT \n" +
                "COUNT(`attempt_index`) AS `count`\n" +
                "FROM `security`.`attempts_login`\n" +
                "WHERE `attempt_email` = ?\n" +
                "AND `attempt_login_result` = 'LOGIN_FAILURE'";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }

    // 해당 ip를 데이터베이스에 올려서 블럭 처리함.
    public void insertBlockip(Connection connection, String ip, int level) throws SQLException {
        String query = "" +
                "INSERT INTO `security`.`block_ips` (\n" +
                "`block_ip`,\n" +
                "`block_expires_at`\n" +
                ") VALUE(\n" +
                "?,\n" +
                "DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? MINUTE)\n" +
                ")";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, ip);
            preparedStatement.setInt(2, level*level);  //처음에는 1분, 4분, 9분, 16분 순으로 늘어남.
            preparedStatement.execute();
        }
    }

    // 해당 ip를 데이터베이스에 올려서 블럭 처리함.
    public void insertBannedEmail(Connection connection, String email) throws SQLException {
        String query = "" +
                "INSERT INTO `security`.`banned_emails` (\n" +
                "`ban_email`\n" +
                ") VALUE (\n" +
                "?\n" +
                ")";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.execute();
        }
    }

    // ip에 대한 차단 정보 가져오기
    public int selectBlockbyip(Connection connection, String ip) throws SQLException {
        int count = 0;
        String query = "" +
                "SELECT COUNT(`block_index`) AS `count`\n" +
                "FROM `security`.`block_ips`\n" +
                "WHERE  `block_ip`= ? AND `block_expires_at` > CURRENT_TIMESTAMP";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, ip);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    count = resultSet.getInt("count");
                }
            }
        }
        return count;
    }

    // email에 대한 차단 정보 가져오기
    public Date selectBannedbyemail(Connection connection, String email) throws SQLException {
        Date banCreatedAt = null;
        String query = "" +
                "SELECT `ban_created_at` AS `banCreatedAt` FROM `security`.`banned_emails` WHERE `ban_email` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    banCreatedAt = resultSet.getDate("banCreatedAt");
                }
            }
        }
        return banCreatedAt;
    }

    //한달 이내에 제일 최근 블록의 레벨 정보 가져오기 아이피로
    public int selectBlocklevelbyip(Connection connection, String ip) throws SQLException {
        int level = 0;
        String query = "" +
                "SELECT \n" +
                "COUNT(`block_index`) AS `level`\n" +
                "FROM `security`.`block_ips` AS `block`\n" +
                "WHERE `block_ip` = ?\n" +
                "AND (SELECT `attempt_created_at` FROM `security`.`attempts_login` WHERE `attempt_ip` = ? AND `attempt_login_result` = 'LOGIN_SUCCESS' ORDER BY `attempt_index` DESC LIMIT 1) < `block_expires_at`";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, ip);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    level = resultSet.getInt("level");
                }
            }
        }
        return level;
    }
}
