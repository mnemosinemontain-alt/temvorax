package com.brunnakampferd.temvorax.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brunnakampferd.temvorax.R

@Composable
fun SplashScreen(
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.splash_temvorax
            ),
            contentDescription = "Tela inicial do Temvorax",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Button(
            onClick = onContinue,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .width(230.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B7BC7),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "INICIAR JORNADA",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
