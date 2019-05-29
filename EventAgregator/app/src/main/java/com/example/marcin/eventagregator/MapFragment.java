package com.example.marcin.eventagregator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    private GoogleMap mMap;
    private List<MarkerOptions> markers;
    private View view;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission is granted.
                    setMapWithUserLocation();
                } else
                {
                    // Permission is not granted.
                    setMapWithoutUserLocation();
                }
                break;

        }
    }

    public void setMarkers(List<MarkerOptions> markers, Context context)
    {
        this.markers = markers;
        this.context = context;
    }

    LatLngBounds.Builder bounds = new LatLngBounds.Builder();

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        }
        else
        {
            // permission is granted
            setMapWithUserLocation();
        }



    }

    @SuppressLint("MissingPermission")
    private void setMapWithUserLocation()
    {
        mMap.setMyLocationEnabled(true);
        Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(getContext()).getLastLocation();
        try
        {
            locationTask
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
                    {
                        @Override
                        public void onSuccess(Location location)
                        {
                            if (location != null)
                            {
                                mMap.setMyLocationEnabled(true);
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                bounds.include(latLng);
                                bounds.include(new LatLng(latLng.latitude - 0.05, latLng.longitude - 0.05));
                                bounds.include(new LatLng(latLng.latitude + 0.05, latLng.longitude + 0.05));

                                for (MarkerOptions marker : markers)
                                {
                                    mMap.addMarker(marker);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
                                }
                            } else
                            {
                                Snackbar.make(view, "Nie znaleziono lokalizacji urządzenia.", Snackbar.LENGTH_LONG).show();
                                for (MarkerOptions marker : markers)
                                {
                                    bounds.include(marker.getPosition());
                                    mMap.addMarker(marker);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
                                }
                            }
                        }
                    });

        } catch (SecurityException e)
        {
            Log.d("exception: ", "Permission for getting location denied");
        }
    }

    private  void setMapWithoutUserLocation()
    {
        for (MarkerOptions marker : markers)
        {
            bounds.include(marker.getPosition());
            mMap.addMarker(marker);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
        }
    }


}



