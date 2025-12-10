package io.kovin.dispatch.management.system.model.entity;

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

    private String destinationNote;
    private String note;
    private Double revenue;
    private Double miles;
    private String broker;
}
