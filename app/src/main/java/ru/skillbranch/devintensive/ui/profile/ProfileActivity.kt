package ru.skillbranch.devintensive.ui.profile

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.isGithubUrl
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_profile)
        Log.d("M_MainActivity", "onCreate")
        initViews(savedInstanceState)
        initViewModel()
    }

    /**
     * Хранение состояний
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    /**
     * Инициализация полей
     */
    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener {
            if (isEditMode) {
                saveProfileInfo()
            }
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        //wr_repository.error = "Невалидный адрес репозитория"

        et_repository.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (!et_repository.text.toString().isGithubUrl()) {
                wr_repository.error = "Невалидный адрес репозитория"
            } else {
                wr_repository.error = null
            }
            return@OnKeyListener false
        })


    }

    /**
     * Инициализация модели
     */
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it)})
        viewModel.getTheme().observe(this, Observer { updateTheme(it)})
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity", "updateTheme")
        delegate.setLocalNightMode(mode)
    }

    /**
     * Заполнить поля в UI значениями из модели
     */
    private fun updateUI(profile: Profile) {
        profile.toMap().also{
            for ((k, v) in viewFields) {
                v.text = it[k].toString()
            }
        }
    }

    /**
     * Сохранить информацию из UI
     */
    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }

    /**
     * Режим чтения/редактирования формы с данными о пользователе
     */
    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for ((_, v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }

        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit) {
            val filter: ColorFilter? = if (isEdit) {
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }

            val icon = if(isEdit) {
                resources.getDrawable(R.drawable.ic_save_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity", "onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity", "onDestroy")
    }
}
