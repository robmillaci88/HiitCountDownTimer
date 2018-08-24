package com.example.robmillaci.SimpleHIITtimer;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

public class BreakTimer extends CountDownTimer {

    transient MainActivity context;

    String timerTag;
    long millisRemaining;
    int instantiatedTime;
    int progress;


    public BreakTimer(long millisInFuture, long countDownInterval, MainActivity context) {
        super(millisInFuture, countDownInterval,1, context);
        this.context = context;
        this.timerTag = "BreakTimer";
        this.instantiatedTime = (int)millisInFuture/1000;


        context.getCircleRestSeekBar().setProgress(instantiatedTime);

        this.progress = context.getCircleRestSeekBar().getProgress();

        Log.d(TAG, "BreakTimer: max is " + context.getCircleRestSeekBar().getMax());
        Log.d(TAG, "BreakTimer: prog is " + context.getCircleRestSeekBar().getProgress());
        Log.d(TAG, "BreakTimer: prog int is " + this.progress);

    }
    int secondsLeft = 0;
    int i = 0;

    @Override
    public void onTick(long millisUntilFinished) {
        Log.d(TAG, "onTick: TICKING");
        millisRemaining = millisUntilFinished;

        context.getTimerText().setTextColor(Color.GREEN);

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

            context.getCircleRestSeekBar().setProgress(context.getCircleRestSeekBar().getProgress() - 1);



            Log.d(TAG, "onTick: circle setting progress from max:" + context.getCircleRestSeekBar().getMax() + "i = " + i + " new max is :" + context.getCircleRestSeekBar().getProgress());
        }
    }


    @Override
    public void onFinish() {
        countedRounds = countedRounds + 1;
        context.getRoundCountdownSeekBar().setProgress(countedRounds-1);
        context.getRoundText().setText(""+countedRounds);


        ObjectAnimator animation = ObjectAnimator.ofInt(context.getCircleRestSeekBar(),"progress", (context.getCircleRestSeekBar().getProgress()), context.getCircleRestSeekBar().getMax());
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        context.onFinishBreakCallback();
    }

    @Override
    public String getTimerTag() {
        return timerTag;
    }


    @Override
    public long getMillisRemaining() {
        return millisRemaining;
    }

    public int getInstantiatedTime() {
        return instantiatedTime;
    }


}
