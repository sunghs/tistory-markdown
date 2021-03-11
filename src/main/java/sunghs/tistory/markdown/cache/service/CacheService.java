package sunghs.tistory.markdown.cache.service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sunghs.tistory.markdown.cache.model.CacheData;

@Service
@Slf4j
public class CacheService {

    private static final CacheData EMPTY_DATA = new CacheData();

    @Cacheable(cacheNames = "exampleStore", key = "#key")
    public CacheData getCacheData(final String key) {
        log.info("이 로그는 캐시가 없는 경우 찍힙니다.");
        return EMPTY_DATA;
    }

    @CachePut(cacheNames = "exampleStore", key = "#key")
    public CacheData updateCacheData(final String key, final String value) {
        log.info("이 로그는 캐시가 업데이트 되는 경우 찍힙니다.");
        CacheData cacheData = new CacheData();
        cacheData.setValue(value);
        cacheData.setExpirationDate(LocalDateTime.now().plusDays(1));
        return cacheData;
    }

    @CacheEvict(cacheNames = "exampleStore", key = "#key")
    public boolean expireCacheData(final String key) {
        log.info("이 로그는 캐시를 지울 경우 찍힙니다.");
        return true;
    }

    public boolean isValidation(final CacheData cacheData) {
        return ObjectUtils.isNotEmpty(cacheData)
            && ObjectUtils.isNotEmpty(cacheData.getExpirationDate())
            && StringUtils.isNotEmpty(cacheData.getValue())
            && cacheData.getExpirationDate().isAfter(LocalDateTime.now());
    }
}