package com.androidv.foodonwheel.activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.fragment.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main_menu.*
import org.w3c.dom.Text

class main_menu : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previourMenuItem: MenuItem? = null

    var doubleBackToExitOnce = false

    lateinit var sharedPreferences: SharedPreferences

    lateinit var name: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        setUpToolBar()

        val username = sharedPreferences.getString(getString(R.string.user_name), "")

        //Used to Add Name in header
        val header = navigationView.getHeaderView(0)
        name = header.findViewById(R.id.nav_name)
        name.text = username

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@main_menu,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        actionBarDrawerToggle.syncState()

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        openHomeFragment()

        navigationView.setNavigationItemSelectedListener {
            if (previourMenuItem != null) {
                previourMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previourMenuItem = it
            println("Item id is $it")
            println("$it")
            when (it.itemId) {
                R.id.home -> {
                    openHomeFragment()
                }

                R.id.profile -> {
                    openProfileFragment()
                }
                R.id.favrest -> {
                    openFavRestFragment()
                }
                R.id.history -> {
                    openHistoryFragment()
                }
                R.id.faq -> {
                    openFaqFragment()
                }
                R.id.aboutapp -> {
                    openAboutAppFragment()
                }
                R.id.logout -> {
                    logout()
                }
            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHomeFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, HomeFragment(this)).commit()
        supportActionBar?.title = "Home"
        drawerLayout.closeDrawers()
        println("Going to Home Fragment")
        navigationView.setCheckedItem(R.id.home)
    }

    fun openProfileFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment(this))
            .commit()
        supportActionBar?.title = "My Profile"
        navigationView.setCheckedItem(R.id.profile)
        drawerLayout.closeDrawers()
    }

    fun openFavRestFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, FavoriteFragment()).commit()
        supportActionBar?.title = "Favourite Restaurants"
        navigationView.setCheckedItem(R.id.favrest)
        drawerLayout.closeDrawers()
    }

    fun openHistoryFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, OrderHistory(this)).commit()
        supportActionBar?.title = "Order History"
        navigationView.setCheckedItem(R.id.history)
        drawerLayout.closeDrawers()
    }

    fun openFaqFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, FaqFragment()).commit()
        supportActionBar?.title = "FAQs"
        navigationView.setCheckedItem(R.id.faq)
        drawerLayout.closeDrawers()
    }

    fun openAboutAppFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, AboutAppFragment()).commit()
        supportActionBar?.title = "About App"
        navigationView.setCheckedItem(R.id.aboutapp)
        drawerLayout.closeDrawers()

    }

    fun logout() {

        val dialog =AlertDialog.Builder(this@main_menu)
        dialog.setTitle("LOGOUT")
        dialog.setMessage("Are you sure you want to logout?")
        dialog.setPositiveButton("YES"){text, listener->
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            val logoutact = Intent(this@main_menu, Login::class.java)
            startActivity(logoutact)
            finish()
        }
        dialog.setNegativeButton("NO"){text, listener->

        }
        dialog.create()
        dialog.show()
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is HomeFragment -> openHomeFragment()

            is HomeFragment -> {
                if (doubleBackToExitOnce) {
                    super.onBackPressed()
                    return
                }
                this.doubleBackToExitOnce = true
                Toast.makeText(this, "Click again to Exit", Toast.LENGTH_SHORT).show()
                Handler().postDelayed(Runnable { doubleBackToExitOnce = false }, 2000)
            }

            else -> super.onBackPressed()

        }
    }

}