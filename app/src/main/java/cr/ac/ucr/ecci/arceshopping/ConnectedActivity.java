package cr.ac.ucr.ecci.arceshopping;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.db.FirebaseHelper;

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
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    endSession(true);
                }

                @Override
                public void onUnavailable()
                { //todo: try to find a new network before sending user back to login screen
                    /*
                    super.onUnavailable();
                    displayMessage("No se halló conexion a tiempo. Volviendo a login");
                    returnToLoginActivity();*/
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
                }

            };

    public void endSession(boolean disconnected){
        if(disconnected) {
            displayMessage("Se ha perdido la conexion.");
        }
        FirebaseHelper.logOut();
        returnToLoginActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        cManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    protected void onStart()
    {
        super.onStart();
        cManager.requestNetwork(networkRequest, networkCallback);
        if(!deviceIsConnected())
        {
            displayMessage("No hay conexion a internet");
        }
    }

    protected void onStop(){
        super.onStop();
        cManager.unregisterNetworkCallback(networkCallback);
    }



    protected void returnToLoginActivity()
    {
        //send user back to login screen
        //todo: close user session too
        finishAffinity(); // Clear backtstack so user cant return to visited activities

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
        DbUsers dbUsers = new DbUsers(this);
        dbUsers.logoutUser(sp.getString("userEmail",""));

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    protected boolean deviceIsConnected () {
        boolean isConnected = true;
        if(cManager.getActiveNetwork() == null)
        {
            isConnected = false;
        }

        return isConnected;
    }

    protected void displayMessage(String message)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
