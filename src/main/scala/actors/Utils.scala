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

  /**
   * Generates [filename, crc] mappings for all
   * files and directories in a passed metafile
   *
   * @param files
   * @return
   */
  def generateMap(files: List[String]): Map[String, Long] =
    files.foldLeft(Map[String,Long]())( (result,file)=> result + (file -> generateCRC(file)) )


  def generateCRC(entity: String): Long = generateCRC(new File(entity))

  /**
   * hashFun evaluates file's/folder's checksum
   * with java.util.zip.CRC32 algorithm
   *
   * @param entity to evaluate
   * @return crc32 value
   */
  def generateCRC(entity: File): Long = {
    import java.util.zip.CRC32
    val checksum = new CRC32()

    def updateChecksum(file: File) {
      val input = new BufferedInputStream(new FileInputStream(file))
      val currentByte = new Array[Byte](1)
      while (input.read(currentByte) != -1)
        checksum.update(currentByte(0))
      input.close()
    }

    def recUpdate(entity: File) {
      if (entity.isFile)
        updateChecksum(entity)
      else
        entity.listFiles().foreach(recUpdate)
    }

    recUpdate(entity)

    checksum.getValue()
  }

}
