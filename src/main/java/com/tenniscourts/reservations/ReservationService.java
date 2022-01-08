package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    private final GuestRepository guestRepository;

    private final ScheduleRepository scheduleRepository;

    private final RefundComponent refundComponent;

    private static BigDecimal DEPOSIT  = new BigDecimal(10L);

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {

        Guest guest = guestRepository.findById(createReservationRequestDTO.getGuestId()).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
        });

        Schedule schedule = scheduleRepository.findById(createReservationRequestDTO.getScheduleId()).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Schedule not found.");
        });

        List<ReservationDTO> reservationDTOS = reservationMapper.map(reservationRepository.findBySchedule_Id(createReservationRequestDTO.getScheduleId()));
        reservationDTOS.forEach(reservation -> {
            if (ReservationStatus.READY_TO_PLAY.toString().equals(reservation.getReservationStatus())) {
                throw new AlreadyExistsEntityException(String.format("%s tennis court already reserved at %s ",
                        schedule.getTennisCourt().getName(), schedule.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))));
            }
        });

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .schedule(schedule)
                .value(DEPOSIT)
                .reservationStatus(ReservationStatus.READY_TO_PLAY)
                .build();

        return reservationMapper.map(reservationRepository.saveAndFlush(reservation));

    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    //todo mat -> de testat
    private Reservation cancel(Long reservationId) {
        return updateReservation(reservationId, ReservationStatus.CANCELLED);
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateReadyToPlay(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public ReservationDTO rescheduleReservation(RescheduleReservationRequestDTO rescheduleReservationRequestDTO) {
        Reservation previousReservation = cancel(rescheduleReservationRequestDTO.getPreviousReservationId());

        if (rescheduleReservationRequestDTO.getNewScheduleId().equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(rescheduleReservationRequestDTO.getNewScheduleId())
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }

    public ReservationDTO reservationCompleted(Long reservationId) {
        return reservationMapper.map(updateReservation(reservationId, ReservationStatus.COMPLETED));

    }
    public ReservationDTO reservationNoShow(Long reservationId) {
        return reservationMapper.map(updateReservation(reservationId, ReservationStatus.NO_SHOW));
    }

    private Reservation updateReservation(Long reservationId, ReservationStatus reservationStatus) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateReadyToPlay(reservation);

            BigDecimal refundValue = refundComponent.getRefundValueByReservationStatus(reservation, reservationStatus);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

//    private BigDecimal getRefundValueByReservationStatus(Reservation reservation, ReservationStatus newReservationStatus) {
//        switch (newReservationStatus){
//            case NO_SHOW: return new BigDecimal(0);
//            case COMPLETED: return reservation.getValue();
//            default: return getRefundValue(reservation);
//        }
//    }

    public List<ReservationDTO> findReservationsHistory(ReservationHistoryRequestDTO reservationHistoryRequestDTO){
        LocalDateTime startDateTime = reservationHistoryRequestDTO.getStartDate()==null?null:LocalDateTime.of(reservationHistoryRequestDTO.getStartDate(), LocalTime.of(0, 0));
        LocalDateTime endDateTime = reservationHistoryRequestDTO.getEndDate()==null?null:LocalDateTime.of(reservationHistoryRequestDTO.getEndDate(), LocalTime.of(23, 59));

        return reservationMapper.map(reservationRepository.findByReservationStatusAndSchedule_StartDateTimeGreaterThanEqualAndSchedule_EndDateTimeLessThanEqualOptional(reservationHistoryRequestDTO.getReservationStatus(), startDateTime, endDateTime));
    }

    }
