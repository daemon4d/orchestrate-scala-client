package ru.quadcom.databaselib.orchestrate.scaldi

import akka.actor.ActorSystem
import ru.quadcom.databaselib.lib.orchestrate.impl.{OrchestrateSearchServiceImpl, OrchestrateKeyValueServiceImpl, OrchestrateClientImpl}
import ru.quadcom.databaselib.lib.orchestrate.traits.{OrchestrateSearchService, OrchestrateKeyValueService}
import scaldi.Module

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by Dmitry on 7/6/2015.
 */
class AppModule extends Module {
  binding identifiedBy 'orchestrateApiURL to "https://api.orchestrate.io/v0"
  binding identifiedBy 'applicationKey to "0680d860-5de6-44d1-94ba-a0e43f631e40"

  bind[ActorSystem] to ActorSystem("Test") destroyWith ((as: ActorSystem) => Await.result(as.terminate(), Duration.Inf)) identifiedBy 'orchestrateClientAS

  bind[OrchestrateClientImpl] to new OrchestrateClientImpl

  bind[OrchestrateKeyValueService] to new OrchestrateKeyValueServiceImpl

  bind[OrchestrateSearchService] to new OrchestrateSearchServiceImpl
}
