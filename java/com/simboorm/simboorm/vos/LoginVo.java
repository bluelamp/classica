package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.utility.Sha512;

public class LoginVo {
    private static final String PASSWORD_REGEX = "^([0-9a-zA-Z~!@#$%^&*()\\\\-_=+\\\\[{\\\\]}\\\\\\\\|;:'\\\",<.>/?]{4,100})$";  //비밀번호는 최소 8자에서 100자 까지
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+(?:[a-zA-Z]{2}|aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel)$";
    private final String email;
    private final String password;
    private final String hashedPassword;
    private boolean isNormalization = false;

    public LoginVo(String email, String password) {
        if(email.matches(LoginVo.EMAIL_REGEX) && password.matches(LoginVo.PASSWORD_REGEX)) {
            this.email = email;
            this.password = password;
            this.hashedPassword = Sha512.hash(password);
            this.isNormalization = true;
        } else {
            this.email = null;
            this.password = null;
            this.hashedPassword = null;
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public boolean isNormalization() {
        return isNormalization;
    }
}
