package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class PointService {

    protected final UserPointTable userPointTable;
    protected final PointHistoryTable pointHistoryTable;
    protected final PointManager pointManager = PointManager.getInstance();

    UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    List<PointHistory> getPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    UserPoint chargePoint(long userId, long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);
        long chargedPoint = pointManager.chargePoint(userPoint.point(), amount);

        userPoint = userPointTable.insertOrUpdate(userId, chargedPoint);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, userPoint.updateMillis());

        return userPoint;
    }

    UserPoint usePoint(long userId, long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);
        long deductedPoint = pointManager.deductPoint(userPoint.point(), amount);

        userPoint = userPointTable.insertOrUpdate(userId, deductedPoint);
        pointHistoryTable.insert(userId, amount, TransactionType.USE, userPoint.updateMillis());

        return userPoint;
    }
}