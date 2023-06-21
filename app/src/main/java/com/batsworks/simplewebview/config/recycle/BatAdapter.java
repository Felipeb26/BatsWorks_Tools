package com.batsworks.simplewebview.config.recycle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.batsworks.simplewebview.R;
import com.batsworks.simplewebview.YoutubeDownload;
import com.batsworks.simplewebview.model.DataToUse;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;

public class BatAdapter extends RecyclerView.Adapter<BatHolder> {

    private final RecyclerListListener recyclerListListener;
    private final Context context;
    private final List<DataToUse> dataList;

    public BatAdapter(Context context, List<DataToUse> dataToUse, RecyclerListListener recyclerListener) {
        this.context = context;
        this.dataList = dataToUse;
        this.recyclerListListener = recyclerListener;
    }

    @NotNull
    @Override
    public BatHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new BatHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BatHolder batHolder, int position) {
        batHolder.urlText.setText(dataList.get(position).getUrl());
        batHolder.mimeType.setText(setOnlyMimeType(dataList.get(position).getMimeType()));
        batHolder.quality.setText(dataList.get(position).getQuality());

        batHolder.qualityLabel.setText(dataList.get(position).getQualityLabel());
        batHolder.contentLength.setText(formatContentLenght(dataList.get(position).getContentLength()));
        batHolder.approxDurationMs.setText(formatDuration(dataList.get(position).getApproxDurationMs()));
        batHolder.size.setText(formatSize(dataList.get(position).getWidth(), dataList.get(position).getHeight()));

        String fps = setFPS(dataList.get(position).getFps());
        batHolder.fps.setText(fps);
        ifNullSetStyle(fps, batHolder.fps);

        String audioQuality = setSpecificAudioQuality(dataList.get(position).getAudioQuality());
        batHolder.audioQuality.setText(audioQuality);
        ifNullSetStyle(audioQuality, batHolder.audioQuality);

        automaticScroll(batHolder.urlText);

        batHolder.cardView.setOnClickListener(click -> {
            Intent intent = new Intent(context, YoutubeDownload.class);
            intent.putExtra("url", dataList.get(batHolder.getAdapterPosition()).getUrl());
            intent.putExtra("size", dataList.get(batHolder.getAdapterPosition()).getContentLength());
            intent.putExtra("mime", dataList.get(batHolder.getAdapterPosition()).getMimeType());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private String setSpecificAudioQuality(String audioQuality) {
        if (audioQuality == null) return MediaType.UNSPECIFIED.media;
        if (audioQuality.trim().equals("")) return MediaType.UNSPECIFIED.media;
        return audioQuality.substring(audioQuality.lastIndexOf("_") + 1);
    }

    private static String setOnlyMimeType(String mimeType) {
        if (mimeType.toLowerCase().contains(MediaType.MP4.media.toLowerCase()))
            return MediaType.MP4.media;
        return MediaType.WEBM.media;
    }

    private static void ifNullSetStyle(String s, TextView textView) {
        if (s == null || s.equals(MediaType.UNSPECIFIED.media)) {
            textView.setTextColor(Color.RED);
        }
    }

    private static String setFPS(Long fps) {
        if (fps != null)
            return String.valueOf(fps);
        return MediaType.UNSPECIFIED.media;
    }

    private static String formatDuration(String time) {
        Duration duration = Duration.ofMillis(Long.parseLong(time));
        String finalTime = duration.toMillis() >= 0 ? duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1:")
                : "-" + duration.toString().substring(3).replaceAll("(\\d[HMS])(?!$)", "$1:");

        return finalTime.substring(0, finalTime.indexOf(".")).concat("s");
    }

    private static String formatSize(Long width, Long height) {
        if (width == null || height == null)
            return null;
        return String.format("%s/%s", width, height);
    }

    private static String formatContentLenght(String size) {
        if (size == null) return MediaType.UNSPECIFIED.media;
        if (size.trim().equals("")) return MediaType.UNSPECIFIED.media;

        double longSize = Double.parseDouble(size);
        if (longSize <= 0) return MediaType.UNSPECIFIED.media;
        final long MEGABYTE = 1024L * 1024L;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double megabytes = longSize / MEGABYTE;
        return decimalFormat.format(megabytes);
    }

    private void automaticScroll(TextView urlText) {
//        urlText.setMovementMethod(new ScrollingMovementMethod());
//        urlText.setHorizontallyScrolling(true);
//        urlText.setSelected(true);
        urlText.setTextColor(0xFc39FF14);
    }

   public enum MediaType {
        UNSPECIFIED("UNSPECIFIED"),
        MP4("MP4"),
        WEBM("MP3");

        private final String media;

        MediaType(String media) {
            this.media = media;
        }

        public String getMedia() {
            return media.toLowerCase();
        }

    }

}
