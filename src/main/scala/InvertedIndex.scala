import java.util.concurrent.{BlockingQueue, ConcurrentHashMap}

import scala.collection.{concurrent, mutable}
import scala.collection.JavaConverters._
import scala.io.Source

/**
 * scala99
 * Created by chengpohi on 11/10/15.
 */
case class User(name: String, id: Int)

class InvertedIndex(val userMap: mutable.Map[String, User]) {
  def this() = this(new mutable.HashMap[String, User]())

  def tokenizeName(user: User): Seq[String] = user.name.split(" ").map(s => s.toLowerCase)

  def add(term: String, user: User): Unit = {
    userMap += term -> user
  }

  def add(user: User): Unit = {
    tokenizeName(user).foreach(t =>
      userMap.synchronized {
        add(t, user)
      }
    )
  }
}

class ConcurrentInvertedIndex(override val userMap: concurrent.Map[String, User]) extends InvertedIndex(userMap) {
  def this() = this(new ConcurrentHashMap[String, User] asScala)
}

class Producer(path: String, queue: BlockingQueue[String]) extends Runnable {
  override def run(): Unit = {
    Source.fromFile(path).getLines().foreach(l =>
      queue.put(l)
    )
  }
}

class Consumer[T](queue: BlockingQueue[T]) extends Runnable {

  def consume(item: T): Unit = ???

  override def run(): Unit = {
    while (true) {
      val item = queue.take()
      consume(item)
    }
  }
}

trait UserMaker {
  def makeUser(item: String): User = {
    item.split(",") match {
      case Array(name, id) => User(name, id.trim.toInt)
    }
  }
}

class IndexConsumer(invertedIndex: InvertedIndex, queue: BlockingQueue[String]) extends Consumer[String](queue) with UserMaker {
  override def consume(item: String): Unit = invertedIndex.add(makeUser(item))
}

object App {
  def main(args: Array[String]) {
    val index = new ConcurrentInvertedIndex()
  }
}