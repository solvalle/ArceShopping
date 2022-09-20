package cr.ac.ucr.ecci.arceshopping;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ConnectedActivity extends AppCompatActivity {
    private NetworkRequest networkRequest = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build();
    private ConnectivityManager cManager;

    private ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                System.out.println("Hay conexion!!!");
            }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    System.out.println("No hay conexion");

                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        cManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        cManager.registerNetworkCallback(networkRequest, networkCallback);
    }




}
