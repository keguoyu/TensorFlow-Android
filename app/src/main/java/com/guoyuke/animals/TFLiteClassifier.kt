package com.guoyuke.animals

import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteClassifier(modelPath: String) {
    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(modelPath))
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val inputStream = FileInputStream(MyApp.INSTANCE.assets.openFd(modelPath).fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = MyApp.INSTANCE.assets.openFd(modelPath).startOffset
        val declaredLength = MyApp.INSTANCE.assets.openFd(modelPath).declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val buffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * 3 * FLOAT_SIZE)
        buffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        return buffer
    }

    fun predict(bitmap: Bitmap): FloatArray {
        val inputBuffer = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(getOutPutSize()) }
        interpreter.run(inputBuffer, output)
        return output[0]
    }

    private fun getOutPutSize(): Int {
        return interpreter.getOutputTensor(0).shape()[1]
    }

    companion object {
        private const val INPUT_SIZE = 224
        private const val FLOAT_SIZE = 4
    }
}