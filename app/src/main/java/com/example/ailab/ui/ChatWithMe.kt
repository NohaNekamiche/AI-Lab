package com.example.ailab.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.ailab.R
import com.example.ailab.databinding.FragmentChatWithMeBinding
import com.example.ailab.databinding.FragmentHomeBinding
import com.example.ailab.ui.home.HomeViewModel
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplyGenerator
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import kotlinx.android.synthetic.main.fragment_chat_with_me.*

class ChatWithMe : Fragment() {
    lateinit var smartReplyGenerator: SmartReplyGenerator
    lateinit var conversations:ArrayList<TextMessage>
    var userId="123456"

    private var _binding: FragmentChatWithMeBinding? = null
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater,
                                container: ViewGroup?,
                                savedInstanceState: Bundle?
    ): View {


        _binding = FragmentChatWithMeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        conversations= ArrayList()
        smartReplyGenerator= SmartReply.getClient()


        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sendBtn.setOnClickListener {
            val msg=textSource.text.toString().trim()
            txtResult.text=""
            conversations.add(
                TextMessage.createForLocalUser(msg,System.currentTimeMillis())
            )

            smartReplyGenerator.suggestReplies(conversations).addOnSuccessListener { result->
                if(result.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE){
                    txtResult.text="STATUS_NOT_SUPPORTED_LANGUAGE"
                }
                else if(result.status== SmartReplySuggestionResult.STATUS_SUCCESS){
                    var res=""
                    for (suggestion in result.suggestions) {
                        val replyText = suggestion.text
                        res=res+"\n"+replyText
                    }
                    txtResult.text=res
                }

            }.addOnFailureListener{
                txtResult.text="Can't find answer ${it.message}"
            }

        }
    }

}