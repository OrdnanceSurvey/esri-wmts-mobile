package uk.os.maps;

import android.app.Application;

import uk.os.elements.maps.esri.legacy.R;

public class OsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String apiKey = getString(R.string.os_api_key);
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("please set your API key");
        }
        OsBasemaps.setKey(apiKey);
    }
}
