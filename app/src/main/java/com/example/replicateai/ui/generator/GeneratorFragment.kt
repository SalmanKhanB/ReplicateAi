package com.example.replicateai.ui.generator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.replicateai.data.model.Voice
import com.example.replicateai.data.model.VoiceModel
import com.example.replicateai.data.model.availableEmotions
import com.example.replicateai.data.model.availableLanguages
import com.example.replicateai.data.model.availableModels
import com.example.replicateai.databinding.FragmentGeneratorBinding
import com.example.replicateai.util.AudioPlayer.playAudio
import com.example.replicateai.util.Constants
import com.example.replicateai.util.Resource
import com.example.replicateai.util.hide
 import com.example.replicateai.util.show
import com.example.replicateai.util.toast
 import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GeneratorViewModel by viewModels()

    private lateinit var voiceAdapter: ArrayAdapter<String>
    private lateinit var emotionAdapter: ArrayAdapter<String>
    private lateinit var languageAdapter: ArrayAdapter<String>

    private var selectedModel: VoiceModel? = null
    private var selectedVoice: Voice? = null
    private var selectedEmotion: String? = null
    private var selectedLanguage: String? = null
    private var currentSpeed: Float = 1.0f
    private var currentVolume: Float = 1.0f
    private var currentPitch: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupModelRadioButtons()
        setupVoiceSpinner()
        setupEmotionSpinner()
        setupLanguageSpinner()
        setupButtons()
        setupAudioControls()

        observeViewModel()
    }

    private fun setupModelRadioButtons() {
        binding.rgModels.removeAllViews()

        availableModels.forEachIndexed { index, model ->
            val radioButton = RadioButton(requireContext()).apply {
                id = View.generateViewId()
                text = "${model.displayName} - ${model.description}"
                isChecked = index == 0
                setPadding(0, 8, 0, 8)
            }

            binding.rgModels.addView(radioButton)

            if (index == 0) {
                onModelSelected(model)
            }
        }

        binding.rgModels.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            val index = group.indexOfChild(radioButton)
            if (index >= 0) {
                onModelSelected(availableModels[index])
            }
        }
    }

    private fun onModelSelected(model: VoiceModel) {
        selectedModel = model

        // Update voice spinner with voices from selected model
        val voiceNames = model.availableVoices.map { it.name }
        voiceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, voiceNames)
        voiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVoice.adapter = voiceAdapter

        // Show/hide emotion and language options based on model support
        if (model.supportEmotions) {
            binding.layoutEmotion.show()
            binding.layoutLanguage.show()
        } else {
            binding.layoutEmotion.hide()
            binding.layoutLanguage.hide()
        }

        // Set default voice
        if (model.availableVoices.isNotEmpty()) {
            selectedVoice = model.availableVoices[0]
        }
    }

    private fun setupVoiceSpinner() {
        binding.spinnerVoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedModel?.let {
                    if (position < it.availableVoices.size) {
                        selectedVoice = it.availableVoices[position]
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupEmotionSpinner() {
        emotionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableEmotions)
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEmotion.adapter = emotionAdapter

        binding.spinnerEmotion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEmotion = availableEmotions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupLanguageSpinner() {
        languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableLanguages)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = languageAdapter

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLanguage = availableLanguages[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupButtons() {
        binding.btnGenerate.setOnClickListener {
            val text = binding.etText.text.toString().trim()

            if (text.isEmpty()) {
                toast("Please enter text to convert")
                return@setOnClickListener
            }

            if (selectedModel == null || selectedVoice == null) {
                toast("Please select a model and voice")
                return@setOnClickListener
            }

            // Create work request
            startVoiceGeneration(text)
        }

        binding.btnPlay.setOnClickListener {
            viewModel.voiceRequest.value?.data?.outputUrl?.let { url ->
                CoroutineScope(Dispatchers.Main).launch {
                    playAudio(requireContext(), url)
                }
            }
        }
    }
    private fun startVoiceGeneration(text: String) {
        binding.progressBar.show()
        binding.cvResult.show()
        binding.btnPlay.hide()
        binding.btnGenerate.isEnabled = false
        // scroll to result
        binding.tvResultText.requestFocus()
        binding.tvResultText.text = "Processing your voice request..."

        viewModel.generateVoice(
            modelId = selectedModel!!.id,
            modelName = selectedModel!!.displayName,
            text = text,
            voiceId = selectedVoice!!.id,
            voiceName = selectedVoice!!.name,
            emotion = selectedEmotion,
            languageBoost = selectedLanguage,
            speed = currentSpeed,
            volume = if (selectedModel!!.id == Constants.MODEL_SPEECH_TURBO) currentVolume else null,
            pitch = if (selectedModel!!.id == Constants.MODEL_SPEECH_TURBO) currentPitch else null
        )
    }

    private fun observeViewModel() {
        viewModel.voiceRequest.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val request = resource.data!!

                    binding.tvResultText.text = when (request.status) {
                        "succeeded" -> "Voice generation completed successfully"
                        "failed" -> "Voice generation failed: ${request.error ?: "Unknown error"}"
                        else -> "Voice generation status: ${request.status}"
                    }
                    if (request.status == "succeeded" && request.outputUrl != null) {
                        binding.btnGenerate.isEnabled = true
                        binding.btnPlay.show()
                    } else {
                        binding.btnPlay.hide()
                    }
                }
                is Resource.Error -> {
                    binding.tvResultText.text = "Error: ${resource.message}"
                    binding.btnPlay.hide()
                }
                is Resource.Loading -> {
                    binding.tvResultText.text = "Loading..."
                    binding.btnPlay.hide()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupAudioControls() {
        // Speed control (0.5-2.0)
        binding.seekSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentSpeed = 0.5f + (progress / 100f)
                binding.tvSpeedValue.text = "%.1fx".format(currentSpeed)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Volume control (0-10)
        binding.seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentVolume = progress / 10f
                binding.tvVolumeValue.text = "%.1f".format(currentVolume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Pitch control (-12 to +12)
        binding.seekPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentPitch = progress - 12
                binding.tvPitchValue.text = currentPitch.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Show/hide controls based on selected model
        binding.rgModels.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            val index = group.indexOfChild(radioButton)
            if (index >= 0) {
                val model = availableModels[index]
                onModelSelected(model)

                // Show volume and pitch only for Turbo model
                val isTurbo = model.id == Constants.MODEL_SPEECH_TURBO
                binding.layoutVolume.visibility = if (isTurbo) View.VISIBLE else View.GONE
                binding.layoutPitch.visibility = if (isTurbo) View.VISIBLE else View.GONE
            }
        }
    }
}