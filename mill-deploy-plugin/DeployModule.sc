import $file.build
import mill._

trait DeployModule extends mill.define.Module {
  def dockerRegistry: Task[String]
}
