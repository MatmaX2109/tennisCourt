package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RequestMapping("guest")
@RestController
@Api(tags="Guest")
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @PostMapping
    @ApiOperation(value = "Add guest")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Guest successfully added") })
    public ResponseEntity<Void> add(@RequestBody @Valid CreateGuestDTO guestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.addGuest(guestDTO).getId())).build();
    }

    @PutMapping
    @ApiOperation(value = "Update guest")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Guest successfully updated") })
    public ResponseEntity<GuestDTO> update(@RequestBody @Valid GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.updateGuest(guestDTO));
    }

    @DeleteMapping("/{guestId}")
    @ApiOperation(value = "Delete guest by id")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Guest successfully deleted") })
    public ResponseEntity<String> delete(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.ok("Guest deleted.");
    }

    @GetMapping("/{guestId}")
    @ApiOperation(value = "Find guest by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Guest founded") })
    public ResponseEntity<GuestDTO> findById(@PathVariable Long guestId) {
        return ResponseEntity.ok(guestService.findGuestById(guestId));
    }

    @GetMapping("/search")
    @ApiOperation(value = "Find guests by name")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Guest founded") })
    public ResponseEntity<List<GuestDTO>> findByName(@RequestParam(value = "name", required = true) String name) {
        return ResponseEntity.ok(guestService.findGuestByName(name));
    }

    @GetMapping("/all")
    @ApiOperation(value = "Find all guests")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Guests founded") })
    public ResponseEntity<List<GuestDTO>> findAll() {
        return ResponseEntity.ok(guestService.findGuests());
    }
}