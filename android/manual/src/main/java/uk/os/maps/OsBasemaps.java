package uk.os.maps;

import com.esri.android.map.Layer;
import com.esri.core.geometry.SpatialReference;

import uk.os.elements.maps.esri.legacy.BuildConfig;
import uk.os.elements.maps.esri.legacy.OsMapsLayer;

public class OsBasemaps {

    public static String sKey = "UNDEFINED";

    public static void setKey(String key) {
        sKey = key;
    }

    public static class EPSG_27700 {

        public static Layer leisure() {
            return get(OsMapsLayer.Type.LEISURE);
        }

        public static Layer light() {
            return get(OsMapsLayer.Type.LIGHT);
        }

        public static Layer night() {
            return get(OsMapsLayer.Type.NIGHT);
        }

        public static Layer outdoor() {
            return get(OsMapsLayer.Type.OUTDOOR);
        }

        public static Layer road() {
            return get(OsMapsLayer.Type.ROAD);
        }

        private static Layer get(OsMapsLayer.Type type) {
            return OsMapsLayer.create(type, OsMapsLayer.EPGS_27700, sKey);
        }
    }

    public static Layer light() {
        return get(OsMapsLayer.Type.LIGHT);
    }

    public static Layer night() {
        return get(OsMapsLayer.Type.NIGHT);
    }

    public static Layer outdoor() {
        return get(OsMapsLayer.Type.OUTDOOR);
    }

    public static Layer road() {
        return get(OsMapsLayer.Type.ROAD);
    }

    private static Layer get(OsMapsLayer.Type type) {
        return OsMapsLayer.create(type, OsMapsLayer.EPSG_3857, sKey);
    }
}
