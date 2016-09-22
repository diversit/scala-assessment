package com.philips.assessment

import com.philips.assessment.business.BusinessLauncher


object Launcher {

  def main(args: Array[String]): Unit = {

    if (!args.isEmpty) {
      try {
        val List(port) = args.toList
        runBusiness(port)
      }
      catch {
        case e: Throwable =>
          e.printStackTrace()
          printHelp
      }
    } else {
      runBusiness("2551")
      runBusiness("2552")
    }
  }

  def runBusiness(port: String) = BusinessLauncher.run(port.toInt)

  def printHelp = println(
    """
      | - endpoint [serverPort,actorPort]
      | - business [actorPort]
    """.stripMargin
  )

}
