package com.example.purrfectfinder

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

        titleChangesStack.add(binding.tvWinTitle.text.toString())

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

            binding.tvWinTitle.text = ContextCompat.getString(
                this@MainActivity,
                R.string.settings
            )
            binding.tvWinTitle.textSize = 23f
            binding.tvWinTitle.setPadding(0, 0, 0, 0)

            addFragment(
                listOf(R.id.profileLayout, R.id.fragmentLayout),
                listOf(ProfileDescHorizontalFragment.newInstance(), SettingsFragment.newInstance()),
                "Настройки",
                titleChangesStack
            )
        }

        binding.btnFilters.setOnClickListener {
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE
            binding.btnFilters.visibility = GONE

            binding.tvWinTitle.text = "Фильтры"
            binding.tvWinTitle.textSize = 23f
            binding.tvWinTitle.setPadding(0, 0, 0, 0)

            addFragment(
                listOf(R.id.profileLayout, R.id.fragmentLayout),
                listOf(null, FiltersFragment.newInstance()),
                "Фильтры",
                titleChangesStack
            )
        }

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    binding.tvWinTitle.text = "Главная"
                    binding.btnFilters.visibility = GONE
                    binding.btnSettings.visibility = GONE

                    titleChangesStack.removeLast()
                    titleChangesStack.add(binding.tvWinTitle.text.toString())

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, null)
                }
                R.id.favourites -> {
                    binding.tvWinTitle.text = "Избранное"
                    binding.btnFilters.visibility = VISIBLE
                    binding.btnSettings.visibility = GONE

                    titleChangesStack.removeLast()
                    titleChangesStack.add(binding.tvWinTitle.text.toString())

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, FavouriteAdvertisementsFragment.newInstance())
                }
                R.id.petshop -> {
                    binding.tvWinTitle.text = ContextCompat.getString(
                        this@MainActivity,
                        R.string.advertisements
                    )
                    binding.btnFilters.visibility = VISIBLE
                    binding.btnSettings.visibility = GONE

                    titleChangesStack.removeLast()
                    titleChangesStack.add(binding.tvWinTitle.text.toString())

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance())
                }
                R.id.clips -> {
                    binding.tvWinTitle.text = "Котоклипы"
                    binding.btnFilters.visibility = GONE
                    binding.btnSettings.visibility = GONE

                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, null)

                    titleChangesStack.removeLast()
                    titleChangesStack.add(binding.tvWinTitle.text.toString())
                }
                R.id.profile -> {
                    binding.tvWinTitle.text = ContextCompat.getString(
                        this@MainActivity,
                        R.string.your_profile
                    )
                    binding.btnFilters.visibility = GONE
                    binding.btnSettings.visibility = VISIBLE

                    titleChangesStack.removeLast()
                    titleChangesStack.add(binding.tvWinTitle.text.toString())

                    setFragment(R.id.profileLayout, ProfileDescriptionFragment.newInstance())
                    setFragment(R.id.fragmentLayout, ProfileFragment.newInstance())
                }
            }
            true
        }

        dataModel.filteredAds.observe(this) {
            setFragment(R.id.fragmentLayout, FilteredAdvertisementsFragment.newInstance())
            supportFragmentManager.beginTransaction().apply {
                addToBackStack(null)
                commit()
            }

            titleChangesStack.removeLast()
            binding.tvWinTitle.text = "Результаты поиска"
            titleChangesStack.add(binding.tvWinTitle.text.toString())
        }

    }


    fun updateHeaderOnEdit(titleName: String) {
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE
            binding.btnFilters.visibility = GONE

            binding.tvWinTitle.text = titleName
            titleChangesStack.add(binding.tvWinTitle.text.toString())

            binding.tvWinTitle.textSize = 17f
            binding.tvWinTitle.setPadding(0, 13, 0, 0)

    }

    fun setFragment(layout: Int, fragment: Fragment?) {
        supportFragmentManager
            .beginTransaction().apply {
                if (fragment != null) {
                    replace(layout, fragment)
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

    fun addFragment(layout: Int, fragment: Fragment, newTitle: String?, titleChangesStack: MutableList<String>) {
        supportFragmentManager
            .beginTransaction().apply {
                if (supportFragmentManager.findFragmentById(layout) != null) {
                    hide(supportFragmentManager.findFragmentById(layout)!!)
                }

                add(layout, fragment)
                addToBackStack(null)

                if (newTitle != null)
                    titleChangesStack.add(newTitle)

                commit()
            }
        Log.e("CURRENT BACKSTACK", supportFragmentManager.backStackEntryCount.toString())
    }

    // перегрузка функции, для того, чтобы добавить в стэк один уровень,
    // но отрисовать фрагменты на нескольких layouts
    fun addFragment(layouts: List<Int>, fragments: List<Fragment?>, newTitle: String?, titleChangesStack: MutableList<String>) {
        supportFragmentManager
            .beginTransaction().apply {
                for (i in 1..layouts.size) {
                    // если текущий фрагмент рассматриваемого layout'а не null
                    // прячем его
                    if (supportFragmentManager.findFragmentById(layouts[i - 1]) != null) {
                        hide(supportFragmentManager.findFragmentById(layouts[i - 1])!!)
                    }

                    // далее, если текущий фрагмент не null, добавляем его на текущий layout
                    if (fragments[i - 1] != null)
                        fragments[i - 1]?.let { add(layouts[i - 1], it) }
                    // иначе удаляем текущий фрагмент
                    else
                        supportFragmentManager.findFragmentById(layouts[i - 1])?.let {
                            remove(it)
                        }
                }
                addToBackStack(null)

                if (newTitle != null)
                    titleChangesStack.add(newTitle)

                commit()
            }
        Log.e("CURRENT BACKSTACK", supportFragmentManager.backStackEntryCount.toString())
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

        Log.e("CURRENT LIST OF TITLES", titleChangesStack.toString())
        Log.e("CURRENT BACKSTACK", supportFragmentManager.backStackEntryCount.toString())
        // Если в стеке только один фрагмент, скрываем кнопку "Назад"
        if (supportFragmentManager.backStackEntryCount == 1) {
            binding.btnPrev.visibility = GONE
        }

        // Удаляем последний элемент из списка заголовков
        if (!titleChangesStack.isEmpty())
            titleChangesStack.removeLast()

        binding.tvWinTitle.text = titleChangesStack.last()

        // Устанавливаем размер текста и отступы в зависимости от длины заголовка
        if (binding.tvWinTitle.text.length < 20) {
            binding.tvWinTitle.textSize = 23f
            binding.tvWinTitle.setPadding(0, 0, 0, 0)
        } else {
            binding.tvWinTitle.textSize = 17f
            binding.tvWinTitle.setPadding(0, 13, 0, 0)
        }

        // Обновляем видимость кнопок в зависимости от текущего заголовка
        when (titleChangesStack.last()) {
            "Главная" -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
            }
            "Избранное" -> {
                binding.btnFilters.visibility = VISIBLE
                binding.btnSettings.visibility = GONE
            }
            "Объявления" -> {
                binding.btnFilters.visibility = VISIBLE
                binding.btnSettings.visibility = GONE
            }
            "Котоклипы" -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
            }
            "Ваш профиль" -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = VISIBLE
            }
            "Фильтры" -> {
                binding.btnFilters.visibility = GONE
                binding.btnSettings.visibility = GONE
            }
        }

        // Возврат на предыдущий фрагмент из стека
        if (supportFragmentManager.backStackEntryCount > 0) {
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

        var titleChangesStack: MutableList<String> = mutableListOf()
    }
}