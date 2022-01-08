package com.tenniscourts.schedules;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class FindScheduleRequestDTO {

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(required = true, example = "2022-02-22")
    private LocalDate startDate;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(required = true, example = "2022-02-21")
    private LocalDate endDate;

}
