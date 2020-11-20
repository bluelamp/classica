package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.utility.Sha512;

import java.util.Date;

public class RegisterVo {
    private static final String PASSWORD_REGEX = "^([0-9a-zA-Z~!@#$%^&*()\\\\-_=+\\\\[{\\\\]}\\\\\\\\|;:'\\\",<.>/?]{4,100})$";  //비밀번호는 최소 8자에서 100자 까지
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+(?:[a-zA-Z]{2}|aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel)$";
    private final String email;
    private final String password;
    private final String hashedPassword;
    private final String name;
    private final String nickname;
    private final String address;
    private final String contact;
    private final String birth;
    private boolean isNormalization = false;

    public RegisterVo(String email, String password, String name, String nickname, String address, String contact, String birth) {
        if(email.matches(RegisterVo.EMAIL_REGEX) && password.matches(RegisterVo.PASSWORD_REGEX)) {
            this.email = email;
            this.password = password;
            this.hashedPassword = Sha512.hash(password);
            this.name = name;
            this.nickname = nickname;
            this.address = address;
            this.contact = contact;
            this.birth = birth;
            this.isNormalization = true;
        } else {
            this.email = null;
            this.password = null;
            this.hashedPassword = null;
            this.name = null;
            this.nickname = null;
            this.address = null;
            this.contact = null;
            this.birth = null;
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

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getBirth() {
        return birth;
    }

    public boolean isNormalization() {
        return isNormalization;
    }
}
