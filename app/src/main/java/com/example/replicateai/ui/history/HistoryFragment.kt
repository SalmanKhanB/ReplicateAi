package com.example.replicateai.ui.history

import HistoryAdapter
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.replicateai.databinding.FragmentHistoryBinding
import com.example.replicateai.util.hide
import com.example.replicateai.util.show
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { request, shouldPlay ->
            if (shouldPlay) {
                // Stop any currently playing audio
                mediaPlayer?.release()

                // Play the new audio
                request.outputUrl?.let { url ->
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(url)
                        prepareAsync()
                        setOnPreparedListener { mp ->
                            mp.start()
                        }
                        setOnCompletionListener {
                            // Update UI when playback completes
                            historyAdapter.currentlyPlayingPosition = -1
                            historyAdapter.isCurrentlyPlaying = false
                            historyAdapter.notifyDataSetChanged()
                        }
                    }
                }
            } else {
                // Pause the audio
                mediaPlayer?.pause()
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
//    private fun setupRecyclerView() {
//        historyAdapter = HistoryAdapter { request ->
//            // Play audio when item is clicked
//            request.outputUrl?.let { url ->
//                CoroutineScope(Dispatchers.Main).launch {
//                    com.example.replicateai.util.playAudio(requireContext(), url)
//                }
//            }
//        }
//
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = historyAdapter
//        }
//    }

    private fun observeViewModel() {
        viewModel.voiceRequests.observe(viewLifecycleOwner) { requests ->
            if (requests.isNotEmpty()) {
                binding.recyclerView.show()
                binding.tvEmpty.hide()
                historyAdapter.submitList(requests)
            } else {
                binding.recyclerView.hide()
                binding.tvEmpty.show()
            }
        }
    }


}

