package com.example.purrfectfinder.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrfectfinder.Adapters.StarsAdapter
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Seller
import com.example.purrfectfinder.SerializableDataClasses.User
import com.example.purrfectfinder.databinding.FragmentAdCardBinding
import com.example.purrfectfinder.databinding.FragmentAdvertisementsBinding
import com.example.purrfectfinder.interfaces.TitleProvider
import kotlinx.coroutines.launch

class AdCardFragment : Fragment(), TitleProvider {

    private var _binding: FragmentAdCardBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentAdCardBinding must not be null")

    private lateinit var starsAdapter: StarsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val bundle = arguments
        args = bundle!!.getStringArrayList("args")!!.toList()
        // 1. sellerId; 2. adImage; 3. adName; 4. adPrice
        Log.e("args received", args.toString())
        _binding = FragmentAdCardBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        starsAdapter = StarsAdapter(emptyList())

        binding.rvStars.apply {
            val layout = LinearLayoutManager(context)
            layout.orientation = RecyclerView.HORIZONTAL
            layoutManager = layout
            adapter = starsAdapter
        }

        binding.sellerInfoSwitcher.displayedChild = 0 // отображение шиммера

        lifecycleScope.launch {
            seller = mutableListOf()

            val adSeller = DbHelper.getInstance().getAllData<Seller>("Sellers").find { it.id == args[0].toInt() }
            Log.e("seller", adSeller.toString())
            val adSellerInfo = DbHelper.getInstance().getAllData<User>("Users").find { it.id == adSeller!!.id }
            Log.e("seller info", adSellerInfo.toString())

            // фото
            if (adSellerInfo!!.pfp != null) {
                Glide.with(binding.ivSellerPFP.context)
                    .load(adSellerInfo.pfp) // Загрузка изображения по ссылке
                    .into(binding.ivSellerPFP) // Установка изображения в ImageView
            }

            // имя фамилия
            seller.add("name" to "${adSellerInfo.secondName} ${adSellerInfo.firstName}")
            seller.add("pfp" to adSellerInfo.pfp)

            binding.tvSellerName.text = seller.find { it.first == "name"}!!.second + " "

            var sumStarRate = adSeller!!.starrate

            binding.tvRating.text = "$sumStarRate"
            val starRate: MutableList<Float> = mutableListOf()

            while (sumStarRate >= 1f) {
                starRate.add(1f)
                sumStarRate -= 1f
            }
            if (sumStarRate != 0f)
                starRate.add(sumStarRate)

            starsAdapter.updateData(starRate)

            binding.sellerInfoSwitcher.displayedChild = 1 // отображение основного контента
        }

        // делаем NxN размерный ImageView
        binding.ivAdPicture.viewTreeObserver.addOnGlobalLayoutListener {
            val width = binding.ivAdPicture.width // получаем ширину
            val layoutParams = binding.ivAdPicture.layoutParams
            layoutParams.height = width // устанавливаем высоту равной ширине
            binding.ivAdPicture.layoutParams = layoutParams
        }

        // подгружаем картинку из аргументов, переданных фрагменту
        Glide.with(binding.ivAdPicture.context)
            .load(args[1]) // Загрузка изображения по ссылке
            .into(binding.ivAdPicture) // Установка изображения в ImageView

        // название и цена объявления
        binding.tvAdName.text = args[2]
        binding.tvAdPrice.text = args[3]
    }

    override fun getTitle(): String {
        return ""
    }

    companion object {
        fun newInstance() = AdCardFragment()
        var args: List<String> = emptyList()
        var seller: MutableList<Pair<String, String?>> = mutableListOf()
    }
}