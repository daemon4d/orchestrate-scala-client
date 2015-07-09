orchestrate-scala-client
========================

Scala client library for [**Orchestrate.io**](http://orchestrate.io) service

The snapshot builds of library can be found at [**our company Nexus**](http://nexus.quadcom.biz:9081/nexus/content/repositories/snapshots) 

Current version is **1.0.0-SNAPSHOT**

Dependency for projects build with SBT 

    libraryDependencies ++= Seq(
                                  ...
                                  "ru.quadcom" & "orchestrate-client-scala" % "1.0.0-SNAPSHOT",
      )
      
    
    resolvers += "QuadCom snapshots repo" at "http://nexus.quadcom.biz:9081/nexus/content/repositories/snapshots"
    
    resolvers += "QuadCom releases repo" at "http://nexus.quadcom.biz:9081/nexus/content/repositories/releases"

