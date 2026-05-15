import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.tables.TableBlock
import scala.jdk.CollectionConverters._

object TableParserDebug {
  def main(args: Array[String]): Unit = {
    val markdown = """| Role | Responsibility |
|------|-----------------|
| **Program Manager** | Coordination |"""

    val parser = Parser.builder()
      .extensions(java.util.Arrays.asList(TablesExtension.create()))
      .build()
    
    val doc = parser.parse(markdown)
    
    doc.getChildren.asScala.foreach { child =>
      println(s"Top level: ${child.getClass.getSimpleName}")
      if (child.isInstanceOf[TableBlock]) {
        val table = child.asInstanceOf[TableBlock]
        println(s"  Table children: ${table.getChildren.size()}")
        
        table.getChildren.asScala.zipWithIndex.foreach { case (node, idx) =>
          println(s"    [$idx] ${node.getClass.getSimpleName}")
          debugNode(node, 3)
        }
      }
    }
  }
  
  def debugNode(node: org.commonmark.node.Node, indent: Int): Unit = {
    val prefix = "  " * indent
    node.getChildren.asScala.foreach { child =>
      val chars = try { child.getChars.toString } catch { case _: Exception => "(no chars)" }
      println(s"$prefix${child.getClass.getSimpleName}: '$chars'")
      debugNode(child, indent + 1)
    }
  }
}
