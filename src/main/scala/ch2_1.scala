/*
리터럴: 숫자 5, 문자 A, 텍스트 Hello World 처럼 소스 코드에 바로 등장하는 데이터.
값: 불변의 타입을 갖는 저장 단위.
변수: 가변의 타입을 갖는 저장 단위.
타입: 데이터의 종류.
*/

object ch2_1 {
  def main(args: Array[String]) {
    // 1. 값 - val <이름>: <타입> = <리터럴>
    val x: Int = 5

    val a: Double = 2.72
    // 값은 불변이라 수정이 불가능
    // a = 27

    // 2. 변수 - var <이름>: <타입> = <리터럴>
    var a2: Double = 2.72
    // 변수는 가변이기 때문에 수정이 가능함.
    a2 = 3.33

    // 3. 타입은 참고로 굳이 명시적으로 선언하지 않아도 된다.
    val x2 = 20 // Int
    val greeting = "Hello world" // String
    val atSymbol = '@' // Char

    // 4. 타입의 종류 (숫자)
    // Byte, Short, Int, Long, Float, Double
    // 높은 순위의 데이터 타입(Long)은 낮은 순위의 타입(Int)으로 자동전환 되지 않는다. (toInt 같은 메서드를 사용해야한다.)
    val l : Long = 20
    val i : Int = l.toInt

    // 5. 타입의 종류 (문자)
    // 문자는 숫자와 마찬가지로 등호 연산자(==)와 같은 기능을 제공한다.
    val matched = (greeting == "Hello, world")

    // 정규 표현식을 통해 문자를 matches 하거나 replaceAll, replaceFirst 를 할 수 있다.
    // val <정규 표현식 값> (<식별자>) = <입력 문자열>
  }
}

