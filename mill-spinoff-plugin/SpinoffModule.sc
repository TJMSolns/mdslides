import $file.build
import mill._

trait SpinoffModule extends mill.define.Module {
  def spinoffCandidatesPath: Task[os.Path]
  def contextMapPath: Task[os.Path]
  def targetOrg: Task[String]
  def githubToken: Task[String]
}
