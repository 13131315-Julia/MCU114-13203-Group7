package com.example.lab14

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Button

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var btnDriving: Button
    private lateinit var btnWalking: Button
    private lateinit var btnBicycling: Button

    private var currentPolyline: Polyline? = null
    private val taipei101 = LatLng(25.033611, 121.565000)
    private val taipeiMainStation = LatLng(25.047924, 121.517081)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && requestCode == 0) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                loadMap()
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化按鈕
        btnDriving = findViewById(R.id.btnDriving)
        btnWalking = findViewById(R.id.btnWalking)
        btnBicycling = findViewById(R.id.btnBicycling)

        // 設置按鈕點擊監聽器
        btnDriving.setOnClickListener { drawRoute(TravelMode.DRIVING) }
        btnWalking.setOnClickListener { drawRoute(TravelMode.WALKING) }
        btnBicycling.setOnClickListener { drawRoute(TravelMode.BICYCLING) }

        loadMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val isAccessFineLocationGranted = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isAccessCoarseLocationGranted = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isAccessFineLocationGranted && isAccessCoarseLocationGranted) {
            map.isMyLocationEnabled = true

            // 添加標記
            map.addMarker(MarkerOptions().position(taipei101).title("台北101"))
            map.addMarker(MarkerOptions().position(taipeiMainStation).title("台北車站"))

            // 初始繪製步行路線
            drawRoute(TravelMode.WALKING)

            // 移動視角到兩個地點之間
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.04, 121.54), 13f))
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
        }
    }

    private fun drawRoute(travelMode: TravelMode) {
        val apiKey = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY")

        if (apiKey.isNullOrEmpty()) {
            return
        }

        val geoApiContext = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()

        // 移除現有的路線
        currentPolyline?.remove()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val directionsResult = DirectionsApi.newRequest(geoApiContext)
                    .origin(com.google.maps.model.LatLng(taipei101.latitude, taipei101.longitude))
                    .destination(com.google.maps.model.LatLng(taipeiMainStation.latitude, taipeiMainStation.longitude))
                    .mode(travelMode)
                    .await()

                CoroutineScope(Dispatchers.Main).launch {
                    if (directionsResult.routes.isNotEmpty()) {
                        val route = directionsResult.routes[0]
                        val decodedPath = PolyUtil.decode(route.overviewPolyline.encodedPath)

                        val polylineOptions = PolylineOptions()
                            .addAll(decodedPath)
                            .width(15f)

                        // 根據交通模式設置不同顏色
                        when (travelMode) {
                            TravelMode.DRIVING -> polylineOptions.color(Color.BLUE)
                            TravelMode.WALKING -> polylineOptions.color(Color.GREEN)
                            TravelMode.BICYCLING -> polylineOptions.color(Color.RED)
                            else -> polylineOptions.color(Color.BLACK)
                        }

                        currentPolyline = map.addPolyline(polylineOptions)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}