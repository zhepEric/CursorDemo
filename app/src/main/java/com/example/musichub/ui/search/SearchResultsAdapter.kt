package com.example.musichub.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musichub.data.model.Song
import com.example.musichub.databinding.ItemSongBinding
import java.util.concurrent.TimeUnit

class SearchResultsAdapter(
    private val onItemClick: (Song) -> Unit
) : ListAdapter<Song, SearchResultsAdapter.SongViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SongViewHolder(
        private val binding: ItemSongBinding,
        private val onItemClick: (Song) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.songTitle.text = song.title
            binding.artistName.text = song.artist
            binding.duration.text = formatDuration(song.duration)

            Glide.with(binding.albumArt)
                .load(song.albumArtUri)
                .centerCrop()
                .into(binding.albumArt)

            itemView.setOnClickListener { onItemClick(song) }
        }

        private fun formatDuration(durationMs: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) -
                    TimeUnit.MINUTES.toSeconds(minutes)
            return String.format("%d:%02d", minutes, seconds)
        }
    }

    private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
} 