package laanayabdrzak.ma.rxconnectivitystatus;

/**
 * Created by abderrazak on 13/04/2016.
 *
 * Class Utility for check network states
 */
public enum UtilityNetworkState {

    /**
    * State available with there description
    * */
    UNKNOWN(""),
    WIFI_CONNECTED(""),
    WIFI_CONNECTED_NETWORK_AVAILABLE("internet available"),
    WIFI_CONNECTED_NETWORK_NOT_AVAILABLE("internet not available"),
    MOBILE_CONNECTED(""),
    OFFLINE("");

    public final String description;

    UtilityNetworkState(final String description) {
        this.description = description;
    }

}
