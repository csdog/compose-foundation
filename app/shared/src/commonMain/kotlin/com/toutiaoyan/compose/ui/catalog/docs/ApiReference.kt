package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.table.Table
import com.toutiaoyan.compose.ui.components.table.TableBody
import com.toutiaoyan.compose.ui.components.table.TableCell
import com.toutiaoyan.compose.ui.components.table.TableHead
import com.toutiaoyan.compose.ui.components.table.TableHeader
import com.toutiaoyan.compose.ui.components.table.TableRow

data class ApiProperty(val name: String, val type: String, val defaultValue: String)

/** Types and defaults describe the actual Kotlin overload, rather than React props. */
@Composable
fun ApiReference(properties: List<ApiProperty>) {
    Table(modifier = Modifier.horizontalScroll(rememberScrollState()).width(640.dp), ariaLabel = "Component API") {
        TableHeader {
            TableHead("Property", Modifier.weight(1f))
            TableHead("Type", Modifier.weight(2f))
            TableHead("Default", Modifier.weight(1f))
        }
        TableBody {
            properties.forEachIndexed { index, property ->
                TableRow(isLast = index == properties.lastIndex) {
                    TableCell(property.name, Modifier.weight(1f))
                    TableCell(property.type, Modifier.weight(2f))
                    TableCell(property.defaultValue, Modifier.weight(1f))
                }
            }
        }
    }
}
