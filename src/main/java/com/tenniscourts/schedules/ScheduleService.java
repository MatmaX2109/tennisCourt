package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.reservations.*;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import com.tenniscourts.tenniscourts.TennisCourtService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.tenniscourts.reservations.ReservationStatus.READY_TO_PLAY;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final ReservationMapper reservationMapper;

    private final ReservationRepository reservationRepository;

    private final TennisCourtService tennisCourtService;

    public ScheduleDTO addSchedule(CreateScheduleRequestDTO createScheduleRequestDTO) {

        TennisCourtDTO tennisCourt = tennisCourtService.findTennisCourtById(createScheduleRequestDTO.getTennisCourtId());

        if(findByTennisCourt_IdAndStartDateTime(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO.getStartDateTime()).size()>0){
            throw new AlreadyExistsEntityException(
                    String.format("Schedule already created for tennis court %s at %s ",tennisCourt.getName(), createScheduleRequestDTO.getStartDateTime()));
        }

        ScheduleDTO schedule = ScheduleDTO.builder()
                .tennisCourt(tennisCourt)
                .tennisCourtId(createScheduleRequestDTO.getTennisCourtId())
                .startDateTime(createScheduleRequestDTO.getStartDateTime())
                .endDateTime(createScheduleRequestDTO.getStartDateTime().plusHours(1))
                .build();

        return scheduleMapper.map(scheduleRepository.saveAndFlush(scheduleMapper.map(schedule)));
    }


    public List<ScheduleDTO> findSchedulesByDates(FindScheduleRequestDTO findScheduleRequestDTO) {

        LocalDateTime startDate = LocalDateTime.of(findScheduleRequestDTO.getStartDate(), LocalTime.of(0, 0));
        LocalDateTime endDate = LocalDateTime.of(findScheduleRequestDTO.getEndDate(), LocalTime.of(23, 59));

        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("The end date is starting before the start date");
        }
        return scheduleMapper.map(scheduleRepository.findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDate, endDate));
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(scheduleMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Guest not found.");
        });
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    public List<ScheduleDTO> findAvailableSchedules(ScheduleDate reservationDate) {
        LocalDateTime startDateTime = LocalDateTime.of(reservationDate.getDate(), LocalTime.of(0, 0));
        LocalDateTime endDateTime = LocalDateTime.of(reservationDate.getDate(), LocalTime.of(23, 59));

        List<ScheduleDTO> scheduleDTOS = scheduleMapper.map(scheduleRepository.findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDateTime, endDateTime));
        if(scheduleDTOS == null || scheduleDTOS.isEmpty()){
            return scheduleDTOS;
        }

        List<ReservationDTO> reservationDTOS_readyToPlay = reservationMapper.map(reservationRepository.findByReservationStatusAndSchedule_StartDateTimeGreaterThanEqualAndSchedule_EndDateTimeLessThanEqual(ReservationStatus.READY_TO_PLAY, startDateTime, endDateTime));

        return scheduleDTOS.stream()
                .filter(s -> !containsSchedule(s, reservationDTOS_readyToPlay))
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> findByTennisCourt_IdAndStartDateTime(final Long tennisCourtId, final LocalDateTime startDateTime) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdAndStartDateTime(tennisCourtId, startDateTime));
    }

    private boolean containsSchedule(final ScheduleDTO scheduleDTO, final List<ReservationDTO> reservationDTOS_readyToPlay){
        return reservationDTOS_readyToPlay.stream().anyMatch(o -> o.getSchedule().equals(scheduleDTO));
    }
}
