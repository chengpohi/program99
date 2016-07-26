package nlp

import nlp.AnalyzerMonad.AnalyzedDoc

import scala.collection.parallel.ParSeq
import scalaz.Free

/**
  * scala99
  * Created by chengpohi on 7/25/16.
  */
class Crawler {

  import AnalyzerMonad._

  def get(url: String): Free[Request, AnalyzerMonad.AnalyzedDoc] = for {
    webdoc <- fetch(GetDoc(url))
    result <- fetch(TokenizeWords(webdoc))
  } yield result

  def crawl(urls: List[Seed]): DocSet = DocSet(urls.par.map(s => crawl(s.url)))
  def crawl(s: String) = get(s).foldMap(AnalyzerInterpreter)
}

case class DocSet(docs: ParSeq[AnalyzedDoc]) {
  def head = docs.head
  def tail = docs.tail
  def find(word: String) = docs.find(d => d.all.exists(i => i.word == word))
  def count(word: String) = docs.count(d => d.all.exists(i => i.word == word))
  def size = docs.size
  def map[A](f: AnalyzedDoc => A) = docs.map(doc => f(doc))
}
case class Seed(url: String, timeout: Option[Int] = Some(60 * 1000))
