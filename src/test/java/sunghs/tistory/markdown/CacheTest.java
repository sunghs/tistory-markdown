package sunghs.tistory.markdown;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sunghs.tistory.markdown.cache.model.CacheData;
import sunghs.tistory.markdown.cache.service.CacheService;

@SpringBootTest
@Slf4j
public class CacheTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void getCacheTest() {
        // 캐시가 없으므로 getCacheData 메소드의 "이 로그는 캐시가 없는 경우 찍힙니다." 가 출력 됨
        CacheData cacheData = cacheService.getCacheData("cacheDataKey");
        if (!cacheService.isValidation(cacheData)) {
            // 캐시 업데이트 되므로 updateCacheData 메소드의 "이 로그는 캐시가 업데이트 되는 경우 찍힙니다." 가 출력 됨
            cacheData = cacheService.updateCacheData("cacheDataKey", this.selectValue());
        }
        log.info(cacheData.toString());

        // 이미 캐시가 있으므로 getCacheData 메소드를 타지않고 바로 값을 가져옴
        CacheData newCacheData = cacheService.getCacheData("cacheDataKey");
        log.info(newCacheData.toString());

        Assertions.assertEquals(cacheData.getValue(), newCacheData.getValue());
        Assertions.assertEquals(cacheData.getExpirationDate(), newCacheData.getExpirationDate());
    }

    /**
     * value를 얻어오는 메소드... RestTemplate, Select, Subscribe 등
     * @return String value
     */
    private String selectValue() {
        return "12345";
    }
}