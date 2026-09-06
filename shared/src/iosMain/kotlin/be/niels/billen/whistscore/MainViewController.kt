package be.niels.billen.whistscore

import androidx.compose.ui.window.ComposeUIViewController
import be.niels.billen.whistscore.presentation.app.App

fun MainViewController() = ComposeUIViewController { App() }