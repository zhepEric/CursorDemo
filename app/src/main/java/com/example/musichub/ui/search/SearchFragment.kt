package com.example.musichub.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musichub.databinding.FragmentSearchBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchView()
        setupRecyclerView()
        setupClearHistoryButton()
        observeViewModel()
    }

    private fun setupSearchView() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text?.toString() ?: "")
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchResultsAdapter { song ->
            // TODO: Handle song click
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setupClearHistoryButton() {
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        updateUI(state)
                    }
                }

                launch {
                    viewModel.searchResults
                        .debounce(300)
                        .collectLatest { results ->
                            adapter.submitList(results)
                            binding.emptyView.visibility = if (results.isEmpty()) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                        }
                }
            }
        }
    }

    private fun updateUI(state: SearchUiState) {
        // Update loading state
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        // Update search history
        binding.searchHistoryChipGroup.removeAllViews()
        state.searchHistory.forEach { query ->
            val chip = Chip(requireContext()).apply {
                text = query
                setOnClickListener {
                    binding.searchEditText.setText(query)
                }
            }
            binding.searchHistoryChipGroup.addView(chip)
        }

        // Show/hide search history group
        binding.searchHistoryGroup.visibility = if (state.searchHistory.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 