package sz.concurrent

import scalaz._
import effect._
import IO._
import concurrent._
import MVar._
import std.anyVal._
import syntax.equal._
import Scalaz._

object MVarUsage extends App {

  def forkIO(f: IO[Unit])(implicit s: Strategy): IO[Unit] = IO {
    s(f.unsafePerformIO)
    ()
  }

  def out(): Unit = {
    def calc(mvar: MVar[Int]): IO[Unit] = mvar.put(42)

    val io = for {
      mvar <- newEmptyMVar[Int]
      _ <- forkIO(calc(mvar))
      a <- mvar.take
    } yield a

    println(io.unsafePerformIO())
  }

  def inout(): Unit = {
    def calc(in: MVar[Int], out: MVar[Int]): IO[Unit] =
      for {
        a <- in.take
        b <- in.take // take will block until there is extra element put in
        _ <- out.put(a * b)
      } yield ()

    val io =
      for {
        in <- newMVar(6)
        out <- newEmptyMVar[Int]
        _ <- forkIO(calc(in, out))
        _ <- in.put(7) // it means we can do something here
        a <- out.take
      } yield a
    println(io.unsafePerformIO)
  }

  def pingpong(): Unit = {
    def pong(c: MVar[String], p: MVar[String]) =
      for {
        _ <- c.take flatMap (s => putStrLn("c: " + s))
        _ <- p.put("pong")
        _ <- c.take flatMap (s => putStrLn("c: " + s))
        _ <- p.put("pong")
      } yield ()

    def io =
      for {
        c <- newMVar("ping")
        p <- newEmptyMVar[String]
        _ <- forkIO(pong(c, p)) //Asynchronous to inject queue
        _ <- p.take flatMap (s => putStrLn("p: " + s)) // block to wait queue
        _ <- c.put("ping")
        _ <- p.take flatMap (s => putStrLn("p: " + s))
      } yield ()

    io.unsafePerformIO
  }

  out()
  inout()
  pingpong()

}
