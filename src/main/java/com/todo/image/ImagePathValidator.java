package com.todo.image;

import org.springframework.stereotype.Component;

@Component
public class ImagePathValidator {
    /**
     * 매개변수가 올바른 파일명인지 검사하기 위한 메소드
     *
     * @param filename 검사할 파일 이름
     * */
    public boolean isValidPath(String filename) {
        if(isNull(filename)) return false;
        if(isContainIllegalChar(filename)) return false;

        // 화이트리스트: 영어, 숫자, 점, 밑줄, 하이픈만 허용
        return isValidFilename(filename);
    }

    /**
     * null/공백 방어를 위한 메소드
     *
     * @param filename 검사할 파일 이름
     * */
    private boolean isNull(String filename) {
        return filename == null || filename.isBlank();
    }

    /**
     * 특수한 문자열 주입으로 인한 해킹 방지 메소드.
     *
     * @param filename 검사할 파일 이름
     * */
    private boolean isContainIllegalChar(String filename) {
        return filename.contains("..") || filename.contains("/") || filename.contains("\\");
    }

    /**
     * 화이트리스트 기반 검증 메소드.
     *
     * @param filename 검사할 파일이름
     * */
    private boolean isValidFilename(String filename) {
        return filename.matches("^[A-Za-z0-9._-]+$");
    }
}