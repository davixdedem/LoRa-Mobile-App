package com.magix.atcommand
import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class GPSService : Service() {

    private var timer: Timer? = null
    private var locationManager: LocationManager? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        startGPSScanning()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGPSScanning()
    }

    private fun startGPSScanning() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                requestLocationUpdates()
                handler.postDelayed(this, 2 * 60 * 1000) // Ripeti l'operazione ogni 5 minuti
            }
        }, 0)
    }

    private fun stopGPSScanning() {
        timer?.cancel()
        timer = null
    }

    private fun requestLocationUpdates() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager?.requestSingleUpdate(
            LocationManager.GPS_PROVIDER,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("GPSService", "Lat: $latitude, Long: $longitude")
                    val coords = latitude+longitude
                    // Puoi inviare la posizione a un altro componente qui
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    // Called when the provider status changes (e.g., GPS enabled or disabled)
                }

                override fun onProviderEnabled(provider: String) {
                    // Called when the provider (e.g., GPS) is enabled by the user
                }

                override fun onProviderDisabled(provider: String) {
                    // Called when the provider (e.g., GPS) is disabled by the user
                }
            },
            null
        )
    }
}
