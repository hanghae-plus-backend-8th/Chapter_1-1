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
    void chargePositivePoint() {
        // given
        long userPoint = 0L;
        long amount = 2000L;

        // when
        long chargedPoint = pointManager.chargePoint(userPoint, amount);

        // then
        assertThat(chargedPoint).isEqualTo(amount);
    }

    @DisplayName("충전할 포인트는 0 이하여서는 안된다.")
    @Test
    void chargeNegativePoint() {
        // given
        long userPoint = 0L;
        long amount = -2000L;

        // when, then
        assertThatThrownBy(() -> pointManager.chargePoint(userPoint, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전할 포인트는 0 이상이어야 합니다.");
    }

    @DisplayName("유저는 최대 한도를 넘어서 포인트를 충전할 수 없다.")
    @Test
    void chargePointToMax() {
        // given
        long userPoint = 0L;
        long amount = 2_000_000L;

        // when, then
        assertThatThrownBy(() -> pointManager.chargePoint(userPoint, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 한도 초과");
    }
  
}