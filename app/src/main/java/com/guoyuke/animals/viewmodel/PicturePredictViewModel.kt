package com.guoyuke.animals.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guoyuke.animals.data.PredictResult
import com.guoyuke.animals.TFLiteClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PicturePredictViewModel: ViewModel() {
    private var classifier: TFLiteClassifier? = null

    private val labels: Array<String> = arrayOf(
        "大象", "松鼠", "牛", "狗", "猫", "羊", "蜘蛛", "蝴蝶", "马", "鸡"
    )

    val preDictResult = MutableSharedFlow<PredictResult>()

    init {
        try {
            classifier = TFLiteClassifier("model.tflite")
        } catch (e: IOException) {
            Log.e("PictureViewModel", "load model error: ${e.message}")
        }
    }

    fun predict(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = classifier?.predict(bitmap)
            if (results != null) {
                var maxIndex = 0
                var maxProb = 0f
                for (i in results.indices) {
                    if (results[i] > maxProb) {
                        maxIndex = i
                        maxProb = results[i]
                    }
                }
                val className = getClassName(maxIndex)
                preDictResult.emit(PredictResult(maxProb, className))
            }
        }

    }

    private fun getClassName(index: Int): String {
        return if (labels.size > index) labels[index] else "未知类别"
    }
}