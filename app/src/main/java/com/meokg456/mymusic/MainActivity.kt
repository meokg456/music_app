package com.meokg456.mymusic

import android.content.Intent
import android.os.Bundle
import android.view.RoundedCorner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.meokg456.mymusic.models.Song
import com.meokg456.mymusic.ui.theme.MyMusicTheme
import okhttp3.internal.http2.Header

class MainActivity : ComponentActivity() {

    private var songs: List<Song> = listOf(
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
        Song(
            title = "Thằng điên",
            authors = listOf("Justatee", "Phương Ly"),
            avatarUrl = "https://photo-resize-zmp3.zadn.vn/w320_r1x1_webp/cover/9/d/5/c/9d5c56a277a06a48ec7956a4fd17e4c1.jpg"
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMusicTheme {
                PlayListScreen()
            }
        }
    }

    @Composable
    fun PlayListScreen() {
        Scaffold(
            topBar = {
                TopAppBar {
                    Text(
                        getString(R.string.playlist),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        ) {
            PlayList(songs)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayList(songs: List<Song>) {
    LazyColumn() {
        items(songs) { song ->
            SongListTile(song)
        }
    }
}

@Composable
fun SongListTile(song: Song) {
    val context = LocalContext.current
    val authors = song.authors.joinToString(separator = ", ")

    Row(
        modifier = Modifier
            .clickable {
                val intent = Intent(context, SongActivity::class.java)
                intent.putExtra("song",song)
                context.startActivity(intent)
            }.fillMaxWidth().padding(vertical = 6.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(data = song.avatarUrl),
            contentDescription = null,
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(size = 5.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column() {
            Text(text = song.title, style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = authors, style = MaterialTheme.typography.subtitle1)
        }
    }
}



