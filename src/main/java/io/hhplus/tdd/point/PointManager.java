package io.hhplus.tdd.point;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointManager {

    private static final PointManager pointManager = new PointManager();
    private final long MAX_POINT = 1_000_000L;

    public static PointManager getInstance() {
        return pointManager;
    }

    public long chargePoint(long point, long amount) {
        long chargedPoint = point + amount;
        if (chargedPoint > MAX_POINT) {
            throw new IllegalArgumentException("포인트 한도 초과");
        }
        return chargedPoint;
    }

    public long deductPoint(long point, long amount) {
        long deductedPoint = point - amount;
        if (deductedPoint < 0) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }
        return deductedPoint;
    }
}
