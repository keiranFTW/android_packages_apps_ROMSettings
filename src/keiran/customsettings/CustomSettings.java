package keiran.customsettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Spannable;
import android.widget.EditText;

public class CustomSettings extends PreferenceActivity implements OnPreferenceChangeListener{

	private static final String PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
	String mCustomLabelText = null;
	Preference mCustomLabel;
	CheckBoxPreference mEnableNavigationBar;
    	ListPreference mNavigationBarHeight;
    	ListPreference mNavigationBarWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.custom_settings);
        PreferenceScreen prefs = getPreferenceScreen();
        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);

	boolean hasNavBarByDefault = getBaseContext().getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        mEnableNavigationBar = (CheckBoxPreference) findPreference("enable_nav_bar");
        mEnableNavigationBar.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_SHOW, hasNavBarByDefault ? 1 : 0) == 1);
	
        // don't allow devices that must use a navigation bar to disable it
        if (hasNavBarByDefault) {
            prefs.removePreference(mEnableNavigationBar);
        }
        mNavigationBarHeight = (ListPreference) findPreference("navigation_bar_height");
        mNavigationBarHeight.setOnPreferenceChangeListener(this);

        mNavigationBarWidth = (ListPreference) findPreference("navigation_bar_width");
        mNavigationBarWidth.setOnPreferenceChangeListener(this);
        updateCustomLabelTextSummary();
	}
	private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(this.getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_warning);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
		
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mNavigationBarWidth) {
		    String newVal = (String) newValue;
		    int dp = Integer.parseInt(newVal);
		    int width = mapChosenDpToPixels(dp);
		    Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH,
		            width);
		    toggleBar();
		    return true;
		} else if (preference == mNavigationBarHeight) {
		    String newVal = (String) newValue;
		    int dp = Integer.parseInt(newVal);
		    int height = mapChosenDpToPixels(dp);
		    Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT,
		            height);
		    toggleBar();
		    return true;
		}
		return false;
	}
	    public int mapChosenDpToPixels(int dp) {
		switch (dp) {
		    case 48:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_48);
		    case 42:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_42);
		    case 36:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_36);
		    case 30:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_30);
		    case 24:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_24);
		}
		return -1;
	    }
	 @Override
	    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
	            Preference preference) {

		if (preference == mEnableNavigationBar) {

		    Settings.System.putInt(getContentResolver(),
		            Settings.System.NAVIGATION_BAR_SHOW,
		            ((CheckBoxPreference) preference).isChecked() ? 1 : 0);

		    /*new AlertDialog.Builder(getActivity())
		            .setTitle("Reboot required!")
		            .setMessage("Please reboot to enable/disable the navigation bar properly!")
		            .setNegativeButton("I'll reboot later", null)
		            .setCancelable(false)
		            .setPositiveButton("Reboot now!", new DialogInterface.OnClickListener() {
		                @Override
		                public void onClick(DialogInterface dialog, int which) {
		                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		                    pm.reboot("New navbar");
		                }
		            })
		            .create()
		            .show();*/

		    return true;
		}

		 if (preference == mCustomLabel) {
	            AlertDialog.Builder alert = new AlertDialog.Builder(this);

	            alert.setTitle(R.string.custom_carrier_label_title);
	            alert.setMessage(R.string.custom_carrier_label_empty);

	            // Set an EditText view to get user input
	            final EditText input = new EditText(this);
	            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
	            alert.setView(input);

	            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    String value = ((Spannable) input.getText()).toString();
	                    Settings.System.putString(getContentResolver(),
	                            Settings.System.CUSTOM_CARRIER_LABEL, value);
	                    updateCustomLabelTextSummary();
	                }
	            });

	            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    // Canceled.
	                }
	            });

	            alert.show();
	        }
		 return super.onPreferenceTreeClick(preferenceScreen, preference);
		 
	 }
	public void toggleBar() {
		boolean isBarOn = Settings.System.getInt(getContentResolver(),
		        Settings.System.NAVIGATION_BAR_SHOW, 1) == 1;
		Settings.System.putInt(getBaseContext().getContentResolver(),
		        Settings.System.NAVIGATION_BAR_SHOW, isBarOn ? 0 : 1);
		Settings.System.putInt(getBaseContext().getContentResolver(),
		        Settings.System.NAVIGATION_BAR_SHOW, isBarOn ? 1 : 0);
	}

}

