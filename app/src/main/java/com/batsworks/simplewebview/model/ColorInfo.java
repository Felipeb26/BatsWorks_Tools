package com.batsworks.simplewebview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorInfo {
    @JsonProperty private String primaries;
    @JsonProperty private String transferCharacteristics;
    @JsonProperty private String matrixCoefficients;
}
