package com.batsworks.simplewebview.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorInfo {
    private String primaries;
    private String transferCharacteristics;
    private String matrixCoefficients;
}
