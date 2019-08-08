package ru.skillbranch.devintensive.ui.profile

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
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
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel
import android.util.TypedValue
import android.text.Editable
import android.text.TextWatcher



class ProfileActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    var isValidRepo = true
    lateinit var viewFields: Map<String, TextView>
    private val textBounds = Rect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_profile)
        Log.d("M_MainActivity", "onCreate")
        initViews(savedInstanceState)
        initViewModel()
        avatarFromInitials()
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
    @SuppressLint("ResourceAsColor")
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
                    if (!isValidRepo) {
                        et_repository.setText("")
                        wr_repository.isErrorEnabled = false
                        wr_repository.error = null
                    }
                    saveProfileInfo()
                }
                isEditMode = !isEditMode
                showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!et_repository.text.toString().isGithubUrl()) {
                    wr_repository.isErrorEnabled = true
                    wr_repository.error = "Невалидный адрес репозитория"
                    isValidRepo = false
                } else {
                    wr_repository.isErrorEnabled = false
                    wr_repository.error = null
                    isValidRepo = true
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

//        et_repository.setOnKeyListener(View.OnKeyListener { _, _, _->
//            if (!et_repository.text.toString().isGithubUrl()) {
//                wr_repository.isErrorEnabled = true
//                wr_repository.error = "Невалидный адрес репозитория"
//                isValidRepo = false
//            } else {
//                wr_repository.isErrorEnabled = false
//                wr_repository.error = null
//                isValidRepo = true
//            }
//            return@OnKeyListener false
//        })

        val colorAccent = resources.getIdentifier("color_accent", "color", packageName)
        iv_avatar.setBorderColor(colorAccent)
        val color = resources.getColor(colorAccent, theme)
        Log.d("M_ProfileActivity", "$color = ${iv_avatar.getBorderColor()}")

        iv_avatar.setBorderColor("#FC4C4C")
        Log.d("M_ProfileActivity", "${Color.parseColor("#FC4C4C")} = ${iv_avatar.getBorderColor()}")
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
        avatarFromInitials()
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
            avatarFromInitials()
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

    private fun avatarFromInitials() {
        val profile = viewModel.getProfileData().value
        if (profile?.firstName.isNullOrEmpty() && profile?.lastName.isNullOrEmpty()) {
            return
        }
        val text = Utils.toInitials(profile?.firstName, profile?.lastName)
        val size = Utils.dpToPx(112)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)

        // canvas background color
        canvas.drawARGB(0, 255, 255, 255)

        var paint = Paint()
        paint.color = getColorByReference(R.attr.colorAccent)
        paint.style = Paint.Style.FILL

        // get device dimensions
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        // circle center
        val radius = size / 2f
        var centerX = radius
        var centerY = radius

        // draw circle
        canvas.drawCircle(centerX, centerY, radius, paint)
        // now bitmap holds the updated pixels

        val textSize = Utils.dpToPx(50).toFloat()
        paint.color = Color.parseColor("#FFFFFF")
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = textSize
        paint.getTextBounds(text, 0, text!!.length, textBounds)
        canvas.drawText(text, centerX, centerY - textBounds.exactCenterY(), paint)

        // set bitmap as background to ImageView
        //iv_avatar.setImageBitmap(BitmapDrawable(resources, bitmap).bitmap)
        iv_avatar.setInitialsBackground(BitmapDrawable(resources, bitmap).bitmap)
    }

    private fun getColorByReference(color: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(color, typedValue, true)
        return typedValue.data
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
