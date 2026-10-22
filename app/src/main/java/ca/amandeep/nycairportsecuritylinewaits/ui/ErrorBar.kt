package ca.amandeep.nycairportsecuritylinewaits.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.amandeep.nycairportsecuritylinewaits.R
import ca.amandeep.nycairportsecuritylinewaits.ui.theme.NYCAirportSecurityLineWaitsTheme
import ca.amandeep.nycairportsecuritylinewaits.util.ConnectionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

/** Shows the age of previous readings when live updates are unavailable. */
@Composable
fun ErrorBar(
    connectivityState: ConnectionState,
    lastUpdated: Long,
    modifier: Modifier = Modifier,
) {
    val minutesAgo by remember(lastUpdated) {
        flow {
            while (true) {
                val ageMillis = (System.currentTimeMillis() - lastUpdated).coerceAtLeast(0L)
                emit(TimeUnit.MILLISECONDS.toMinutes(ageMillis))
                delay(TimeUnit.MINUTES.toMillis(1) - ageMillis % TimeUnit.MINUTES.toMillis(1))
            }
        }
    }.collectAsStateWithLifecycle(
        initialValue = TimeUnit.MILLISECONDS.toMinutes(
            (System.currentTimeMillis() - lastUpdated).coerceAtLeast(0L),
        ),
    )
    val offline = connectivityState == ConnectionState.Unavailable
    val containerColor = MaterialTheme.colorScheme.secondaryContainer
    val contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    val status = stringResource(if (offline) R.string.offline_status else R.string.refresh_error_status)
    val updated = if (minutesAgo == 0L) {
        stringResource(R.string.updated_just_now)
    } else {
        stringResource(R.string.updated_minutes_ago, minutesAgo)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, MaterialTheme.shapes.medium)
            .heightIn(min = 48.dp)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(if (offline) R.drawable.ic_wifi_off else R.drawable.ic_cloud_error),
            modifier = Modifier.size(20.dp),
            contentDescription = null,
            tint = contentColor,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(status)
                }
                append(" · ")
                append(updated)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}

@Composable
@Preview(name = "Light", showBackground = true, widthDp = 360)
@Preview(name = "Dark", showBackground = true, widthDp = 360, uiMode = UI_MODE_NIGHT_YES)
private fun ErrorBarPreview(
    @PreviewParameter(SampleConnectionStateProvider::class) connectivityState: ConnectionState,
) {
    NYCAirportSecurityLineWaitsTheme {
        ErrorBar(connectivityState, System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5))
    }
}

class SampleConnectionStateProvider : PreviewParameterProvider<ConnectionState> {
    override val values = sequenceOf(
        ConnectionState.Available,
        ConnectionState.Unavailable,
    )
}
