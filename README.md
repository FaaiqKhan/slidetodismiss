# Slide to dismiss

This library provides the functionality of slide to dismiss the item feature. It is developed using Jetpack compose.

## Built With 🛠

### Jetpack Compose
    Android's recommended modern toolkit for building native UI.
### Kotlin
    First class and official programming language for Android development.
### Coroutines
    A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously.
### Material design API 3
    Jetpack Compose UIs with Material Design 3 Components, the next evolution of Material Design

# How to use :writing_hand:
#### Left icon only
```
LazyColumn {
    items(count = 2, key = { index: Int -> index }) { count ->
        SlideToDismiss(
            data = count,
            leftAction = {},
            leftIcon = Icons.Default.share,
            leftIconTint = Color.Green
        ) {
            Card {
                Text(text = count.toString())
            }
        }
    }
}
```
#### Right icon only
```
LazyColumn {
    items(count = 2, key = { index: Int -> index }) { count ->
        SlideToDismiss(
            data = count,
            rightAction = {},
            rightIcon = Icons.Default.Delete,
            rightIconTint = Color.Red
        ) {
            Card {
                Text(text = count.toString())
            }
        }
    }
}
```
#### With left and Right icon
```
LazyColumn {
    items(count = 2, key = { index: Int -> index }) { count ->
        SlideToDismiss(
            data = count,
            leftAction = {},
            leftIcon = Icons.Default.Share,
            leftIconTint = Color.Green,
            rightAction = {},
            rightIcon = Icons.Default.Delete,
            rightIconTint = Color.Red
        ) {
            Card {
                Text(text = count.toString())
            }
        }
    }
}
```
#### Without icons
```
LazyColumn {
    items(count = 2, key = { index: Int -> index }) { count ->
        SlideToDismiss(
            data = count,
            onDismiss = {},
        ) {
            Card {
                Text(text = count.toString())
            }
        }
    }
}
```

## Arguments
```
modifier: Modifier -> For modifications and update appearance of main view.
leftIconTint: Color -> (optional) Default value is red.
rightIconTint: Color -> (optional) Default value is red.
data: T -> (optional) Return data on dismiss.
leftIcon: ImageVector -> (optional) Icon.
rightIcon: ImageVector -> (optional) Icon.
leftAction: (data: T) -> (optional) Function return data on dismess.
rightAction: (data: T) -> (optional) Function return data on dismiss.
onDismiss: (data: T) -> (optional) Function return data on dismiss.
content: @Composable -> It takes `Row scope` composables.
```

## Hint
    If you want to give content fill width then use weight() rather then fillMaxWidth() of modifier.