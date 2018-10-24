package city.bilboard.aryasoft.com.bilboardcity.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import city.bilboard.aryasoft.com.bilboardcity.R;

public class BoardMapActivityDetail extends AppCompatActivity
{
    private String Latitude;
    private String Longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_map_detail);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.board_map);
        Latitude=getIntent().getStringExtra("Latitude");
        Longitude=getIntent().getStringExtra("Longitude");
        mapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                LatLng BoardCoordinate = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
                googleMap.addMarker(new MarkerOptions().position(BoardCoordinate).title("مکان تابلو"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BoardCoordinate, 5));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

            }
        });
    }
}
