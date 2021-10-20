package com.example.backgroundlocation.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenLockBroadcastReceiver(private val cb: () -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_USER_PRESENT)) {
            cb()
        }
    }
}