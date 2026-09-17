package com.example.taxione.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.taxione.R
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.model.Slot
import com.example.taxione.ui.theme.BORDER_GRAY
import com.example.taxione.ui.theme.CONTAINER_YELLOW
import com.example.taxione.ui.theme.TaxiOneTheme

@Composable
fun DetailScreen(
    navController: NavController,
    slot: Slot,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val location by viewModel.locationFor(slot).collectAsStateWithLifecycle(initialValue = null)

    DetailScreenContent(
        slot = slot,
        location = location,
        onNicknameChange = { viewModel.updateNickname(slot, it) },
        onConfirm = { navController.popBackStack() },
    )
}

@Composable
private fun DetailScreenContent(
    slot: Slot,
    location: SelectedLocation?,
    onNicknameChange: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
    ) {
        location?.let { loc ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(slot.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text(loc.address, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.padding(start = 32.dp)) {
                Text(text = stringResource(R.string.aqi), color = Color.Gray, modifier = Modifier.width(100.dp))
                Text("${loc.aqi ?: 0}", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = loc.nickname,
                onValueChange = { if (it.length <= 20) onNicknameChange(it) },
                placeholder = { Text(text = stringResource(R.string.nickname), fontSize = 18.sp, color = Color.Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BORDER_GRAY,
                    unfocusedBorderColor = BORDER_GRAY,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CONTAINER_YELLOW,
                    contentColor = Color.Black,
                ),
            ) {
                Text(text = stringResource(R.string.label_v), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        } ?: Text(text = stringResource(R.string.nickname))
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Detail — location set")
@Composable
private fun DetailScreenPreview() {
    TaxiOneTheme {
        DetailScreenContent(
            slot = Slot.A,
            location = SelectedLocation(
                latLng = LatLng(37.564, 127.001),
                aqi = 42,
                address = "Seocho District, Yangjae 2(i)-dong",
            ),
            onNicknameChange = {},
            onConfirm = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Detail — with nickname")
@Composable
private fun DetailScreenPreviewNickname() {
    TaxiOneTheme {
        DetailScreenContent(
            slot = Slot.B,
            location = SelectedLocation(
                latLng = LatLng(37.500, 127.050),
                aqi = 35,
                address = "Gangnam District, Yeoksam 1(il)-dong",
                nickname = "Home",
            ),
            onNicknameChange = {},
            onConfirm = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Detail — empty")
@Composable
private fun DetailScreenPreviewEmpty() {
    TaxiOneTheme {
        DetailScreenContent(
            slot = Slot.A,
            location = null,
            onNicknameChange = {},
            onConfirm = {},
        )
    }
}
