package svoelkl2.mauc.androidtest01.ui.assistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import svoelkl2.mauc.androidtest01.MainViewModel
import svoelkl2.mauc.androidtest01.R

class AssistantFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_assistant, container, false)
        
        val messageText = root.findViewById<TextView>(R.id.assistantMessage)
        val inputField = root.findViewById<EditText>(R.id.assistantInput)
        val sendButton = root.findViewById<Button>(R.id.sendButton)

        viewModel.socialBattery.observe(viewLifecycleOwner) { battery ->
            val advice = when {
                battery < 30 -> getString(R.string.assistant_advice_low)
                battery < 70 -> getString(R.string.assistant_advice_balanced)
                else -> getString(R.string.assistant_advice_high)
            }
            messageText.text = getString(R.string.assistant_intro, advice)
        }
        
        sendButton.setOnClickListener {
            val userText = inputField.text.toString()
            if (userText.isNotBlank()) {
                val currentConversation = messageText.text.toString()
                val response = "That sounds interesting! I'm here to support you. Have you checked the Social Map for new Quests lately?"
                val newConversation = "$currentConversation\n\nYou: $userText\n\nAssistant: $response"
                messageText.text = newConversation
                inputField.text.clear()
            }
        }

        return root
    }
}