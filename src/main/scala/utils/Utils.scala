package actors.utils

import java.io._

object Utils {




  def getFileSystem(root: String): List[String] = {
    val rootF = new File(root)
    exploreFileTree(List(rootF)) map (_.getAbsolutePath)
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

  /**
   *
   * @param oldMap,
   * @param newMap, who's key set are subset of oldMap's key set
   * @return Either list of corrupted files
   *         Or true
   */
  def compareCRCMaps(oldMap: Map[String, Long], newMap: Map[String, Long]):
      Either[List[String],Boolean] = {
    val corruptedFiles = newMap.foldLeft(List[String]()){
      (either, kv) => if (oldMap exists (_ == kv)) either else kv._1 :: either
    }
    if (corruptedFiles.isEmpty)
      Right(true)
    else
      Left(corruptedFiles)
  }







  /*
  def compareMaps(FSMap: Map[String, Long], toCheckMap: Map[String, Long])
                 (implicit logger: Option[Logger] = None): Boolean = {
    val result = toCheckMap.forall(keyValue => belongsToMap(keyValue, FSMap))
    logger.foreach(_.close())
    updateSuccessfulCheck()
    result
  }

  private def belongsToMap(kv:(String,Long), map: Map[String,Long])
                          (implicit logger: Option[Logger] = None): Boolean = {
    var result = false
    if (map exists (_ == kv)){
      logger.foreach(_.fileNotChanged(kv._1))
      result = true
    }else if (map exists (_._1 == kv._1))
      logger.foreach(_.fileChangedSince(kv._1,1000))
    else
      logger.foreach(_.fileDisappearedSince(kv._1,1000))
    result
  }

  */
}
