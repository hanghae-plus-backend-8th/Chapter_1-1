package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
//@Primary
@Service
public class ReentrantLockPointService extends PointService {

    private final Map<Long, ReentrantLock> keyMap = new ConcurrentHashMap<>();

    public ReentrantLockPointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        super(userPointTable, pointHistoryTable);
    }

    public UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    public UserPoint chargePoint(long userId, long amount) {
        ReentrantLock lock = keyMap.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();

        try {
            return super.chargePoint(userId, amount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public UserPoint usePoint(long userId, long amount) {
        ReentrantLock lock = keyMap.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();

        try {
            return super.usePoint(userId, amount);
        } finally {
            lock.unlock();
        }
    }
}