package sunghs.tistory.markdown;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

public enum ExamEnums {

    SUCCESS("200", "성공"),
    WAITING("300", "대기 중"),
    FAIL("500", "실패");

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
        Stream.of(values()).collect(Collectors.toMap(ExamEnums::getCode, ExamEnums::name)));
    @Getter
    private final String code;

    @Getter
    private final String description;

    ExamEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ExamEnums of(final String code) {
        return ExamEnums.valueOf(CODE_MAP.get(code));
    }
}
