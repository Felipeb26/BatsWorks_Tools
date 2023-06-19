package com.batsworks.simplewebview.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataToUse {
    private String url;
    private String mimeType;
    private String quality;
    private Long fps;
    private String qualityLabel;
    private String contentLength;
    private String approxDurationMs;
    private String audioQuality;
    private Long width;
    private Long height;
}
