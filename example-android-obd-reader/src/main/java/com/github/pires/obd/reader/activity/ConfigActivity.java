package com.github.pires.obd.reader.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.reader.R;
import com.github.pires.obd.reader.config.ObdConfig;

import java.util.ArrayList;
import java.util.Set;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.qrcode_data.QrcodeItems;
import com.qrcode_data.qrcodeObject;

/**
 * Configuration com.github.pires.obd.reader.activity.
 */
public class ConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
    public static final String UPLOAD_DATA_KEY = "upload_data_preference";

    public static final String UPLOAD_URL_KEY = "upload_url_preference";
    public static final String TRANSMIT_METHOD_KEY = "transmit_method";
    public static final String USERNAME_KEY = "user_preference";
    public static final String PASSWORD_KEY = "password_preference";


    public static final String OBD_UPDATE_PERIOD_KEY = "obd_update_period_preference";
    public static final String VEHICLE_ID_KEY = "vehicle_id_preference";
    public static final String ENGINE_DISPLACEMENT_KEY = "engine_displacement_preference";
    public static final String VOLUMETRIC_EFFICIENCY_KEY = "volumetric_efficiency_preference";
    public static final String IMPERIAL_UNITS_KEY = "imperial_units_preference";
    public static final String COMMANDS_SCREEN_KEY = "obd_commands_screen";
    public static final String PROTOCOLS_LIST_KEY = "obd_protocols_preference";
    public static final String ENABLE_GPS_KEY = "enable_gps_preference";
    public static final String GPS_UPDATE_PERIOD_KEY = "gps_update_period_preference";
    public static final String GPS_DISTANCE_PERIOD_KEY = "gps_distance_period_preference";
    public static final String ENABLE_BT_KEY = "enable_bluetooth_preference";
    public static final String MAX_FUEL_ECON_KEY = "max_fuel_econ_preference";
    public static final String CONFIG_READER_KEY = "reader_config_preference";
    public static final String ENABLE_FULL_LOGGING_KEY = "enable_full_logging";
    public static final String DIRECTORY_FULL_LOGGING_KEY = "dirname_full_logging";
    public static final String DEV_EMAIL_KEY = "dev_email";
    String userName;
    String password;
    String url;
    String radioMethod;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * @param prefs
     * @return
     */
    public static int getObdUpdatePeriod(SharedPreferences prefs) {
        String periodString = prefs.
                getString(ConfigActivity.OBD_UPDATE_PERIOD_KEY, "4"); // 4 as in seconds
        int period = 4000; // by default 4000ms

        try {
            period = (int) (Double.parseDouble(periodString) * 1000);
        } catch (Exception e) {
        }

        if (period <= 0) {
            period = 4000;
        }

        return period;
    }

    /**
     * @param prefs
     * @return
     */
    public static double getVolumetricEfficieny(SharedPreferences prefs) {
        String veString = prefs.getString(ConfigActivity.VOLUMETRIC_EFFICIENCY_KEY, ".85");
        double ve = 0.85;
        try {
            ve = Double.parseDouble(veString);
        } catch (Exception e) {
        }
        return ve;
    }

    /**
     * @param prefs
     * @return
     */
    public static double getEngineDisplacement(SharedPreferences prefs) {
        String edString = prefs.getString(ConfigActivity.ENGINE_DISPLACEMENT_KEY, "1.6");
        double ed = 1.6;
        try {
            ed = Double.parseDouble(edString);
        } catch (Exception e) {
        }
        return ed;
    }

    /**
     * @param prefs
     * @return
     */
    public static ArrayList<ObdCommand> getObdCommands(SharedPreferences prefs) {
        ArrayList<ObdCommand> cmds = ObdConfig.getCommands();
        ArrayList<ObdCommand> ucmds = new ArrayList<>();
        for (int i = 0; i < cmds.size(); i++) {
            ObdCommand cmd = cmds.get(i);
            boolean selected = prefs.getBoolean(cmd.getName(), true);
            if (selected)
                ucmds.add(cmd);
        }
        return ucmds;
    }

    /**
     * @param prefs
     * @return
     */
    public static double getMaxFuelEconomy(SharedPreferences prefs) {
        String maxStr = prefs.getString(ConfigActivity.MAX_FUEL_ECON_KEY, "70");
        double max = 70;
        try {
            max = Double.parseDouble(maxStr);
        } catch (Exception e) {
        }
        return max;
    }

    /**
     * @param prefs
     * @return
     */
    public static String[] getReaderConfigCommands(SharedPreferences prefs) {
        String cmdsStr = prefs.getString(CONFIG_READER_KEY, "atsp0\natz");
        String[] cmds = cmdsStr.split("\n");
        return cmds;
    }

    /**
     * Minimum time between location updates, in milliseconds
     *
     * @param prefs
     * @return
     */
    public static int getGpsUpdatePeriod(SharedPreferences prefs) {
        String periodString = prefs
                .getString(ConfigActivity.GPS_UPDATE_PERIOD_KEY, "1"); // 1 as in seconds
        int period = 1000; // by default 1000ms

        try {
            period = (int) (Double.parseDouble(periodString) * 1000);
        } catch (Exception e) {
        }

        if (period <= 0) {
            period = 1000;
        }

        return period;
    }

    /**
     * Min Distance between location updates, in meters
     *
     * @param prefs
     * @return
     */
    public static float getGpsDistanceUpdatePeriod(SharedPreferences prefs) {
        String periodString = prefs
                .getString(ConfigActivity.GPS_DISTANCE_PERIOD_KEY, "5"); // 5 as in meters
        float period = 5; // by default 5 meters

        try {
            period = Float.parseFloat(periodString);
        } catch (Exception e) {
        }

        if (period <= 0) {
            period = 5;
        }

        return period;
    }


    private void getPreferencesValues(String qrcode) {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        if (qrcode == null) {
            qrcode = mPrefs.getString("endpointsJSON", null);
            qrcodeObject.userUrl = mPrefs.getString("puburl", null);
        } else {
            qrcodeObject.userUrl = null;
        }

        if (qrcode != null) {
            qrcodeObject.getJSONData(qrcode);
            userName = qrcodeObject.username;
            password = qrcodeObject.password;
            radioMethod = mPrefs.getString("method", null);
            radioMethod = radioMethod == null ? getResources().getString(R.string.app_method) : radioMethod;


            if (radioMethod.equals("0")) {
                url = qrcodeObject.userUrl == null ? qrcodeObject.urlPubHTTP : qrcodeObject.userUrl;
            } else {
                url = qrcodeObject.userUrl == null ? qrcodeObject.urlPubMQTT : qrcodeObject.userUrl;
            }

        } else {
            setDefaults();
        }

    }

    public void setDefaults() {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        userName = getResources().getString(R.string.app_username);
        password = getResources().getString(R.string.app_password);
        radioMethod = radioMethod == null ? getResources().getString(R.string.app_method) : radioMethod;

        if (radioMethod.equals("0")) {
            url = mPrefs.getString("puburl", null) == null ? getResources().getString(R.string.app_urlPubHTTP) : mPrefs.getString("puburl", null);
        } else {
            url = mPrefs.getString("puburl", null) == null ? getResources().getString(R.string.app_urlPubMQTT) : mPrefs.getString("puburl", null);
        }
    }

    private void savePreferences() {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        ListPreference preferenceMethod = (ListPreference) getPreferenceScreen().findPreference(TRANSMIT_METHOD_KEY);
        EditTextPreference preferenceURL = (EditTextPreference) getPreferenceScreen().findPreference(UPLOAD_URL_KEY);
        EditTextPreference preferenceUser = (EditTextPreference) getPreferenceScreen().findPreference(USERNAME_KEY);
        EditTextPreference preferencePassword = (EditTextPreference) getPreferenceScreen().findPreference(PASSWORD_KEY);

        mEditor.putString("username", preferenceUser.getText().toString()).commit();
        qrcodeObject.updateJSONData(QrcodeItems.user, preferenceUser.getText().toString());

        mEditor.putString("password", preferencePassword.getText().toString()).commit();
        qrcodeObject.updateJSONData(QrcodeItems.pass, preferencePassword.getText().toString());

        mEditor.putString("puburl", preferenceURL.getText().toString()).commit();
        qrcodeObject.userUrl = preferenceURL.getText().toString();

        mEditor.putString("method", preferenceMethod.getValue().toString()).commit();
        radioMethod = preferenceMethod.getValue().toString();


        mEditor.putString("endpointsJSON", qrcodeObject.jsonString).commit();


        //mEditor.putString("method", radioMethod).commit();

    }


    public void onPause() {
        super.onPause();  // Always call the superclass method first
        savePreferences();
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CheckBoxPreference uploadOption = (CheckBoxPreference) getPreferenceScreen().findPreference(UPLOAD_DATA_KEY);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String qrcode = b.getString("qrcode");
            uploadOption.setChecked(true);
            getPreferencesValues(qrcode);
        } else {
            getPreferencesValues(null);
        }


        ListPreference preferenceMethod = (ListPreference) getPreferenceScreen().findPreference(TRANSMIT_METHOD_KEY);
        EditTextPreference preferenceURL = (EditTextPreference) getPreferenceScreen().findPreference(UPLOAD_URL_KEY);
        EditTextPreference preferenceUser = (EditTextPreference) getPreferenceScreen().findPreference(USERNAME_KEY);
        EditTextPreference preferencePassword = (EditTextPreference) getPreferenceScreen().findPreference(PASSWORD_KEY);

        preferenceURL.setText(url);
        preferenceURL.setSummary(url);
        preferenceUser.setText(userName);
        preferenceUser.setSummary(userName);
        preferencePassword.setText(password);
        preferencePassword.setSummary(password);

        if (radioMethod.equals("0")) {
            preferenceMethod.setValueIndex(0);
            preferenceMethod.setSummary("REST");
        } else {
            preferenceMethod.setValueIndex(1);
            preferenceMethod.setSummary("MQTT");
        }


    /*
     * Read preferences resources available at res/xml/preferences.xml
     */


        checkGps();

        ArrayList<CharSequence> pairedDeviceStrings = new ArrayList<>();
        ArrayList<CharSequence> vals = new ArrayList<>();
        ListPreference listBtDevices = (ListPreference) getPreferenceScreen()
                .findPreference(BLUETOOTH_LIST_KEY);
        ArrayList<CharSequence> protocolStrings = new ArrayList<>();
        ListPreference listProtocols = (ListPreference) getPreferenceScreen()
                .findPreference(PROTOCOLS_LIST_KEY);
        String[] prefKeys = new String[]{ENGINE_DISPLACEMENT_KEY,
                VOLUMETRIC_EFFICIENCY_KEY, OBD_UPDATE_PERIOD_KEY, MAX_FUEL_ECON_KEY};


        preferenceMethod.setOnPreferenceChangeListener(this);
        preferenceURL.setOnPreferenceChangeListener(this);
        preferenceUser.setOnPreferenceChangeListener(this);
        preferencePassword.setOnPreferenceChangeListener(this);

        for (String prefKey : prefKeys) {
            EditTextPreference txtPref = (EditTextPreference) getPreferenceScreen()
                    .findPreference(prefKey);
            txtPref.setOnPreferenceChangeListener(this);
        }

    /*
     * Available OBD commands
     *
     * TODO This should be read from preferences database
     */
        ArrayList<ObdCommand> cmds = ObdConfig.getCommands();
        PreferenceScreen cmdScr = (PreferenceScreen) getPreferenceScreen()
                .findPreference(COMMANDS_SCREEN_KEY);
        for (ObdCommand cmd : cmds) {
            CheckBoxPreference cpref = new CheckBoxPreference(this);
            cpref.setTitle(cmd.getName());
            cpref.setKey(cmd.getName());
            cpref.setChecked(true);
            cmdScr.addPreference(cpref);
        }

    /*
     * Available OBD protocols
     *
     */
        for (ObdProtocols protocol : ObdProtocols.values()) {
            protocolStrings.add(protocol.name());
        }
        listProtocols.setEntries(protocolStrings.toArray(new CharSequence[0]));
        listProtocols.setEntryValues(protocolStrings.toArray(new CharSequence[0]));

    /*
     * Let's use this device Bluetooth adapter to select which paired OBD-II
     * compliant device we'll use.
     */
        final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            listBtDevices
                    .setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
            listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));

            // we shouldn't get here, still warn user
            Toast.makeText(this, "This device does not support Bluetooth.",
                    Toast.LENGTH_LONG).show();

            return;
        }

    /*
     * Listen for preferences click.
     *
     * TODO there are so many repeated validations :-/
     */
        final Activity thisActivity = this;
        listBtDevices.setEntries(new CharSequence[1]);
        listBtDevices.setEntryValues(new CharSequence[1]);
        listBtDevices.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // see what I mean in the previous comment?
                if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
                    Toast.makeText(thisActivity,
                            "This device does not support Bluetooth or it is disabled.",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

    /*
     * Get paired devices and populate preference list.
     */
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
                vals.add(device.getAddress());
            }
        }
        listBtDevices.setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
        listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * OnPreferenceChangeListener method that will validate a preferencen new
     * value when it's changed.
     *
     * @param preference the changed preference
     * @param newValue   the value to be validated and set if valid
     */
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (OBD_UPDATE_PERIOD_KEY.equals(preference.getKey())
                || VOLUMETRIC_EFFICIENCY_KEY.equals(preference.getKey())
                || ENGINE_DISPLACEMENT_KEY.equals(preference.getKey())
                || MAX_FUEL_ECON_KEY.equals(preference.getKey())
                || GPS_UPDATE_PERIOD_KEY.equals(preference.getKey())
                || GPS_DISTANCE_PERIOD_KEY.equals(preference.getKey())) {
            try {
                Double.parseDouble(newValue.toString().replace(",", "."));
                return true;
            } catch (Exception e) {
                Toast.makeText(this,
                        "Couldn't parse '" + newValue.toString() + "' as a number.",
                        Toast.LENGTH_LONG).show();
            }
        }


        ListPreference preferenceMethod = (ListPreference) getPreferenceScreen().findPreference(TRANSMIT_METHOD_KEY);
        EditTextPreference preferenceURL = (EditTextPreference) getPreferenceScreen().findPreference(UPLOAD_URL_KEY);
        EditTextPreference preferenceUser = (EditTextPreference) getPreferenceScreen().findPreference(USERNAME_KEY);
        EditTextPreference preferencePassword = (EditTextPreference) getPreferenceScreen().findPreference(PASSWORD_KEY);


        if (UPLOAD_URL_KEY.equals(preference.getKey())) {
            preferenceURL.setSummary((String) newValue);

        }

        if (USERNAME_KEY.equals(preference.getKey())) {
            preferenceUser.setSummary((String) newValue);
        }


        if (PASSWORD_KEY.equals(preference.getKey())) {
            preferencePassword.setSummary((String) newValue);
        }


        if (TRANSMIT_METHOD_KEY.equals(preference.getKey())) {
            if (newValue.equals("0")) {
                preferenceMethod.setSummary("REST");
                preferenceMethod.setValueIndex(0);
                url = qrcodeObject.urlPubHTTP;
            } else {
                //TODO, CREATE A PROCEDURE TO SEND BY MQTT
                //TODO, REMOVE THIS CODE BELOW
                Context context = getApplicationContext();
                CharSequence text = "NOT YET IMPLEMENTED!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                //TODO, UNCOMMENT THIS CODE BELOW
                //preferenceMethod.setSummary("MQTT");
                ///preferenceMethod.setValueIndex(1);
                //url = qrcodeObject.urlPubMQTT;
            }
        }

        preferenceURL.setSummary(url);
        return false;
    }

    private void checkGps() {
        LocationManager mLocService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocService != null) {
            LocationProvider mLocProvider = mLocService.getProvider(LocationManager.GPS_PROVIDER);
            if (mLocProvider == null) {
                hideGPSCategory();
            }
        }
    }

    private void hideGPSCategory() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(getResources().getString(R.string.pref_gps_category));
        if (preferenceCategory != null) {
            preferenceCategory.removeAll();
            preferenceScreen.removePreference((Preference) preferenceCategory);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Config Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
