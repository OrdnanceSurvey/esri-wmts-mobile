package uk.os.elements.maps.esri.legacy;

import android.os.Bundle;

import android.app.Activity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.esri.android.map.Layer;

import uk.os.maps.OsBasemaps;
import uk.os.maps.OsMapView;

public class MainActivity extends Activity {

  private OsMapView mMapView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mMapView = (OsMapView)findViewById(R.id.map);

    mMapView.setBasemap(OsBasemaps.EPSG_27700.leisure());
    mMapView.setReadyListener(new OsMapView.ReadyListener() {
      @Override
      public void onMapReady() {
        Toast.makeText(MainActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.layers, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Layer layer;
    switch (item.getItemId()) {
      case R.id.layer_light: {
        layer = OsBasemaps.light();
        break;
      }
      case R.id.layer_night: {
        layer = OsBasemaps.night();
        break;
      }
      case R.id.layer_outdoor: {
        layer = OsBasemaps.outdoor();
        break;
      }
      case R.id.layer_road: {
        layer = OsBasemaps.road();
        break;
      }
      case R.id.layer_leisure_27700: {
        layer = OsBasemaps.EPSG_27700.leisure();
        break;
      }
      case R.id.layer_light_27700: {
        layer = OsBasemaps.EPSG_27700.light();
        break;
      }
      case R.id.layer_night_27700: {
        layer = OsBasemaps.EPSG_27700.night();
        break;
      }
      case R.id.layer_outdoor_27700: {
        layer = OsBasemaps.EPSG_27700.outdoor();
        break;
      }
      case R.id.layer_road_27700: {
        layer = OsBasemaps.EPSG_27700.road();
        break;
      }
      default: {
        throw new IllegalArgumentException("unknown layer");
      }
    }
    mMapView.setBasemap(layer);
    return true;
  }
}
