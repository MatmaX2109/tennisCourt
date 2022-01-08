package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequestMapping("/schedule")
@RestController
@Api(tags="Scheduler")
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @PostMapping
    @ApiOperation("Add schedule to a tennis court")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Schedules slots created for a given tennis court") })
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody @Valid CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO).getId())).build();
    }

    @GetMapping("/{scheduleId}")
    @ApiOperation("Find a reservation by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Found schedule") })
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }

    @GetMapping("/bydate")
    @ApiOperation("Find a tennis court by date")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Schedule found"),
            @ApiResponse(code = 404, message = "Schedule not found")
    })
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(FindScheduleRequestDTO findScheduleRequestDTO) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(findScheduleRequestDTO));
    }

    @GetMapping("/availableSchedules")
    @ApiOperation("Find schedules available for a specific date")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Schedule found"),
            @ApiResponse(code = 404, message = "Schedule not found")
    })
    public List<ScheduleDTO> findAvailableSchedules(ScheduleDate scheduleDate){
        return scheduleService.findAvailableSchedules(scheduleDate);
    }

}
