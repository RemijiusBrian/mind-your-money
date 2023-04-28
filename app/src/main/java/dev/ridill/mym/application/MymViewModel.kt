package dev.ridill.mym.application

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.mym.core.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MymViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val preferences = preferencesManager.preferences
    val appTheme = preferences.map { it.theme }.distinctUntilChanged()
    val materialYouTheme = preferences.map { it.materialYouTheme }.distinctUntilChanged()
}