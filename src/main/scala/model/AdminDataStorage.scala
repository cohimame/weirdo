package model

import akka.actor.{ActorRef, ActorPath}

object AdminDataStorage {
  private var workerFileSystem = Map[String, List[String]]()

  def putWorkerFS(sender: ActorRef, fs: List[String]): Unit = {
    val senderAlias = getAlias(sender)
    workerFileSystem += (senderAlias -> fs)
  }

  def getWorkerFS(sender: ActorRef): Option[List[String]] = {
    val senderAlias = getAlias(sender)
    workerFileSystem.get(senderAlias)
  }

  private def getAlias(senderPath: ActorRef):String = senderPath.path.toString.split("//*")(1)

}
