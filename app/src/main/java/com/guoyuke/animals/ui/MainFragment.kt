package com.guoyuke.animals.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.guoyuke.animals.viewmodel.PicturePredictViewModel
import com.guoyuke.animals.R
import com.guoyuke.animals.databinding.FragmentMainBinding
import com.guoyuke.animals.ui.base.BaseFragment

class MainFragment: BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnPredictResources.setOnClickListener {
            findNavController().navigate(R.id.resourcesFragment)
        }
    }
}