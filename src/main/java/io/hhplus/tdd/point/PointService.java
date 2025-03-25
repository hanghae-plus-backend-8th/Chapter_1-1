package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final PointManager pointManager = PointManager.getInstance();

    public UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);
        long chargedPoint = pointManager.chargePoint(userPoint.point(), amount);

        userPoint = userPointTable.insertOrUpdate(userId, chargedPoint);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, userPoint.updateMillis());

        return userPoint;
    }

    public UserPoint usePoint(long userId, long amount) {
        return null;
    }
}
