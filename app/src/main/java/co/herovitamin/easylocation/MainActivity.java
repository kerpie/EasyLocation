package co.herovitamin.easylocation;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    @Bind(R.id.latitude)
    TextView latitude;
    @Bind(R.id.longitude)
    TextView longitude;

    GoogleApiClient google_api_client;
    Location last_known_location;
    LocationRequest my_location_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, R.string.gps_on, Toast.LENGTH_LONG).show();
        google_api_client.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(google_api_client.isConnected())
            startLocationUpdates();
        else
            Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(google_api_client, this);
    }

    protected synchronized void buildGoogleApiClient() {
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_wizard:
                Toast.makeText(this, R.string.the_app_wizard, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, R.string.connected , Toast.LENGTH_SHORT).show();
        last_known_location = LocationServices.FusedLocationApi.getLastLocation(google_api_client);
        if (last_known_location != null) {
            startLocationUpdates();
            update_ui();
        }else{
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(google_api_client, my_location_request, this);
    }

    private void update_ui() {
        latitude.setText(getResources().getString(R.string.latitude) + "\n" + String.valueOf(last_known_location.getLatitude()));
        longitude.setText(getResources().getString(R.string.longitude) + "\n" + String.valueOf(last_known_location.getLongitude()));
    }

    protected void createLocationRequest() {
        my_location_request = new LocationRequest();
        my_location_request.setInterval(1000);
        my_location_request.setFastestInterval(500);
        my_location_request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, R.string.connection_suspended, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        last_known_location = location;
        update_ui();
    }
}
