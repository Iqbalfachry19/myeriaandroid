package com.example.myeria.presentation.announcement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AnnouncementScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Pengumuman",
            fontWeight = FontWeight.Medium,
            modifier = modifier
        )
        AsyncImage(
            model = "https://media-exp1.licdn.com/dms/image/C4E03AQHqAHbkEPikzg/profile-displayphoto-shrink_800_800/0/1634629613877?e=2147483647&v=beta&t=DETYb0uDO-b92ho8x1OH03bHECWXmyAFlhiiNdPA2XI",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier

                .size(60.dp)
                .clip(CircleShape)

        )
        Text(
            text = "Muhammad Iqbal Fachry Krisbudiana",
            fontWeight = FontWeight.Medium,
            modifier = modifier


        )
        Text(
            text = "muhammad.iqbal3070@student.unri.ac.id",
            fontWeight = FontWeight.Medium,
            modifier = modifier


        )
    }
}