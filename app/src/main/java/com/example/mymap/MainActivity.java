package com.example.mymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private LocationComponent locationComponent;
    private boolean isLocationTracking = false;
    private FloatingActionButton locationButton;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Mapbox
        Mapbox.getInstance(this);
        setContentView(R.layout.activity_main);

        // Init MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(mapboxMap -> {
            // Set the map style to use TMS tiles
            String osmUrl = "https://tile.openstreetmap.org/{x}/{y}/{z}.png";
            String styleJson = "{\n" +
                    "  \"version\": 8,\n" +
                    "  \"sources\": {\n" +
                    "    \"tms-tiles\": {\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"tiles\": [\"" + osmUrl + "\"],\n" +
                    "      \"tileSize\": 256\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"layers\": [\n" +
                    "    {\n" +
                    "      \"id\": \"osm-tiles\",\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"source\": \"osm-tiles\",\n" +
                    "      \"minzoom\": 0,\n" +
                    "      \"maxzoom\": 22\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            // Disable default compass
            mapboxMap.getUiSettings().setCompassEnabled(false);

            // ImageView for Compass
            ImageView compassImageView = findViewById(R.id.compass);

            // Set listener for map rotation
            mapboxMap.addOnCameraMoveListener(() -> {
                float bearing = (float) mapboxMap.getCameraPosition().bearing;
                // Rotate the compass icon based on map bearing
                compassImageView.animate().rotation(-bearing).setDuration(200).start();
            });

            mapboxMap.setStyle(new Style.Builder().fromJson(styleJson), style -> {
                // Add image for location pin
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_my_location_24);

                // Check if bitmap was loaded correctly
                if (bitmap != null) {
                    style.addImage("location_pin", bitmap);
                } else {
                    Log.e("MainActivity", "Failed to load image for location pin.");
                    // Optionally: Add a default image or handle the error
                }

                // Add marker locations
                LatLng locationCamera = new LatLng(-7.7694009, 110.382087);
                LatLng location1 = new LatLng(-7.7750186532415055, 110.37445757178699);
                LatLng location2 = new LatLng(-7.773659921467422, 110.37368949522963);
                LatLng location3 = new LatLng(-7.7742863176896195, 110.37476680765322);
                LatLng location4 = new LatLng(-7.774155076715827, 110.37481482649366);
                LatLng location5 = new LatLng(-7.77441932768577, 110.37338124844051);
                LatLng location6 = new LatLng(-7.775093900759946, 110.37312421569018);
                LatLng location7 = new LatLng(-7.775361978815284, 110.3742658323759);
                LatLng location8 = new LatLng(-7.775262020590699, 110.38040646673782);

                // Features for each marker
                Feature feature1 = Feature.fromGeometry(Point.fromLngLat(location1.getLongitude(), location1.getLatitude()));
                Feature feature2 = Feature.fromGeometry(Point.fromLngLat(location2.getLongitude(), location2.getLatitude()));
                Feature feature3 = Feature.fromGeometry(Point.fromLngLat(location3.getLongitude(), location3.getLatitude()));
                Feature feature4 = Feature.fromGeometry(Point.fromLngLat(location4.getLongitude(), location4.getLatitude()));
                Feature feature5 = Feature.fromGeometry(Point.fromLngLat(location5.getLongitude(), location5.getLatitude()));
                Feature feature6 = Feature.fromGeometry(Point.fromLngLat(location6.getLongitude(), location6.getLatitude()));
                Feature feature7 = Feature.fromGeometry(Point.fromLngLat(location7.getLongitude(), location7.getLatitude()));
                Feature feature8 = Feature.fromGeometry(Point.fromLngLat(location8.getLongitude(), location8.getLatitude()));

                // Combine features into one feature collection
                FeatureCollection featureCollection = FeatureCollection.fromFeatures(new Feature[]{
                        feature1, feature2, feature3, feature4, feature5, feature6, feature7, feature8
                });

                // Add GeoJson source
                GeoJsonSource geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
                style.addSource(geoJsonSource);

                // Display markers
                SymbolLayer symbolLayer = new SymbolLayer("marker-layer", "marker-source")
                        .withProperties(
                                PropertyFactory.iconImage("marker-icon-id"),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconSize(1.0f)  // Increased size for better visibility
                        );


                style.addLayer(symbolLayer);

                // Set initial camera position
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(locationCamera).zoom(10.0).build());

                // Zoom in/out buttons
                FloatingActionButton zoomInButton = findViewById(R.id.btn_ZoomIn);
                FloatingActionButton zoomOutButton = findViewById(R.id.btn_ZoomOut);

                // Zoom In functionality
                zoomInButton.setOnClickListener(v -> {
                    CameraPosition position = new CameraPosition.Builder()
                            .target(mapboxMap.getCameraPosition().target)
                            .zoom(mapboxMap.getCameraPosition().zoom + 1) // Increase zoom level
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500);
                });

                // Zoom Out functionality
                zoomOutButton.setOnClickListener(v -> {
                    CameraPosition position = new CameraPosition.Builder()
                            .target(mapboxMap.getCameraPosition().target)
                            .zoom(mapboxMap.getCameraPosition().zoom - 1) // Decrease zoom level
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500);
                });

                // Location component setup and add location puck
                checkLocationPermission(style, mapboxMap);

                // Location button functionality
                locationButton = findViewById(R.id.btn_location);
                locationButton.setOnClickListener(v -> {
                    if (locationComponent != null && locationComponent.getLastKnownLocation() != null) {
                        isLocationTracking = !isLocationTracking;
                        if (isLocationTracking) {
                            // User's last known location
                            LatLng userLocation = new LatLng(
                                    locationComponent.getLastKnownLocation().getLatitude(),
                                    locationComponent.getLastKnownLocation().getLongitude());

                            // Zoom into user location at level 13
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(userLocation) // Set camera to user location
                                    .zoom(13) // Set zoom level
                                    .build();

                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);

                            // Change icon to location icon
                            locationButton.setImageResource(R.drawable.baseline_my_location_24);
                        } else {
                            locationComponent.setCameraMode(CameraMode.NONE);
                            locationButton.setImageResource(R.drawable.baseline_location_disabled_24);
                        }
                    } else {
                        Toast.makeText(this, "Lokasi Tidak Tersedia", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    // Check location permissions
    private void checkLocationPermission(Style style, MapboxMap mapboxMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Enable location component
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // Check if last known location is available
            if (locationComponent.getLastKnownLocation() != null) {
                // Center map to user location
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(
                                locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude()))
                        .zoom(10.0)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            } else {
                // Handle the case where location is not available yet
                Log.e("MainActivity", "Last known location is null. Unable to set camera position.");
                Toast.makeText(this, "Unable to obtain location. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }
}