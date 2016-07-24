package securities.rp

import scala.collection.BitSet

/**
 * Created by kaifer on 2016. 6. 3..
 */

case class RPTree[+T](val value: T, val children: List[RPTree[T]] = Nil) { self =>
  def isLeaf = children.isEmpty

  def toBitSetTree[V]() = {
    var cnt = -1
    self transform(v => { cnt += 1; (v, BitSet(cnt)) },
      (v, cs: List[RPTree[(T, BitSet)]]) => (v, cs.foldLeft(BitSet())((b, a) => b | a.value._2)))
  }

  def transform[S](leaf: T => S, branch: (T, List[RPTree[S]]) => S): RPTree[S] = {
    val children = self.children.map(_.transform(leaf, branch))
    val value = if (self.isLeaf) leaf(self.value) else branch(self.value, children)
    RPTree[S](value, children)
  }

  def toMap[V](role: RPTree[(V, BitSet)]): Map[V, BitSet] = {
    val children = role.children.foldLeft(Map[V, BitSet]())((b, a) => b ++ toMap(a))
    val value = Map(role.value._1 -> role.value._2)
    value ++ children
  }
}
