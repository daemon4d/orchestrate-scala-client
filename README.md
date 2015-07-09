orchestrate-scala-client
========================

Scala client library for [**Orchestrate.io**](http://orchestrate.io) service

The snapshot builds of library can be found at [**our company Nexus**](http://nexus.quadcom.biz:9081/nexus/content/repositories/snapshots) 

Current version can be found at library [**pom**](https://github.com/daemon4d/orchestrate-scala-client/blob/master/pom.xml)

Dependency for projects build with SBT 

    libraryDependencies ++= Seq(
                                  ...
                                  "ru.quadcom" & "orchestrate-client-scala" % "1.0.0-SNAPSHOT",
      )
      
    
    resolvers += "QuadCom snapshots repo" at "http://nexus.quadcom.biz:9081/nexus/content/repositories/snapshots"
    
    resolvers += "QuadCom releases repo" at "http://nexus.quadcom.biz:9081/nexus/content/repositories/releases"
    
The library is using [**Scaldi**](http://scaldi.org/) framework for dependency injection. Different parts of Orchestrate.io API provided as different Scala traits.  

In order to use library services one should define Scaldi module similar to module used in tests.

        class AppModule extends Module {
        binding identifiedBy 'orchestrateApiURL to "https://api.orchestrate.io/v0"
        binding identifiedBy 'applicationKey to "provide-orchestrate-app-key-here"
        
        bind[ActorSystem] to ActorSystem("OrchestrateClient") destroyWith ((as: ActorSystem) => Await.result(as.terminate(), Duration.Inf)) identifiedBy 'orchestrateClientAS
        
        bind[OrchestrateClientImpl] to new OrchestrateClientImpl
        
        bind[OrchestrateKeyValueService] to new OrchestrateKeyValueServiceImpl
        
        bind[OrchestrateSearchService] to new OrchestrateSearchServiceImpl
        }



