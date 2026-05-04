package com.androidforge.streakhabit.data.remote.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.androidforge.streakhabit.core.common.Constants
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdMobManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var interstitialAd: InterstitialAd? = null
    private var isLoadingAd = false

    private val _adEvent = MutableSharedFlow<AdEvent>()
    val adEvent: SharedFlow<AdEvent> = _adEvent.asSharedFlow()

    sealed class AdEvent {
        object AdDismissed : AdEvent()
        object AdFailedToShow : AdEvent()
        object AdShown : AdEvent()
        object AdLoaded : AdEvent()
        object AdLoadFailed : AdEvent()
    }

    fun loadInterstitialAd() {
        if (interstitialAd == null && !isLoadingAd) {
            isLoadingAd = true
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdMobManager", "Interstitial Ad failed to load: ${adError.message}")
                    interstitialAd = null
                    isLoadingAd = false
                    _adEvent.tryEmit(AdEvent.AdLoadFailed)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("AdMobManager", "Interstitial Ad was loaded.")
                    interstitialAd = ad
                    isLoadingAd = false
                    _adEvent.tryEmit(AdEvent.AdLoaded)
                }
            })
        } else if (interstitialAd != null) {
            _adEvent.tryEmit(AdEvent.AdLoaded) // Ad is already loaded
        }
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit = {}) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMobManager", "Ad was dismissed.")
                    interstitialAd = null
                    loadInterstitialAd() // Pre-load the next ad
                    _adEvent.tryEmit(AdEvent.AdDismissed)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("AdMobManager", "Ad failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitialAd() // Pre-load the next ad
                    _adEvent.tryEmit(AdEvent.AdFailedToShow)
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("AdMobManager", "Ad showed on screen.")
                    _adEvent.tryEmit(AdEvent.AdShown)
                }
            }
            interstitialAd?.show(activity)
        } else {
            Log.d("AdMobManager", "Interstitial Ad wasn't ready yet.")
            onAdDismissed() // If ad is not ready, proceed without showing
            loadInterstitialAd() // Try loading it again
        }
    }

    fun isAdLoaded(): Boolean = interstitialAd != null
}