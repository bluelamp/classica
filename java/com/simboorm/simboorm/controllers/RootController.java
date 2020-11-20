package com.simboorm.simboorm.controllers;

import com.simboorm.simboorm.Converter;
import com.simboorm.simboorm.enums.*;
import com.simboorm.simboorm.services.BoardService;
import com.simboorm.simboorm.services.UserService;
import com.simboorm.simboorm.utility.Client;
import com.simboorm.simboorm.utility.Variable;
import com.simboorm.simboorm.vos.AuthorityVo;
import com.simboorm.simboorm.vos.BoardResultResponseVo;
import com.simboorm.simboorm.vos.BoardVo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

@Controller
@RequestMapping(value="/")
public class RootController extends StandardWebController {
    private final UserService userService;
    private final BoardService boardService;

    @Autowired
    public RootController(UserService userService, BoardService boardService) {
        this.userService = userService;
        this.boardService = boardService;
    }

    @RequestMapping(value="")
    public String root(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(name = "result", defaultValue = "") String result) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        return "/main";
    }
    /************************ 로그인                                                          */
    @RequestMapping(value="login", method= RequestMethod.GET)
    public String loginGet(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(name="returnURL", defaultValue = "") String returnURL) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        return "/login";
    }

    /************************ 로그아웃                                                          */
    @RequestMapping(value="logout", method= RequestMethod.GET)
    public String logoutGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        session.setAttribute("UserVo", null);
        response.sendRedirect("/");
        return "/main";
    }
    /************************ 회원가입                                                         */
    @RequestMapping(value="register", method= RequestMethod.GET)
    public String registerGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        return "/register";
    }

    /************************ 회원가입 이메일 인증                                                         */
    @RequestMapping(value="emailAuth", method= RequestMethod.GET)
    public String loginAuth(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(name="code", defaultValue = "") String code) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        if(this.userService.getUserIndexbyRegisterCode(code) != null) {
            return "/emailAuth";
        } else {
            request.setAttribute("UserResetPasswordResult", UserResetPasswordResult.EXPIRES_CODE);
            return "/main";
        }
    }

    /************************ 로그인 문제/ 이메일 찾기 또는 비밀번호 리셋                                            */
    @RequestMapping(value="loginProblem", method= RequestMethod.GET)
    public String loginProblem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        return "/loginProblem";
    }

    /************************ 로그인 문제/ 비밀번호 리셋 링크                                            */
    @RequestMapping(value="reset-password", method= RequestMethod.GET)
    public String resetPasswordGet(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam(name="code", defaultValue = "") String code) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        if(this.userService.getUserIndexbyCode(code) > 0) {
            return "/resetPassword";
        } else {
            request.setAttribute("UserResetPasswordResult", UserResetPasswordResult.EXPIRES_CODE);
            return "/main";
        }
    }

    @RequestMapping(value="mypage", method= RequestMethod.GET)
    public String mypageGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        return "/mypage";
    }

    @RequestMapping(value="mypage_changemyinfo", method= RequestMethod.GET)
    public String mypageChangeMyInfoGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        return "/mypage_changemyinfo";
    }

    @RequestMapping(value="leave", method= RequestMethod.GET)
    public String leaveGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        return "/mypage_leave";
    }

    /************************ 자유게시판 가져오기                                                */
    @RequestMapping(value="freeboard", method= RequestMethod.GET)
    public String freeBoardGet(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(name="page", defaultValue="1")String page,
                               @RequestParam(name="search", defaultValue="")String search ) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        Categories categories = Categories.FREE;
        BoardResultResponseVo boardResultResponseVo = null;
        ArrayList<BoardVo> boardVoArrayList = null;
        BoardResponseResult result = null;
        AuthorityVo authorityVo = null;
        try {
            int currentPage = Integer.parseInt(page);
            int totalPage = 1;
            if(search.equals("")) {
                totalPage = this.boardService.getTotalPage(categories);
                if (this.boardService.getBoardListCount(categories, currentPage) >= 0) {
                    out.print(BoardResponseResult.RESPONSE_SUCCESS);
                    boardVoArrayList = this.boardService.getBoardList(categories, currentPage);
                    authorityVo = this.boardService.getAuthority(categories);
                } else {
                    out.print(BoardResponseResult.NONE_DATA);
                }
            } else {
                String querySearch = "%"+search+"%";
                totalPage = this.boardService.getFindTotalPage(categories, querySearch);
                if (this.boardService.getFindBoardListCount(categories, querySearch, currentPage) >= 0) {
                    out.print(BoardResponseResult.RESPONSE_SUCCESS);
                    boardVoArrayList = this.boardService.getFindBoardList(categories, querySearch, currentPage);
                    authorityVo = this.boardService.getAuthority(categories);
                } else {
                    out.print(BoardResponseResult.NONE_DATA);
                }
            }

            if(!boardVoArrayList.isEmpty() && boardVoArrayList != null) {
                JSONArray array = new JSONArray();
                Iterator iterator = boardVoArrayList.iterator();
                while(iterator.hasNext()) {
                    BoardVo boardVo = (BoardVo)iterator.next();
                    JSONObject object = new JSONObject();
                    object.put("index", boardVo.getIndex());
                    object.put("categories", boardVo.getCategories());
                    object.put("views", boardVo.getViews());
                    object.put("comments", boardVo.getComments());
                    object.put("createAt", boardVo.getCreateAt().toString());
                    object.put("userNickname", boardVo.getUserNickname());
                    object.put("title", boardVo.getTitle());
                    object.put("text", boardVo.getText());
                    object.put("recommend", boardVo.getRecommend());
                    object.put("status", boardVo.getStatus());
                    array.put(object);
                }

                boardResultResponseVo = new BoardResultResponseVo(
                        BoardResponseResult.RESPONSE_SUCCESS,
                        array,
                        currentPage,
                        totalPage,
                        authorityVo != null ? authorityVo.getReadLevel(): 9,
                        authorityVo != null ? authorityVo.getWriteLevel(): 9
                );
                session.setAttribute("BoardResultResponseVo", boardResultResponseVo);
            }  else {
                boardResultResponseVo = new BoardResultResponseVo(
                        BoardResponseResult.NONE_DATA,
                        null,
                        currentPage,
                        totalPage,
                        authorityVo != null ? authorityVo.getReadLevel() : 9,
                        authorityVo != null ? authorityVo.getWriteLevel() : 9
                );
                session.setAttribute("BoardResultResponseVo", boardResultResponseVo);
            }
            return "/freeboard";
        } catch(NumberFormatException ignored) {

            return "/error";
        }
    }

    /************************ 공게시판 가져오기                                                */
    @RequestMapping(value="notiboard", method= RequestMethod.GET)
    public String notiBoardGet(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(name="page", defaultValue="1")String page,
                               @RequestParam(name="search", defaultValue="")String search ) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        Categories categories = Categories.NOTI;
        BoardResultResponseVo boardResultResponseVo = null;
        ArrayList<BoardVo> boardVoArrayList = null;
        BoardResponseResult result = null;
        AuthorityVo authorityVo = null;
        try {
            int currentPage = Integer.parseInt(page);
            int totalPage = 1;
            if(search.equals("")) {
                totalPage = this.boardService.getTotalPage(categories);
                if (this.boardService.getBoardListCount(categories, currentPage) >= 0) {
                    out.print(BoardResponseResult.RESPONSE_SUCCESS);
                    boardVoArrayList = this.boardService.getBoardList(categories, currentPage);
                    authorityVo = this.boardService.getAuthority(categories);
                } else {
                    out.print(BoardResponseResult.NONE_DATA);
                }
            } else {
                String querySearch = "%"+search+"%";
                totalPage = this.boardService.getFindTotalPage(categories, querySearch);
                if (this.boardService.getFindBoardListCount(categories, querySearch, currentPage) >= 0) {
                    out.print(BoardResponseResult.RESPONSE_SUCCESS);
                    boardVoArrayList = this.boardService.getFindBoardList(categories, querySearch, currentPage);
                    authorityVo = this.boardService.getAuthority(categories);
                } else {
                    out.print(BoardResponseResult.NONE_DATA);
                }
            }

            if(!boardVoArrayList.isEmpty() && boardVoArrayList != null) {
                JSONArray array = new JSONArray();
                Iterator iterator = boardVoArrayList.iterator();
                while(iterator.hasNext()) {
                    BoardVo boardVo = (BoardVo)iterator.next();
                    JSONObject object = new JSONObject();
                    object.put("index", boardVo.getIndex());
                    object.put("categories", boardVo.getCategories());
                    object.put("views", boardVo.getViews());
                    object.put("comments", boardVo.getComments());
                    object.put("createAt", boardVo.getCreateAt().toString());
                    object.put("userNickname", boardVo.getUserNickname());
                    object.put("title", boardVo.getTitle());
                    object.put("text", boardVo.getText());
                    object.put("recommend", boardVo.getRecommend());
                    object.put("status", boardVo.getStatus());
                    array.put(object);
                }

                boardResultResponseVo = new BoardResultResponseVo(
                        BoardResponseResult.RESPONSE_SUCCESS,
                        array,
                        currentPage,
                        totalPage,
                        authorityVo != null ? authorityVo.getReadLevel(): 9,
                        authorityVo != null ? authorityVo.getWriteLevel(): 9
                );
                session.setAttribute("BoardResultResponseVo", boardResultResponseVo);
            }  else {
                boardResultResponseVo = new BoardResultResponseVo(
                        BoardResponseResult.NONE_DATA,
                        null,
                        currentPage,
                        totalPage,
                        authorityVo != null ? authorityVo.getReadLevel() : 9,
                        authorityVo != null ? authorityVo.getWriteLevel() : 9
                );
                session.setAttribute("BoardResultResponseVo", boardResultResponseVo);
            }
            return "/notiboard";
        } catch(NumberFormatException ignored) {

            return "/error";
        }
    }

    /************************ QNA게시판 가져오기                                                */
    @RequestMapping(value="qnaboard", method= RequestMethod.GET)
    public String qnaBoardGet(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(name="page", defaultValue="1") String page,
                              @RequestParam(name="search", defaultValue="")String search ) throws IOException, SQLException {
        response.setContentType("text/html; charset=UTF-8;");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        Categories categories = Categories.QNA;
        BoardResultResponseVo boardResultResponseVo = null;
        ArrayList<BoardVo> boardVoArrayList = null;
        BoardResponseResult result = null;
        AuthorityVo authorityVo = null;
        try {
            int currentPage = Integer.parseInt(page);
            int totalPage = 1;
            if(search.equals("")) {
                totalPage = this.boardService.getTotalPage(categories);
                if (this.boardService.getBoardListCount(categories, currentPage) >= 0) {
                    out.print(BoardResponseResult.RESPONSE_SUCCESS);
                    boardVoArrayList = this.boardService.getBoardList(categories, currentPage);
                    authorityVo = this.boardService.getAuthority(categories);
                } else {
                    out.print(BoardResponseResult.NONE_DATA);
                }
            } else {
                String querySearch = "%"+search+"%";
                totalPage = this.boardService.getFindTotalPage(categories, querySearch);
                if (this.boardService.getFindBoardListCount(categories, querySearch, currentPage) >= 0) {
                    out.print(BoardResponseResult.RESPONSE_SUCCESS);
                    boardVoArrayList = this.boardService.getFindBoardList(categories, querySearch, currentPage);
                    authorityVo = this.boardService.getAuthority(categories);
                } else {
                    out.print(BoardResponseResult.NONE_DATA);
                }
            }

            if(!boardVoArrayList.isEmpty() && boardVoArrayList != null) {
                JSONArray array = new JSONArray();
                Iterator iterator = boardVoArrayList.iterator();
                while(iterator.hasNext()) {
                    BoardVo boardVo = (BoardVo)iterator.next();
                    JSONObject object = new JSONObject();
                    object.put("index", boardVo.getIndex());
                    object.put("categories", boardVo.getCategories());
                    object.put("views", boardVo.getViews());
                    object.put("comments", boardVo.getComments());
                    object.put("createAt", boardVo.getCreateAt().toString());
                    object.put("userNickname", boardVo.getUserNickname());
                    object.put("title", boardVo.getTitle());
                    object.put("text", boardVo.getText());
                    object.put("recommend", boardVo.getRecommend());
                    object.put("status", boardVo.getStatus());
                    array.put(object);
                }

                boardResultResponseVo = new BoardResultResponseVo(
                        BoardResponseResult.RESPONSE_SUCCESS,
                        array,
                        currentPage,
                        totalPage,
                        authorityVo != null ? authorityVo.getReadLevel(): 9,
                        authorityVo != null ? authorityVo.getWriteLevel(): 9
                );
                session.setAttribute("BoardResultResponseVo", boardResultResponseVo);
            }  else {
                boardResultResponseVo = new BoardResultResponseVo(
                        BoardResponseResult.NONE_DATA,
                        null,
                        currentPage,
                        totalPage,
                        authorityVo != null ? authorityVo.getReadLevel() : 9,
                        authorityVo != null ? authorityVo.getWriteLevel() : 9
                );
                session.setAttribute("BoardResultResponseVo", boardResultResponseVo);
            }
            return "/qnaboard";
        } catch(NumberFormatException ignored) {
            return "/error";
        }
    }

//    이미지 가져오기
    @RequestMapping(
            value = "download_image",
            method = RequestMethod.GET,
            produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] downloadImage(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(name = "id", defaultValue = "") String idText) throws
            SQLException, IOException {
        int index = Converter.stringToInt(idText, -1);
        if (index > 0) {
            return this.boardService.downloadImage(index);
        } else {
            return null;
        }
    }
}
