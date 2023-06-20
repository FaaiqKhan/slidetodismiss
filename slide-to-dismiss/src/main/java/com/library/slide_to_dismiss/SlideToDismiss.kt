package com.library.slide_to_dismiss

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> SlideToDismiss(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    data: T? = null,
    onDismiss: (data: T?) -> Unit = {},
    iconTint: Color = Color.Red,
    icon: ImageVector = Icons.Default.Delete,
    content: @Composable RowScope.() -> Unit
) {
    var selected by remember { mutableStateOf(false) }

    val offsetX = remember { Animatable(0f) }
    val transition = updateTransition(
        selected,
        label = stringResource(id = R.string.transition_label)
    )

    val swipeModifier = modifier
        .defaultMinSize(minHeight = dimensionResource(id = R.dimen.minimum_height))
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        .pointerInput(Unit) {
            val decay = splineBasedDecay<Float>(this)
            coroutineScope {
                while (true) {
                    val velocityTracker = VelocityTracker()
                    // Stop any ongoing animation.
                    offsetX.stop()
                    awaitPointerEventScope {
                        // Detect a touch down event.
                        val pointerId = awaitFirstDown().id

                        horizontalDrag(pointerId) { change ->
                            // Update the animation value with touch events.
                            val targetPosition = offsetX.value + change.positionChange().x
                            if (targetPosition > 0)
                                return@horizontalDrag
                            launch { offsetX.snapTo(targetValue = targetPosition) }
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                        }
                    }
                    // No longer receiving touch events. Prepare the animation.
                    val velocity = velocityTracker.calculateVelocity().x
                    val targetOffsetX = decay.calculateTargetValue(
                        offsetX.value,
                        velocity
                    )
                    // The animation stops when it reaches the bounds.
                    offsetX.updateBounds(
                        lowerBound = (size.width / 4)
                            .toFloat()
                            .unaryMinus(),
                        upperBound = 0f,
                    )
                    launch {
                        // Slide back
                        offsetX.animateTo(
                            targetValue = 0f,
                            initialVelocity = velocity
                        )
                        if (targetOffsetX.absoluteValue > (size.width / 10)) {
                            selected = !selected
                        }
                    }
                }
            }
        }

    Row(
        modifier = swipeModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        content()
        // AnimatedVisibility as a part of the transition.
        transition.AnimatedVisibility(
            exit = shrinkHorizontally(),
            enter = expandHorizontally(),
            visible = { targetSelected -> targetSelected },
        ) {
            Icon(
                tint = iconTint,
                imageVector = icon,
                contentDescription = null,
                modifier = iconModifier
                    .weight(1f)
                    .clickable { onDismiss(data) },
            )
        }
    }
}