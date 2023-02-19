package org.orca.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.halilibo.richtext.ui.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.orca.common.ui.theme.AppTheme

@Composable
fun HtmlText(
    document: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    uriHandler: UriHandler = LocalUriHandler.current,
    domain: String? = null
) {
    val html = Jsoup.parse(document)

    RichText {
        FlowRow {
            HtmlText(html, modifier, style, uriHandler, domain)
        }
    }
}

@Composable
fun RichTextScope.HtmlText(
    node: Node,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    uriHandler: UriHandler = LocalUriHandler.current,
    domain: String? = null
) {
    node.childNodes().filterNot { it.toString().trim() == "" }.forEach {
        when (it) {
            is TextNode -> Text(text = it.text(), modifier = modifier, style = style)
            is Element -> {

                when (it.nodeName()) {
                    "strong" ->
                        HtmlText(it, modifier, style.copy(fontWeight = FontWeight.Bold))
                    "b" ->
                        HtmlText(it, modifier, style.copy(fontWeight = FontWeight.Bold))
                    "i" ->
                        HtmlText(it, modifier, style.copy(fontStyle = FontStyle.Italic))
                    "em" ->
                        HtmlText(it, modifier, style.copy(fontStyle = FontStyle.Italic))
                    "blockquote" ->
                        BlockQuote {
                            HtmlText(it, modifier, style)
                        }
                    "br" -> LineBreak()
                    "ol" -> HtmlList(it, ListType.Ordered, modifier, style)
                    "ul" -> HtmlList(it, ListType.Unordered, modifier, style)
                    "hr" -> HorizontalRule()
                    "p" -> Paragraph {
                        HtmlText(it, modifier, style)
                    }
                    "table" -> HtmlTable(it)
                    "a" -> HtmlLink(it, modifier, style, uriHandler, domain)

                    else -> HtmlText(it, modifier, style)
                }
            }
        }
    }
}

@Composable
fun RichTextScope.HtmlLink(
    node: Element,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    uriHandler: UriHandler = LocalUriHandler.current,
    domain: String? = null
) {
    var href = node.attr("href")
    if (!href.startsWith("http")) HtmlText(node, modifier, style)
    else {
        if (domain != null && href.startsWith("/")) href = "https://$domain$href"
        HtmlText(node, modifier.clickable { uriHandler.openUri(href) }, style.copy(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline))
    }
}

@Composable
fun RichTextScope.LineBreak() {
    Spacer(Modifier.fillMaxWidth())
}

@Composable
fun RichTextScope.Paragraph(content: @Composable () -> Unit) {
    LineBreak()
    content()
    LineBreak()
}

@Composable
fun RichTextScope.HtmlList(
    node: Element,
    listType: ListType,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
) {

    val list: List<Node> = node.childNodes().filterNot { it.toString().trim() == "" }

    Paragraph {
        Column {
            list.forEachIndexed { index, listItem ->
                FlowRow {
                    when (listType) {
                        ListType.Ordered -> Text("${index+1}.", style = style)
                        ListType.Unordered -> Text("•")
                    }
                    Text(" ")
                    HtmlText(listItem, modifier, style)
                }
            }
        }
    }
}

@Composable
private fun RichTextScope.HtmlTable(
    node: Element,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    uriHandler: UriHandler = LocalUriHandler.current,
    domain: String? = null

) {
    val children =  node.children()
    val body = children.filter { it.nodeName() == "tbody" }

    Paragraph {
        Column(
            modifier
                .height(IntrinsicSize.Min)
        ) {
            // handle elements explicitly in the body
            body.forEach { body ->
                body.children().forEach { tableRow ->
                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth()
                    ) {
                        tableRow.children().forEach { tableCell ->
                            Column(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                HtmlText(
                                    tableCell,
                                    Modifier.padding(8.dp),
                                    style,
                                    uriHandler,
                                    domain
                                )
                            }
                        }
                    }
                }
            }
            // todo: alternatively handle elements not in the body
        }
    }
}