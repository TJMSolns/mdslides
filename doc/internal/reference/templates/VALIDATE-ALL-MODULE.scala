// Template: ValidateAllModule
// Purpose: Compose plugin validation commands into a single Mill command that runs in parallel.
// Usage: Copy into your service build, adjust package/object names, and mix into the root module.

import mill._
import mill.define.Command
import com.retisio.mill.{DomainModule, SpecificationModule, TestingModule, QualityModule, ObservabilityModule}

trait ValidateAllModule
    extends DomainModule
    with SpecificationModule
    with TestingModule
    with QualityModule
    with ObservabilityModule:

  /**
   * Run all validations in parallel (Mill automatically parallelizes T.task dependencies).
   * Adjust the list if some plugins are not in use for a given service.
   */
  def validateAll(): Command[Unit] = T.command:
    println("🧪 Running all validations in parallel...")

    // Evaluate commands; underlying tasks execute in parallel up to --jobs
    val commands = Seq(
      T.task { domainAll()() },
      T.task { specificationAll()() },
      T.task { testAll()() },
      T.task { qualityValidate()() },
      T.task { observabilityValidate()() }
    )

    commands.foreach(_.evaluate())
    println("\n✅ All validations completed (validateAll)")
