package io.kovin.dispatch.management.system.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.kovin.dispatch.management.system.model.entity.enums.LoadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MileageData {

    private BigDecimal revenue;
    private BigDecimal miles;
    private String broker;
    private String representative;
    private String pickUpLocation;
    private String deliveryLocation;
    private LocalDate pickUpDate;
    private LocalDate deliveryDate;
    private LoadStatus loadStatus;
}
