package com.androidforge.streakhabit.domain.usecase.ads

import android.app.Activity
import com.androidforge.streakhabit.data.remote.ads.AdMobManager
import javax.inject.Inject

/**
 * Use case for displaying a previously loaded AdMob interstitial ad.
 * Provides a callback for when the ad is dismissed, allowing the caller to proceed with their flow.
 */
class ShowInterstitialAdUseCase @Inject constructor(
    private val adMobManager: AdMobManager
) {
    operator fun invoke(activity: Activity, onAdDismissed: () -> Unit = {}) {
        adMobManager.showInterstitialAd(activity, onAdDismissed)
    }
}