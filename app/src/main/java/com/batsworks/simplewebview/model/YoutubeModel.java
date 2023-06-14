package com.batsworks.simplewebview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeModel {

    private @JsonProperty String expiresInSeconds;
    private @JsonProperty List<FormatsModel> formats;
    private @JsonProperty List<AdaptiveFormatsModel> adaptiveFormats;
    private @JsonProperty String probeUrl;
}
