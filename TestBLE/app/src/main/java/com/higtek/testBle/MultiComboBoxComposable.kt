package com.higtek.testBle


import android.R.attr.value
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp


class MultiComboBoxComposable {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuWithScrollStateSample() {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = scrollState
        ) {
            repeat(30) {
                //DropdownMenuItem(onClick = { /* Handle item! */ }) { Text("Item ${it + 1}") }
                DropdownMenuItem(onClick = { /* Handle item! */ }, text = { Text("Item ${it + 1}") })
            }
        }
        LaunchedEffect(expanded) {
            if (expanded) {
                // Scroll to show the bottom menu items.
                scrollState.scrollTo(scrollState.maxValue)
            }
        }
    }
}



//@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiAutocompleteExposedDropdownMenuSample() {
    /**
     * Returns the TextRange of the current token around the cursor, where commas define token
     * boundaries.
     */
    fun TextFieldState.currentTokenRange(): TextRange? {
        if (!selection.collapsed) return null

        val cursor = selection.start
        var start = cursor
        while (start > 0 && text[start - 1] != ',') {
            start--
        }
        while (start < cursor && text[start] == ' ') {
            start++
        }

        var end = cursor
        while (end < text.length && text[end] != ',') {
            end++
        }
        return TextRange(start, end)
    }

    fun TextFieldState.replaceThenAddComma(start: Int, end: Int, text: CharSequence) = edit {
        replace(start, end, text)
        val afterText = start + text.length
        if (afterText == this.length || this.charAt(afterText) != ',') {
            insert(afterText, ", ")
            placeCursorBeforeCharAt(afterText + 2)
        } else {
            placeCursorAfterCharAt(afterText)
        }
    }

    //val allOptions: List<String> = SampleData
    val allOptions: List<String> = mutableStateListOf<String>("One", "Two", "Four", "Four1", "Four2")
    //val allOptions: List<String> = mutableListOf<String>("EMPTY")
    val textFieldState = rememberTextFieldState(allOptions[0])
    val tokenSelection = textFieldState.currentTokenRange()
    val tokenAtCursor =
        if (tokenSelection != null) textFieldState.text.substring(tokenSelection) else ""

    val filteredOptions =
        if (tokenAtCursor.isBlank()) listOf<String>() else allOptions.filter{s -> s.contains(tokenAtCursor)}

    /*
        val filteredOptions =
            if (tokenAtCursor.isBlank()) emptyList() else allOptions.filteredBy(tokenAtCursor)
    */
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded && filteredOptions.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {

        TextField(
            modifier =
            Modifier.width(280.dp).menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )

/*
        var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }

        BasicTextField(
            state = textFieldState,
        )
*/

/*
//        var text by remember { mutableStateOf("Hello") }

        TextField(
            modifier =
            //Modifier.width(280.dp).menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            Modifier.width(280.dp).menuAnchor(),
//            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    //modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                    modifier = Modifier.menuAnchor(),
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
*/

        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = 280.dp),
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        if (tokenSelection != null) {
                            textFieldState.replaceThenAddComma(
                                tokenSelection.start,
                                tokenSelection.end,
                                option
                            )
                        }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSample() {
    //val options: List<String> = SampleData.take(5)
    val options: List<String> = mutableStateListOf<String>("One", "Two", "Four", "Four1", "Four2")

    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(options[0])

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            state = textFieldState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Label") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableExposedDropdownMenuSample() {
    //val options: List<String> = SampleData
    val options: List<String> = mutableStateListOf<String>("One", "Two", "Four", "Four1", "Four2")

    val textFieldState = rememberTextFieldState()

    // The text that the user inputs into the text field can be used to filter the options.
    // This sample uses string subsequence matching.
    //val filteredOptions = options.filteredBy(textFieldState.text)
    val filteredOptions = options.filter{s -> s.contains(textFieldState.text, ignoreCase = true)}

    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val expanded = allowExpanded && filteredOptions.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. An editable text field has
            // the anchor type `PrimaryEditable`.
            modifier =
            Modifier.width(280.dp).menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Login") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    // If the text field is editable, it is recommended to make the
                    // trailing icon a `menuAnchor` of type `SecondaryEditable`. This
                    // provides a better experience for certain accessibility services
                    // to choose a menu option without typing.
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = 280.dp),
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        //textFieldState.setTextAndPlaceCursorAtEnd(option.text)
                        textFieldState.setTextAndPlaceCursorAtEnd(option)
                        setExpanded(true)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

/*
@Composable
fun MultiComboBox(
    labelText: String,
    options: List<ComboOption>,
    onOptionsChosen: (List<ComboOption>) -> Unit,
    modifier: Modifier = Modifier,
    selectedIds: List<Int> = emptyList(),
) {
    var expanded by remember { mutableStateOf(false) }
    // when no options available, I want ComboBox to be disabled
    val isEnabled by rememberUpdatedState { options.isNotEmpty() }
    var selectedOptionsList  = remember { mutableStateListOf<Int>()}

    //Initial setup of selected ids
    selectedIds.forEach{
        selectedOptionsList.add(it)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (isEnabled()) {
                expanded = !expanded
                if (!expanded) {
                    onOptionsChosen(options.filter { it.id in selectedOptionsList }.toList())
                }
            }
        },
        modifier = modifier,
    ) {
        val selectedSummary = when (selectedOptionsList.size) {
            0 -> ""
            1 -> options.first { it.id == selectedOptionsList.first() }.text
            else -> "Wybrano ${selectedOptionsList.size}"
        }
        TextField(
            enabled = isEnabled(),
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedSummary,
            onValueChange = {},
            label = { Text(text = labelText) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onOptionsChosen(options.filter { it.id in selectedOptionsList }.toList())
            },
        ) {
            for (option in options) {

                //use derivedStateOf to evaluate if it is checked
                var checked = remember {
                    derivedStateOf{option.id in selectedOptionsList}
                }.value

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { newCheckedState ->
                                    if (newCheckedState) {
                                        selectedOptionsList.add(option.id)
                                    } else {
                                        selectedOptionsList.remove(option.id)
                                    }
                                },
                            )
                            Text(text = option.text)
                        }
                    },
                    onClick = {
                        if (!checked) {
                            selectedOptionsList.add(option.id)
                        } else {
                            selectedOptionsList.remove(option.id)
                        }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
*/