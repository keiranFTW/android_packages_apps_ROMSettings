package keiran.customsettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Spannable;
import android.widget.EditText;

public class CustomSettings extends PreferenceActivity implements OnPreferenceChangeListener{
	private static final String PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
	String mCustomLabelText = null;
	public static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
	Preference mCustomLabel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.custom_settings);
        PreferenceScreen prefs = getPreferenceScreen();
        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
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
		// TODO Auto-generated method stub
		return false;
	}
	 @Override
	    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
	            Preference preference) {
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

}

