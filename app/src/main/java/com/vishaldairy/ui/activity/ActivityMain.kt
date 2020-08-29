package com.vishaldairy.ui.activity

import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.vishaldairy.R
import com.vishaldairy.base.BaseActivity
import com.vishaldairy.databinding.ActivityMainBinding
import com.vishaldairy.model.MAddress
import com.vishaldairy.model.MUser
import com.vishaldairy.ui.fragment.*
import com.vishaldairy.viewModel.VMHome

class ActivityMain : BaseActivity<ActivityMainBinding>() {

    var binding: ActivityMainBinding? = null
    private var viewModel: VMHome? = null
    var user = MUser(
        "Jane Doe",
        R.drawable.icon_person,
        "+911313131313",
        "jane.doe@gmail.com",
        MAddress(
            "Jane Doe",
            "Apartment or Gated Society",
            "G-118",
            "Emerald Garden",
            "",
            "Emerald Garden",
            "Kanpur",
            "UP",
            208001
        ),
        "Student",
        5,
        1010
    )

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initViews() {
        binding = getViewDataBinding()
        viewModel = ViewModelProvider(this).get(VMHome::class.java)
        binding?.viewModel = viewModel
        viewModel?.setNavigator(this)
        binding?.lifecycleOwner = this
    }

    override fun initListeners() {
    }

    override fun bindDataWithViews() {
        viewModel?.getData(1)

        addFragment(
            FragmentDashboard(),
            R.id.frame_layout,
            "0"
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        supportFragmentManager.findFragmentByTag("0")?.isVisible?.let {
            if (it) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.main_wallet_btn) {
            replaceFragment(
                FragmentMyWallet(),
                R.id.frame_layout,
                "6",
                true
            )
        }

        if (item.itemId == android.R.id.home) {
            supportFragmentManager.findFragmentByTag("0")?.isVisible?.let {
                if (it) {
                    findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START)
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("1")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("2")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("3")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("4")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("5")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("6")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("7")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("9")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("10")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("11")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("12")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("13")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("14")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("15")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }

            supportFragmentManager.findFragmentByTag("16")?.isVisible?.let {
                if (it) {
                    onBackPressed()
                    return true
                }
            }
        }

        return false
    }
}