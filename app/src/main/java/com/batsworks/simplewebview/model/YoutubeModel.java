package com.batsworks.simplewebview.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeModel {

    private Long itag;
    private String url;
    private String mimeType;
    private String lastModified;
    private String contentLength;
    private String quality;
    private String audioQuality;
    private String approxDurationMs;
    private String qualityLabel;
    private String fps;
    private String expiresInSeconds;

}
