
/*
스칼라 사용 이뮤
- 1급 함수
- 클로저

간결함
- 타입 추론
- 함수 리터널


해당 챕터는 식, 값, 함수, 클래스, 상속, 트레잇, 타입에 대해 배운다.
*/

object ch1 {
  def main(args: Array[String]): Unit = {
    // 간단하게 함수를 만들수 있음.
    def addOne(m: Int): Int = m + 1
    val three = addOne(2)
    println(three)

    def three2() = 1 + 2
    println(three2())

    def timesTwo(i: Int): Int = {
      i * 2
    }

    // 이름 없는 함수
    val addOne2 = (x: Int) => x + 1
    println(addOne2(1))

    // 인자의 일부만 사용해 호출
    def adder(m: Int, n: Int) = m + n
    val add2 = adder(2, _:Int) // 2는 이미 고정 시키고, 3만 받도록 add2 함수를 다시 만듦
    println(add2(3))

    // 커리 함수
    /// 함수의 인자중 일부만 적용하고, 나머지 인자는 나중에 적용할수 있음
    def multiply(m: Int)(n: Int): Int = m * n
    println(multiply(2)(3))
    val timesTwo2 = multiply(2) _
    println(timesTwo2(3))

    // 가변 길이 인자
    def capitalizeAll(args: String*): Unit = {
      args.map { args =>
        args.capitalize
      }
    }
    capitalizeAll("rarity", "applejack")

    // 클래스
    class Calculator {
      val brand: String = "HP"
      def add(m: Int, n: Int): Int = m + n
    }
    val calc = new Calculator
    println(calc.add(1,2))
    print(calc.brand)

    // 클래스 - 생성자
    /// 입력받은 brand를 통해 color 값이 정해진다. (java, python 처럼 생성자가 존재하지 않는다.)
    class Calculator(brand: String) {
      val color: String = if (brand == "TI") {
        "blue"
      } else if (brand = "HP") {
        "black"
      } else {
        "white"
      }

      def add(m: Int, n: Int): Int = m + n
    }

    // 상속
    class ScientificCalculator(brand: String) extends Calculator(brand) {
      def log(m: Double, base:Double) = math.log(m) / math.log(base)
    }
    /// 상속 오버라이딩
    class EvenMoreScientificCalculator(brand: String) extends ScientificCalculator(brand) {
      def log(m: Int): Double = log(m, math.exp(1))
    }

    // 추상 클래스
    abstract class Shape {
      def getArea(): Int
    }

    class Circle(r: Int) extends Shape {
      def getArea(): Int = {r * r * 3}
    }

    // Trait (자바의 인터페이스)
    /// 추상 클래스와의 중요한 차이점은?
    /// 트레잇은 여러개를 사용할수 있다. 상속은 오로지 하나만 가능. 되도록이면 트레잇을 써라.
    /// 생성자 매개변수가 필요한 경우엔 추상 클래스를 사용하라. 생성자는 매개변수를 받지만, 트레잇은 매개변수를 못받
    trait Car {
      val brand: String
    }
    trait Shiny {
      val shineRefraction: Int
    }
    class BMW extends Car with Shiny{
      val brand = "BMW"
      val shineRefraction = 12
    }

    // 타입
    trait Cache[K, V] {
      def get(key: K): V
      def put(key: K, value: V)
      def delete(key: K)
    }
  }
}

