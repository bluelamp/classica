package com.simboorm.simboorm.controllers;

import com.simboorm.simboorm.enums.*;
import com.simboorm.simboorm.services.SecurityService;
import com.simboorm.simboorm.services.UserService;
import com.simboorm.simboorm.utility.Client;
import com.simboorm.simboorm.utility.Variable;
import com.simboorm.simboorm.vos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@RestController
@RequestMapping(value = "/usr/api")
public class UserController {
    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    /************************ 로그인                                                          */
    @RequestMapping(value="login", method= RequestMethod.POST)
    public void loginPost(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(name="email", defaultValue = "") String email,
                          @RequestParam(name="password", defaultValue = "") String password)  throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        LoginVo loginVo = new LoginVo(email, password);
        UserLoginResult userLoginResult = null;
        String ip = Client.getip(request);

        if(this.securityService.checkBlockbyip(ip)) {
            userLoginResult = UserLoginResult.IP_BLOCK;
        } else if(this.securityService.checkBlockbyemail(email) != null) {
            userLoginResult = UserLoginResult.EMAIL_BANNED;
        } else {
            if(loginVo.isNormalization()) {
                userLoginResult = this.userService.login(loginVo, session);
            } else {
                userLoginResult = UserLoginResult.NORMALIZATION_ERROR;
            }
        }

        AttemptLoginVo attemptLoginVo = new AttemptLoginVo(email, password, Client.getip(request), userLoginResult.name());
        this.securityService.attemptLogin(attemptLoginVo);

        if(userLoginResult == UserLoginResult.LOGIN_FAILURE) {
            this.securityService.blockbyip(ip, 10);
            out.print("LoginCountBythisEmail="+this.securityService.bannedbyEamil(email, 5));
        }

        out.print(userLoginResult.name());
    }

    /************************ 회원가입                                                          */
    @RequestMapping(value="register", method= RequestMethod.POST)
    public void registerPost(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(name="email", defaultValue = "") String email,
                             @RequestParam(name="password", defaultValue = "") String password,
                             @RequestParam(name="name", defaultValue = "") String name,
                             @RequestParam(name="nickname", defaultValue = "") String nickname,
                             @RequestParam(name="address", defaultValue = "") String address,
                             @RequestParam(name="contact", defaultValue = "") String contact,
                             @RequestParam(name="birth", defaultValue = "") String birth) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        RegisterVo registerVo = new RegisterVo(email,password,name, nickname,address, contact, birth);
        UserRegisterResult userRegisterResult;
        if(!registerVo.isNormalization()) {
            out.print(UserRegisterResult.NORMALIZATION_FAILURE);
        } else {
            userRegisterResult = this.userService.checkRegister(registerVo);
            if(userRegisterResult == UserRegisterResult.CHECK_OKAY) {
                this.userService.registerUser(registerVo);
                FindEmailVo findEmailVo = new FindEmailVo(registerVo.getName(), registerVo.getContact());
                if(this.userService.findEmail(findEmailVo).length() > 0) {
                    userRegisterResult = UserRegisterResult.REGISTER_SUCCESS;
                }
            }
            out.print(userRegisterResult);
        }
    }

    /************************ 회원가입 이메일 인증                                                         */
    @RequestMapping(value="emailAuth", method = RequestMethod.POST)
    public void emailAuthPost(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(name="code", defaultValue = "") String code,
                              @RequestParam(name="email", defaultValue = "") String email,
                              @RequestParam(name="password", defaultValue = "") String password) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        UserLoginResult userLoginResult = null;

        LoginVo loginVo = new LoginVo(email, password);
        userLoginResult = this.userService.login(loginVo, session);
        String emailbycode = this.userService.getUserIndexbyRegisterCode(code);

        if(emailbycode.equals(email)) {
            if(userLoginResult == UserLoginResult.NOT_APPROVED) {
                this.userService.AuthorityEmail(email, code);
                this.userService.login(loginVo, session);
                out.print(UserLoginResult.LOGIN_SUCCESS);
            } else if(userLoginResult == UserLoginResult.LOGIN_FAILURE) {
                out.print(userLoginResult);
            } else {
                out.print(UserLoginResult.LOGIN_FAILURE);
            }
        } else {
            out.print(UserRegisterResult.EMAIL_AUTH_FAILURE);
        }
    }

    /************************ 이메일 찾기                                                          */
    @RequestMapping(value="find-email", method=RequestMethod.POST)
    public void findEmail(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(name="name", defaultValue = "") String name,
                          @RequestParam(name="contact", defaultValue = "") String contact) throws IOException, SQLException{
        response.setContentType("text/html; charset=UTF-8;");
        PrintWriter out = response.getWriter();

        System.out.println("이메일 찾기"+name+"/"+contact);
        FindEmailVo findEmailVo = new FindEmailVo(name, contact);
        String Email = this.userService.findEmail(findEmailVo);
        if(Email != null) {
            out.print(UserFindEmailResult.SUCCESS_FIND.name()+"="+Variable.getHideEmail(Email));
        } else {
            out.print(UserFindEmailResult.NONE_DATA.name());
        }
    }

    /************************ 비밀번호 리셋 시퀀스중 리셋코드 보내는 것                                                           */
    @RequestMapping(value="send-reset-code", method=RequestMethod.POST)
    public void sendResetCode(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(name="email", defaultValue = "") String email,
                          @RequestParam(name="name", defaultValue = "") String name,
                          @RequestParam(name="contact", defaultValue = "") String contact) throws IOException, SQLException{
        response.setContentType("text/html; charset=UTF-8;");
        PrintWriter out = response.getWriter();

        System.out.println("비밀번호 리셋 :"+email+"/"+name+"/"+contact);
        ResetCheckVo resetCheckVo = new ResetCheckVo(email,contact, name);
        out.print(this.userService.getResetCode(resetCheckVo));
    }

    /************************ 비밀번호 리셋 시퀀스 중 실제로 비밀번호 리셋하는 과정                                          */
    @RequestMapping(value="reset-password", method=RequestMethod.POST)
    public void resetPassword(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(name="code", defaultValue = "") String code,
                                @RequestParam(name="password", defaultValue = "") String password) throws IOException, SQLException{
        response.setContentType("text/html; charset=UTF-8;");
        PrintWriter out = response.getWriter();
        int index = this.userService.getUserIndexbyCode(code);
        if(index > 0) {
            ResetPasswordVo resetPasswordVo = new ResetPasswordVo(index, password, code);
            if(resetPasswordVo.isNormalization()) {
                this.userService.resetPassword(resetPasswordVo);
                out.print(UserResetPasswordResult.RESET_SUCCESS);
            } else {
                out.print(UserResetPasswordResult.PASSWORD_NORMALIZATION_FAILURE);
            }
        } else {
            out.print(UserResetPasswordResult.NONE_DATA);
        }
    }

    /************************ 회원정보 변경                                                          */
    @RequestMapping(value="changeinfo", method=RequestMethod.POST)
    public void changeMyinfo(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(name="email", defaultValue = "") String email,
                             @RequestParam(name="password", defaultValue = "") String password,
                             @RequestParam(name="name", defaultValue = "") String name,
                             @RequestParam(name="nickname", defaultValue = "") String nickname,
                             @RequestParam(name="address", defaultValue = "") String address,
                             @RequestParam(name="contact", defaultValue = "") String contact,
                             @RequestParam(name="birth", defaultValue = "") String birth) throws IOException, SQLException{
        response.setContentType("text/html; charset=UTF-8;");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        UserVo userVo = Variable.getUser(session);

        RegisterVo registerVo = new RegisterVo(email, password, name, nickname, address, contact, birth);
        UserInfoChangeResult userInfoChangeResult = null;
        if(!registerVo.isNormalization()) {
            userInfoChangeResult = UserInfoChangeResult.NORMALIZATION_FAILURE;
        } else {
            //닉네임, 생년월일이랑 연락처, 주소 변경 가능
            userInfoChangeResult = this.userService.changeUserInfo(userVo, registerVo);
        }
        if(userInfoChangeResult != null) {
            out.print(userInfoChangeResult);
        } else {
            out.print(UserInfoChangeResult.CHANGE_FAILURE);
        }
    }

    /************************ 회원정보 탈퇴                                                          */
    @RequestMapping(value="leave", method=RequestMethod.POST)
    public void leave(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(name="email", defaultValue = "") String email,
                             @RequestParam(name="password", defaultValue = "") String password
                             ) throws IOException, SQLException{
        response.setContentType("text/html; charset=UTF-8;");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        UserVo userVo = Variable.getUser(session);

        LoginVo loginVo = new LoginVo(email, password);
        UserLeaveResult userLeaveResult = null;
        if(!loginVo.isNormalization()) {
            userLeaveResult = UserLeaveResult.NORMALIZATION_FAILURE;
        } else {
            userLeaveResult = this.userService.leaveUser(loginVo, session);
        }
        if(userLeaveResult != null) {
            out.print(userLeaveResult);
        } else {
            out.print(UserLeaveResult.LEAVE_FAILURE);
        }
    }
}
