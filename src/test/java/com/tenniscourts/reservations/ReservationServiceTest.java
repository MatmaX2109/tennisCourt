package com.tenniscourts.reservations;

import com.tenniscourts.schedules.Schedule;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = RefundComponent.class)
public class ReservationServiceTest {

    private static BigDecimal DEPOSIT  = new BigDecimal(10L);

    @InjectMocks
    RefundComponent refundComponent;

    @Test
    public void getRefundValueFullRefund() {
        final BigDecimal expected = DEPOSIT;

        Schedule schedule = new Schedule();
        schedule.setStartDateTime(LocalDateTime.now().plusDays(2));

        BigDecimal resp = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.COMPLETED);
        Assert.assertEquals(resp, expected);
    }

    @Test
    public void getRefundValue0Refund() {

        final BigDecimal expected = BigDecimal.valueOf(0);

        Schedule schedule = new Schedule();
        schedule.setStartDateTime(LocalDateTime.now().plusDays(2));

        BigDecimal resp = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.NO_SHOW);
        Assert.assertEquals(resp, expected);
    }

    @Test
    public void getRefundValue25Refund() {

        final BigDecimal expected = (DEPOSIT).multiply(BigDecimal.valueOf(0.25));

        Schedule schedule = new Schedule();
        schedule.setStartDateTime(LocalDateTime.now().plusHours(1));

        BigDecimal respCancelled = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.CANCELLED);
        Assert.assertEquals(respCancelled, expected);
        BigDecimal respRescheduled = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.RESCHEDULED);
        Assert.assertEquals(respRescheduled, expected);
    }

    @Test
    public void getRefundValue50Refund() {

        final BigDecimal expected = (DEPOSIT).multiply(BigDecimal.valueOf(0.50));

        Schedule schedule = new Schedule();
        schedule.setStartDateTime(LocalDateTime.now().plusHours(3));

        BigDecimal respCancelled = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.CANCELLED);
        Assert.assertEquals(respCancelled, expected);
        BigDecimal respRescheduled = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.RESCHEDULED);
        Assert.assertEquals(respRescheduled, expected);
    }

    @Test
    public void getRefundValue75Refund() {

        final BigDecimal expected = (DEPOSIT).multiply(BigDecimal.valueOf(0.75));

        Schedule schedule = new Schedule();
        schedule.setStartDateTime(LocalDateTime.now().plusHours(13));

        BigDecimal respCancelled = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.CANCELLED);
        Assert.assertEquals(respCancelled, expected);
        BigDecimal respRescheduled = refundComponent.getRefundValueByReservationStatus(initReservation(schedule, ReservationStatus.READY_TO_PLAY), ReservationStatus.RESCHEDULED);
        Assert.assertEquals(respRescheduled, expected);
    }

    private Reservation initReservation(Schedule schedule, ReservationStatus reservationStatus){
        return Reservation.builder().
                schedule(schedule)
                .value(DEPOSIT)
                .reservationStatus(reservationStatus)
                .build();
    }
}