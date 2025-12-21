import mill._
import mill.scalalib._
import mill.scalalib.publish._

object millBootstrapPlugin extends ScalaModule with PublishModule {
  def scalaVersion = "3.3.1"
  def millVersion = "0.11.6"

  override def ivyDeps = Agg(
    ivy"com.lihaoyi::os-lib:0.9.1",
    ivy"com.lihaoyi::requests:0.8.0",
    ivy"com.lihaoyi::upickle:3.1.3"
  )

  override def scalacOptions = Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-deprecation",
    "-unchecked"
  )

  // Publishing configuration
  def publishVersion = "1.0.0"

  def pomSettings = PomSettings(
    description = "Mill plugin for bootstrapping new projects with ceremony-based SDLC framework",
    organization = "com.retisio",
    url = "https://github.com/RETISIO/copilot-training",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("RETISIO", "copilot-training"),
    developers = Seq(
      Developer("retisio", "RETISIO Engineering Team", "https://github.com/RETISIO")
    )
  )

  def artifactName = "mill-bootstrap"

  // Test configuration
  object test extends ScalaTests with TestModule.Munit {
    override def ivyDeps = Agg(
      ivy"org.scalameta::munit:0.7.29",
      ivy"org.scalameta::munit-scalacheck:0.7.29"
    )
  }
}
