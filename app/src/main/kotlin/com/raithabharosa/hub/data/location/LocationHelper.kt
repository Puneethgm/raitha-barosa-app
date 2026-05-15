package com.raithabharosa.hub.data.location

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// This is a simple helper placeholder to request coarse location permission and return null/placeholder.
// For production, integrate Google Play Services FusedLocationProvider for better accuracy.

@Composable
fun rememberLocationPermissionRequest(): Pair<() -> Unit, () -> Boolean> {
    val granted = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> granted.value = ok }
    val request = { launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
    val isGranted = { granted.value }
    return Pair(request, isGranted)
}
