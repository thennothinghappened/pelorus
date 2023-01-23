package org.orca.common.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.onClick
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.halilibo.richtext.ui.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

fun parseHtmlFromString(document: String): Node = Jsoup.parse(document).body()

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RichTextScope.HtmlText(
    node: Node,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    uriHandler: UriHandler = LocalUriHandler.current
) {
    node.childNodes().filterNot { it.toString().trim() == "" }.forEach {
        when (it) {
            is TextNode -> Text(text = it.text(), modifier = modifier, style = style)
            is Element -> {

                when (it.nodeName()) {
                    "strong" ->
                        HtmlText(it, modifier, style.copy(fontWeight = FontWeight.Bold))

                    "div" -> {
                        HtmlText(it, modifier, style)
                    }
                    "blockquote" ->
                        BlockQuote {
                            HtmlText(it, modifier, style)
                        }
                    "br" -> Text("\n")
                    "ol" -> HtmlList(it, ListType.Ordered, modifier, style)
                    "ul" -> HtmlList(it, ListType.Unordered, modifier, style)
                    "hr" -> HorizontalRule()
                    "p" -> {
                        HtmlText(it, modifier, style)
                    }
                    "a" -> {
                        val href = it.attr("href")
                        if (!href.startsWith("http")) HtmlText(it, modifier, style)
//                        else HtmlText(it, modifier.onClick { uriHandler.openUri(href) }, style.copy(color = Color.Blue, textDecoration = TextDecoration.Underline))
                    }

                    else -> HtmlText(it, modifier, style)
                }
            }
        }
    }
}

@Composable
fun RichTextScope.HtmlList(
    node: Node,
    listType: ListType,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
) {

    val list: List<Node> = node.childNodes().filterNot { it.toString().trim() == "" }

    FormattedList<Node>(
        listType = listType,
        items = list,
        drawItem = {
            HtmlText(it, modifier, style)
        }
    )
}

@Composable
private fun RichTextScope.HtmlTable(
    node: Node,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
) {

}