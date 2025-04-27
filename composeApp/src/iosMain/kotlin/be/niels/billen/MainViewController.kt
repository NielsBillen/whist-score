package be.niels.billen

import androidx.compose.ui.window.ComposeUIViewController
import be.niels.billen.presentation.app.App

fun MainViewController() = ComposeUIViewController { App() }