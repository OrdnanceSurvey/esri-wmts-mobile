package uk.os.elements.maps.esri.legacy;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

/**
 * Note: this should extend WMTS layer to autoconfigure!
 */
public class OsMapsLayer extends ZxyLayer {

    public static final int EPGS_27700 = 27700;
    public static final int EPSG_3857 = 3857;

    private static String GENERIC_TEMPLATE = "https://api2.ordnancesurvey.co.uk/mapping_api/v1/service/wmts?key={key}&SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&LAYER={LAYER}&STYLE=&FORMAT=image/png&TILEMATRIXSET=EPSG:{SRID_INT}&TILEMATRIX=EPSG:{SRID_INT}:{z}&TILEROW={y}&TILECOL={x}";

    public enum Type {OUTDOOR, ROAD, LIGHT, NIGHT, LEISURE}

    /**
     * @param defaultSpatialReference
     * @param tileInfo
     * @param fullExtent
     * @param initialExtent
     * @param urlTemplate containing {z}, {x} and {y} placeholders to level, row and column,
     */
    private OsMapsLayer(SpatialReference defaultSpatialReference, TileInfo tileInfo,
                        Envelope fullExtent, Envelope initialExtent, String urlTemplate) {
        super(defaultSpatialReference, tileInfo, fullExtent, initialExtent, urlTemplate);
    }

    public static OsMapsLayer create(Type type, int srid, String key) {
        boolean isGood = srid == EPGS_27700 || srid == EPSG_3857;
        if (!isGood) {
            throw new IllegalArgumentException("unsupported SRID");
        }

        SpatialReference spatialReference = SpatialReference.create(srid);
        TileInfo tileInfo;
        Envelope fullExtent;
        Envelope initialExtent;
        String urlTemplate;
        switch (srid) {
            case EPGS_27700:
                tileInfo = Epsg27700.TileSpec.TILE_INFO;
                fullExtent = Epsg27700.FULL_EXTENT;
                initialExtent = Epsg27700.INITIAL_EXTENT;
                urlTemplate = Epsg27700.getUrlTemplate(type);
                break;
            case EPSG_3857:
                tileInfo = Epsg3857.TileSpec.TILE_INFO;
                fullExtent = Epsg3857.FULL_EXTENT;
                initialExtent = Epsg3857.INITIAL_EXTENT;
                urlTemplate = Epsg3857.getUrlTemplate(type);
                break;
            default: throw new IllegalStateException();
        }
        urlTemplate = urlTemplate.replaceAll("\\{key\\}", key);

        return new OsMapsLayer(spatialReference, tileInfo, fullExtent, initialExtent, urlTemplate);
    }

    public String getAttributionText() {
        return "<a href=\"https://www.ordnancesurvey.co.uk/business-and-government/licensing/crown-copyright.html\">Crown copyright</a>";
    }

    private static class Epsg27700 {

        public static final Envelope INITIAL_EXTENT = new Envelope(-675199.9830666328,
                -500356.44737956114, 1391199.9830666329, 1780356.4473795616);
        public static final Envelope FULL_EXTENT = new Envelope(1393.0196, 13494.9764, 671196.3657,
            1230275.0454);

        public static String getUrlTemplate(Type type) {
            String layerName;

            switch (type) {
                case LEISURE:
                    layerName = "Leisure%2027700";
                    break;
                case LIGHT:
                    layerName = "Light%2027700";
                    break;
                case NIGHT:
                    layerName = "Night%2027700";
                    break;
                case OUTDOOR:
                    layerName = "Outdoor%2027700";
                    break;
                case ROAD:
                    layerName = "Road%2027700";
                    break;
                default: throw new IllegalArgumentException("unsupported layer");
            }

            return OsMapsLayer.getUrlTemplate(27700, layerName);
        }

        /**
         * The tile specification for OS Maps.
         * Note: this should be automatically be obtained from the WMTS service endpoint.
         * TODO: remove
         */
        private static class TileSpec {
            private static final int TILE_DPI = 96;
            //    private static final int TILE_DPI = 91;
            private static final int TILE_WIDTH = 256;
            private static final int TILE_HEIGHT = 256;

            private static final double[] RESOLUTIONS = new double[]{
                    896.0,
                    448.0,
                    224.0,
                    112.0,
                    56.0,
                    28.0,
                    14.0,
                    7.0,
                    3.5,
                    1.75,
                    0.875,
                    0.4375,
                    0.21875,
                    0.109375
            };
            private static final double[] SCALES = new double[]{
                    3386456.693,
                    1693228.346,
                    846614.1732,
                    423307.0866,
                    211653.5433,
                    105826.7717,
                    52913.38583,
                    26456.69291,
                    13228.34646,
                    6614.173228,
                    3307.086614,
                    1653.014173,
                    826.5070866,
                    412.7244094
            };

            private static final int LEVELS = RESOLUTIONS.length;

            private static final Point ORIGIN = new Point(-238375.0, 1376256.0D);

            private static final TileInfo TILE_INFO = new TileInfo(ORIGIN, SCALES, RESOLUTIONS, LEVELS, TILE_DPI,
                    TILE_WIDTH, TILE_HEIGHT);
        }
    }

    private static class Epsg3857 {

        private static final Envelope FULL_EXTENT = new Envelope(-2.003750834278E7D, -2.003750834278E7D,
                2.003750834278E7D, 2.003750834278E7D);
        private static final Envelope INITIAL_EXTENT = FULL_EXTENT;

        public static String getUrlTemplate(Type type) {
            String layerName;

            switch (type) {
                case LEISURE:
                    layerName = "Leisure%203857";
                    break;
                case LIGHT:
                    layerName = "Light%203857";
                    break;
                case NIGHT:
                    layerName = "Night%203857";
                    break;
                case OUTDOOR:
                    layerName = "Outdoor%203857";
                    break;
                case ROAD:
                    layerName = "Road%203857";
                    break;
                default: throw new IllegalArgumentException("unsupported layer");
            }

            return OsMapsLayer.getUrlTemplate(3857, layerName);
        }

        /**
         * The tile specification for OS Maps.
         * Note: this should be automatically be obtained from the WMTS service endpoint.
         * TODO: remove
         */
        private static class TileSpec {
            private static final int TILE_DPI = 96;
            private static final int TILE_WIDTH = 256;
            private static final int TILE_HEIGHT = 256;

            private static final double[] RESOLUTIONS = new double[]{
                    156543.0339279998D,
                    78271.5169639999D,
                    39135.7584820001D,
                    19567.8792409999D,
                    9783.93962049996D,
                    4891.96981024998D,
                    2445.98490512499D,
                    1222.99245256249D,
                    611.49622628138D,
                    305.748113140558D,
                    152.874056570411D,
                    76.4370282850732D,
                    38.2185141425366D,
                    19.1092570712683D,
                    9.55462853563415D,
                    4.77731426794937D,
                    2.38865713397468D,
                    1.19432856685505D,
                    0.597164283559817D,
                    0.298582141647617D,
                    0.149291070823808D,
                    0.074645535411904D,
                    0.037322767705952D,
                    0.018661383985268D
            };

            private static final double[] SCALES = new double[]{
                    .91657527591555E8D,
                    2.95828763795777E8D,
                    1.47914381897889E8D,
                    7.3957190948944E7D,
                    3.6978595474472E7D,
                    1.8489297737236E7D,
                    9244648.868618D,
                    4622324.434309D,
                    2311162.217155D,
                    1155581.108577D,
                    577790.554289D,
                    288895.277144D,
                    144447.638572D,
                    72223.819286D,
                    36111.909643D,
                    18055.954822D,
                    9027.977411D,
                    4513.988705D,
                    2256.994353D,
                    1128.497176D,
                    564.248588D,
                    282.124294D,
                    141.062147D,
                    70.531074D
            };

            private static final int LEVELS = RESOLUTIONS.length;

            private static final Point ORIGIN = new Point(-2.003750834278E7D, 2.003750834278E7D);

            private static final TileInfo TILE_INFO = new TileInfo(ORIGIN, SCALES, RESOLUTIONS, LEVELS, TILE_DPI,
                    TILE_WIDTH, TILE_HEIGHT);
        }
    }

    public static String getUrlTemplate(int srid, String layerName) {
        return GENERIC_TEMPLATE
                .replaceAll("\\{SRID_INT\\}", String.valueOf(srid))
                .replaceAll("\\{LAYER\\}", layerName);
    }
}
