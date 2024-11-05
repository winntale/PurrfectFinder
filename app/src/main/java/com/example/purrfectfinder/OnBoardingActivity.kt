package com.example.purrfectfinder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.purrfectfinder.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private var _binding: ActivityOnBoardingBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityOnBoardingBinding must not be null")

    private lateinit var networkChangeReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        networkChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (isInternetAvailable(context)) {
                    Toast.makeText(context, "Интернет доступен", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Интернет недоступен", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@OnBoardingActivity, NoConnectionActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.btnPrev.visibility = INVISIBLE

        binding.btnNext.setOnClickListener {
            changeOnBoarding()
        }

        binding.btnPrev.setOnClickListener {
            changeOnBoardingBack()
        }

        binding.btnSkip.setOnClickListener() {
            gotoRegPage()
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkChangeReceiver)
    }

    // Метод для проверки наличия подключения к интернету
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            )
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    private fun changeOnBoarding() {

        with(binding) {

            if (ivOnBoardingProgressLines.tag == "pl1") {

                ivOnBoardingPicture.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_verified_user
                    )
                )

                ivOnBoardingProgressLines.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_progress_lines_2
                    )
                )

                tvOnBoardingTitle.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obTitle2
                )

                tvOnBoardingDesc.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obDesc2
                )

                ivOnBoardingProgressLines.tag = "pl2"

                btnPrev.isVisible = true

            } else if (ivOnBoardingProgressLines.tag == "pl2") {

                ivOnBoardingPicture.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_girl_with_cat
                    )
                )

                ivOnBoardingProgressLines.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_progress_lines_3
                    )
                )

                tvOnBoardingTitle.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obTitle3
                )

                tvOnBoardingDesc.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obDesc3
                )

                btnNext.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.btnNextTextOB3
                )

                ivOnBoardingProgressLines.tag = "pl3"

                btnSkip.visibility = INVISIBLE

            } else if (ivOnBoardingProgressLines.tag == "pl3") {
                gotoRegPage()
            }

        }


    }


    private fun changeOnBoardingBack() {

        with(binding) {

            if (ivOnBoardingProgressLines.tag == "pl3") {

                ivOnBoardingPicture.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_verified_user
                    )
                )

                ivOnBoardingProgressLines.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_progress_lines_2
                    )
                )

                tvOnBoardingTitle.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obTitle2
                )

                tvOnBoardingDesc.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obDesc2
                )

                btnNext.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.next
                )

                ivOnBoardingProgressLines.tag = "pl2"

                btnSkip.visibility = VISIBLE
            } else if (ivOnBoardingProgressLines.tag == "pl2") {

                ivOnBoardingPicture.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_man_with_cat
                    )
                )

                ivOnBoardingProgressLines.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OnBoardingActivity,
                        R.drawable.ic_progress_lines_1
                    )
                )

                tvOnBoardingTitle.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obTitle1
                )

                tvOnBoardingDesc.text = ContextCompat.getString(
                    this@OnBoardingActivity,
                    R.string.obDesc1
                )

                btnPrev.isVisible = false

                ivOnBoardingProgressLines.tag = "pl1"

            }

        }


    }


    private fun gotoRegPage() {
        val intent = Intent(this@OnBoardingActivity, AuthorizationActivity::class.java)
        startActivity(intent)
    }


    private fun dpToPx(dp: Int): Int {
        val density = this.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}