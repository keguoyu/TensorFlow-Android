package com.guoyuke.animals.ui.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.guoyuke.animals.R

class ImagePagerAdapter(
    fragment: Fragment,
    private val bitmaps: List<Int>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = bitmaps.size

    override fun createFragment(position: Int): Fragment {
        return ImageFragment.newInstance(bitmaps[position])
    }
}

class ImageFragment : Fragment() {
    companion object {
        fun newInstance(data: Int) = ImageFragment().apply {
            arguments = bundleOf("data" to data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.item_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        Log.d("guoyu-debug", "id: ${arguments?.getInt("data")}")
        val path = arguments?.getInt("data") ?: R.drawable.ic_launcher_background
        imageView.setImageResource(path)
    }
}