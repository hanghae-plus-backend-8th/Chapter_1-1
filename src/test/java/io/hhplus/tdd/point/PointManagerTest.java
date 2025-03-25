package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PointManagerTest {

    private PointManager pointManager = PointManager.getInstance();

    @DisplayName("유저는 포인트를 충전할 수 있다.")
    @Test
    void chargePoint() {
        // given
        long userPoint = 0L;
        long amount = 2000L;

        // when
        long chargedPoint = pointManager.chargePoint(userPoint, amount);

        // then
        assertThat(chargedPoint).isEqualTo(amount);
    }

    @DisplayName("유저는 최대 한도를 넘어서 포인트를 충전할 수 없다.")
    @Test
    void canNotChargePointToMax() {
        // given
        long userPoint = 0L;
        long amount = 2_000_000L;

        // when, then
        assertThatThrownBy(() -> pointManager.chargePoint(userPoint, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 한도 초과");
    }

    @DisplayName("유저는 포인트를 사용할 수 있다.")
    @Test
    void deductPoint() {
        // given
        long userPoint = 2_000L;
        long amount = 2_000L;

        // when
        long deductedPoint = pointManager.deductPoint(userPoint, amount);

        // then
        assertThat(deductedPoint).isZero();
    }

    @DisplayName("유저는 본인의 잔고 내에서만 포인트를 사용할 수 있다.")
    @Test
    void canNotDeductPointToMinus() {
        // given
        long userPoint = 2_000L;
        long amount = 3_000L;

        // when, then
        assertThatThrownBy(() -> pointManager.deductPoint(userPoint, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔고가 부족합니다.");
    }
}