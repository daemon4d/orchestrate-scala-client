package ru.quadcom.databaselib.orchestrate.api

import ru.quadcom.databaselib.lib.orchestrate.traits.OrchestrateKeyValueService

/**
 * Created by Dmitry on 7/6/2015.
 */
class InjectionsTests extends OrchestrateAPISpec {

  "OrchestrateKeyValueService" should " be initialized " in {
    val keyValueServ = inject[OrchestrateKeyValueService]
    assert(keyValueServ != null)
  }
}
