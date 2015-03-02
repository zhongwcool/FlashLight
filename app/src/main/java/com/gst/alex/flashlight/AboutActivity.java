/**
 * 
 */
package com.gst.alex.flashlight;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author alex
 *
 */
public class AboutActivity extends Activity implements OnClickListener{
	private ImageButton imageButton = null;
	private TextView versionTextView = null;
	private TextView changelogTextView = null;
	
	private String[] changelogs = new String[]{
			"1、完成基本功能",};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setTitle(R.string.title_setting_about);
		
		imageButton = (ImageButton) findViewById(R.id.imageButton_app);
		imageButton.setOnClickListener(this);
		versionTextView = (TextView) findViewById(R.id.textView_version);
		changelogTextView = (TextView) findViewById(R.id.textView_changelog);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			ApplicationInfo appinfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
			String applable = (String) getPackageManager().getApplicationLabel(appinfo);
			versionTextView.setText(applable + " "+ pinfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		changelogTextView.setText(getResources().getString(R.string.title_change_log) + "\n\n" +changelogs[0]);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// do something
        switch(v.getId()) {
            case R.id.imageButton_app:{

            }break;
            default:break;
        }
	}

}
