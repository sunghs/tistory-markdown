package sunghs.tistory.markdown;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Recusrive {

    static Map<Integer, Integer> map = new HashMap<>();

    static int fibCnt = 0;

    static int memFibCnt = 0;

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now();
        fib(25);
        log.info("running time : {} nano", ChronoUnit.NANOS.between(start, LocalDateTime.now()));

        start = LocalDateTime.now();
        memFib(25);
        log.info("running time : {} nano", ChronoUnit.NANOS.between(start, LocalDateTime.now()));
    }

    static int fib(int n) {
        log.info("fib의 호출횟수 : {}", ++fibCnt);

        if (n <= 3) {
            return Math.max(n, 0);
        } else {
            return fib(n - 1) + fib(n - 2);
        }
    }

    static int memFib(int n) {
        log.info("memFib의 호출횟수 : {}", ++memFibCnt);

        if (n <= 3) {
            return Math.max(n, 0);
        } else {
            Integer i1 = map.get(n - 1);
            Integer i2 = map.get(n - 2);
            if (i1 == null) {
                i1 = memFib(n - 1);
                map.put(n - 1, i1);
            }
            if (i2 == null) {
                i2 = memFib(n - 2);
                map.put(n - 2, i2);
            }
            return i1 + i2;
        }
    }
}
