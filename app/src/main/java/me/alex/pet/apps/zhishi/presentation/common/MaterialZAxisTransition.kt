package me.alex.pet.apps.zhishi.presentation.common

import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis

object MaterialZAxisTransition {
    fun setupOrigin(fragment: Fragment) {
        fragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        fragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    fun setupDestination(fragment: Fragment) {
        fragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        fragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    fun setupOriginAndDestination(fragment: Fragment) {
        setupOrigin(fragment)
        setupDestination(fragment)
    }
}