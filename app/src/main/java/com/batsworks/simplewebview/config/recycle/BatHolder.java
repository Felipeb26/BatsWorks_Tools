package com.batsworks.simplewebview.config.recycle;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.batsworks.simplewebview.R;
import org.jetbrains.annotations.NotNull;

public class BatHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView urlText, mimeType, quality, audioQuality, fps, qualityLabel, contentLength, approxDurationMs, size;

    public BatHolder(@NonNull @NotNull View view) {
        super(view);
        cardView = view.findViewById(R.id.card_item);
        urlText = view.findViewById(R.id.url);
        mimeType = view.findViewById(R.id.mime_type);
        quality = view.findViewById(R.id.quality);
        audioQuality = view.findViewById(R.id.audio_quality);
        fps = view.findViewById(R.id.fps);
        qualityLabel = view.findViewById(R.id.quality_label);
        contentLength = view.findViewById(R.id.content_length);
        approxDurationMs = view.findViewById(R.id.approx_duration);
        size = view.findViewById(R.id.size);
    }

}
