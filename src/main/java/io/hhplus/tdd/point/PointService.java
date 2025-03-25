package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;

    public UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }
}
