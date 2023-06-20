package com.batsworks.simplewebview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.batsworks.simplewebview.config.notification.Snack;
import com.batsworks.simplewebview.config.recycle.BatAdapter;
import com.batsworks.simplewebview.model.AdaptiveFormatsModel;
import com.batsworks.simplewebview.model.DataToUse;
import com.batsworks.simplewebview.model.FormatsModel;
import com.batsworks.simplewebview.model.YoutubeModel;
import com.batsworks.simplewebview.services.RecyclerListListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DownLoadOption extends AppCompatActivity implements RecyclerListListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private YoutubeModel youtubeModel;
    private List<DataToUse> dataToUseList;
    private RecyclerView recyclerView;
    private BatAdapter adapter;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_option);
        String youtube = getIntent().getStringExtra("class");
        view = findViewById(android.R.id.content);
        initComponents();

        configRecycleView(youtube);
    }

    private void initComponents() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        recyclerView = findViewById(R.id.recyclerview);
    }

    @SuppressLint("CheckResult")
    private void configRecycleView(String youtube) {
        dataToUseList = new ArrayList<>();
        adapter = new BatAdapter(DownLoadOption.this, dataToUseList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(DownLoadOption.this));
        recyclerView.setAdapter(adapter);

        Observable<List<DataToUse>> observable = Observable.just(dataToUseList);
        observable.subscribe(sub -> youtubeToData(youtube),
                error -> {
                    Snack.bar(view, error.getMessage());
                },
                () -> {
                    adapter = new BatAdapter(DownLoadOption.this, dataToUseList, this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
        );
    }

    private void youtubeToData(String youtube) throws JsonProcessingException {
        youtubeModel = mapper.readValue(youtube, YoutubeModel.class);
        dataToUseList = convertYoutubeDataToImportant(youtubeModel);
        dataToUseList = dataToUseList.stream().sorted(Comparator.comparing(DataToUse::getMimeType)).distinct().collect(Collectors.toList());
    }

    private List<DataToUse> convertYoutubeDataToImportant(YoutubeModel youtubeModel) {
        List<DataToUse> dataToUseList = new ArrayList<>();
        for (FormatsModel format : youtubeModel.getFormats()) {
            DataToUse dataToUse = DataToUse.builder()
                    .url(format.getUrl())
                    .mimeType(format.getMimeType())
                    .quality(format.getQuality())
                    .fps(format.getFps())
                    .qualityLabel(format.getQualityLabel())
                    .contentLength(format.getContentLength())
                    .approxDurationMs(format.getApproxDurationMs())
                    .audioQuality(format.getAudioQuality())
                    .width(format.getWidth())
                    .height(format.getHeight())
                    .build();
            dataToUseList.add(dataToUse);
        }
        for (AdaptiveFormatsModel adaptive : youtubeModel.getAdaptiveFormats()) {
            DataToUse dataToUse = DataToUse.builder()
                    .url(adaptive.getUrl())
                    .mimeType(adaptive.getMimeType())
                    .quality(adaptive.getQuality())
                    .fps(adaptive.getFps())
                    .qualityLabel(adaptive.getQualityLabel())
                    .contentLength(adaptive.getContentLength())
                    .approxDurationMs(adaptive.getApproxDurationMs())
                    .audioQuality(adaptive.getAudioQuality())
                    .width(adaptive.getWidth())
                    .height(adaptive.getHeight())
                    .build();
            dataToUseList.add(dataToUse);
        }
        return dataToUseList;
    }


    @Override
    public void onItemClick(int position) {

    }
}