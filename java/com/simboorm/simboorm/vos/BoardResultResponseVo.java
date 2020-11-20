package com.simboorm.simboorm.vos;

import com.simboorm.simboorm.enums.BoardResponseResult;
import org.json.JSONArray;

import java.util.ArrayList;

public class BoardResultResponseVo {
    private final BoardResponseResult boardResponseResult;
    private final JSONArray boardVoArrayList;
    private final int currentPage;
    private final int startPage;
    private final int endPage;
    private final int totalPage;
    private final int readLevel;
    private final int writeLevel;

    public BoardResultResponseVo(BoardResponseResult boardResponseResult, JSONArray boardVoArrayList, int currentPage, int totalPage, int readLevel, int writeLevel) {
        this.boardResponseResult = boardResponseResult;
        this.boardVoArrayList = boardVoArrayList;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.startPage = currentPage > 5 ? currentPage - 4 : 1;
        this.endPage = currentPage + 5 > totalPage ? totalPage : currentPage + 5;
        this.readLevel = readLevel;
        this.writeLevel = writeLevel;
    }
    public BoardResultResponseVo(BoardResponseResult boardResponseResult) {
        this.boardResponseResult = boardResponseResult;
        this.boardVoArrayList = null;
        this.currentPage = 1;
        this.totalPage = -1;
        this.startPage = -1;
        this.endPage = -1 ;
        this.readLevel = -1;
        this.writeLevel = -1;
    }

    public BoardResponseResult getBoardResponseResult() {
        return boardResponseResult;
    }

    public JSONArray getBoardVoArrayList() {
        return boardVoArrayList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getReadLevel() {
        return readLevel;
    }

    public int getWriteLevel() {
        return writeLevel;
    }
}
