package com.example.purrfectfinder

import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.purrfectfinder.Fragments.AdvertisementsFragment
import com.example.purrfectfinder.Fragments.FavouriteAdvertisementsFragment
import com.example.purrfectfinder.Fragments.FilteredAdvertisementsFragment
import com.example.purrfectfinder.Fragments.FiltersFragment
import com.example.purrfectfinder.Fragments.LoadingFragment
import com.example.purrfectfinder.Fragments.ProfileDescHorizontalFragment
import com.example.purrfectfinder.Fragments.ProfileDescriptionFragment
import com.example.purrfectfinder.Fragments.ProfileFragment
import com.example.purrfectfinder.Fragments.SettingsFragment
import com.example.purrfectfinder.databinding.ActivityMainBinding
import com.example.purrfectfinder.interfaces.TitleProvider
import java.io.File

class MainActivity : AppCompatActivity() {
    private val dataModel: DataModel by viewModels()

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            // Обрабатываем кнопку "Назад"
            handleBackPressed()
        }

        setFragment(R.id.loadingLayout, LoadingFragment.newInstance())

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
        currentUserSecondName = bundleReceived?.getString(SECONDNAME)
        currentUserFirstName = bundleReceived?.getString(FIRSTNAME)
        currentUserCreatedAt = bundleReceived?.getString(CREATEDAT)
        currentUserPFP = bundleReceived?.getString(PFP)

        // navigationview
        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView) { view, insets ->
            view.updatePadding(bottom = 0)
            insets
        }

        setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance())

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
                true
            )

            Log.e("BackstackSettings", supportFragmentManager.backStackEntryCount.toString())
        }

        binding.btnFilters.setOnClickListener {
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE
            binding.btnFilters.visibility = GONE


            setFragment(R.id.profileLayout, null)
            setFragment(R.id.fragmentLayout, FiltersFragment.newInstance(), false, true)

            updateLoadingFragmentText("Загружаем доступные фильтры...");

            Log.e("CURRENT BACKSTACK FILTERS", supportFragmentManager.backStackEntryCount.toString())
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

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, null)
                }
                R.id.favourites -> {
                    binding.btnFilters.visibility = VISIBLE
                    binding.btnSettings.visibility = GONE

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, FavouriteAdvertisementsFragment.newInstance())
                    updateLoadingFragmentText("Загружаем избранные объявления...")
                }
                R.id.petshop -> {
                    binding.btnFilters.visibility = VISIBLE
                    binding.btnSettings.visibility = GONE

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance())
                    updateLoadingFragmentText("Ищем котиков...")

                    Log.e("CURRENT BACKSTACK", supportFragmentManager.backStackEntryCount.toString())
                }
                R.id.clips -> {
                    binding.tvWinTitle.text = "Котоклипы"
                    binding.btnFilters.visibility = GONE
                    binding.btnSettings.visibility = GONE

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, null)
                }
                R.id.profile -> {
                    binding.btnFilters.visibility = GONE
                    binding.btnSettings.visibility = VISIBLE

                    showLoadingScreen(false)
                    setFragment(R.id.profileLayout, ProfileDescriptionFragment.newInstance())
                    setFragment(R.id.fragmentLayout, ProfileFragment.newInstance())
                }
            }
            true
        }

        dataModel.filteredAds.observe(this) {
            setFragment(R.id.fragmentLayout, FilteredAdvertisementsFragment.newInstance())
        }

        supportFragmentManager.addOnBackStackChangedListener {
            // Если в стеке только один фрагмент, скрываем кнопку "Назад"
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.btnPrev.visibility = GONE
            }

            val activeFragment = supportFragmentManager.fragments.lastOrNull {
                it.isVisible && it is TitleProvider
            }
            binding.tvWinTitle.text = (activeFragment as? TitleProvider)?.getTitle() ?: "Главная"

            // Устанавливаем размер текста и отступы в зависимости от длины заголовка
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

    }


    fun updateHeaderOnEdit() {
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE
            binding.btnFilters.visibility = GONE

            binding.tvWinTitle.textSize = 17f
            binding.tvWinTitle.setPadding(0, 13, 0, 0)

    }

    fun setFragment(layout: Int, fragment: Fragment?, isAdding: Boolean = false, addToBackStack: Boolean = false) {
        supportFragmentManager
            .beginTransaction().apply {
                if (fragment != null) {
                    if (isAdding) {
                        if (supportFragmentManager.findFragmentById(layout) != null) {
                            hide(supportFragmentManager.findFragmentById(layout)!!)
                        }

                        add(layout, fragment)
                    }
                    else replace(layout, fragment)

                    if (fragment is TitleProvider) {
                        binding.tvWinTitle.text = fragment.getTitle()
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

        Log.e("CURRENT BACKSTACK", supportFragmentManager.backStackEntryCount.toString())
    }

    fun setFragment(layouts: List<Int>, fragments: List<Fragment?>, isAdding: Boolean = false) {
        supportFragmentManager
            .beginTransaction().apply {
                for (i in 1..layouts.size) {
                    if (fragments[i - 1] != null) {
                        if (isAdding) {
                            if (supportFragmentManager.findFragmentById(layouts[i - 1]) != null) {
                                hide(supportFragmentManager.findFragmentById(layouts[i - 1])!!)
                            }

                            fragments[i - 1]?.let { add(layouts[i - 1], it) }
                        } else fragments[i - 1]?.let { replace(layouts[i - 1], it) }

                        fragments[i - 1]?.let {
                            if (it is TitleProvider) {
                                binding.tvWinTitle.text = it.getTitle()
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
                setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance(), false, addToBackStack = false)
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
        const val SECONDNAME = "secondName"
        const val FIRSTNAME = "firstName"
        const val CREATEDAT = "createdAt"
        const val PFP = "pfp"

        var currentUserId: Int? = null
        var currentUserEmail: String? = null
        var currentUserSecondName: String? = null
        var currentUserFirstName: String? = null
        var currentUserCreatedAt: String? = null
        var currentUserPFP: String? = null
    }
}