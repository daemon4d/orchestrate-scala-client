package ru.quadcom.databaselib.orchestrate.testCases

import org.scalatest.FlatSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by Dmitry on 7/6/2015.
 */
class StackSpec extends FlatSpec {

  trait Builder {
    val builder = new StringBuilder("ScalaTest is ")
  }

  trait Buffer {
    val buffer = ListBuffer("ScalaTest", "is")
  }

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new mutable.Stack[Int]
    stack.push(1)
    stack.push(2)
    assert(stack.pop() === 2)
    assert(stack.pop() === 1)
  }

  it must "be two" in {
    val a = 5
    val b = 2
    assertResult(3) {
      a - b
    }
  }

  "Testing" should "be productive" in new Builder {
    builder.append("productive!")
    assert(builder.toString === "ScalaTest is productive!")
  }
}
