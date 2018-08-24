package com.example.robmillaci.SimpleHIITtimer;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.countdowntimer.R;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Serializable, CountDownTimer.listener {
    public static final String TAG = "MainActivity";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    transient TextView timerText;
    transient TextView roundText;
    transient TextView totalRoundText;
    transient TextView restTextView;
    transient TextView fragmentContainer;
    transient TextView rounds_text;
    transient TextView roundtimeText;
    transient TextView resttimerText;
    transient TextView restTitle;

    transient ImageView roundTimerMinus;
    transient ImageView roundTimerPlus;
    transient ImageView restTimerMinus;
    transient ImageView restTimerPlus;
    transient ImageView roundPlus;
    transient ImageView roundMinus;
    transient ImageView volIcon;

    transient Button startTimer;
    transient Button restartTimer;

    transient SeekBar roundTimerSeekBar;
    transient SeekBar roundSeekBar;
    transient SeekBar restSeekBar;
    transient SeekBar volseekbar;


    CountDownTimer myTimer;

    transient ArrayList<View> viewList;

    transient ProgressBar roundCountdownSeekBar;
    transient ProgressBar circleRestSeekBar;

    static boolean viewsGone;

    static MainActivity context;

    transient MediaPlayer sound;
    transient MediaPlayer.OnCompletionListener listener;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AppRater.app_launched(this);

        context = this;

        //initialize views;
        timerText = findViewById(R.id.timeText);
        roundText = findViewById(R.id.roundTextView);
        totalRoundText = findViewById(R.id.totalRoundsTextView);
        restTextView = findViewById(R.id.restTextView);
        fragmentContainer = findViewById(R.id.fragmentCountDown);
        rounds_text = findViewById(R.id.roundsText);
        roundtimeText = findViewById(R.id.roundTimeText);
        resttimerText = findViewById(R.id.resttimerText);
        restTitle = findViewById(R.id.restTitle);

        roundTimerMinus = findViewById(R.id.roundTimer_minus);
        roundTimerPlus = findViewById(R.id.roundTimer_plus);
        restTimerMinus = findViewById(R.id.rest_minus);
        restTimerPlus = findViewById(R.id.rest_plus);
        roundPlus = findViewById(R.id.rounds_plus);
        roundMinus = findViewById(R.id.rounds_minus);

        startTimer = findViewById(R.id.startTimer);
        restartTimer = findViewById(R.id.resetTimer);

        roundTimerSeekBar = findViewById(R.id.timerSeekBar);
        roundSeekBar = findViewById(R.id.numberOfRoundsSeekBar);

        restSeekBar = findViewById(R.id.restSeekBar);
        roundCountdownSeekBar = findViewById(R.id.roundCountdownSeekBar);


        circleRestSeekBar = findViewById(R.id.restCircleSeek);
        circleRestSeekBar.setProgress(0);

        volIcon = findViewById(R.id.volumeIcon);
        volseekbar = findViewById(R.id.seekBar_volume);

        //end of initialization


        //Store views in arrayList for easy property changes
        viewList = new ArrayList<>();
        viewList.add(timerText);
        viewList.add(roundText);
        viewList.add(totalRoundText);
        viewList.add(restTextView);
        viewList.add(roundTimerMinus);
        viewList.add(roundTimerPlus);
        viewList.add(restTimerMinus);
        viewList.add(restTimerPlus);
        viewList.add(roundPlus);
        viewList.add(roundMinus);
        viewList.add(startTimer);
        viewList.add(restartTimer);
        viewList.add(roundTimerSeekBar);
        viewList.add(roundSeekBar);
        viewList.add(restSeekBar);
        viewList.add(rounds_text);
        viewList.add(roundtimeText);
        viewList.add(resttimerText);
        viewList.add(restTitle);
        viewList.add(roundCountdownSeekBar);
        Log.d(TAG, "onCreate: viewlist is " + viewList);


        /***********************************************************************************************************************************
         * Reload state after activity restarts
         ***********************************************************************************************************************************/
        if (savedInstanceState != null) {
            startTimer.setTag(savedInstanceState.getString("buttonTag"));
            myTimer = (CountDownTimer) savedInstanceState.getSerializable("timer");
            if (myTimer != null) {
                myTimer.cancel();
                Log.d(TAG, "onCreate: timer cancelled");
                if (myTimer instanceof BreakTimer) {
                    timerText.setTextColor(Color.GREEN);

                    circleRestSeekBar.setMax(savedInstanceState.getInt("circleseekMax"));

                    if (startTimer.getTag().toString().equals("stop")) {
                        startTimer.setBackgroundResource(R.drawable.stop_btn);
                        startTimer.setTag("stop");
                    } else {
                        startTimer.setBackgroundResource(R.drawable.play_btn);
                        startTimer.setTag("play");
                    }

                    roundText.setText(String.valueOf("" + CountDownTimer.countedRounds));

                    if (startTimer.getTag().toString().equals("stop")) {
                        myTimer = new BreakTimer(savedInstanceState.getLong("millis") + 500, 100, this);
                        myTimer.start();
                    }

                } else if (myTimer instanceof CountDownTimer) {

                    if (startTimer.getTag().toString().equals("stop")) {
                        startTimer.setBackgroundResource(R.drawable.stop_btn);
                        startTimer.setTag("stop");
                    } else {
                        startTimer.setBackgroundResource(R.drawable.play_btn);
                        startTimer.setTag("play");
                    }


                    if (startTimer.getTag().toString().equals("stop")) {
                        myTimer = new CountDownTimer(savedInstanceState.getLong("millis") + 500, 100, savedInstanceState.getInt("round"), this);
                        startTimer.setBackgroundResource(R.drawable.stop_btn);
                        myTimer.start();
                    }


                    roundCountdownSeekBar.setProgress(savedInstanceState.getInt("roundcountdownbar"));
                    roundCountdownSeekBar.setMax(savedInstanceState.getInt("roundcoutndownbarMax"));
                    getRoundText().setText("" + myTimer.getCountedRounds());

                }


            } else {
                startTimer.setTag("start");
            }
        } else {
            startTimer.setTag("start");
        }

        if (roundText.getText().equals("")) {
            Log.d(TAG, "onCreate: called if timer value is blank");
            roundText.setText("1");
        }


        timerText.setFocusable(false);
        roundTimerSeekBar.setMax(600);
        restSeekBar.setMax(300);
        totalRoundText.setText("OF 01");
        Log.d(TAG, "onCreate: apply preferences");

        applyPrefrences();

        roundCountdownSeekBar.setMax(roundSeekBar.getProgress());
        roundCountdownSeekBar.setProgress(CountDownTimer.countedRounds);


        restTextView.setText(restSeekBar.getProgress() + "s");
        restTextView.setTextColor(Color.GREEN);
        totalRoundText.setText(((roundSeekBar.getProgress() < 10) ? "OF 0" : "OF ") + roundSeekBar.getProgress());


        // startTimer.setTag("start");
        Log.d(TAG, "onCreate: assigning start timer tag = " + startTimer.getTag());


        /***
         * Set up on click listeners
         */
        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    mp.reset();
                    mp.prepare();
                    mp.stop();
                    mp.release();
                    mp = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager =
                        (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {

                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
                    volIcon.setBackgroundResource(R.drawable.ic_volume_off_black_24dp);
                    volseekbar.setProgress(0);
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVol, AudioManager.FLAG_SHOW_UI);
                    volIcon.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
                    volseekbar.setProgress(currentVol);
                }
            }
        });

        volseekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volseekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
                if (progress > 0) {
                    volIcon.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
                } else {
                    volIcon.setBackgroundResource(R.drawable.ic_volume_off_black_24dp);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        roundTimerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roundTimerSeekBar.setProgress(roundTimerSeekBar.getProgress() + 1);
                timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            }
        });

        roundTimerMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundTimerSeekBar.setProgress(roundTimerSeekBar.getProgress() - 1);
                timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            }
        });


        roundPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roundSeekBar.setProgress(roundSeekBar.getProgress() + 1);
                roundCountdownSeekBar.setProgress(0);
                roundCountdownSeekBar.setMax(roundSeekBar.getProgress());
            }
        });

        roundMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundSeekBar.setProgress(roundSeekBar.getProgress() - 1);
                roundCountdownSeekBar.setProgress(0);
                roundCountdownSeekBar.setMax(roundSeekBar.getProgress());
            }
        });

        restTimerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restSeekBar.setProgress(restSeekBar.getProgress() + 1);
                circleRestSeekBar.setMax(circleRestSeekBar.getMax() + 1);
            }
        });

        restTimerMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                restSeekBar.setProgress(restSeekBar.getProgress() - 1);
                circleRestSeekBar.setMax(circleRestSeekBar.getMax() - 1);

            }
        });

        roundTimerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if (progress < 10) {
                    timerText.setText("00:0" + progress);
                } else if (progress > 10 && progress < 60) {
                    timerText.setText("00:" + progress);
                } else {
                    timerText.setText("0" + (progress / 60) + ((progress % 60) < 10 ? ":0" + (progress % 60) : ":" + (progress % 60)));
                }

                if (timerText.getText().toString().equals("010:00")) {
                    timerText.setText("10:00");
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        roundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //ToDo prevent round number of 0


                if (progress > 1 && progress < 10) {
                    totalRoundText.setText(((roundSeekBar.getProgress() < 10) ? "OF 0" : "OF ") + roundSeekBar.getProgress());
                    roundCountdownSeekBar.setMax(progress);

                } else {
                    totalRoundText.setText(((roundSeekBar.getProgress() < 10) ? "OF 0" : "OF ") + roundSeekBar.getProgress());
                    roundCountdownSeekBar.setMax(progress);
                }

                //circleRestSeekBar.setMax(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        restSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 60) {
                    restTextView.setText(progress + "s");
                } else {
                    restTextView.setText((progress / 60) + "m" + (progress % 60) + "s");
                }
                circleRestSeekBar.setMax(progress);
//                circleRestSeekBar.setProgress(0);


                if (restTextView.getText().toString().equals("010:00")) {
                    restTextView.setText("10:00");
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (roundTimerSeekBar.getProgress() == 0) {
                    Toast.makeText(MainActivity.this, "Please set a time first", Toast.LENGTH_LONG).show();
                    CountDownTimer.setCountedRounds(1);
                    roundCountdownSeekBar.setProgress(1);
                    roundText.setText("1");

                } else if (roundSeekBar.getProgress() == 0) {
                    Toast.makeText(MainActivity.this, "Please set at least one round", Toast.LENGTH_LONG).show();
                } else {

                    //fixed a bug with the break timer color reverting to rest when stopped
                    if (!(myTimer instanceof BreakTimer)) {
                        timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }


                    roundCountdownSeekBar.setProgress(0);
                    enableObjects(false);
                    if (startTimer.getTag().toString().equals("start")) {
                        startTimer.setVisibility(View.GONE);
                        restartTimer.setVisibility(View.GONE);

                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, FragmentCountdown.newInstance(1, "", MainActivity.this, viewList), "fragmentCountdown")
                                .addToBackStack("fragmentCountdown")
                                .commit();
                        getSupportFragmentManager().executePendingTransactions();

                        roundText.setText(String.valueOf(CountDownTimer.countedRounds));

                    } else {
                        myTimer.cancel();
                        startTimer.setTag("start");
                        startTimer.setBackgroundResource(R.drawable.play_btn);

                        if (sound != null) {
                            sound.reset();
                            sound.release();
                        }


                        // enableObjects(true);

                    }
                }
            }
        });

        restartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myTimer != null) {
                    CountDownTimer.setCountedRounds(1);
                    nullTimer();
                }


                applyPrefrences();

                roundCountdownSeekBar.setProgress(0);
                roundText.setText("1");
                timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                startTimer.setBackgroundResource(R.drawable.play_btn);
                startTimer.setTag("start");
                enableObjects(true);
                circleRestSeekBar.setProgress(0);
            }
        });


        //fixes a bug with the time text for the round time not updating with changed settings on first run
        roundTimerMinus.callOnClick();
        roundTimerPlus.callOnClick();
        roundTimerMinus.callOnClick();
        roundTimerPlus.callOnClick();
        Log.d(TAG, "onCreate: timer fix called");
        //fix ends


    }

    public static void applyPrefrences() {
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext());

        String rounds = preferences.getString("Rounds", "10");
        String roundTime = preferences.getString("roundsTime", "60");
        String restTime = preferences.getString("rest", "30");

        int roundsInt = Integer.parseInt(rounds);
        int roundTimeInt = Integer.parseInt(roundTime);
        int restTimeInt = Integer.parseInt(restTime);



        context.roundSeekBar.setProgress(roundsInt);
        context.restSeekBar.setProgress(restTimeInt);
        context.roundTimerSeekBar.setProgress(roundTimeInt);
        context.circleRestSeekBar.setMax(restTimeInt);
        context.roundCountdownSeekBar.setMax(roundsInt);


        //fixes a bug with round timer setting to 00:00 on first run of application

        context.getRoundTimerPlus().callOnClick();
        context.getRoundTimerMinus().callOnClick();

    }

    /**
     * Sets enabled propertry of views to prevent / enable user interaction
     *
     * @param enabled - true/false
     */

    private void enableObjects(boolean enabled) {
        roundSeekBar.setEnabled(enabled);
        restSeekBar.setEnabled(enabled);
        roundTimerSeekBar.setEnabled(enabled);

        restTimerPlus.setEnabled(enabled);
        roundPlus.setEnabled(enabled);
        roundTimerPlus.setEnabled(enabled);

        restTimerMinus.setEnabled(enabled);
        roundTimerMinus.setEnabled(enabled);
        roundMinus.setEnabled(enabled);
    }


    /**
     * Create and inflate options menu
     *
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);

        return true;
    }


    /**
     * Build alert dialog for the menu About
     *
     * @param item - the menue item selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Settings:
                if (myTimer == null | (startTimer.getTag().toString().equals("start"))) {
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please stop the timer to view settings", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
                builder.setMessage("About Simple HIIT timer")
                        .setView(R.layout.about_dialog_view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();

                TextView leaveARating = dialog.findViewById(R.id.leaveARating);
                leaveARating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        linktoAppStore();
                    }
                });
        }
        return true;
    }


    /**
     * Cancels and sets the active timer to null
     */
    public void nullTimer() {
        myTimer.cancel();
        myTimer = null;
    }


    /**
     * Call back from countdown timer finish to make relevant changes to views properties after call back is finished
     * OR creates a new breakdown timer if number of total rounds > number of rounds counted
     */
    @Override
    public void onFinishCallback() {
        if (myTimer.getCountedRounds() == myTimer.getTotalRounds()) {
            nullTimer();
            startTimer.setBackgroundResource(R.drawable.play_btn);
            startTimer.setTag("start");
            roundCountdownSeekBar.setProgress(roundCountdownSeekBar.getMax());
            timerText.setTextColor(Color.BLUE);
            roundTimerSeekBar.setProgress(0);
            timerText.setText("FINISH");
            CountDownTimer.setCountedRounds(1);

            sound = MediaPlayer.create(this, R.raw.endoftimer);
            sound.setOnCompletionListener(listener);
            sound.start();

            enableObjects(true);
        } else {
            nullTimer();
            sound = MediaPlayer.create(this, R.raw.endofround);
            sound.setOnCompletionListener(listener);
            sound.start();
            myTimer = new BreakTimer(((restSeekBar.getProgress()) * 1000) + 500, 100, MainActivity.this);
            circleRestSeekBar.setMax(((restSeekBar.getProgress())));

            myTimer.start();

        }
    }

    /**
     * call back for the end of a break round, creating the next rounds timer
     */

    @Override
    public void onFinishBreakCallback() {
        try {
            sound.reset();
            sound.prepare();
            sound.stop();
            sound.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        nullTimer();
        sound = MediaPlayer.create(this, R.raw.endofround);
        sound.setOnCompletionListener(listener);
        sound.start();
        myTimer = new CountDownTimer((roundTimerSeekBar.getProgress() * 1000) + 500, 100, roundSeekBar.getProgress(), MainActivity.this);
        timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        myTimer.start();
    }


    /**
     * Starts a new countdown timer if the timer is null. Or restores a currently active timer if activity is destroyed
     */

    public void startCountdown() {
        setViewsVisibility(View.VISIBLE);
        if (myTimer == null) {
            long millis = roundTimerSeekBar.getProgress() * 1000;
            long countDownInterval = 100;
            int totalRounds = roundSeekBar.getProgress();
            myTimer = new CountDownTimer(millis + 500, countDownInterval, totalRounds, MainActivity.this);
            myTimer.start();
            startTimer.setBackgroundResource(R.drawable.stop_btn);
            startTimer.setTag("stop");
        } else {
            long millis = myTimer.getMillisRemaining();
            String timerTag = myTimer.getTimerTag();

            int totalRounds = myTimer.getTotalRounds();
            int countedRounds = myTimer.getCountedRounds();
            myTimer.cancel();
            myTimer = null;
            switch (timerTag) {
                case "CountDownTimer":
                    myTimer = new CountDownTimer(millis + 500, 100, totalRounds, MainActivity.this);
                    CountDownTimer.setCountedRounds(countedRounds);
                    myTimer.start();
                    timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    startTimer.setBackgroundResource(R.drawable.stop_btn);
                    startTimer.setTag("stop");
                    break;

                case "BreakTimer":
                    myTimer = new BreakTimer(millis + 500, 100, MainActivity.this);
                    BreakTimer.setCountedRounds(countedRounds);
                    myTimer.start();
                    timerText.setTextColor(Color.GREEN);
                    startTimer.setBackgroundResource(R.drawable.stop_btn);
                    startTimer.setTag("stop");
                    break;
            }
        }
    }

    /***
     * sets the visibility of the views for launch and removal of fragment
     * @param visiblity - true/false depending on requirment to hide/show
     */

    public void setViewsVisibility(int visiblity) {
        for (View view : viewList) {

            view.setVisibility(visiblity);
        }

        if (visiblity == View.GONE) {
            viewsGone = true;
        } else {
            viewsGone = false;
        }
    }

    /***
     * Getters and setters
     */
    public TextView getTimerText() {
        return timerText;
    }

    public TextView getRoundText() {
        return roundText;
    }

    public TextView getTotalRoundText() {
        return totalRoundText;
    }

    public TextView getRestTextView() {
        return restTextView;
    }

    public ImageView getRoundTimerMinus() {
        return roundTimerMinus;
    }

    public ImageView getRoundTimerPlus() {
        return roundTimerPlus;
    }

    public ImageView getRestTimerMinus() {
        return restTimerMinus;
    }

    public ImageView getRestTimerPlus() {
        return restTimerPlus;
    }

    public ImageView getRoundPlus() {
        return roundPlus;
    }

    public ImageView getRoundMinus() {
        return roundMinus;
    }

    public Button getStartTimer() {
        return startTimer;
    }

    public Button getRestartTimer() {
        return restartTimer;
    }

    public SeekBar getRoundTimerSeekBar() {
        return roundTimerSeekBar;
    }

    public SeekBar getRoundSeekBar() {
        return roundSeekBar;
    }

    public SeekBar getRestSeekBar() {
        return restSeekBar;
    }

    public CountDownTimer getMyTimer() {
        return myTimer;
    }

    public TextView getFragmentContainer() {
        return fragmentContainer;
    }

    public ArrayList getViewList() {
        return viewList;
    }


    /***
     * Values to be save and restores on activity destroyed / recreated
     * @param outState
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (myTimer != null) {
            outState.putSerializable("timer", myTimer);
            outState.putLong("millis", myTimer.getMillisRemaining());
            outState.putInt("round", myTimer.getTotalRounds());
            outState.putInt("countedRounds", myTimer.getCountedRounds());
            outState.putInt("totalRounds", myTimer.getTotalRounds());
            outState.putString("buttonTag", startTimer.getTag().toString());
            outState.putInt("circleseekProg", circleRestSeekBar.getProgress());
            outState.putInt("circleseekMax", circleRestSeekBar.getMax());
            outState.putString("roundText", roundText.getText().toString());
            outState.putString("buttonTag", startTimer.getTag().toString());


            //bug fix for sound continuing after rotation
            if (sound != null) {
                try {
                    sound.reset();
                    sound.release();
                    sound.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public boolean isViewsGone() {
        return viewsGone;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sound != null) {
            try {
                try {
                    sound.reset();
                    sound.prepare();
                    sound.stop();
                    sound.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (viewsGone) {
            setViewsVisibility(View.GONE);
        } else {
            setViewsVisibility(View.VISIBLE);
        }

    }

    public void fragmentCallback() {
        for (View v : viewList) {
            v.setVisibility(View.VISIBLE);
        }

    }


    /***
     * Call back for countdown fragment finish , restoring the views visibility
     */
    @Override
    public void onFinishCountDownCallback() {

        if (FragmentCountdown.readyToStart) {
            sound = MediaPlayer.create(context, R.raw.endofround);
            sound.setOnCompletionListener(listener);
            sound.start();
            startCountdown();
        }
    }

    public ProgressBar getRoundCountdownSeekBar() {
        return roundCountdownSeekBar;
    }

    public void linktoAppStore() {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.robmillaci.countdowntimer"));
        startActivity(i);
    }

    public ProgressBar getCircleRestSeekBar() {
        return circleRestSeekBar;
    }


    public static MainActivity getContext() {
        return context;
    }

    static int ringstate = 0;

    private void soundOn(boolean off) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (off) {   //turn off ringing/sound
            //get the current ringer mode
            ringstate = audio.getRingerMode();
            if (ringstate != AudioManager.RINGER_MODE_SILENT)
                audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);//turn off
        } else {
            //restore previous state
            audio.setRingerMode(ringstate);


        }

    }

    public MediaPlayer getSound() {
        return sound;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sound != null) {
            try {
                sound.reset();
                sound.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}