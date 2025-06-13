package com.berkayalagoz.aifitnessapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class AiFitnessApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            
            // Configure Firestore with better offline handling
            val firestore = FirebaseFirestore.getInstance()
            
            // Only set settings if not already set
            try {
                val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)  // Enable offline persistence
                    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
                
                firestore.firestoreSettings = settings
                
                // Enable network first, then fall back to cache
                firestore.enableNetwork()
                
                Log.d("AiFitnessApp", "Firestore offline persistence enabled successfully")
            } catch (e: Exception) {
                Log.w("AiFitnessApp", "Firestore settings already configured or failed to configure", e)
            }
            
        } catch (e: Exception) {
            Log.e("AiFitnessApp", "Failed to initialize Firebase", e)
        }
    }
} 