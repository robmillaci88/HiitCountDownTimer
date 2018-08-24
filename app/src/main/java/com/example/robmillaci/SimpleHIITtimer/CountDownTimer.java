package com.example.robmillaci.SimpleHIITtimer;

import android.media.MediaPlayer;
import android.util.Log;

import com.example.robmillaci.countdowntimer.R;

import java.io.Serializable;
import java.util.logging.Handler;


public class CountDownTimer extends android.os.CountDownTimer implements Serializable {
    public static final String TAG = "CountDownTimer";

    String timerTag;
    transient MainActivity context;
    static int countedRounds = 1;
    int totalRounds = 0;
    int flag;

    long millisRemaining;
   transient MediaPlayer.OnCompletionListener listener;
   transient MediaPlayer sound;

    public CountDownTimer(long millisInFuture, long countDownInterval, int totalRounds, MainActivity context) {
        super(millisInFuture, countDownInterval);
        this.timerTag = "CountDownTimer";
        this.context = context;
        this.totalRounds = totalRounds;

        listener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    mp.reset();
                    mp.stop();
                    mp.release();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    int secondsLeft = 0;
    @Override
    public void onTick(long millisUntilFinished) {
        Log.d(TAG, "onTick: TICKING");
        millisRemaining = millisUntilFinished;


        if (Math.round((float)millisUntilFinished / 1000.0f) != secondsLeft) {
            secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
            long mins = secondsLeft/60;
            secondsLeft = secondsLeft%60;

            if (mins < 10 && secondsLeft < 10) {
                context.getTimerText().setText("0" + mins + ":0" + secondsLeft);
            } else if (mins < 10 && secondsLeft >= 10) {
                context.getTimerText().setText("0" + mins + ":" + secondsLeft);
            } else if (mins >= 10 && secondsLeft < 10) {
                context.getTimerText().setText(mins + ":0" + secondsLeft);
            } else if (mins >= 10 && secondsLeft >= 10) {
                context.getTimerText().setText(mins + ":" + secondsLeft);
            }


        }
    }

    @Override
    public void onFinish() {
        sound = MediaPlayer.create(context, R.raw.endofround);
        sound.setOnCompletionListener(listener);
        sound.start();
        context.onFinishCallback();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    sound.reset();
                    sound.stop();
                    sound.release();
                    Log.d("mediaplayer", "run: post handler clear media player");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },1000);
    }


    interface listener {
        void onFinishCallback();
        void onFinishCountDownCallback();
        void onFinishBreakCallback();
    }


    public String getTimerTag() {
        return timerTag;
    }

    public MainActivity getContext() {
        return context;
    }

    public int getCountedRounds() {
        return countedRounds;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public long getMillisRemaining() {
        return millisRemaining;
    }


    public void setMillisRemaining(long millisRemaining) {
        this.millisRemaining = millisRemaining;
    }

    public static void setCountedRounds(int countedRounds) {
        CountDownTimer.countedRounds = countedRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }



}

