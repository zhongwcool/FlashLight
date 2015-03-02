package com.gst.alex.flashlight;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView flashImage = null;
    private boolean isFlashOn = false;
    private Camera camera = null;
    private Vibrator vibrator = null;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) this.getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        flashImage = (ImageView) findViewById(R.id.imageView);
        flashImage.setOnClickListener(this);
        flashImage.setImageResource(R.drawable.flashlight_light_on);

        camera = Camera.open();
        Camera.Parameters camParameter = camera.getParameters();
        camParameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(camParameter);

        isFlashOn = true;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

        if(camera != null) {
            camera.release();
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // vibrate for 300 millisecond to enhance human experience
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        if(this.sharedPreferences.getBoolean("preference_touch_vibrate", true)) {
            vibrator.vibrate(80);
        }

        switch (v.getId()) {
            case R.id.imageView: {
                if(!isFlashOn) {
                    flashImage.setImageResource(R.drawable.flashlight_light_on);

                    camera = Camera.open();
                    Camera.Parameters camParameter = camera.getParameters();
                    camParameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(camParameter);

                    isFlashOn = true;
                }else {
                    flashImage.setImageResource(R.drawable.flashlight_light_off);

                    Camera.Parameters cameraParameter = camera.getParameters();
                    cameraParameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(cameraParameter);
                    camera.release();

                    isFlashOn = false;
                }
            } break;

            default:
                break;
        }
    }

    private boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager)this.getSystemService(Service.AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }
}
