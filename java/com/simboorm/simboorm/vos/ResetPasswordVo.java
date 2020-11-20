package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.utility.Sha512;

public class ResetPasswordVo {
    private static final String PASSWORD_REGEX = "^([0-9a-zA-Z~!@#$%^&*()\\\\-_=+\\\\[{\\\\]}\\\\\\\\|;:'\\\",<.>/?]{4,100})$";  //비밀번호는 최소 8자에서 100자 까지
    private final int index;
    private final String code;
    private final String password;
    private final String hashedPassword;
    private boolean isNormalization = false;

    public ResetPasswordVo(int index, String password, String code) {
        if(password.matches(ResetPasswordVo.PASSWORD_REGEX)) {
            this.index = index;
            this.password = password;
            this.hashedPassword = Sha512.hash(password);
            this.isNormalization = true;
            this.code = code;
        } else {
            this.index = -1;
            this.password = null;
            this.hashedPassword = null;
            this.code = null;
        }
    }

    public int getIndex() {
        return index;
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

    public String getCode() {
        return code;
    }
}
