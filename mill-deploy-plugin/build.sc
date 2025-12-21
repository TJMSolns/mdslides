import mill._
import mill.scalalib._
import mill.scalalib.publish._

object millDeployPlugin extends ScalaModule with PublishModule {
  def scalaVersion = "2.13.12"
  
  def millVersion = "0.11.6"
  
  def ivyDeps = Agg(
    ivy"com.lihaoyi::mill-scalalib:${millVersion}",
    ivy"com.lihaoyi::os-lib:0.9.1",
    ivy"com.lihaoyi::upickle:3.1.3",
    ivy"io.circe::circe-yaml:0.14.2",
    ivy"io.circe::circe-parser:0.14.6",
    ivy"org.typelevel::cats-effect:3.5.2"
  )
  
  def publishVersion = "0.1.0-SNAPSHOT"
  
  def pomSettings = PomSettings(
    description = "Mill plugin for automated deployment validation and execution",
    organization = "io.github.retisio",
    url = "https://github.com/RETISIO/copilot-training",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("RETISIO", "copilot-training"),
    developers = Seq(
      Developer("retisio-platform", "RETISIO Platform Team", "https://github.com/RETISIO")
    )
  )
  
  object test extends ScalaTests {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.17",
      ivy"org.scalatestplus::scalacheck-1-17:3.2.17.0",
      ivy"org.scalacheck::scalacheck:1.17.0"
    )
    
    def testFramework = "org.scalatest.tools.Framework"
  }
}
