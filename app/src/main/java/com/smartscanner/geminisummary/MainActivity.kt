package com.smartscanner.geminisummary

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartscanner.geminisummary.ui.theme.SmartSummarizerTheme
import com.smartscanner.geminisummary.viewmodel.SummaryViewModel
import java.io.InputStream
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity()
{
    private val viewModel: SummaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                SummaryApp(viewModel)
            }
        }
    }
}

@Composable
fun SummaryApp(viewModel: SummaryViewModel) {
    val context = LocalContext.current
    val extractedText by viewModel.extractedText.collectAsState()
    val summaryText by viewModel.summaryText.collectAsState()
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val stream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(stream)
            selectedImageBitmap = bitmap
            bitmap?.let { bmp ->
                viewModel.extractTextFromImage(bmp)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing) // handles status bar
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pick Image")
        }

        selectedImageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        if (extractedText.isNotEmpty()) {
            Text("Extracted Text:", style = MaterialTheme.typography.titleMedium)
            Text(extractedText, style = MaterialTheme.typography.bodyMedium)

            Button(onClick = { viewModel.summarizeText(extractedText) }) {
                Text("Summarize")
            }
        }

        if (summaryText.isNotEmpty()) {
            HorizontalDivider()
            Text("Summary:", style = MaterialTheme.typography.titleMedium)
            Text(summaryText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    SmartSummarizerTheme {

    }
}