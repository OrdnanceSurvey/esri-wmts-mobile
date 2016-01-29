package uk.os.maps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

public class OsMapView extends FrameLayout {

    public interface ReadyListener {
        void onMapReady();
    }

    private static Point DEFAULT_CENTER_POINT = new Point(0.1275, 51.5);
    private static double DEFAULT_SCALE = 50000;

    private MapView mMapView;
    private Layer mBasemap;
    private ReadyListener mReadyListener;

    public OsMapView(Context context) {
        super(context);
        init();
    }

    public OsMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OsMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setReadyListener(ReadyListener readyListener) {
        mReadyListener = readyListener;
    }

    public void setBasemap(Layer basemap) {
        boolean isExistingBasemap = mBasemap != null;
        if (isExistingBasemap) {
            mMapView.removeLayer(mBasemap);
        }
        mBasemap = basemap;

        SpatialReference mapSrid = getMapSrid();
        SpatialReference layerSrid = getLayerSrid(basemap);

        boolean isSameSrid = mapSrid == null ? layerSrid == null : mapSrid.equals(layerSrid);
        if (isSameSrid) {
            mMapView.addLayer(basemap);
        } else {
            Point currentCenter = getWgs84Point();
            if (currentCenter == null) {
                currentCenter = DEFAULT_CENTER_POINT;
            }

            double currentScale = mMapView.getScale();
            if (currentScale <= 0D || Double.isNaN(currentScale)) {
                currentScale = DEFAULT_SCALE;
            }

            final Point mPoint = currentCenter;
            final double scale = currentScale;

            ViewGroup parent = (ViewGroup) mMapView.getParent();
            ViewGroup.LayoutParams params = mMapView.getLayoutParams();
            parent.removeView(mMapView);
            mMapView = createNewMapView(mPoint, scale);

            parent.addView(mMapView, params);
            mMapView.addLayer(basemap, 0);
        }
    }

    private MapView createNewMapView(final Point p, final double scale) {
        final MapView mapView = new MapView(getContext());
        mapView.setAllowRotationByPinch(true);

        // Set the Esri logo to be visible, and enable map to wrap around date line.
        mapView.setEsriLogoVisible(true);
        mapView.enableWrapAround(true);

        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {

            private static final long serialVersionUID = 1L;

            public void onStatusChanged(Object source, STATUS status) {
                // Set the map extent once the map has been initialized, and the basemap is added
                // or changed; this will be indicated by the layer initialization of the basemap layer. As there is only
                // a single layer, there is no need to check the source object.

                if (status == STATUS.INITIALIZED && source == mMapView) {
                    mapView.centerAt(p.getY(), p.getX(), false);
                    mapView.setScale(scale);

                    if (mReadyListener != null) {
                        mReadyListener.onMapReady();
                    }
                    return;
                }
            }
        });

        return mapView;
    }

    private SpatialReference getLayerSrid(Layer layer) {
        SpatialReference spatialReference = layer.getSpatialReference();
        if (spatialReference == null) {
            spatialReference = layer.getDefaultSpatialReference();
        }
        return spatialReference;
    }

    private SpatialReference getMapSrid() {
        SpatialReference mapSrid = mMapView.getSpatialReference();
        if (mapSrid == null) {
            for (Layer l : mMapView.getLayers()) {
                if (l.getSpatialReference() != null) {
                    mapSrid = l.getSpatialReference();
                    break;
                } else if (l.getDefaultSpatialReference() != null) {
                    mapSrid = l.getDefaultSpatialReference();
                    break;
                }
            }
        }
        return mapSrid;
    }

    /**
     * NOTE: USED TO GET THE CENTER OF AN EXISTING BASEMAP
     *
     * @return a WGS84 point or null if undefined center
     */
    private Point getWgs84Point() {
        if (mMapView == null) {
            return null;
        }
        Point existingCenter = mMapView.getCenter();
        SpatialReference spatialReference = mMapView.getSpatialReference();

        boolean hasSpatialReference = spatialReference != null;
        if (hasSpatialReference) {
            if (!spatialReference.isWGS84()) {
                Point p = existingCenter;
                SpatialReference in = spatialReference;
                SpatialReference out = SpatialReference.create(SpatialReference.WKID_WGS84);
                return (Point) GeometryEngine.project(p, in, out);
            } else {
                return existingCenter;
            }
        }
        return null;
    }

    private void init() {
        Point currentCenter = new Point(0.1275, 51.5);
        double scale = 25000;
        mMapView = createNewMapView(currentCenter, scale);
        addView(mMapView);
    }
}
