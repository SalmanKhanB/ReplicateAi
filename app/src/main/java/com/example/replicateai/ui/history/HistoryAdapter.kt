import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.replicateai.data.db.entity.VoiceRequest
import com.example.replicateai.databinding.ItemVoiceRequestBinding
import com.example.replicateai.util.AudioPlayer
import com.example.replicateai.util.hide
import com.example.replicateai.util.show
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
     onItemClick: (VoiceRequest, isPlaying: Boolean) -> Unit
) : ListAdapter<VoiceRequest, HistoryAdapter.VoiceRequestViewHolder>(VoiceRequestDiffCallback()) {

    var currentlyPlayingPosition: Int = -1
    var isCurrentlyPlaying: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceRequestViewHolder {
        val binding = ItemVoiceRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VoiceRequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoiceRequestViewHolder, position: Int) {
        holder.bind(getItem(position), position == currentlyPlayingPosition && isCurrentlyPlaying)
    }

    inner class VoiceRequestViewHolder(
        private val binding: ItemVoiceRequestBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(voiceRequest: VoiceRequest, isPlaying: Boolean) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

            binding.tvModelName.text = voiceRequest.modelName
            binding.tvVoiceName.text = "Voice: ${voiceRequest.voiceName}"
            binding.tvText.text = voiceRequest.text
            binding.tvDate.text = dateFormat.format(voiceRequest.createdAt)

            // Show emotion and language if available
            if (voiceRequest.emotion != null) {
                binding.tvEmotion.show()
                binding.tvEmotion.text = "Emotion: ${voiceRequest.emotion}"
            } else {
                binding.tvEmotion.hide()
            }

            if (voiceRequest.languageBoost != null) {
                binding.tvLanguage.show()
                binding.tvLanguage.text = "Language: ${voiceRequest.languageBoost}"
            } else {
                binding.tvLanguage.hide()
            }

            // Show status
            binding.tvStatus.text = "Status: ${voiceRequest.status}"

            // Show error if any
            if (voiceRequest.error != null) {
                binding.tvError.show()
                binding.tvError.text = "Error: ${voiceRequest.error}"
            } else {
                binding.tvError.hide()
            }

            // Show play button if output URL is available
            if (voiceRequest.status == "succeeded" && voiceRequest.outputUrl != null) {
                binding.btnPlay.show()

                // Update play/pause icon
                if (isPlaying) {
                    binding.btnPlay.setText("pause")
                } else {
                    binding.btnPlay.setText("play")
//                    binding.btnPlay.setImageResource(android.R.drawable.ic_media_play)
                }
            } else {
                binding.btnPlay.hide()
            }
            binding.btnPlay.setOnClickListener {
                val newPosition = adapterPosition
                val isCurrentlyPlayingThisItem = AudioPlayer.isPlaying(voiceRequest.outputUrl.toString())

                // Stop any other playing audio
                if (currentlyPlayingPosition != newPosition) {
                    AudioPlayer.stopAudio()
                }

                // Toggle play state
                if (isCurrentlyPlayingThisItem) {
                    AudioPlayer.stopAudio()
                    currentlyPlayingPosition = -1
                } else {
                    currentlyPlayingPosition = newPosition
                    AudioPlayer.playAudio(itemView.context, voiceRequest.outputUrl!!) {
                        // On completion
                        currentlyPlayingPosition = -1
                        notifyItemChanged(newPosition)
                    }
                }

                // Update UI
                notifyItemChanged(newPosition)
            }
       }
    }

    class VoiceRequestDiffCallback : DiffUtil.ItemCallback<VoiceRequest>() {
        override fun areItemsTheSame(oldItem: VoiceRequest, newItem: VoiceRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VoiceRequest, newItem: VoiceRequest): Boolean {
            return oldItem == newItem
        }
    }
}