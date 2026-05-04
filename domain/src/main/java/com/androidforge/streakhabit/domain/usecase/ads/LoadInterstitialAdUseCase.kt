package com.androidforge.streakhabit.domain.usecase.ads

import com.androidforge.streakhabit.data.remote.ads.AdMobManager
import javax.inject.Inject

/**
 * Use case for initiating the loading of an AdMob interstitial ad.
 */
class LoadInterstitialAdUseCase @Inject constructor(
    private val adMobManager: AdMobManager
) {
    operator fun invoke() {
        adMobManager.loadInterstitialAd()
    }
}