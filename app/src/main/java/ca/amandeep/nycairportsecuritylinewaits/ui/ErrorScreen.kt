package ca.amandeep.nycairportsecuritylinewaits.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ca.amandeep.nycairportsecuritylinewaits.R
import ca.amandeep.nycairportsecuritylinewaits.ui.theme.NYCAirportSecurityLineWaitsTheme
import ca.amandeep.nycairportsecuritylinewaits.util.ConnectionState

/**
 * Show a full screen error screen with an icon and a message, differing depending on if there is an
 * internet connection or not.
 */
@Composable
fun ErrorScreen(
    connectivityState: ConnectionState,
    forceUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val offline = connectivityState == ConnectionState.Unavailable
    BoxWithConstraints(modifier.fillMaxSize()) {
        val compact = maxHeight < 360.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(if (compact) 12.dp else 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 48.dp else 72.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(if (compact) 28.dp else 40.dp),
                    painter = painterResource(
                        if (offline) R.drawable.ic_wifi_off else R.drawable.ic_cloud_error,
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Spacer(Modifier.height(if (compact) 12.dp else 24.dp))
            Text(
                text = stringResource(if (offline) R.string.offline_title else R.string.load_error_title),
                modifier = Modifier.widthIn(max = if (compact) 480.dp else 220.dp),
                style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(if (compact) 8.dp else 12.dp))
            Text(
                text = stringResource(
                    if (offline) R.string.offline_description else R.string.load_error_description,
                ),
                modifier = Modifier.widthIn(max = 300.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (!offline) {
                Spacer(Modifier.height(if (compact) 12.dp else 24.dp))
                Button(
                    onClick = forceUpdate,
                    modifier = Modifier.heightIn(min = 48.dp).widthIn(min = 130.dp),
                ) {
                    Text(stringResource(R.string.try_again))
                }
            }
        }
    }
}

@Composable
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ErrorScreenPreview(
    @PreviewParameter(SampleConnectionStateProvider::class) connectivityState: ConnectionState,
) {
    NYCAirportSecurityLineWaitsTheme {
        ErrorScreen(
            connectivityState = connectivityState,
            forceUpdate = {},
        )
    }
}
