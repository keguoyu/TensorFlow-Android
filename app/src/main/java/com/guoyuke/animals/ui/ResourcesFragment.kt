package com.guoyuke.animals.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.guoyuke.animals.ui.adapter.ImagePagerAdapter
import com.guoyuke.animals.viewmodel.PicturePredictViewModel
import com.guoyuke.animals.R
import com.guoyuke.animals.databinding.FragmentResourcesBinding
import com.guoyuke.animals.ui.base.BaseFragment
import kotlinx.coroutines.launch

class ResourcesFragment: BaseFragment<FragmentResourcesBinding>(
    FragmentResourcesBinding::inflate
) {
    private val picturePredictViewModel by lazy {
        ViewModelProvider(requireParentFragment())[PicturePredictViewModel::class.java]
    }

    private var imageResIds: MutableList<Int> = mutableListOf()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val typeArray = resources.obtainTypedArray(R.array.test_images)
        for (i in 0 until typeArray.length()) {
            imageResIds.add(typeArray.getResourceId(i, 0))
        }
        typeArray.recycle()

        imageResIds.shuffle()

        Log.d("guoyu-debug", "imageResIds: ${imageResIds.first()}")
        viewBinding.viewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1 // 预加载数量
            adapter = ImagePagerAdapter(this@ResourcesFragment, imageResIds.toMutableList())
        }
        viewBinding.pbtnPedict.setOnClickListener {
            Log.d("guoyu-debug", "currentItem: ${viewBinding.viewPager.currentItem}")
            val resId = imageResIds[viewBinding.viewPager.currentItem]
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            picturePredictViewModel.predict(bitmap)
        }

        lifecycleScope.launch {
            picturePredictViewModel.preDictResult.collect {
                viewBinding.tvResult.text = "概率：${String.format("%.2f%%", (it.maxProp * 100))}\n动物：${it.labelName}"
            }
        }
    }
}