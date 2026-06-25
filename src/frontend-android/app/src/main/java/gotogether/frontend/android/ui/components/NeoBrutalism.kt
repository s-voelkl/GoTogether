package gotogether.frontend.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gotogether.frontend.android.ui.theme.Black
import gotogether.frontend.android.ui.theme.BrandYellow

/**
 * A reusable card component following the Neo-Brutalism style:
 * Thick black borders and optional background color.
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = Black,
    borderWidth: Int = 2,
    cornerRadius: Int = 16,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(cornerRadius.dp))
            .border(borderWidth.dp, borderColor, RoundedCornerShape(cornerRadius.dp))
            .padding(16.dp),
        content = content
    )
}

/**
 * A reusable button component following the Neo-Brutalism style.
 */
@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrandYellow,
    contentColor: Color = Black,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .background(if (enabled) backgroundColor else Color.LightGray, RoundedCornerShape(12.dp))
            .border(2.dp, Black, RoundedCornerShape(12.dp))
            .then(
                if (enabled) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) contentColor else Color.Gray,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp
        )
    }
}
