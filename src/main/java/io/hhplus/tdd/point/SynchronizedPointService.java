package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Primary
@Service
public class SynchronizedPointService extends PointService {

    public SynchronizedPointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        super(userPointTable, pointHistoryTable);
    }

    public UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    synchronized public UserPoint chargePoint(long userId, long amount) {
        return super.chargePoint(userId, amount);
    }

    @Override
    synchronized public UserPoint usePoint(long userId, long amount) {
        return super.usePoint(userId, amount);
    }
}