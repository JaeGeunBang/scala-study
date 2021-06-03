
/*
컬렉션 (리스트, 집합, 튜플, 맵), Option
함수 콤비네이터 (map, foreach, filter, zip... 등)
*/

object ch3 {
  def main(args: Array[String]): Unit = {
    val numbers = List(1, 2, 3, 4) // 리스트
    Set (1, 1, 2) // 집합
    val hostPort = ("localhost", 80) // 튜플: 클래스 없이 아이템을 묶을수 있음.
    hostPort._1
    hostPort._2

    hostPort match {
      case ("localhost", port) => ...
      case (host, port) => ...
    }
    Map(1 -> 2) // 맵: 기본 데이터 타입 외에, 다른 맵이나 함수를 값으로 보관할수 있다.
    Map("foo" -> "bar")
    Map(1 -> Map("foo" -> "bar"))
    Map("timesTwo" -> { timesTwo(_) })

    // 옵션
    /// 객체가 존재하거나 존재하지 않을 수 있을때 사용
    /// 옵션에 기본 인터페이스는 아래와 같다.
    trait Option[T] { def isDefined: Boolean def get: T def getOrElse(t: T): T }
    val numbers = Map("one" -> 1, "two" -> 2)
    println(numbers.get("two")) // Map.get은 Option을 반환함. 값이 있을수도 있고 없을수도 있고~
    println(numbers.get("three"))

    /// getOrElse() 와 같은 함수로 쉽게 기본값을 지정할 수 있다.
    val result = res1.getOrElse(0) * 2
    val result = res1 match { case Some(n) => n * 2 case None => 0 }

    // map
    numbers.map((i: Int) => i * 2)

    def timesTwo(i: Int): Int = i * 2
    numbers.map(timesTwo _)

    // foreach
    numbers.foreach((i: Int) => i * 2)
    val doubled = numbers.foreach((i: Int) => i * 2)

    // filter
    numbers.filter((i: Int) => i % 2 == 0)

    def isEven(i: Int): Boolean = i % 2 == 0
    numbers.filter(isEven _)

    // zip: 쌍(튜플)로 이루어진 단일 리스트 반환
    List(1, 2, 3).zip(List("a", "b", "c"))

    // flatten: 내포 단계를 줄여 내포된 리스트의 원소를 상위 리스트로 옮김. 즉 하나의 list로 만듬
    List(List(1, 2), List(3, 4)).flatten

    // partition: 함수 반환 값에 따라 list를 둘로 나눔
    val numbers = List(1,2,3,4,5,6,7,8,9,10)
    numbers.partition(_ % 2 == 0)

    // find: 조건을 만족하는 가장 첫 원소를 반환
    numbers.find((i: Int) => i > 5)

    // drop, dropWhile
    numbers.drop(5)
    numbers.dropWhile(_ % 2 != 0)

    // foldLeft
    /// 0은 시작값, m은 값을 누적
    numbers.foldLeft(0)((m: Int, n: Int) => m + n)
    numbers.foldLeft(0) { (m: Int, n: Int) => println("m: " + m + " n: " + n); m + n }

    // foldRight
    /// foldLeft의 동작이 반대
    scala> numbers.foldRight(0) { (m: Int, n: Int) => println("m: " + m + " n: " + n); m + n }

    // flatMap
    /// map과 flatten을 합성한것
    val nestedNumbers = List(List(1, 2), List(3, 4))
    nestedNumbers.flatMap(x => x.map(_ * 2)) // return: List(2,4,6,8)
    nestedNumbers.map((x: List[Int]) => x.map(_ * 2)).flatten // 위와 같은 표현

  }
}

