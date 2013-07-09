package actors

import java.io._

object Utils {

  private val root = new File("./iotestdir/")

  def getFileSystem(): List[String] = {
    exploreFileTree(List(root)) map (_.getAbsolutePath)
  }

  def exploreFileTree(files: List[File]): List[File] = files match {
    case head :: tail if head.isFile => head :: exploreFileTree(tail)
    case head :: tail if head.isDirectory => head :: exploreFileTree(head.listFiles().toList ::: tail)
    case Nil => Nil
  }
}
