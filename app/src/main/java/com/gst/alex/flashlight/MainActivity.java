package com.gst.alex.flashlight;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.special.residemenu.ResideMenu;
import com.special.residemenu.ResideMenuItem;


public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView flashImage = null;
    private boolean isFlashOn = false;
    private Camera camera = null;
    private Vibrator vibrator = null;
    private SharedPreferences sharedPreferences = null;
    private SoundPool soundpool = null;
    private int touchSoundId;
    private ResideMenu resideMenu;
    private ResideMenuItem itemAbout;
    private ResideMenuItem itemSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupResideMenu();

        // initial the Vibrator
        vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        // SoundPool and latest android api lollipop compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundpool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(aa)
                    .build();
        } else {
            soundpool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
        touchSoundId = soundpool.load(getApplicationContext(), R.raw.sound_touch_water_drop, 1);

        flashImage = (ImageView) findViewById(R.id.imageView);
        flashImage.setOnClickListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (sharedPreferences.getBoolean("preference_start_on", true)) {
            flashImage.setImageResource(R.drawable.flashlight_light_on);

            try {
                camera = Camera.open();
                Camera.Parameters camParameter = camera.getParameters();
                camParameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(camParameter);
            } catch (RuntimeException e) {
                Toast.makeText(this, "can't open camera", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }

            isFlashOn = true;
        } else {
            flashImage.setImageResource(R.drawable.flashlight_light_off);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setupResideMenu() {
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.7f);

        // create menu item
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "Settings");
        itemAbout = new ResideMenuItem(this, R.drawable.icon_profile, "About");

        itemSettings.setOnClickListener(resideMenuItemClickListener);
        itemAbout.setOnClickListener(resideMenuItemClickListener);

        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (camera != null) {
            camera.release();
        }

        if (soundpool != null) {
            soundpool.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;
            }

            case R.id.action_about: {
                Dialog ad = new Dialog(this);
                ad.setTitle(R.string.action_about);
                ad.setContentView(R.layout.dialog_about);
                ((TextView) ad.findViewById(R.id.tv_about)).setText(R.string.summary_about);
                ad.show();

                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // vibrate for 300 millisecond to enhance human experience
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean("preference_touch_vibrate", true)) {
            vibrator.vibrate(80);
        }

        if (isAudioNormal() && sharedPreferences.getBoolean("preference_touch_sound", true)) {
            soundpool.play(touchSoundId, 1, 1, 0, 0, 1);
        }

        switch (v.getId()) {
            case R.id.imageView: {
                if (!isFlashOn) {
                    flashImage.setImageResource(R.drawable.flashlight_light_on);

                    camera = Camera.open();
                    Camera.Parameters camParameter = camera.getParameters();
                    camParameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(camParameter);

                    isFlashOn = true;
                } else {
                    flashImage.setImageResource(R.drawable.flashlight_light_off);

                    Camera.Parameters cameraParameter = camera.getParameters();
                    cameraParameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(cameraParameter);
                    camera.release();

                    isFlashOn = false;
                }
            }
            break;

            default:
                break;
        }
    }

    private boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager) this.getSystemService(Service.AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(MainActivity.this, "Menu is opened!", Toast.LENGTH_SHORT).show();
            Log.d("ALEX", "Menu is opened");
        }

        @Override
        public void closeMenu() {
            //Toast.makeText(MainActivity.this, "Menu is closed!", Toast.LENGTH_SHORT).show();
            Log.d("ALEX", "Menu is closed");
        }
    };

    private View.OnClickListener resideMenuItemClickListener = new View.OnClickListener() {

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {

            if(view == itemSettings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if(view == itemAbout) {
                Dialog ad = new Dialog(MainActivity.this);
                ad.setTitle(R.string.action_about);
                ad.setContentView(R.layout.dialog_about);
                ((TextView) ad.findViewById(R.id.tv_about)).setText(R.string.summary_about);
                ad.show();
            }

            resideMenu.closeMenu();
        }
    };

}
