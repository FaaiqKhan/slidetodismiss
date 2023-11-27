package com.library.slide_to_dismiss

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun <T> SlideToDismiss(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    data: T? = null,
    onDismiss: (data: T?) -> Unit = {},
    iconTint: Color = Color.Red,
    icons: List<ImageVector?> = List(size = 2, init = { null }),
    content: @Composable RowScope.() -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    var iconWidth by remember { mutableStateOf(0f) }

    var selected by remember { mutableStateOf(false) }

    val swipeModifier = modifier
        .defaultMinSize(minHeight = dimensionResource(id = R.dimen.minimum_height))
        .pointerInput(Unit) {
            val decay = splineBasedDecay<Float>(this)
            coroutineScope {
                while (true) {
                    val velocityTracker = VelocityTracker()
                    offsetX.stop() // Stop any ongoing animation.

                    awaitPointerEventScope {
                        val pointerId = awaitFirstDown().id // Detect a touch down event.
                        horizontalDrag(pointerId) { change ->
                            // Update the animation value with touch events.
                            launch {
                                offsetX.snapTo(offsetX.value.plus(change.positionChange().x))
                            }
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                        }
                    }

                    // No longer receiving touch events. Prepare the animation.
                    val velocity = velocityTracker.calculateVelocity().x
                    val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)

                    // The animation stops when it reaches the bounds.
                    offsetX.updateBounds(
                        lowerBound = -size.width.toFloat(),
                        upperBound = size.width.toFloat()
                    )

                    launch {
                        selected = if (targetOffsetX.absoluteValue <= (size.width / 10)) {
                            // Slide back
                            offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                            false
                        } else {
                            offsetX.animateTo(
                                targetValue = if (selected) {
                                    0f
                                } else {
                                    if (offsetX.value > 0) iconWidth * -1
                                    else iconWidth
                                },
                                initialVelocity = velocity
                            )
                            !selected
                        }
                    }
                }
            }
        }
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }

    val slideIconModifier = iconModifier
        .onGloballyPositioned {
            iconWidth = it.size.width
                .toFloat()
                .unaryMinus()
        }
        .clickable { onDismiss(data) }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        icons.forEachIndexed lit@{ index, imageVector ->
            if (imageVector == null) return@lit
            Box(
                modifier = iconModifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = dimensionResource(id = R.dimen.minimum_height)),
                contentAlignment = if (index == 0) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Icon(
                    tint = iconTint,
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = slideIconModifier
                )
            }
        }
        Row(
            modifier = swipeModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content
        )
    }
}