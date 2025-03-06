package com.example.musichub.ui.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musichub.databinding.FragmentOnlineMusicBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnlineMusicFragment : Fragment() {

    private var _binding: FragmentOnlineMusicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnlineMusicViewModel by viewModels()

    private lateinit var featuredAdapter: OnlineMusicAdapter
    private lateinit var topChartsAdapter: OnlineMusicAdapter
    private lateinit var newReleasesAdapter: OnlineMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        featuredAdapter = OnlineMusicAdapter { playlist ->
            // TODO: Handle playlist click
        }
        topChartsAdapter = OnlineMusicAdapter { playlist ->
            // TODO: Handle playlist click
        }
        newReleasesAdapter = OnlineMusicAdapter { playlist ->
            // TODO: Handle playlist click
        }

        binding.featuredRecyclerView.adapter = featuredAdapter
        binding.topChartsRecyclerView.adapter = topChartsAdapter
        binding.newReleasesRecyclerView.adapter = newReleasesAdapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        updateUI(state)
                    }
                }
            }
        }
    }

    private fun updateUI(state: OnlineMusicUiState) {
        binding.swipeRefresh.isRefreshing = state.isLoading

        // Update featured playlists
        featuredAdapter.submitList(state.featuredPlaylists)
        binding.featuredGroup.visibility = if (state.featuredPlaylists.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        // Update top charts
        topChartsAdapter.submitList(state.topCharts)
        binding.topChartsGroup.visibility = if (state.topCharts.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        // Update new releases
        newReleasesAdapter.submitList(state.newReleases)
        binding.newReleasesGroup.visibility = if (state.newReleases.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        // Show error if any
        state.error?.let { error ->
            Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 