package com.example.marcin.eventagregator;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private List<LatLng> markers = new ArrayList<>();
    private LatLngBounds.Builder bounds;
    private ArrayList<String> titles;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("kot", "onCreateView");

        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        return v;
    }

    public void setMarkers(List<LatLng> markers, LatLngBounds.Builder bounds, ArrayList<String> titles)
    {
        Log.d("kot", "setMarkers");
        this.markers = markers;
        this.bounds = bounds;
        this.titles = titles;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.d("kot", "onMapReady");

        mMap = googleMap;
        for (int i=0; i< markers.size(); i++)
        {
            mMap.addMarker(new MarkerOptions().position(markers.get(i))).setTitle(titles.get(i));
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
        }
    }
}
