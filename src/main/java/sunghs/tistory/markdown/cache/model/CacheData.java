package sunghs.tistory.markdown.cache.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CacheData {

    private String value;

    private LocalDateTime expirationDate;
}