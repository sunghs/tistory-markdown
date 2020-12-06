package sunghs.tistory.markdown;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestClass {

    public static void main(String[] args) {
        String resultCode = "500";

        ExamEnums examEnums = ExamEnums.of(resultCode);

        log.info(examEnums.getCode());
        log.info(examEnums.getDescription());
    }
}
