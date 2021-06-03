
/*
케이스 클래스, 객체, 패키지, apply, update, 함수는 객체이다, 패턴 매치
*/

object ch2 {
  def main(args: Array[String]): Unit = {
    // apply 메서드를 통해 클래스나 객체의 용도가 주로 하나아 있는 경우를 표현할수 있음
    class Foo {}
    object FooMaker{
      def apply() = new Foo
    }

    class Bar {
      def apply() = 0
    }
    val bar = new Bar
    println(bar())

    // 객체 (object)는 클래스의 유일한 인스턴스를 넣기 위해 사용.
    /// new 없이도 생성할수 있다.
    object Timer {
      var count = 0

      def currentCount(): Long = {
        count += 1
        count
      }
    }
    println(Timer.currentCount())

    // 짝 객체. 보통 팩토리를 만들때 사용한다.
    class Bar2(foo:String)
    object Bar2 {
      def apply(foo: String) = new Bar2(foo)
    }

    // 함수는 객체이다.
    /// Function은 1~22까지 있음. 22인 이유는 22가 넘는게 잘 없어
    object addOne extends Function1[Int, Int] {
      def apply(m: Int): Int = m + 1
    }
    object addOne2 extends (Int => Int) {
      def apply(m: Int): Int = m + 1
    }

    // 패턴 매칭
    val times = 1
    times match {
      case 1 => "one"
      case 2 => "two"
      case _ => "some other number"
    }
    times match {
      case i if i == 1 => "one"
      case i if i == 2 => "two"
      case _ => "some other number"
    }
    // 타입 매칭
    def bigger(o: Any): Any = {
      o match {
        case i: Int if i < 0 => i - 1
        case i: Int => i + 1
        case d: Double if d < 0.0 => d - 0.1
        case d: Double => d + 0.1
        case text: String => text + "s"
      }
    }

    // case class
    /// 내용을 어떤 클래스에 저장하고,그에 따라 매치하고 싶은 경우 사용한다.
    /// new를 사용하지 않고도 케이스 클래스의 인스턴스 생성이 가능
    case class Calculator(brand: String, model: String)
    val hp20b = Calculator("HP", "20b")
    val hp20B = Calculator("HP", "20b")
    println(hp20b == hp20B)

    def calcType(calc: Calculator) = calc match {
      case Calculator("HP", "20B") => "financial"
      case Calculator("HP", "48G") => "scientific"
      case Calculator("HP", "30B") => "business"
      case Calculator(ourBrand, ourModel) => "Calculator: %s %s is of unknown type".format(ourBrand, ourModel)
    }

    // 예외
    try {
      remoteCalculatorService.add(1, 2)
    } catch {
      case e: ServerIsDownException => log.error(e, "the remote calculator service is unavailble. should have kept your trustry HP.")
    } finally {
      remoteCalculatorService.close()
    }

    val result: Int = try {
      remoteCalculatorService.add(1, 2)
    } catch {
      case e: ServerIsDownException => {
        log.error(e, "the remote calculator service is unavailble. should have kept your trustry HP.")
        0
      }
    } finally {
      remoteCalculatorService.close()
    }
  }
}

