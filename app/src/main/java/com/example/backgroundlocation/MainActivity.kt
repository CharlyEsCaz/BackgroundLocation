package com.example.backgroundlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.backgroundlocation.databinding.ActivityMainBinding
import com.example.backgroundlocation.services.ScreenLockBroadcastReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var vBind: ActivityMainBinding
    private lateinit var tts: TextToSpeech


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBind = DataBindingUtil.setContentView(this, R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tts = TextToSpeech(this, this)

        checkPermissions()

        val br: BroadcastReceiver = ScreenLockBroadcastReceiver {
            val mp = MediaPlayer.create(this, R.raw.coins)
            mp.start()
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val message =
                        "Nueva posicion: ${location?.latitude ?: 0.0}, ${location?.longitude ?: 0.0}"
                    readLocation(message)
                    vBind.tvTitle.text = message
                    Log.d(
                        "BACKGROUND_TEST",
                        "Location: ${location?.latitude ?: 0.0}, ${location?.longitude ?: 0.0}"
                    )
                }
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SHUTDOWN)
        }
        registerReceiver(br, filter)
    }

    private fun readLocation(text: String) {
        if (text.isNotEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
        }
    }

    private fun checkPermissions() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 425
            )
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.ROOT
        }
    }
}