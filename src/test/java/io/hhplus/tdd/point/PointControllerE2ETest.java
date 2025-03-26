package io.hhplus.tdd.point;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PointControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    private final long userId = 1L;
    private final long amount = 10_000L;

    @DisplayName("포인트 조회 E2E 테스트")
    @Order(1)
    @Test
    void getPoint() throws Exception {
        // when, then
        mockMvc.perform(
                    get("/point/" + userId)
                )
               .andExpect(status().isOk());
    }

    @DisplayName("포인트 충전/이용 내역 조회 E2E 테스트")
    @Order(2)
    @Test
    void getPointHistories() throws Exception {
        // when, then
        mockMvc.perform(
                        get("/point/" + userId + "/histories")
                )
                .andExpect(status().isOk());
    }

    @DisplayName("포인트 충전 E2E 테스트")
    @Order(3)
    @Test
    void chargePoint() throws Exception {
        // when, then
        mockMvc.perform(
                        patch("/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(String.valueOf(amount))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(amount));
    }

    @DisplayName("포인트 사용 E2E 테스트")
    @Order(4)
    @Test
    void usePoint() throws Exception {
        // when, then
        mockMvc.perform(
                        patch("/point/" + userId + "/use")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(String.valueOf(amount))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(0L));
    }
}