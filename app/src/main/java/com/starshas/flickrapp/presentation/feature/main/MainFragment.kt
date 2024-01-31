package com.starshas.flickrapp.presentation.feature.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.starshas.flickrapp.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private val flickrAdapter get() = binding.recyclerView.adapter as FlickrAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerView
        val isLargeScreen = resources.configuration.screenWidthDp >= DP_LARGE_SCREEN

        val layoutManager = if (isLargeScreen) {
            GridLayoutManager(requireContext(), NUMBER_OF_RECYCLERVIEW_COLUMNS_LARGE_SCREEN)
        } else {
            LinearLayoutManager(requireContext())
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = FlickrAdapter(
            context = requireContext(),
            actionOpenLinkInBrowser = {
                openLinkInBrowser(requireContext(), it)
            },
        )

        binding.buttonReload.setOnClickListener {
            it.visibility = View.GONE
            viewModel.refreshListData()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeListData()
        observeErrors()
    }

    private fun observeErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { errorMessage ->
                    if (errorMessage != null) {
                        Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.resetErrorMessage()
                        binding.buttonReload.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeListData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlowListFlickr.collect { list ->
                    flickrAdapter.setData(list)
                }
            }
        }
    }

    private fun openLinkInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Timber.d("openLinkInBrowser", "No Intent available to handle the action")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val NUMBER_OF_RECYCLERVIEW_COLUMNS_LARGE_SCREEN = 3
        const val DP_LARGE_SCREEN = 600
    }
}
