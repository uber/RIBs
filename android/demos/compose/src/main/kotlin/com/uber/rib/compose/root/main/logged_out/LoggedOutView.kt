package com.uber.rib.compose.root.main.logged_out

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.unit.dp
import androidx.core.view.setPadding
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable

class LoggedOutView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), LoggedOutInteractor.LoggedOutPresenter {
  init {
    setBackgroundColor(Color.YELLOW)
    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
      gravity = Gravity.CENTER
    }
    addView(
      LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        addView(TextView(context).apply {
          text = "(view)"
          setTextColor(Color.BLACK)
        })
        addView(EditText(context).apply {
          hint = "Username"
          setHintTextColor(Color.BLACK)
          setTextColor(Color.BLACK)
        })
        addView(EditText(context).apply {
          hint = "Password"
          setHintTextColor(Color.BLACK)
          setTextColor(Color.BLACK)
        })
        addView(Button(context).apply {
          text = "Login"
          setTextColor(Color.BLACK)
        })
      }
    )
  }

  override fun loginClicks(): Observable<Unit> {
    return with (getChildAt(0) as ViewGroup) {
      RxView.clicks(getChildAt(childCount - 1)).map { Unit }
    }
  }
}
