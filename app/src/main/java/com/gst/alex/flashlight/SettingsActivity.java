package com.gst.alex.flashlight;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsActivity extends Activity {
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);
        
        // initial
        getFragmentManager().beginTransaction().replace(
        		android.R.id.content, 
        		new SettingsFragement()
        		).commit();  
    }  
      
      
    public static class SettingsFragement extends PreferenceFragment{
        @Override  
        public void onCreate(Bundle savedInstanceState) {  
            // TODO Auto-generated method stub  
            super.onCreate(savedInstanceState);  
            addPreferencesFromResource(R.xml.preferences_settings);  
        }  
    }  
}  
