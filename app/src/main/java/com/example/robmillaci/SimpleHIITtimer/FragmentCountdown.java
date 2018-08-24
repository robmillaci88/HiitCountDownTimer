package com.example.robmillaci.SimpleHIITtimer;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.robmillaci.countdowntimer.R;

import java.util.ArrayList;


public class FragmentCountdown extends Fragment {
    private static Bundle bdl;
    transient private MainActivity context;
    static boolean timerIsRunning;
    static boolean readyToStart = true;
    CountDownTimer myTimer;
    static ObjectAnimator animation;
    final MediaPlayer coutndownSound = MediaPlayer.create(MainActivity.getContext(),R.raw.countdown_beep);
    final MediaPlayer.OnCompletionListener countdownListener =  new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            try {
                mp.reset();
                mp.prepare();
                mp.stop();
                mp.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    public void onPause() {
        Log.d("pause", "onPause: called on pause");
        getCoutndownSound().stop();
     try{
         getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentCountdown.this).commit();
         timerIsRunning = false;
         readyToStart = true;
         myTimer.cancel();
     } catch (Exception e){
         e.printStackTrace();
     }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // MainActivity context = (MainActivity) bdl.getSerializable("Activity");
        //   viewList = (ArrayList<View>)bdl.getSerializable("viewList");



        if (savedInstanceState != null) {
            // myTimer = (CountDownTimer) savedInstanceState.getSerializable("timer");
            context = (MainActivity) savedInstanceState.getSerializable("context");
            // readyToStart = savedInstanceState.getBoolean("readytostart");


        }



        if (myTimer != null) {
            myTimer.cancel();
            myTimer = null;
            timerIsRunning = false;
        }


        return inflater.inflate(R.layout.fragment_fragment_countdown, container, false);
    }


    public static final FragmentCountdown newInstance(int title, String message, MainActivity mainActivity, ArrayList viewList) {
        FragmentCountdown f = new FragmentCountdown();
        f.context = mainActivity;
        bdl = new Bundle(2);
        bdl.putInt("1", title);
        bdl.putString("Insance fragment", message);
        readyToStart = true;
        // bdl.putSerializable("Activity", mainActivity);
        // bdl.putSerializable("viewList",viewList);

        f.setArguments(bdl);




        return f;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //hide views in parent activity
//        context.setViewsVisibility(View.GONE);
        final TextView fragmentTextView = getView().findViewById(R.id.fragmentCountDown);
        final View fragmentLayout = getView().findViewById(R.id.fragmentLayout);
        fragmentLayout.setBackgroundColor(Color.WHITE);




        if (!timerIsRunning) {
            Log.d("***", "onViewCreated: not running");
            // myTimer.cancel();
            myTimer = null;

            myTimer = (CountDownTimer) new CountDownTimer(5500, 100, 1, context) {

                @Override
                public void onTick(long millisUntilFinished) {

                    if ((Math.round((float) millisUntilFinished / 1000.0f)) != secondsLeft) {
                        secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                        Animation countdownAnim = AnimationUtils.loadAnimation(context, R.anim.countdown_anim);
                        timerIsRunning = true;

                        fragmentTextView.setText("" + secondsLeft);
                        fragmentTextView.startAnimation(countdownAnim);

                        coutndownSound.start();
//todo move the sound of of here into Main
                    }
                }


                @Override
                public void onFinish() {
                    Log.d(TAG, "onFinish: run: starting countdown on finish called");

                    timerIsRunning = false;

                    context.onFinishCountDownCallback();
                    try {
                        Log.d(TAG, "onFinish: myTimer is " + myTimer.toString());
                        getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentCountdown.this).commit();
                        try {
                            coutndownSound.reset();
                            coutndownSound.prepare();
                            coutndownSound.stop();
                            coutndownSound.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (NullPointerException e) {
                        Log.d("frag on finish", "onFinish: Null pointer ");
                    }
                }
            }.start();

        } else {

            timerIsRunning = false;
            try {
                coutndownSound.reset();
                coutndownSound.prepare();
                coutndownSound.stop();
                coutndownSound.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentCountdown.this).commit();


        }
        animation = ObjectAnimator.ofInt(context.getCircleRestSeekBar(), "progress", (context.getCircleRestSeekBar().getProgress()), context.getCircleRestSeekBar().getMax());
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

    }

    public void setContext(MainActivity context) {
        this.context = context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("fragment", "onSaveInstanceState: called");
        outState.putSerializable("context", context);
        readyToStart = false;
        outState.putBoolean("readytostart", readyToStart);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    public static boolean isTimerIsRunning() {
        return timerIsRunning;
    }

    public MediaPlayer getCoutndownSound() {
        return coutndownSound;
    }
}