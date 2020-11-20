package com.simboorm.simboorm.daos;

import com.simboorm.simboorm.vos.*;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserDao {
    public UserVo selectUser(Connection connection, LoginVo loginVo) throws SQLException {
        UserVo userVo = null;
        String query = "" +
                "SELECT \n" +
                "\t`user`.`user_index` \tAS `userIndex`,\n" +
                "\t`user`.`email` \t\tAS `Email`,\n" +
                "    `user`.`name`\t\t\tAS `Name`,\n" +
                "    `user`.`nickname` \t\tAS `Nickname`,\n" +
                "    `user`.`address` \t\tAS `Address`,\n" +
                "    `user`.`contact`\t\tAS `Contact`,\n" +
                "    `user`.`status_key`\tAS `Status`,\n" +
                "    `user`.`user_birth`\tAS `Birth`,\n" +
                "    `user`.`user_level`\tAS `Level`,\n" +
                "    `user`.`is_admin`\t\tAS `Admin`,\n" +
                "    `user`.`create_at`\t\tAS `CreatedAt`,\n" +
                "    `user`.`signed_at`\t\tAS `SignedAt`,\n" +
                "    `user`.`status_changed_at` AS `StatusChangedAt`,\n" +
                "    `user`.`password_modified_at` AS `PasswordModifiedAt` \n" +
                " FROM `classica`.`users` AS `user` \n" +
                " WHERE `email` = ? AND `password` = ?\n" +
                " LIMIT 1";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, loginVo.getEmail());
            preparedStatement.setString(2, loginVo.getHashedPassword());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    userVo = new UserVo(
                            resultSet.getInt("userIndex"),
                            resultSet.getString("Email"),
                            resultSet.getString("Name"),
                            resultSet.getString("Nickname"),
                            resultSet.getString("Address"),
                            resultSet.getString("Contact"),
                            resultSet.getString("Status"),
                            resultSet.getDate("Birth"),
                            resultSet.getInt("Level"),
                            resultSet.getBoolean("Admin"),
                            resultSet.getDate("CreatedAt"),
                            resultSet.getDate("SignedAt"),
                            resultSet.getDate("StatusChangedAt"),
                            resultSet.getDate("PasswordModifiedAt")
                    );
                }
            }
        }
        return userVo;
    }

    public String selectUser(Connection connection, FindEmailVo findEmailVo) throws SQLException {
        String email = null;
        String query = "" +
                "SELECT \n" +
                "\t`user`.`user_index` \tAS `userIndex`,\n" +
                "\t`user`.`email` \t\tAS `Email`\n" +
                " FROM `classica`.`users` AS `user` \n" +
                " WHERE `Name` = ? AND `Contact` = ?\n" +
                " LIMIT 1";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, findEmailVo.getName());
            preparedStatement.setString(2, findEmailVo.getContact());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    email = resultSet.getString("Email");
                }
            }
        }
        return email;
    }
    public UserVo selectUser(Connection connection, ResetCheckVo resetCheckVo) throws SQLException {
        UserVo userVo = null;
        String query = "" +
                "SELECT \n" +
                "\t`user`.`user_index` \tAS `userIndex`,\n" +
                "\t`user`.`email` \t\tAS `Email`,\n" +
                "    `user`.`name`\t\t\tAS `Name`,\n" +
                "    `user`.`nickname` \t\tAS `Nickname`,\n" +
                "    `user`.`address` \t\tAS `Address`,\n" +
                "    `user`.`contact`\t\tAS `Contact`,\n" +
                "    `user`.`status_key`\tAS `Status`,\n" +
                "    `user`.`user_birth`\tAS `Birth`,\n" +
                "    `user`.`user_level`\tAS `Level`,\n" +
                "    `user`.`is_admin`\t\tAS `Admin`,\n" +
                "    `user`.`create_at`\t\tAS `CreatedAt`,\n" +
                "    `user`.`signed_at`\t\tAS `SignedAt`,\n" +
                "    `user`.`status_changed_at` AS `StatusChangedAt`,\n" +
                "    `user`.`password_modified_at` AS `PasswordModifiedAt` \n" +
                " FROM `classica`.`users` AS `user` \n" +
                " WHERE `email` = ? AND `name` = ? AND `contact`=?\n" +
                " LIMIT 1";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, resetCheckVo.getEmail());
            preparedStatement.setString(2, resetCheckVo.getName());
            preparedStatement.setString(3, resetCheckVo.getContact());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    userVo = new UserVo(
                            resultSet.getInt("userIndex"),
                            resultSet.getString("Email"),
                            resultSet.getString("Name"),
                            resultSet.getString("Nickname"),
                            resultSet.getString("Address"),
                            resultSet.getString("Contact"),
                            resultSet.getString("Status"),
                            resultSet.getDate("Birth"),
                            resultSet.getInt("Level"),
                            resultSet.getBoolean("Admin"),
                            resultSet.getDate("CreatedAt"),
                            resultSet.getDate("SignedAt"),
                            resultSet.getDate("StatusChangedAt"),
                            resultSet.getDate("PasswordModifiedAt")
                    );
                }
            }
        }
        return userVo;
    }

    public int selectCountUserbyEmail(Connection connection, String email) throws SQLException {
        String query = "" +
                "SELECT COUNT(`user_index`) AS `count` FROM `classica`.`users` WHERE `email`= ?";
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
    public int selectCountUserbyNickname(Connection connection, String nickname) throws SQLException {
        String query = "" +
                "SELECT COUNT(`user_index`) AS `count` FROM `classica`.`users` WHERE `nickname`= ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nickname);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }
    public int selectCountUserbyContact(Connection connection, String Contact) throws SQLException {
        String query = "" +
                "SELECT COUNT(`user_index`) AS `count` FROM `classica`.`users` WHERE `contact`= ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, Contact);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }

    public void updateTheLastestLogin(Connection connection, LoginVo loginVo) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`users` SET `signed_at` = CURRENT_TIMESTAMP \n" +
                "WHERE `email` = ? AND `password` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, loginVo.getEmail());
            preparedStatement.setString(2, loginVo.getHashedPassword());
            preparedStatement.executeUpdate();
        }
    }

    public void insertUser(Connection connection, RegisterVo registerVo) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`users` (\n" +
                "\t\t`email` \t\t,\n" +
                "        `password`\t\t,\n" +
                "\t\t`name`\t\t\t,\n" +
                "\t\t`nickname` \t\t,\n" +
                "\t\t`address` \t\t,\n" +
                "\t\t`contact`\t\t,\n" +
                "\t\t`user_birth`\t\n" +
                "    ) VALUE (\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    ?,\n" +
                "    ?\n" +
                "    )";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, registerVo.getEmail());
            preparedStatement.setString(2, registerVo.getHashedPassword());
            preparedStatement.setString(3, registerVo.getName());
            preparedStatement.setString(4, registerVo.getNickname());
            preparedStatement.setString(5, registerVo.getAddress());
            preparedStatement.setString(6, registerVo.getContact());
            preparedStatement.setString(7, registerVo.getBirth());
            preparedStatement.execute();
        }
    }

    public void insertResetCode(Connection connection, int userIndex, String code) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`reset-codes` (\n" +
                "\t`user_index`,\n" +
                "    `reset_code`,\n" +
                "    `reset_expires_at`\n" +
                ") VALUE \n" +
                "(?, ?, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE))";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userIndex);
            preparedStatement.setString(2, code);
            preparedStatement.execute();
        }
    }
    // 코드 만료시킴.
    public void updateCodeExpires(Connection connection, String code) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`reset-codes` SET `reset_expires_at` = CURRENT_TIMESTAMP \n" +
                "WHERE `reset_code` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            preparedStatement.executeUpdate();
        }
    }

    public int selectUserCountbyCode(Connection connection, String code) throws SQLException {
        String query = "" +
                "SELECT COUNT(`user_index`) AS `count` FROM `classica`.`reset-codes` \n" +
                "WHERE `reset_code` = ?\n" +
                "AND `reset_expires_at` > CURRENT_TIMESTAMP";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    if(resultSet.getInt("count") > 0) {
                        return this.selectUserIndexbyCode(connection, code);
                    }
                }
            }
        }
        return -1;
    }

    public int selectUserIndexbyCode(Connection connection, String code) throws SQLException {
        String query = "" +
                "SELECT `user_index` AS `index` FROM `classica`.`reset-codes` \n" +
                "WHERE `reset_code` = ?\n" +
                "AND `reset_expires_at` > CURRENT_TIMESTAMP";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getInt("index");
                }
            }
        }
        return -1;
    }
    public void updateUserPassword(Connection connection, ResetPasswordVo resetPasswordVo) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`users` SET `password` = ?\n" +
                ",`password_modified_at` = CURRENT_TIMESTAMP \n" +
                "WHERE `user_index`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, resetPasswordVo.getHashedPassword());
            preparedStatement.setInt(2, resetPasswordVo.getIndex());
            preparedStatement.executeUpdate();
        }
    }

    public void insertRegisterResetCode(Connection connection, String email, String code) throws SQLException {
        String query = "" +
                "INSERT INTO `classica`.`register-codes` (\n" +
                "\t`user_email`,\n" +
                "    `register_code`,\n" +
                "    `register_expires_at`\n" +
                ") VALUE \n" +
                "(?, ?, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 30 MINUTE))";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, code);
            preparedStatement.execute();
        }
    }
    // 코드 만료시킴.
    public void updateRegisterCodeExpires(Connection connection, String code) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`register-codes` SET `register_expires_at` = CURRENT_TIMESTAMP \n" +
                "WHERE `register_code` = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            preparedStatement.executeUpdate();
        }
    }
    public String selectUserCountbyRegisterCode(Connection connection, String code) throws SQLException {
        String query = "" +
                "SELECT COUNT(`register_index`) AS `count` FROM `classica`.`register-codes` \n" +
                "WHERE `register_code` = ?\n" +
                "AND `register_expires_at` > CURRENT_TIMESTAMP";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    if(resultSet.getInt("count") > 0) {
                        return this.selectUserIndexbyRegisterCode(connection, code);
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public String selectUserIndexbyRegisterCode(Connection connection, String code) throws SQLException {
        String query = "" +
                "SELECT `user_email` AS `email` FROM `classica`.`register-codes` \n" +
                "WHERE `register_code` = ?\n" +
                "AND `register_expires_at` > CURRENT_TIMESTAMP";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        }
        return null;
    }
    public void updateUserEmailAuth(Connection connection, String email) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`users` SET `user_level`=9 WHERE `email`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        }
    }
    public void updateUser(Connection connection, UserVo userVo, RegisterVo registerVo) throws SQLException {
        String query = "" +
                "UPDATE `classica`.`users`\n" +
                " SET `password`=?, `nickname`=? , `contact`= ?, `user_birth`= ?,  `address`=? \n" +
                "WHERE `user_index` =?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, registerVo.getHashedPassword());
            preparedStatement.setString(2, registerVo.getNickname());
            preparedStatement.setString(3, registerVo.getContact());
            preparedStatement.setString(4, registerVo.getBirth());
            preparedStatement.setString(5, registerVo.getAddress());
            preparedStatement.setInt(6, userVo.getIndex());
            preparedStatement.execute();
        }
    }

    public void deleteUser(Connection connection, LoginVo loginVo) throws SQLException {
        String query = "" +
                "DELETE FROM `classica`.`users` WHERE `email`=? AND `password`=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, loginVo.getEmail());
            preparedStatement.setString(2, loginVo.getHashedPassword());
            preparedStatement.execute();
        }
    }
}
