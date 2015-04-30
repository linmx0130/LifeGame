package com.sweetdum.lifegame;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

public class MainFragment extends Fragment {
    private Button startBtn,nextBtn;
    private BoardView boardView;
    private SeekBar speedSeek;
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_main, container, false);
        startBtn= (Button)v.findViewById(R.id.start_btn);
        nextBtn=(Button) v.findViewById(R.id.next_btn);
        boardView = (BoardView)v.findViewById(R.id.board_view);
        speedSeek= (SeekBar) v.findViewById(R.id.speed_seek);
        speedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int time=1000-(progress*50);
                boardView.setWaitTime(time);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boardView.isAutoGen()){
                    boardView.stopAutoGen();
                    startBtn.setText(R.string.start_btn_title);
                    nextBtn.setEnabled(true);
                    speedSeek.setEnabled(true);
                }else {
                    boardView.startAutoGen();
                    startBtn.setText(R.string.stop_btn_title);
                    nextBtn.setEnabled(false);
                    speedSeek.setEnabled(false);
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.nextGeneration();
            }
        });
        return v;
    }
}
