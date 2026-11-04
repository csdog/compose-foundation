package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Table(ariaLabel = "Component API") {
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
