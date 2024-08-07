package net.datasa.web5.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.UUID;

import java.io.File;

public class AttachmentUtil {

    // 이미 업로드 된 첨부파일이 있는 경우의 처리
    //
    //

    /**
     * 저장할 경로의 폴더가 없으면 생성
     * 
     * @param path
     * @return String
     */
    public File ensureDirectoryExists(String path) {
        File directoryPath = new File(path); // 삭제 생성 확인 정도를 할수있는 패키지
        if (directoryPath.isDirectory()) {
            directoryPath.mkdirs(); // 디렉터리가 존재하는지 확인하고 없으면 if구문 안으로 들어와 디렉터리 생성함
        }
        return directoryPath;
    }

    /**
     * 새로운 파일 명을 만들어 주는 메서드
     * 
     * @param originalFileName
     * @return String
     * 
     */
    // 홍길동의 이력서.doc -> 20240806_UUID로 생성한 랜덤문자열(16진수128비트의문자열).doc
    // 20240806_d8e91593-f693-4280-9904-10637d85a46f.doc
    public String createUniqueFileName(String originalFileName) {
        // 새로운 파일명(저장할 파일 명)
        String extension = getExtenstion(originalFileName);
        // 유틸클래스의 Date가 아니라 time을 쓴다..
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // UUID 클래스 설명.. : 클래스명 뒤에 바로. 붙이고 호출하는 메서드 = 스태틱 메서드
        String uuidString = UUID.randomUUID().toString(); // 고유한 식별자를 생성할 때 사용하는 자바유틸클래스
        String fileName = dateString + "_" + uuidString + extension;

        return fileName;
    }

    /**
     * 파일명에서 확장자를 가져와 리턴하는 메서드
     * 
     * @param fileName
     * @return String
     */
    public String getExtenstion(String fileName) {
        // 파일이름에서 마지막 dot의 인덱스를 확인
        int dotIndex = fileName.lastIndexOf('.');
        // 확장자가 없는 파일의 처리
        if (dotIndex == -1)
            return ""; // 빈 문자열 리턴
        // dot를 포함하여 문자열을 리턴
        return fileName.substring(dotIndex);
    }
}
