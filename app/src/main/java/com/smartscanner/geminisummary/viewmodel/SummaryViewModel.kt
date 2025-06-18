package com.smartscanner.geminisummary.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummaryViewModel : ViewModel()
{

    private val _extractedText = MutableStateFlow("")
    val extractedText = _extractedText.asStateFlow()

    private val _summaryText = MutableStateFlow("")
    val summaryText = _summaryText.asStateFlow()

    fun extractTextFromImage(bitmap: Bitmap)
    {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                _extractedText.value = visionText.text
            }
            .addOnFailureListener {
                _extractedText.value = "Failed to extract text."
            }
    }

    fun summarizeText(text: String)
    {
        viewModelScope.launch {
            // 🧠 Mocking Gemini Nano summarization for now
            _summaryText.value = mockSummarize(text)
        }
    }

    private fun mockSummarize(text: String): String
    {
        // Simulate Gemini Nano-like summary (real implementation coming soon)
        return if (text.length > 150)
        {
            "Summary: ${text.take(120)}..."
        } else
        {
            "Summary: $text"
        }
    }
}
