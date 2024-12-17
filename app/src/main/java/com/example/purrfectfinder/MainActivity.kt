package com.example.purrfectfinder

import android.content.Context
import android.icu.text.CaseMap.Title
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.transition.Visibility
import android.util.Log
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.purrfectfinder.Fragments.AdCardFragment
import com.example.purrfectfinder.Fragments.AdvertisementsFragment
import com.example.purrfectfinder.Fragments.ChatFragment
import com.example.purrfectfinder.Fragments.CreatingAdFragment
import com.example.purrfectfinder.Fragments.FavouriteAdvertisementsFragment
import com.example.purrfectfinder.Fragments.FavouriteAdvertisementsFragment.Companion.allFavs
import com.example.purrfectfinder.Fragments.FilteredAdvertisementsFragment
import com.example.purrfectfinder.Fragments.FiltersFragment
import com.example.purrfectfinder.Fragments.LoadingFragment
import com.example.purrfectfinder.Fragments.ProfileDescHorizontalFragment
import com.example.purrfectfinder.Fragments.ProfileDescriptionFragment
import com.example.purrfectfinder.Fragments.ProfileEditFragment
import com.example.purrfectfinder.Fragments.ProfileFragment
import com.example.purrfectfinder.Fragments.SettingsFragment
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.SerializableDataClasses.DBStamp
import com.example.purrfectfinder.databinding.ActivityMainBinding
import com.example.purrfectfinder.databinding.AdItemBinding
import com.example.purrfectfinder.interfaces.TitleProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val dataModel: DataModel by viewModels()

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }

//        val dbStamp = DBStamp()

        setFragment(R.id.loadingLayout, LoadingFragment.newInstance(), null)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundleReceived = intent.getBundleExtra("BUNDLE")
        currentUserId = bundleReceived?.getInt(ID)
        currentUserEmail = bundleReceived?.getString(EMAIL)
        currentUserPassword = bundleReceived?.getString(PASSWORD)
        currentUserSecondName = bundleReceived?.getString(SECONDNAME)
        currentUserFirstName = bundleReceived?.getString(FIRSTNAME)
        currentUserCreatedAt = bundleReceived?.getString(CREATEDAT)
        currentUserPFP = bundleReceived?.getString(PFP)

        val db = DbHelper()

        showLoadingScreen(true)
        lifecycleScope.launch {
            dataModel.allAds.value = db.getAllData<Advertisement>("Advertisements")
            dataModel.favAdsIds.value = db.getAllFavAds(currentUserId!!)
            dataModel.favAds.value = db.getClient().postgrest["Advertisements"]
                .select() {
                    filter {
                        isIn("id", dataModel.favAdsIds.value!!) // фильтруем по списку advertisementId
                    }
                }
                .decodeList<Advertisement>()

            Log.e("FAVOURITES", dataModel.favAds.value.toString())

            return@launch
        }
        showLoadingScreen(false)

        // navigationview
        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView) { view, insets ->
            view.updatePadding(bottom = 0)
            insets
        }

        setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance(), null)

        binding.navigationView.selectedItemId = R.id.petshop

        binding.btnPrev.setOnClickListener {
            handleBackPressed()
        }

        binding.btnSettings.setOnClickListener {
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE


            setFragment(
                listOf(R.id.profileLayout, R.id.fragmentLayout),
                listOf(ProfileDescHorizontalFragment.newInstance(), SettingsFragment.newInstance()),
                null,
                true
            )

            Log.e("BackstackSettings", supportFragmentManager.backStackEntryCount.toString())
        }

        binding.btnFilters.setOnClickListener {
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE
            binding.btnFilters.visibility = GONE

            setFragment(R.id.profileLayout, null, null)
            setFragment(R.id.fragmentLayout, FiltersFragment.newInstance(), null, false, true)

            updateLoadingFragmentText("Загружаем доступные фильтры...");

            Log.e("CURRENT BACKSTACK FILTERS", supportFragmentManager.backStackEntryCount.toString())
        }

        binding.btnCreateAd.setOnClickListener {
            setFragment(R.id.profileLayout, null, null)
            setFragment(R.id.fragmentLayout, CreatingAdFragment.newInstance(), null, false, true)
        }

        binding.navigationView.setOnItemSelectedListener { item ->
            while (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStackImmediate()
            }
            binding.btnPrev.visibility = GONE

            when(item.itemId) {
                R.id.home -> {
                    binding.tvWinTitle.text = "Главная"
                    binding.btnFilters.visibility = GONE
                    binding.btnSettings.visibility = GONE
                    binding.btnCreateAd.visibility = GONE

                    setFragment(R.id.profileLayout, null, null)
                    setFragment(R.id.fragmentLayout, null, null)
                }
                R.id.favourites -> {
                    setFragment(R.id.profileLayout, null, null)
                    setFragment(R.id.fragmentLayout, FavouriteAdvertisementsFragment.newInstance(), null)
                    updateLoadingFragmentText("Загружаем избранные объявления...")
                }
                R.id.petshop -> {
                    setFragment(R.id.profileLayout, null, null)
                    setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance(), null)
                    updateLoadingFragmentText("Ищем котиков...")

                    Log.e("CURRENT BACKSTACK", supportFragmentManager.backStackEntryCount.toString())
                }
                R.id.clips -> {
                    binding.tvWinTitle.text = "Котоклипы"
                    binding.btnFilters.visibility = GONE
                    binding.btnSettings.visibility = GONE
                    binding.btnCreateAd.visibility = GONE

                    setFragment(R.id.profileLayout, null, null)
                    setFragment(R.id.fragmentLayout, null, null)
                }
                R.id.profile -> {
                    showLoadingScreen(false)
                    setFragment(R.id.profileLayout, ProfileDescriptionFragment.newInstance(), args = listOf(currentUserId.toString()))
                    setFragment(R.id.fragmentLayout, ProfileFragment.newInstance(), args = listOf(currentUserId.toString()))
                }
            }
            true


        }

        dataModel.filteredAds.observe(this) {
            setFragment(R.id.fragmentLayout, FilteredAdvertisementsFragment.newInstance(), null)
        }

        dataModel.isAdsLoaded.observe(this) {
            if (it == true) {
                binding.btnCreateAd.visibility = VISIBLE
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            // Если в стеке только один фрагмент, скрываем кнопку "Назад"
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.btnPrev.visibility = GONE
            }

            // меняем тайтл
            val activeFragment = supportFragmentManager.fragments.lastOrNull {
                it.isVisible && it is TitleProvider
            }
            binding.tvWinTitle.text = (activeFragment as? TitleProvider)?.getTitle() ?: "Главная"
            updateUIForFragment(activeFragment)

            // устанавливаем размер текста и отступы в зависимости от длины заголовка
            if (binding.tvWinTitle.text.length < 20) {
                binding.tvWinTitle.textSize = 23f
                binding.tvWinTitle.setPadding(0, 0, 0, 0)
            } else {
                binding.tvWinTitle.textSize = 17f
                binding.tvWinTitle.setPadding(0, 13, 0, 0)
            }

            Log.d("BackStack", "Current back stack entries: ${supportFragmentManager.backStackEntryCount}")
            supportFragmentManager.fragments.forEach {
                Log.d("BackStack", "Fragment: ${it::class.java.simpleName}, isVisible: ${it.isVisible}")
            }
        }

        // кнопки из других фрагментов, привязанные к низу экрана
        binding.btnChatSeller.setOnClickListener {
            Log.e("chat args", AdCardFragment.args.toString())
            setFragment(R.id.fragmentLayout, ChatFragment.newInstance(), null, false, true)
        }

    }

    private fun updateUIForFragment(fragment: Fragment?) {
        when (fragment) {
            // главная

            // избранное
            is FavouriteAdvertisementsFragment -> {
                binding.btnFilters.visibility = VISIBLE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 20)
                )

                binding.tvWinTitle.textSize = 23f
                binding.tvWinTitle.setPadding(0, 0, 0, 0)
            }
            // объявления
            is AdvertisementsFragment -> {
                binding.btnPrev.visibility = GONE
                binding.btnFilters.visibility = VISIBLE
                binding.btnSettings.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 0)
                )

                binding.tvWinTitle.textSize = 23f
                binding.tvWinTitle.setPadding(0, 0, 0, 0)
            }
            // котоклипы

            // профиль
            is ProfileFragment -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = VISIBLE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(10, -20)
                )

                binding.tvWinTitle.textSize = 23f
                binding.tvWinTitle.setPadding(0, 0, 0, 0)
            }
            // фильтры
            is FiltersFragment -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 0)
                )

                binding.tvWinTitle.textSize = 23f
                binding.tvWinTitle.setPadding(0, 0, 0, 0)
            }
            // настройки
            is SettingsFragment -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 20)
                )

                binding.tvWinTitle.textSize = 23f
                binding.tvWinTitle.setPadding(0, 0, 0, 0)
            }
            // редактирование
            is ProfileEditFragment -> {
                binding.btnPrev.visibility = VISIBLE
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 80)
                )

                binding.tvWinTitle.textSize = 17f
                binding.tvWinTitle.setPadding(0, 13, 0, 0)
            }
            // карточка объявления
            is AdCardFragment -> {
                binding.btnPrev.visibility = VISIBLE
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 1
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 40)
                )
            }
            // чат
            is ChatFragment -> {
                binding.btnPrev.visibility = VISIBLE
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 1
                binding.bottomFlipper.displayedChild = 2
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 60)
                )

                val pfp = AdCardFragment.seller.find { it.first == "pfp" }!!.second

                // информация о продавце
                if (pfp != null)
                    Glide.with(binding.ivSellerPFP.context)
                        .load(pfp) // Загрузка изображения по ссылке
                        .into(binding.ivSellerPFP) // Установка изображения в ImageView
                else
                    binding.ivSellerPFP.setImageResource(R.drawable.ic_user_pfp)

                binding.tvSellerName.text = AdCardFragment.seller.find { it.first == "name" }!!.second
            }
            // создание объявления
            is CreatingAdFragment -> {
                binding.btnPrev.visibility = VISIBLE
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 40)
                )
            }

            else -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
                binding.btnCreateAd.visibility = GONE
                binding.headerFlipper.displayedChild = 0
                binding.bottomFlipper.displayedChild = 0
                binding.llActionBtns.visibility = GONE
                changeMarginTop(
                    listOf(binding.profileLayout.id, binding.fragmentLayout.id),
                    listOf(0, 0)
                )
            }
        }
    }

    private fun changeMarginTop(viewIds: List<Int>, values: List<Int>) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.main)
        for (i in 1..viewIds.size)
            constraintSet.setMargin(viewIds[i - 1], ConstraintSet.TOP, values[i - 1])

        constraintSet.applyTo(binding.main)
    }

    fun setFragment(layout: Int, fragment: Fragment?, args: List<String>?, isAdding: Boolean = false, addToBackStack: Boolean = false) {
        supportFragmentManager
            .beginTransaction().apply {
                if (fragment != null) {
                    // перекидываем аргументы
                    val argsArrayList = args?.let { ArrayList(it) }
                    fragment.arguments = bundleOf("args" to argsArrayList)

                    // если добавляем, то хайдим старый
                    if (isAdding) {
                        if (supportFragmentManager.findFragmentById(layout) != null) {
                            hide(supportFragmentManager.findFragmentById(layout)!!)
                        }

                        add(layout, fragment)
                    }
                    else replace(layout, fragment)

                    if (fragment is TitleProvider) {
                        binding.tvWinTitle.text = fragment.getTitle()
                        updateUIForFragment(fragment)
                    }

                    if (addToBackStack) addToBackStack(null)
                } else {
                    // Если передан null, удаляем все фрагменты из контейнера
                    supportFragmentManager.findFragmentById(layout)?.let {
                        remove(it)
                    }
                }
                commit()
            }
    }

    // always will be added to back stack
    fun setFragment(layouts: List<Int>, fragments: List<Fragment?>, args: List<String>?, isAdding: Boolean = false) {
        supportFragmentManager
            .beginTransaction().apply {
                for (i in 1..layouts.size) {
                    if (fragments[i - 1] != null) {
                        if (args != emptyList<String>()) {
                            fragments[i - 1]!!.arguments = bundleOf("args" to args)
                        }

                        if (isAdding) {
                            if (supportFragmentManager.findFragmentById(layouts[i - 1]) != null) {
                                hide(supportFragmentManager.findFragmentById(layouts[i - 1])!!)
                            }

                            fragments[i - 1]?.let { add(layouts[i - 1], it) }
                        } else fragments[i - 1]?.let { replace(layouts[i - 1], it) }

                        fragments[i - 1]?.let {
                            if (it is TitleProvider) {
                                binding.tvWinTitle.text = it.getTitle()
                                updateUIForFragment(it)
                            }
                        }
                    } else {
                        // Если передан null, удаляем все фрагменты из контейнера
                        supportFragmentManager.findFragmentById(layouts[i - 1])?.let {
                            remove(it)
                        }
                    }
                }
                addToBackStack(null)
                commit()
            }
    }

    fun updateLoadingFragmentText(text: String) {
        val loadingFragment = supportFragmentManager.findFragmentById(R.id.loadingLayout) as? LoadingFragment
        loadingFragment?.updateLoadingText(text)
    }

    fun showLoadingScreen(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.visibility = VISIBLE
        }
        else {
            binding.loadingLayout.visibility = GONE
        }
    }

    // Логика для того, что должно произойти при нажатии кнопки "Назад"
    private fun handleBackPressed() {
        updateLoadingFragmentText("Откатываемся назад...")

        // Возврат на предыдущий фрагмент из стека
        if (supportFragmentManager.backStackEntryCount >= 0) {
            val currentFragment = supportFragmentManager.fragments.lastOrNull()

            if (currentFragment is FilteredAdvertisementsFragment) {
                // Заменяем фрагмент результатов поиска на объявления
                setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance(), null, false, addToBackStack = false)
            }
            supportFragmentManager.popBackStack()
        } else {
            // Важно вызвать super.onBackPressed(), чтобы сохранить стандартное поведение
            super.onBackPressed() // В этом случае, если стек пуст, вызываем стандартное поведение для выхода из активности
        }
    }

    companion object {
        const val ID = "id"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val SECONDNAME = "secondName"
        const val FIRSTNAME = "firstName"
        const val CREATEDAT = "createdAt"
        const val PFP = "pfp"

        var currentUserId: Int? = null
        var currentUserEmail: String? = null
        var currentUserPassword: String? = null
        var currentUserSecondName: String? = null
        var currentUserFirstName: String? = null
        var currentUserCreatedAt: String? = null
        var currentUserPFP: String? = null
    }
}