package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.purrfectfinder.Adapters.PostsAdapter
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.GridSpacingItemDecoration
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Post
import com.example.purrfectfinder.databinding.FragmentProfilePhotoBinding
import kotlinx.coroutines.launch

class ProfilePhotoFragment : Fragment() {

    private var _binding: FragmentProfilePhotoBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentProfilePhotoBinding must not be null")

    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments
        userId = bundle!!.getInt("userId")

        _binding = FragmentProfilePhotoBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postsAdapter = PostsAdapter {
            with(activity as? MainActivity) {
                this?.setFragment(R.id.profileLayout, null, null)
                this?.setFragment(R.id.fragmentLayout, CreatingPostFragment.newInstance(), null, false, true)
            }
        }

        binding.rvPosts.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = postsAdapter
            addItemDecoration(GridSpacingItemDecoration(3, 75, false))
        }

        lifecycleScope.launch {
            // пост пустышка под добавление
            val post = Post(
                sellerId = -1,
                name = "",
                desc = "",
                photo = listOf("https://apddnnxfhleknlnmxwqz.supabase.co/storage/v1/object/public/petphotos/posts/add_post.png"),
                views = -1
            )

            val userPosts = DbHelper.getInstance().getAllPosts(userId) + post
            Log.e("posts", userPosts.toString())
            postsAdapter.updatePhotos(userPosts)

        }
    }


    companion object {
        fun newInstance() = ProfilePhotoFragment()
        var userId: Int = -1
    }
}