package com.tenniscourts.reservations;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReservationHistoryRequestDTO {

    @JsonFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(example = "2022-02-10")
    private LocalDate startDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(example = "2022-02-30")
    private LocalDate endDate;

    @ApiModelProperty(example = "READY_TO_PLAY")
    private ReservationStatus reservationStatus;
}
