package com.example.musichub.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.musichub.R
import com.example.musichub.databinding.LayoutMiniPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MiniPlayerFragment : Fragment() {

    private var _binding: LayoutMiniPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutMiniPlayerBinding.inflate(inflater, container, false)
        // Initially hide the mini player
//        binding.root.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observePlaybackState()
    }

    private fun setupClickListeners() {
        binding.playPauseButton.setOnClickListener {
            viewModel.playPause()
        }

        binding.nextButton.setOnClickListener {
            viewModel.skipToNext()
        }

        binding.root.setOnClickListener {
            // TODO: Navigate to full player
        }
    }

    private fun observePlaybackState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updatePlayPauseButton(state.isPlaying)
                    updateSongInfo(state)
                }
            }
        }
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        binding.playPauseButton.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        )
    }

    private fun updateSongInfo(state: PlayerUiState) {
        val song = state.currentSong
        if (song != null) {
            binding.songTitle.text = song.title
            binding.artistName.text = song.artist
            binding.albumArt.load(song.albumArtUri) {
                crossfade(true)
                placeholder(R.drawable.ic_album)
                error(R.drawable.ic_album)
            }
//            if (binding.root.visibility != View.VISIBLE) {
//                binding.root.visibility = View.VISIBLE
//            }
        }
//        else {
//            if (binding.root.visibility != View.GONE) {
//                binding.root.visibility = View.GONE
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 