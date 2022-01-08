package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RequestMapping("/reservation")
@RestController
@Api(tags="Reservation")
public class ReservationController extends BaseRestController {

    private final ReservationService reservationService;

    @PostMapping
    @ApiOperation("Book a reservation")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Tennis court successfully booked") })
    public ResponseEntity<Void> bookReservation(@RequestBody @Valid CreateReservationRequestDTO createReservationRequestDTO) {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @GetMapping("/{reservationId}")
    @ApiOperation("Find a reservation by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Found reservation") })
    public ResponseEntity<ReservationDTO> findReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }

    @DeleteMapping("/{reservationId}")
    @ApiOperation("Cancel a reservation by id")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Reservation canceled successfully") })
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @PutMapping("/reschedule")
    @ApiOperation("Reschedule a reservation")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Reservation reschedule successfully") })
    public ResponseEntity<ReservationDTO> rescheduleReservation(@RequestBody @Valid RescheduleReservationRequestDTO rescheduleReservationRequestDTO) {
        return ResponseEntity.ok(reservationService.rescheduleReservation(rescheduleReservationRequestDTO));
    }
    @PutMapping("/completed")
    @ApiOperation("Reservation completed")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Refund Successful")
    })
    public ResponseEntity<ReservationDTO> reservationCompleted(@RequestParam(value = "reservationId", required = true) Long reservationId) {
        return ResponseEntity.ok(reservationService.reservationCompleted(reservationId));
    }

    @PutMapping("/noShow")
    @ApiOperation("No show on reservation")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Reservation updated")
    })
    public ResponseEntity<ReservationDTO> reservationNoShow(@RequestParam(value = "reservationId", required = true)Long reservationId) {
        return ResponseEntity.ok(reservationService.reservationNoShow(reservationId));
    }

    @GetMapping("/history")
    @ApiOperation("Find reservations history")
    @ApiResponses(value = {
            @ApiResponse(code = 200 , message = "Reservations Found"),
            @ApiResponse(code = 404 , message = "No Reservations Found")
    })
    public ResponseEntity<List<ReservationDTO>> findReservationsHistory(@RequestBody ReservationHistoryRequestDTO reservationHistoryRequestDTO) {
        return ResponseEntity.ok(reservationService.findReservationsHistory(reservationHistoryRequestDTO));
    }
}
