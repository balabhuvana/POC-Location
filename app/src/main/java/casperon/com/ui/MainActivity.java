package casperon.com.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;

import database.CommentsDataSource;
import model.LocationModel;
import model.StackOverflowQuestions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Location sample.
 * <p>
 * Demonstrates use of the Location API to retrieve the last known location for a device.
 * This sample uses Google Play services (GoogleApiClient) but does not need to authenticate a user.
 * See https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart if you are
 * also using APIs that need authentication.
 */
public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "MainActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private CommentsDataSource datasource;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));
        datasource = new CommentsDataSource(this);
        datasource.open();


        startAlert();
        networkTask();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }*/
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.format("%s: %f", "mLatitudeLabel",
                    mLastLocation.getLatitude()));
            mLongitudeText.setText(String.format("%s: %f", "mLongitudeLabel",
                    mLastLocation.getLongitude()));

            String lat = String.valueOf(mLastLocation.getLatitude());
            String longi = String.valueOf(mLastLocation.getLongitude());

            SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
            String currentDateAndTime = sdf.format(new Date());

            datasource.createComment(lat + " - " + longi + " - " + currentDateAndTime);

        } else {
            Toast.makeText(this, "no_location_detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void nextScreen(View mView) {
        Intent mIntent = new Intent(getApplicationContext(), SecondActivity.class);
        startActivity(mIntent);
    }

    public static class MyReceiver extends BroadcastReceiver {

        public MyReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            networkTask();
        }
    }

    public void startAlert() {
        Intent alarmIntent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 30000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

        buildGoogleApiClient();


        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public static void networkTask() {

        stackOverFlowQuestion();

    }

    public static void stackOverFlowQuestion() {
        Call<StackOverflowQuestions> call = MyApplication.apiService.loadQuestions("android");
        call.enqueue(new Callback<StackOverflowQuestions>() {
            @Override
            public void onResponse(Call<StackOverflowQuestions> call, Response<StackOverflowQuestions> response) {
                Log.d("stackOverFlowQues -> ", "" + response.body().getItems().size());
            }

            @Override
            public void onFailure(Call<StackOverflowQuestions> call, Throwable t) {
                Log.d("stackOverFlowQues -> ", "onFailure");
            }

        });

    }

    public  void getLocationTask() {
        buildGoogleApiClient();
    }

}

