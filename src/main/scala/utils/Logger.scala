package checker

import java.io.BufferedWriter
import java.io.FileWriter
import java.sql.Timestamp

/**
 * Class was made to provide each checksum process
 * with a log file
 *
 * @author Silvestrov
 */
class Logger {

  private val CreationTime = new Timestamp(System.currentTimeMillis()).toString
  private val logName = CreationTime.replaceAll(":", ".") + ".txt"
  private val logWriter = new BufferedWriter(new FileWriter(logName))

  logWriter.write("check started at " +  CreationTime+"\n")

  def fileNotChanged: String => Unit =
    file => write("file " + file + " is not changed\n")

  def fileChangedSince: (String, Long) => Unit =
    (file,lastCheck) => write(
      "file " + file + " changed since last check(" +
        new Timestamp(lastCheck).toString +")\n")

  def fileDisappearedSince: (String, Long) => Unit =
    (file,lastCheck) => write(
      "file " + file + "was not found since last succesfull check("
        + new Timestamp(lastCheck).toString + ")\n")

  /**
   * Writes passed message in the current log file
   *
   * @param message to write in a log
   */
  def write(message: String) {
    logWriter.write(message)
  }

  /**
   * closes log file
   */
  def close() {
    logWriter.write("check finished at " +  new Timestamp(System.currentTimeMillis()).toString)
    logWriter.flush()
    logWriter.close()
  }

}
