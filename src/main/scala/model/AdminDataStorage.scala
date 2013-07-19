package model

import akka.actor.ActorPath

object AdminDataStorage {
  var workerFileSystem = Map[ActorPath, List[String]]()
}
