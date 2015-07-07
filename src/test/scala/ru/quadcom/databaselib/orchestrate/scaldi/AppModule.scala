package ru.quadcom.databaselib.orchestrate.scaldi

import akka.actor.ActorSystem
import ru.quadcom.databaselib.lib.orchestrate.impl.{OrchestrateKeyValueServiceImpl, OrchestrateClientImpl}
import ru.quadcom.databaselib.lib.orchestrate.traits.{OrchestrateKeyValueService, OrchestrateClient}
import scaldi.Module

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by Dmitry on 7/6/2015.
 */
class AppModule extends Module {
  binding identifiedBy 'url to "https://api.orchestrate.io/v0"
  binding identifiedBy 'key to "0680d860-5de6-44d1-94ba-a0e43f631e40"

  bind[ActorSystem] to ActorSystem("Test") destroyWith ((as: ActorSystem) => Await.result(as.terminate(), Duration.Inf))

  bind[OrchestrateClient] to new OrchestrateClientImpl(
    actorSystem = inject[ActorSystem],
    url = inject[String](identified by 'url),
    key = inject[String](identified by 'key)
  )

  bind[OrchestrateKeyValueService] to new OrchestrateKeyValueServiceImpl(
    actorSystem = inject[ActorSystem],
    client = inject[OrchestrateClient]
  )
}
