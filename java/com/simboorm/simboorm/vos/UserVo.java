package com.simboorm.simboorm.vos;

import java.util.Date;

public class UserVo {
    private final int index;
    private final String email;
    private final String name;
    private final String nickname;
    private final String address;
    private final String contact;
    private final String userStatus;
    private final Date birth;
    private final int level;                //게시판 글 쓰기, 글 읽기 권한.
    private final boolean admin;            //관리자 관해
    private final Date userCreatedAt;       //회원가입 시기
    private final Date userSignedAt;        //최근 로그인
    private final Date userStatusChangedAt;     //회원 상태 관련
    private final Date userPasswordModifiedAt;  //비밀번호 변경한


    public UserVo(
            int index,
            String email,
            String name,
            String nickname,
            String address,
            String contact,
            String userStatus,
            Date birth,
            int level,
            boolean admin,
            Date userCreatedAt,
            Date userSignedAt,
            Date userStatusChangedAt,
            Date userPasswordModifiedAt) {
        this.index = index;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.address = address;
        this.contact = contact;
        this.userStatus = userStatus;
        this.birth = birth;
        if(admin) {this.level = 0;} else {this.level = level;}
        this.admin = admin;
        this.userCreatedAt = userCreatedAt;
        this.userSignedAt = userSignedAt;
        this.userStatusChangedAt = userStatusChangedAt;
        this.userPasswordModifiedAt = userPasswordModifiedAt;
    }

    public int getIndex() {
        return index;
    }

    public String getEmail() {
        return email;
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

    public int getLevel() {
        return level;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public Date getBirth() {
        return birth;
    }

    public Date getUserCreatedAt() {
        return userCreatedAt;
    }

    public Date getUserSignedAt() {
        return userSignedAt;
    }

    public Date getUserStatusChangedAt() {
        return userStatusChangedAt;
    }

    public Date getUserPasswordModifiedAt() {
        return userPasswordModifiedAt;
    }
}
