package com.simboorm.simboorm.utility;

import com.simboorm.simboorm.vos.BoardResultResponseVo;
import com.simboorm.simboorm.vos.UserVo;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Random;

public class Variable {
    public static String getHideEmail(String Email) {
        String result = null;
        String REGEX = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]";

        String OpenAccount = Email.substring(0,2);
        String OpenDomain = Email.substring(Email.indexOf("@")+1,Email.indexOf("@")+3);

        String temp = Email.substring(2, Email.indexOf("@")).replaceAll(REGEX, "*");
        String temp1 = Email.substring(Email.indexOf("@")+3, Email.length()).replaceAll(REGEX, "*");
        result = OpenAccount+temp.replaceAll(REGEX, "*")+"@"+OpenDomain+temp1;
        return result;
    }

    public static String getRandomCode(int length) {
        String result = "";
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            int rIndex = rnd.nextInt(4);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
                case 3:
                    //특수 문자 !
                    temp.append((char) 33);
                    break;
                case 4:
                    //특수 문자 ( )
                    temp.append((char) ((int) (rnd.nextInt(2) + 40)));
                    break;
            }
        }
        result = temp.toString();

        return result;
    }

    public static UserVo getUser(HttpSession session) {
        Object obj = session.getAttribute("UserVo");
        UserVo userVo = null;
        if(obj!=null && obj instanceof UserVo) {
            userVo = (UserVo)obj;
        } else {
            userVo = new UserVo(
                    9,
                    "nan",
                    "guest",
                    "guest",
                    "nan",
                    "nan",
                    "nan",
                    null,
                    10,
                    false,
                    null,
                    null,
                    null,
                    null
            );
        }
        return userVo;
    }
    public static JSONArray getBoardVoList(HttpSession session) {
        Object obj = session.getAttribute("BoardVoList");
        JSONArray boardVoList = null;
        if(obj!=null && obj instanceof JSONArray) {
            boardVoList = (JSONArray)obj;
        }
        return boardVoList;
    }

    public static BoardResultResponseVo getBoardResultResponseVo(HttpSession session) {
        Object obj = session.getAttribute("BoardResultResponseVo");
        BoardResultResponseVo boardResultResponseVo = null;
        if(obj!=null && obj instanceof BoardResultResponseVo) {
            boardResultResponseVo = (BoardResultResponseVo)obj;
        }
        return boardResultResponseVo;
    }
}
