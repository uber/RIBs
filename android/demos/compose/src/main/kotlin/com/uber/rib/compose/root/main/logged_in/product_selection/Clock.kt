package com.uber.rib.compose.root.main.logged_in.product_selection

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Clock(modifier: Modifier = Modifier) {
  val handColor = if (isSystemInDarkTheme()) Color.White else Color.Black
  var currentAngle = 0f
  var previous = 0f

  val infiniteTransition = rememberInfiniteTransition()
  val t by infiniteTransition.animateFloat(
    initialValue = 0f, targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    )
  )

  Canvas(modifier = modifier) {
    val middle = Offset(size.minDimension / 2, size.minDimension / 2)
    drawCircle(
      color = handColor,
      center = middle,
      radius = size.minDimension/2,
      style = Stroke(4.dp.toPx()),
    )
    withTransform(
      {
        if (previous > t) {
          currentAngle += 360/60
        }
        previous = t
        rotate( currentAngle, middle)
      }, {
        drawLine(
          cap = StrokeCap.Round,
          strokeWidth = 12.dp.toPx(),
          color = handColor,
          start = middle,
          end = Offset(size.minDimension / 2, 36.dp.toPx())
        )
      }
    )
    withTransform(
      {
        rotate( 360*t, middle)
      }, {
        drawLine(
          strokeWidth = 8.dp.toPx(),
          cap = StrokeCap.Round,
          color = Color.Red,
          start = middle,
          end = Offset(size.minDimension / 2, 12.dp.toPx())
        )
      }
    )
  }
}

@Preview(widthDp = 128, heightDp = 128)
@Composable
fun ClockPreview() {
  Clock(modifier = Modifier.padding(4.dp))
}
