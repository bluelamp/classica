package com.simboorm.simboorm.services;

import com.simboorm.simboorm.daos.SecurityDao;
import com.simboorm.simboorm.daos.UserDao;
import com.simboorm.simboorm.utility.Client;
import com.simboorm.simboorm.vos.AttemptLoginVo;
import com.simboorm.simboorm.vos.BlockipVo;
import com.simboorm.simboorm.vos.LoginVo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

@Service
public class SecurityService {
    private final DataSource dataSource;
    private final SecurityDao securityDao;

    public SecurityService(DataSource dataSource, SecurityDao securityDao) {
        this.dataSource = dataSource;
        this.securityDao = securityDao;
    }

    public void attemptLogin(AttemptLoginVo attemptLoginVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            this.securityDao.insertAttemptips(connection, attemptLoginVo);
        }
    }

    public void blockbyip(String ip, int count) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            if(this.securityDao.selectAttemptCountbyip(connection, ip) >= count) {
                int level = this.securityDao.selectBlocklevelbyip(connection, ip)+1;  //레벨 추가
                this.securityDao.insertBlockip(connection, ip, level);
            }
        }
    }

    public int bannedbyEamil(String email, int count) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            int EmailAttempt = this.securityDao.selectAttemptCountbyemail(connection, email);
            if(EmailAttempt >= count) {
                this.securityDao.insertBannedEmail(connection, email);
            }
            return EmailAttempt;
        }
    }

    public Boolean checkBlockbyip(String ip) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            if(this.securityDao.selectBlockbyip(connection, ip) > 0)
                return true;
            else
                return false;
        }
    }
    public String checkBlockbyemail(String email) throws  SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            Date date = this.securityDao.selectBannedbyemail(connection, email);
            if(date != null) {
                String bannedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                return bannedAt;
            } else {
                return null;
            }
        }
    }
}
