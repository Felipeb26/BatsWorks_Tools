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
public class FormatsModel {

    @JsonProperty private Long itag;
    @JsonProperty private String url;
    @JsonProperty private String mimeType;
    @JsonProperty private Long bitrate;
    @JsonProperty private Long width;
    @JsonProperty private Long height;
    @JsonProperty private String lastModified;
    @JsonProperty private String contentLength;
    @JsonProperty private String quality;
    @JsonProperty private Long fps;
    @JsonProperty private String qualityLabel;
    @JsonProperty private String projectionType;
    @JsonProperty private String audioQuality;
    @JsonProperty private String approxDurationMs;
    @JsonProperty private String audioSampleRate;
    @JsonProperty private Long audioChannels;
}
