package model

object FSStorage {
  // worker_full_path -> worker_filesystem
  var workerFileSystem = Map[String, List[String]]()

  // worker_full_path -> worker_file_crc_mapping
  var workerCRCMaps = Map[String, Map[String, Long]]()
}
