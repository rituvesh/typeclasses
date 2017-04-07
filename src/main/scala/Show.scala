package show

import shapeless.{ HNil, HList, :: }
import shapeless._
import shapeless.labelled.{ field, FieldType }
import shapeless.Generic
import shapeless.LabelledGeneric
import shapeless.{Coproduct, :+:, CNil, Inl, Inr}
import shapeless.Witness
import scala.reflect.runtime.universe.TypeTag

trait Show[A] {
  def show(a: A): String
}

object Show {

  def apply[A](implicit sh: Show[A]): Show[A] = sh

  object ops {
    def show[A: Show](a: A) = Show[A].show(a)

    implicit class ShowOps[A: Show](a: A) {
      def show = Show[A].show(a)
    }
  }

  implicit val intCanShow: Show[Int] =
    int => s"int $int"

  implicit val stringCanShow: Show[String] =
    str => s"string $str"

  implicit val hnilCanShow: Show[HNil] =
    hnil => ""

  implicit def hlistCanShow[K <: Symbol, H, T <: HList](
    implicit
    witness: Witness.Aux[K],
    headCanShow: Show[H],
    tailCanShow: Show[T]
  ): Show[FieldType[K, H] :: T] =
    { case h :: t => {
      val name = witness.value.name
      s"${name}: ${headCanShow.show(h)}, ${tailCanShow.show(t)}"
    } }

  implicit def genericCanShow[A, H <: HList](
    implicit
    gen: LabelledGeneric.Aux[A, H],
    canShow: Lazy[Show[H]],
    tag: TypeTag[A]
  ): Show[A] =
    a => {
      s"${tag.tpe} :: ${canShow.value.show(gen.to(a))}"
    }

}
