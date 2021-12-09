package com.meokg456.mymusic

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.meokg456.mymusic.models.Song
import com.meokg456.mymusic.ui.theme.MyMusicTheme

class SongActivity : ComponentActivity() {

    lateinit var song : Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        song = intent.getSerializableExtra("song") as Song
        setContent {
            MyMusicTheme {
                SongScreen(song = song)
            }
        }
    }
}

@Composable
fun SongAppBar(song: Song) {
    val context = LocalContext.current as Activity
    val authors = song.authors.joinToString(separator = ", ")
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            context.finish()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_ios),
                contentDescription = null
            )
        }
        Column() {

            Text(
                song.title,
                style = MaterialTheme.typography.caption
            )
            Text(
                authors,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Composable
fun SongScreen(song: Song) {
    var isPlaying by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    var currentRotation by remember { mutableStateOf(0f) }
    val angle by infiniteTransition.animateFloat(
        initialValue = currentRotation,
        targetValue = currentRotation + if (isPlaying) 360F else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar {
                SongAppBar(song = song)
            }
        },
        isFloatingActionButtonDocked = true,
        bottomBar = {
            BottomAppBar(
                cutoutShape = MaterialTheme.shapes.small.copy(
                    CornerSize(percent = 50)
                )
            ) { }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isPlaying = !isPlaying
                if(!isPlaying) {
                    currentRotation = angle
                }
            }) {
                Icon(
                    painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                    contentDescription = null
                )

            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(20.dp))
            Image(
                painter = rememberImagePainter(data = song.avatarUrl),
                contentDescription = null,
                modifier = Modifier.size(300.dp)
                    .clip(CircleShape).graphicsLayer {
                        rotationZ = if (isPlaying) angle else currentRotation
                    }
            )
        }
    }
}