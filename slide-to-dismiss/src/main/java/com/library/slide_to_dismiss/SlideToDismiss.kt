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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun <T> SlideToDismiss(
    modifier: Modifier = Modifier,
    leftIconTint: Color = Color.Red,
    rightIconTint: Color = Color.Red,
    data: T? = null,
    leftIcon: ImageVector? = null,
    rightIcon: ImageVector? = null,
    leftAction: (data: T?) -> Unit = {},
    rightAction: (data: T?) -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    val offsetX = remember { Animatable(0f) }

    var iconWidth by remember { mutableStateOf(0f) }
    var selected by remember { mutableStateOf(false) }
    var scope: CoroutineScope? = null

    Box(
        modifier = modifier.defaultMinSize(minHeight = dimensionResource(id = R.dimen.minimum_height)),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            leftIcon?.let {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = null,
                    tint = leftIconTint,
                )
            }
            rightIcon?.let {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = null,
                    tint = rightIconTint,
                )
            }
        }
        Row(
            modifier = Modifier
                .pointerInput(Unit) {
                    val decay = splineBasedDecay<Float>(this)
                    coroutineScope {
                        scope = this@coroutineScope
                        while (true) {
                            if (leftIcon == null && rightIcon == null) return@coroutineScope

                            offsetX.stop() // Stop any ongoing animation.

                            // The animation stops when it reaches the bounds.
                            offsetX.updateBounds(
                                lowerBound = -size.width / 4f,
                                upperBound = size.width / 4f
                            )

                            awaitPointerEventScope {
                                val pointerId = awaitFirstDown().id // Detect a touch down event.
                                horizontalDrag(pointerId) { change ->
                                    // Update the animation value with touch events.
                                    if (leftIcon == null && change.positionChange().x > 0)
                                        return@horizontalDrag
                                    if (rightIcon == null && change.positionChange().x < 0)
                                        return@horizontalDrag
                                    launch {
                                        offsetX.snapTo(offsetX.value.plus(change.positionChange().x))
                                    }
                                }
                            }

                            launch {
                                // No longer receiving touch events. Prepare the animation.
                                val velocity = VelocityTracker().calculateVelocity().x
                                val targetOffsetX =
                                    decay.calculateTargetValue(offsetX.value, velocity)

                                selected = if (targetOffsetX.absoluteValue <= size.width / 10) {
                                    offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                                    false
                                } else {
                                    offsetX.animateTo(
                                        targetValue = if (selected) {
                                            0f
                                        } else {
                                            if (offsetX.value >= 0) iconWidth * -1
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
                .offset { IntOffset(offsetX.value.roundToInt(), 0) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0f),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            leftIcon?.let {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = null,
                    tint = leftIconTint,
                    modifier = Modifier
                        .onGloballyPositioned {
                            iconWidth = (it.size.width + 8)
                                .toFloat()
                                .unaryMinus()
                        }
                        .clickable {
                            if (offsetX.value > 0) {
                                scope?.run {
                                    launch {
                                        selected = false
                                        offsetX.animateTo(
                                            targetValue = 0f,
                                            initialVelocity = VelocityTracker().calculateVelocity().x
                                        )
                                    }
                                }
                                leftAction(data)
                            }
                        }
                )
            }
            rightIcon?.let {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = null,
                    tint = rightIconTint,
                    modifier = Modifier
                        .onGloballyPositioned {
                            iconWidth = (it.size.width + 8)
                                .toFloat()
                                .unaryMinus()
                        }
                        .clickable {
                            if (offsetX.value < 0) {
                                scope?.run {
                                    launch {
                                        selected = false
                                        offsetX.animateTo(
                                            targetValue = 0f,
                                            initialVelocity = VelocityTracker().calculateVelocity().x
                                        )
                                    }
                                }
                                rightAction(data)
                            }
                        }
                )
            }
        }
    }
}