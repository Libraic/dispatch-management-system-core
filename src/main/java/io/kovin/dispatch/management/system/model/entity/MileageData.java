package io.kovin.dispatch.management.system.model.entity;

import java.math.BigDecimal;
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
}
