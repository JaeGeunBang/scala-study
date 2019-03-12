/*
일급함수
- 함수형 프로그래밍의 핵심 가치는 일급 객체여야 하며, 함수가 선언되고 호출되는 것 외에 다른 데이터 타입처럼 언어의 모든 부분에 사용될 수 있다.
- 고차 함수
-- 다른 함수를 매개변수로 받거나, 반환값으로 함수를 사용하는 함수.
-- 대표적으로 map(), reduce()가 있다.
*/

object ch5 {
  def main(args: Array[String]) {
    // 1. 함수 타입의 값
    def double(x: Int) Int = x * 2
    // 함수 타입을 선언할 수 있다. 
    // (Int) => Int 
    // - Int형으로 input을 받아 Int형으로 output을 낸다. output을 위해 double() 함수를 사용한다.
    val myDouble: (Int) => Int = double
    myDouble(5)

    // 와일드 카드 연산자로 함수 할당하기
    def double(x: Int): Int = x * 2
    val myDouble = double _
    val amount = myDouble(20)

    def max(a: Int, b: Int) = if ( a > b ) a else b
    // - Int형으로 2개의 input을 받아 Int형으로 output을 낸다.
    val maximize: (Int, Int) => Int = max
    maximize(50, 30)

    // 2. 고차함수
    def saftStringOp(s:String, f:String => String) = {
      if (s != null) f(s)
      else s
    }
    def reverser(s:String) = s.reverse
    saftStringOp(null, reverser) // null 값 출력
    saftStringOp("ready", reverser) // ydare 값 출력

    // 3. 함수 리터널 (익명함수)
    // val로 선언한다. def X
    // 이름이 없는 함수.
    val doubler = (x: Int) => x * 2
    val doubled = doubler(22)
    //
    val greeter = (name: String) => "Hello $name"
    val hi = greeter("World")
    //
    def max(a: Int, b: Int) = if( a > b ) a else b // 원본 함수
    val maximize: (Int, Int) => Int = max // 함수값에 할당. 원본 함수를 할당
    val maximize = (a:Int, b:Int) => if(a > b) a else b // 함수 리터널로 재정의
    maximize(84, 96)
    //
    def logStart() = "=" * 50 + "\nStarting Now\n" + "=" + * 50 // 일반 함수
    val start = () => "=" * 50 + "\nStarting Now\n" + "=" + * 50 // 함수 리터널
    println( start() )
    // 위 고차함수와 동일한 로직.
    def safeStringOp(s: String, f: String => String) = {
      if (s != null) f(s) else s
    }
    safeStringOp(null, s => s.reverse)
    safeStringOp("ready", s => s.reverse)

    // 4. 자리표시자 구문
    def safeStringOp(s: String, f:String => String) = {
      if( s!= null) f(s) else s
    }
    // reverse 함수에 파라미터 값을 _로 표시한다.
    safeStringOp(null, _.reverse)
    safeStringOp("Ready", _.reverse)
    //
    def combination(x: Int, y: Int, f: (Int, Int) => Int) = f(x,y)
    combination(23, 12, _ * _) // 두 파라미터를 곱해주는 함수를 넘겨주라는 의미.
    //
    def tripleOp(a: Int, b: Int, c: Int, f: (Int, Int, Int) => Int) = f( a, b, c)
    tripleOp(23, 92, 14, _ * _ + _)
    // Input, output 타입을 지정할 수 있음.
    def tripleOp[A,B](a:A, b:A, c:A, f: (A,A,A) => B) = f(a,b,c)
    tripleOp[Int, Int](23, 92, 14, _ * _ + _)
    tripleOp[Int, Double](23, 92, 14, 1.0 * _ / _ / _)
    tripleOp[Int, Boolean](23, 92, 14, _ > _ + _)

    // 5. 부분 적용 함수와 커링
    // factorOf 함수의 파라미터를 다시 타이핑하고 싶지 않을 때 와일드카드(_)를 쓴다.
    def factorOf(x: Int, y: Int) = y % x == 0
    val f = factorOf _
    val x = f(7, 20)
    // 또한, factorOf 파라미터의 일부만 부분 함수로 적용하고 싶다면 아래와 같다.
    val multipleOf3 = factorOf(3, _:Int)
    val y = multipleOf3(78) // 3은 이미 고정되어 있음.
    
    def factorOf(x: Int)(y: Int) = y % x == 0
    val isEven = factorOf(2) _ //위 부분함수 적용을 이와 같이 표현할 수 있음.
    val z = isEven(32) // 2는 이미 고정되어 있음
    
    // 6. 이름에 의한 호출 매개변수
    def doubles(x: => Int) = {
      println("Now doubling " + x)
      x * 2
    }

    // 7. 부분 함수
    val statusHandler: Int => String = {
      case 200 => "Okay"
      case 400 => "Your Error"
      case 500 => "Our error"
    }

    // 8. 함수 리터널 블록으로 고차 함수 호출하기
    def safeStringOp(s: String, f:String => String) {
      if (s != null) f(s) else s
    }
    val uuid = java.util.UUID.randomUUID.toString
    val timedUUID = safeStringOp(uuid, { s =>
      val now = System.currentTimeMillis
      val timed = s.take(24) + now
      timed.toUpperCase
    })
    // 다른 표현
    val timedUUID = safeStringOp(uuid) { s => 
      val now = System.currentTimeMillis
      val timed = s.take(24) + now
      timed.toUpperCase
    }
  }
}

