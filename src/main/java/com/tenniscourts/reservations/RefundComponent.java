package com.tenniscourts.reservations;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class RefundComponent {

    private BigDecimal getRefundValue(final Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        }

        if (hours >= 12 && hours <= 23) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.75));
        }

        if (hours >= 2 && hours <= 11) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.50));
        }

        if (hours >= 0 && hours < 2) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.25));
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getRefundValueByReservationStatus(final Reservation reservation, final ReservationStatus newReservationStatus) {
        switch (newReservationStatus){
            case NO_SHOW: return new BigDecimal(0);
            case COMPLETED: return reservation.getValue();
            default: return getRefundValue(reservation);
        }
    }
}
