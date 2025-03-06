package com.example.musichub.ui.local

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musichub.databinding.FragmentLocalMusicBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocalMusicFragment : Fragment() {

    private var _binding: FragmentLocalMusicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocalMusicViewModel by viewModels()
    private lateinit var adapter: LocalMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = LocalMusicAdapter { song ->
            viewModel.playSong(song)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadMusic()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: LocalMusicUiState) {
        binding.swipeRefresh.isRefreshing = state.isLoading
        binding.emptyView.visibility = if (state.songs.isEmpty() && !state.isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
        adapter.submitList(state.songs)

        state.error?.let { error ->
            Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 