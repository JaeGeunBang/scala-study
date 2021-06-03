
/*
패턴 매치와 함수 합성
*/

object ch4 {
  def main(args: Array[String]): Unit = {
    // 함수 합성
    def addAhem(x: String) = x + " ahem"

    def addUmm(x: String) = x + " umm"

    /// compose: f compose g를 하면 두 함수를 f(g(x))와 같이 합성함
    val ummThenAhem = addAhem _ compose addUmm _
    println(ummThenAhem("well"))

    /// andThen: compose와 비슷하지만, 순서가 다름 g(f(x))
    val ahemThenUmm = addAhem _ andThen addUmm _
    println(ahemThenUmm("well"))

    // 부분 함수
    val one: PartialFunction[Int, String] = {
      case 1 => "one"
    }

    one.isDefinedAt(1)
    one.isDefinedAt(2)
    one(1)

    val two: PartialFunction[Int, String] = {
      case 2 => "two"
    }
    val three: PartialFunction[Int, String] = {
      case 3 => "three"
    }
    val wildcard: PartialFunction[Int, String] = {
      case _ => "something else"
    }
    val partial = one orElse two orElse three orElse wildcard
    /// 위에서 정의한 부분 함수들을 합성할 수 있다

    println(partial(5))
    println(partial(2))

    // 케이스문의 신비
    case class PhoneExt(name: String, ext: Int)
    val extensions = List(PhoneExt("steve", 100), PhoneExt("rebey", 200))
    extension.filter(case PhoneExt(name, extension) => extension < 200)
  }
}

