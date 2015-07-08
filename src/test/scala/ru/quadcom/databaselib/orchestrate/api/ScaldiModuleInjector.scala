package ru.quadcom.databaselib.orchestrate.api

import org.scalatest.{Suite, SuiteMixin}
import ru.quadcom.databaselib.orchestrate.scaldi.AppModule
import scaldi.Injectable

/**
 * Created by Dmitry on 7/6/2015.
 */
trait ScaldiModuleInjector extends SuiteMixin with Injectable{
  this: Suite =>

  implicit val injector = new AppModule

  abstract override def withFixture(test: NoArgTest) = {
    super.withFixture(test)
  }
}
