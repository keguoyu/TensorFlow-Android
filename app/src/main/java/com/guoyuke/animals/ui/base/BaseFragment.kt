package com.guoyuke.animals.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T: ViewBinding>(
    private val viewInflater: ((LayoutInflater, ViewGroup?, Boolean) -> T)
): Fragment() {

    protected lateinit var viewBinding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = viewInflater.invoke(inflater, container, false)
        return viewBinding.root
    }
}