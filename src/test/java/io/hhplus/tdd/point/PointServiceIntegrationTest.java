package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointServiceIntegrationTest {

    @SpyBean
    private UserPointTable userPointTable;

    @SpyBean
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private PointService pointService;

    @DisplayName("유저는 포인트를 조회할 수 있다.")
    @Test
    void getPoint() {
        // given
        long userId = 1L;

        // when
        UserPoint userPoint = userPointTable.selectById(userId);

        // then
        assertThat(userPoint).isNotNull()
                .extracting("point")
                .isEqualTo(0L);
    }

    @DisplayName("유저는 포인트 충전/이용 내역 조회를 할 수 있다.")
    @Test
    void getPointHistories() {
        // given
        long userId = 1L;

        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);

        // then
        assertThat(pointHistories).isNotNull()
                .isEmpty();
    }

    @DisplayName("포인트 충전 시, 충전 내역이 저장된다.")
    @Test
    void saveChargedPointHistory() {
        // given
        long userId = 1L;
        long amount = 50_000L;

        // when
        UserPoint chargedPoint = pointService.chargePoint(userId, amount);
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);
        PointHistory pointHistory = pointHistories.get(pointHistories.size() - 1);

        // then
        verify(userPointTable, Mockito.times(1)).insertOrUpdate(userId, chargedPoint.point());
        verify(pointHistoryTable, Mockito.times(1)).insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());
    }

    @DisplayName("포인트 사용 시, 이용 내역이 저장된다.")
    @Test
    void saveUsePointHistory() {
        // given
        long userId = 1L;
        long chargeAmount = 50_000L;
        long useAmount = 30_000L;

        // when
        pointService.chargePoint(userId, chargeAmount);
        pointService.usePoint(userId, useAmount);
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);
        PointHistory pointHistory = pointHistories.get(pointHistories.size() - 1);

        // then
        verify(userPointTable, Mockito.times(1)).insertOrUpdate(userId, chargeAmount - useAmount);
        verify(pointHistoryTable, Mockito.times(1)).insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());
    }

    @DisplayName("같은 유저가 동시에 여러 번 포인트를 사용할 수 없다.")
    @Test
    void deductPointsConcurrently() throws InterruptedException {
        // given
        long userId = 1L;
        long amount = 50_000L;
        int repeatCount = 20;
        CountDownLatch countDownLatch = new CountDownLatch(repeatCount);

        // when
        pointService.chargePoint(userId, amount * repeatCount);

        for (int i = 0; i < repeatCount; i++) {
            new Thread(() -> {
                pointService.usePoint(userId, amount);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        UserPoint userPoint = pointService.getPoint(userId);

        // then
        assertThat(userPoint.point()).isZero();
    }

    @DisplayName("같은 유저가 동시에 여러 번 포인트를 충전할 수 없다.")
    @Test
    void chargePointsConcurrently() throws InterruptedException {
        // given
        long userId = 1L;
        long amount = 50_000L;
        int repeatCount = 20;
        CountDownLatch countDownLatch = new CountDownLatch(repeatCount);

        // when
        for (int i = 0; i < repeatCount; i++) {
            new Thread(() -> {
                pointService.chargePoint(userId, amount);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        UserPoint userPoint = pointService.getPoint(userId);

        // then
        assertThat(userPoint.point()).isEqualTo(amount * repeatCount);
    }
}