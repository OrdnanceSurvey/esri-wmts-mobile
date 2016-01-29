package uk.os.elements.maps.esri.legacy.wmts;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Activity;

import android.util.Log;

import com.esri.android.map.MapView;
import com.esri.android.map.ogc.WMTSLayer;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.ogc.wmts.WMTSLayerInfo;
import com.esri.core.ogc.wmts.WMTSServiceInfo;
import com.esri.core.ogc.wmts.WMTSServiceMode;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // after the content of this activity is set
        // the map can be accessed from the layout
        mMapView = (MapView) findViewById(R.id.map);

        final String apiKey = getString(R.string.os_api_key);

        new AsyncTask<Void, Void, WMTSLayerInfo>() {

            @Override
            protected WMTSLayerInfo doInBackground(Void... params) {
                try {
                    // NO GOOD: WMTSServiceInfo serviceInfo = WMTSServiceInfo.fetch("https://api2.ordnancesurvey.co.uk/mapping_api/v1/service/wmts?key=apikey");
                    // > because it defaults to REST service mode
                    UserCredentials userCredentials = new UserCredentials();
                    userCredentials.setAuthenticationType(UserCredentials.AuthenticationType.NONE);
                    userCredentials.setSSLRequired(true);
                    Log.d(TAG, "Checkpoint 1");

                    String url = "https://api2.ordnancesurvey.co.uk/mapping_api/v1/service/wmts?key=" + apiKey;
                    WMTSServiceInfo serviceInfo = WMTSServiceInfo.fetch(url, userCredentials,
                            WMTSServiceMode.KVP);
                    Log.d(TAG, "Checkpoint 2");

                    WMTSLayerInfo layerInfo = serviceInfo.getLayerInfos().get(0);
                    Log.d(TAG, "Checkpoint 3");
                    return layerInfo;
                } catch (Exception e) {
                    Log.e(TAG, "problem getting WMTS service info", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WMTSLayerInfo layerInfo) {
                if (layerInfo == null) {
                    return;
                }
                Log.d(TAG, "Checkpoint 4");
                WMTSLayer layer = new WMTSLayer(layerInfo, SpatialReference.create(3857));
                Log.d(TAG, "Checkpoint 5");
                layer.layerInitialise();
                Log.d(TAG, "Checkpoint 6");
                mMapView.addLayer(layer);
                Log.d(TAG, "Checkpoint 8");
            }
        }.execute();
    }
}
