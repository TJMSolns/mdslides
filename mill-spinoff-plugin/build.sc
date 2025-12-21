import mill._
import mill.scalalib._
import mill.scalalib.publish._

object millSpinoffPlugin extends ScalaModule with PublishModule {
  def scalaVersion = "3.3.1"
  def millVersion = "0.11.6"

  def artifactName = "mill-spinoff"

  def publishVersion = "1.0.0"

  def pomSettings = PomSettings(
    description = "Mill plugin for spinning off bounded context services from a master training repository to production repositories",
    organization = "com.retisio",
    url = "https://github.com/RETISIO/mill-spinoff-plugin",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("RETISIO", "mill-spinoff-plugin"),
    developers = Seq(
      Developer("retisio", "RETISIO Engineering Team", "https://github.com/RETISIO")
    )
  )

  // NOTE: GitHub Packages publishing configured via mill publish command with --repository flag
  // Example: mill millSpinoffPlugin.publish --repository https://maven.pkg.github.com/RETISIO/mill-spinoff-plugin

  def ivyDeps = Agg(
    ivy"com.lihaoyi::os-lib:0.9.3",
    ivy"com.lihaoyi::ujson:3.1.4",
    ivy"com.lihaoyi::requests:0.8.0"
  )

  object test extends ScalaTests {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.18"
    )
    def testFramework = "org.scalatest.tools.Framework"
  }
}
