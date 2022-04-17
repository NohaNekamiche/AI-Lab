package com.example.ailab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.ailab.R
import com.example.ailab.databinding.FragmentHomeBinding
import com.example.ailab.ui.gallery.GalleryFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imgRecCard.setOnClickListener {v->
            v?.findNavController()?.navigate(R.id.action_nav_home_to_nav_gallery2)

        }
        imgLabCard.setOnClickListener {v->
            v?.findNavController()?.navigate(R.id.action_nav_home_to_nav_slideshow2)
        }
        langCard.setOnClickListener {v->
            v?.findNavController()?.navigate(R.id.action_nav_home_to_nav_language_translate2)
        }
        chatCard.setOnClickListener { v->
            v?.findNavController()?.navigate(R.id.action_nav_home_to_nav_chat)
        }

    }


}