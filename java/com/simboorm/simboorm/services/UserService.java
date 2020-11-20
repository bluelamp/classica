package com.simboorm.simboorm.services;

import com.simboorm.simboorm.daos.UserDao;
import com.simboorm.simboorm.enums.*;
import com.simboorm.simboorm.utility.Sha512;
import com.simboorm.simboorm.utility.Variable;
import com.simboorm.simboorm.vos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class UserService {
    private final DataSource dataSource;
    private final UserDao userDao;
    private final MailService mailService;

    @Autowired
    public UserService(DataSource dataSource, UserDao userDao, MailService mailService) {
        this.dataSource = dataSource;
        this.userDao = userDao;
        this.mailService = mailService;
    }

    public UserLoginResult login(LoginVo loginVo, HttpSession session) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
               UserVo userVo = this.userDao.selectUser(connection, loginVo);
               if(userVo == null) {
                   return UserLoginResult.LOGIN_FAILURE;
               } else {
                   if(userVo.getLevel() < 10) {
                       session.setAttribute("UserVo", userVo);
                       this.userDao.updateTheLastestLogin(connection, loginVo);
                       return UserLoginResult.LOGIN_SUCCESS;
                   } else {
                       return UserLoginResult.NOT_APPROVED;
                   }
               }
        }
    }

    public String findEmail(FindEmailVo findEmailVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            return this.userDao.selectUser(connection, findEmailVo);
        }
    }
    public UserResetPasswordResult getResetCode(ResetCheckVo resetCheckVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            UserVo userVo = this.userDao.selectUser(connection, resetCheckVo);
            if(userVo != null) {
                String code = Sha512.hash(String.format("%s%s%s%s",
                        resetCheckVo.getEmail(),
                        resetCheckVo.getName(),
                        new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
                        Variable.getRandomCode(64)
                ));;
                this.userDao.insertResetCode(connection, userVo.getIndex(), code);
                // TODO : 실제로는 해당 코드를 이메일로 보내줘야됨. 해당 홈페이지의 reset-password로 들어가기.
                SendMailVo sendMailVo = new SendMailVo(
                        "[Classica] 비밀번호 재설정 링크",
                        String.format("비밀번호 재설정 링크입니다. 해당 링크로 비밀번호를 재설정하여 주십시오.%nhttp://allrandom.co.kr/reset-password?code=%s",
                                code),
                        resetCheckVo.getEmail()
                );
                this.mailService.send(sendMailVo);

                return UserResetPasswordResult.SEND_CODE;
            } else {
                return UserResetPasswordResult.NONE_DATA;
            }
        }
    }

    public UserRegisterResult checkRegister(RegisterVo registerVo) throws  SQLException {
        UserRegisterResult userRegisterResult = null;
        try(Connection connection = this.dataSource.getConnection()) {
            if(this.userDao.selectCountUserbyEmail(connection, registerVo.getEmail()) >0) {
                userRegisterResult = UserRegisterResult.DUPLICATE_EMAIL;
            } else if(this.userDao.selectCountUserbyContact(connection, registerVo.getContact()) >0) {
                userRegisterResult = UserRegisterResult.DUPLICATE_CONTACT;
            } else if(this.userDao.selectCountUserbyNickname(connection, registerVo.getNickname()) >0) {
                userRegisterResult = UserRegisterResult.DUPLICATE_NICKNAME;
            } else {
                userRegisterResult = UserRegisterResult.CHECK_OKAY;
            }
        }
        return userRegisterResult;
    }

    public void registerUser(RegisterVo registerVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            this.userDao.insertUser(connection, registerVo);
            String code = sendRegisterEmailAuthCode(registerVo.getEmail());
            this.userDao.insertRegisterResetCode(connection, registerVo.getEmail(), code);
        }
    }

    public String sendRegisterEmailAuthCode(String email) {
        String code = Sha512.hash(String.format("%s%s%s",
                email,
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
                Variable.getRandomCode(64)
        ));
        // TODO : 이메일 인증으로 보내줘야됨. 해당 홈페이지의 register로 들어가기.
        SendMailVo sendMailVo = new SendMailVo(
                "[Classica] 이메일 인증 링크",
                String.format("회원가입을 위한 이메일 인증 링크입니다.%nhttp://allrandom.co.kr/emailAuth?code=%s",
                        code),
                email
        );
        this.mailService.send(sendMailVo);
        return code;
    }

    public int getUserIndexbyCode(String code) throws SQLException { // 유저 인덱스 리턴해줌.
        try(Connection connection = this.dataSource.getConnection()) {
            return this.userDao.selectUserCountbyCode(connection, code);
        }
    }

    public UserResetPasswordResult resetPassword(ResetPasswordVo resetPasswordVo) throws SQLException {
        try(Connection connection = this.dataSource.getConnection()) {
            this.userDao.updateUserPassword(connection, resetPasswordVo);
            this.userDao.updateCodeExpires(connection, resetPasswordVo.getCode());
        }
        return UserResetPasswordResult.RESET_SUCCESS;
    }

    public String getUserIndexbyRegisterCode(String code) throws SQLException { // 유저 인덱스 리턴해줌.
        try(Connection connection = this.dataSource.getConnection()) {
            return this.userDao.selectUserCountbyRegisterCode(connection, code);
        }
    }

    public void AuthorityEmail(String email, String code) throws SQLException { // 해당 이메일 인증해줌.
        try(Connection connection = this.dataSource.getConnection()) {
            this.userDao.updateUserEmailAuth(connection, email);
            this.userDao.updateRegisterCodeExpires(connection, code);
        }
    }

    public UserInfoChangeResult changeUserInfo(UserVo userVo, RegisterVo registerVo) throws SQLException {
        UserInfoChangeResult userInfoChangeResult = null;
        try(Connection connection = this.dataSource.getConnection()) {
            if(this.userDao.selectCountUserbyContact(connection, registerVo.getContact()) >0 && !userVo.getContact().equals(registerVo.getContact())) {
                userInfoChangeResult = UserInfoChangeResult.DUPLICATE_CONTACT;
            } else if(this.userDao.selectCountUserbyNickname(connection, registerVo.getNickname()) >0 && !userVo.getNickname().equals(registerVo.getNickname())) {
                userInfoChangeResult = UserInfoChangeResult.DUPLICATE_NICKNAME;
            } else {
                this.userDao.updateUser(connection, userVo, registerVo);
                userInfoChangeResult = UserInfoChangeResult.CHANGE_SUCCESS;
            }
        }
        return userInfoChangeResult;
    }
    public UserLeaveResult leaveUser(LoginVo loginVo, HttpSession session) throws SQLException {
        UserLeaveResult userLeaveResult = null;
        try(Connection connection = this.dataSource.getConnection()) {
            UserVo userVo = this.userDao.selectUser(connection, loginVo);
            if(userVo == null) {
                userLeaveResult = UserLeaveResult.LEAVE_FAILURE;
            } else {
                session.setAttribute("UserVo", null);
                this.userDao.deleteUser(connection, loginVo);
                this.sendLeaveUser(loginVo.getEmail());
                userLeaveResult = UserLeaveResult.LEAVE_SUCCESS;
            }
        }
        return userLeaveResult;
    }

    public void sendLeaveUser(String email) {
        // TODO : 이메일 인증으로 보내줘야됨. 해당 홈페이지의 register로 들어가기.
        SendMailVo sendMailVo = new SendMailVo(
                "[Classica] 회원탈퇴 알림",
                String.format("%s %n클래시카 회원탈퇴하셨습니다.",
                        new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분").format(new Date())),
                email
        );
        this.mailService.send(sendMailVo);
    }
}
