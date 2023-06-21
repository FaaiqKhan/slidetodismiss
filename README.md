# Slide to dismiss

This library is build the feature slide to dismiss the item. It is developed using Jetpack compose.

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
```
LazyColumn {
    items(count = 2, key = { index: Int -> index }) { count ->
        SlideToDismiss(
            data = count,
            modifier = Modifier.fillMaxWidth(),
            onDismiss = { },
        ) {
            Card(modifier = Modifier.weight(1f)) {
                Text(text = count.toString())
            }
        }
    }
```

## Arguments
```
modifier: Modifier -> For modifications and update appearance of `SlideToDismiss` view. 
iconModifier: Modifier -> For icon modifications and update in appearance of `SlideToDismiss icon` view.
data: T? -> (optional) Pass if you want the dismissed item or identifier of dismissed iteam.
onDismiss: (data: T?) -> Function when dismiss icon pressed, return the same data which was provided in `data` field.
iconTint: Color -> Color of dismiss icon by default it's red.
icon: ImageVector -> Dismiss icon by default it's Icons.Default.Delete.
content: @Composable -> It takes composables ans has `Row scope`.
```

## Hint 
    If you want to give content fill width then use weight() rather then fillMaxWidth() of modifier.