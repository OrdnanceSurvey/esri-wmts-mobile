package uk.os.elements.maps.esri.legacy;


import android.util.Log;

import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.SpatialReference;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * See:
 * http://help.arcgis.com/en/webapi/silverlight/apiref/ESRI.ArcGIS.Client~ESRI.ArcGIS.Client.TiledMapServiceLayer.html
 */
public class ZxyLayer extends TiledServiceLayer {

    private static String TAG = ZxyLayer.class.getSimpleName();

    private final TileInfo mTileInfo;
    private final Envelope mFullExtent;
    private final Envelope mInitialExtent;
    private final SpatialReference mDefaultSpatialReference;
    private final String mUrlTemplate;
    private Downloader mDownloader = new Downloader();

    private static final boolean INITIALIZE = true;

    /**
     *
     * @param defaultSpatialReference
     * @param tileInfo
     * @param fullExtent
     * @param initialExtent
     * @param urlTemplate containing {z}, {x} and {y} placeholders to level, row and column,
     *                    respectively.
     */
    public ZxyLayer(SpatialReference defaultSpatialReference, TileInfo tileInfo,
                    Envelope fullExtent, Envelope initialExtent, String urlTemplate) {
        super(INITIALIZE);
        mTileInfo = tileInfo;
        mFullExtent = fullExtent;
        mInitialExtent = initialExtent;
        mDefaultSpatialReference = defaultSpatialReference;
        mUrlTemplate = urlTemplate;

        setDefaultSpatialReference(defaultSpatialReference);

        try {
            getServiceExecutor().submit(new Runnable() {
                public void run() {
                    ZxyLayer.this.initLayer();
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "cannot initialize layer", e);
        }
    }

    protected void initLayer() {
        if (getID() == 0L) {
            nativeHandle = create();
        }

        if (getID() == 0L) {
            changeStatus(OnStatusChangedListener.STATUS.INITIALIZATION_FAILED);
            Log.e(TAG, "name =" + getName());
        } else {
            try {
                setDefaultSpatialReference(mDefaultSpatialReference);
                setFullExtent(mFullExtent);
                setTileInfo(mTileInfo);
                super.initLayer();
            } catch (Exception exception) {
                this.changeStatus(OnStatusChangedListener.STATUS.INITIALIZATION_FAILED);
                Log.e(TAG, "Zxy map name =" + getName(), exception);
            }
        }
    }

    protected byte[] getTile(int lev, int col, int row) throws Exception {
        String url = mUrlTemplate
                .replaceAll("\\{z\\}", String.valueOf(lev))
                .replaceAll("\\{x\\}", String.valueOf(col))
                .replaceAll("\\{y\\}", String.valueOf(row));

        Log.d(ZxyLayer.class.getSimpleName(), "UCalling: " + url);

        return mDownloader.getData(url);
    }

    private static class Downloader {
        protected final OkHttpClient mClient;

        private final CacheControl mCacheControl;

        private final Cache mCache;

        {
            File cacheDirectory = new File(System.getProperty("java.io.tmpdir") +
                    File.separator + "esri-maps-cache" + File.separator + "online");
            long cacheSize = 250L * 1024L * 1024L; // 250 MiB

            mCache = new Cache(cacheDirectory, cacheSize);

            mClient = new OkHttpClient.Builder()
                    .cache(mCache)
                    .build();

            mCacheControl = new CacheControl.Builder()
                    .maxStale(90, TimeUnit.DAYS)
                    .maxAge(3, TimeUnit.DAYS)
                    .build();
        }

        public byte[] getData(String url) throws IOException {
            Request request = new Request.Builder()
                    .cacheControl(mCacheControl)
                    .url(url)
                    .build();
            Response resp = mClient.newCall(request).execute();

            byte[] result = null;
            if (resp.body().contentType() != null) {
                result = resp.body().bytes();
            }
            return result;
        }
    }
}
