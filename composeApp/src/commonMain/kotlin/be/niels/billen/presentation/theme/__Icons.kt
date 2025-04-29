package be.niels.billen.presentation.theme

import androidx.compose.ui.graphics.vector.ImageVector
import be.niels.billen.presentation.theme.icons.Clubs
import be.niels.billen.presentation.theme.icons.Diamonds
import be.niels.billen.presentation.theme.icons.Hearts
import be.niels.billen.presentation.theme.icons.Spades
import kotlin.collections.List as ____KtList

public object Icons

private var __AllIcons: ____KtList<ImageVector>? = null

public val Icons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Clubs, Diamonds, Hearts, Spades)
    return __AllIcons!!
  }
