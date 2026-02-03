package ca.amandeep.nycairportsecuritylinewaits.ui.main

import androidx.compose.runtime.Composable
import ca.amandeep.nycairportsecuritylinewaits.ui.preview.MarketingEwrPreview
import ca.amandeep.nycairportsecuritylinewaits.ui.preview.MarketingJfkPreview
import ca.amandeep.nycairportsecuritylinewaits.ui.preview.MarketingLgaPreview
import ca.amandeep.nycairportsecuritylinewaits.ui.preview.MarketingSelectionPreview
import ca.amandeep.nycairportsecuritylinewaits.ui.preview.MarketingSelectionSingleThemePreview
import ca.amandeep.nycairportsecuritylinewaits.ui.preview.PreviewPixel9DayNight
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@PreviewPixel9DayNight
@Composable
private fun AirportSelectionScreenshot() = MarketingSelectionPreview()

@PreviewTest
@PreviewPixel9DayNight
@Composable
private fun AirportSelectionSingleThemeScreenshot() = MarketingSelectionSingleThemePreview(darkTheme = false)

@PreviewTest
@PreviewPixel9DayNight
@Composable
private fun AirportEwrScreenshot() = MarketingEwrPreview()

@PreviewTest
@PreviewPixel9DayNight
@Composable
private fun AirportJfkScreenshot() = MarketingJfkPreview()

@PreviewTest
@PreviewPixel9DayNight
@Composable
private fun AirportLgaScreenshot() = MarketingLgaPreview()
