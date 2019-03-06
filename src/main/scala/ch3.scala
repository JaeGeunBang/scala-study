/*
표현식.
: 기존 데이터를 변경하는 것 대신 새로운 값을 반환함. 이는 함수형 프로그래밍의 기반이 됨. (불변의 데이터)
*/

object ch3 {
  def main(args: Array[String]) {
    // 1. 표현식 블록 (한줄)
    val amount = { val x = 5 * 20; x + 10 }
    println(amount)
    // 표현식 블록 (여러 줄) - 세미콜론은 없어져도 됨.
    val amount2 = {
      val x = 5 * 20
      x + 10
    }
    println(amount2)
    // 2. if 표현식
    // if (<부울식>) <표현식>
    if( 47 % 3 > 0 )
      println("Not a multiple of 3")
    val result = if ( true ) "what does this return?"
    println(result)
    // 3.if~else 표현식
    // if (<부울식>) <표현식> else <표현식>
    val max = if ( 10 > 20 ) 10 else 20
    println(max)

    // 4. 매치 표현식 (자바에 switch)
    /*
    <표현식> match {
      case <패턴 매치> => <표현식>
        ...
    }
    */
    val x = 10
    val y = 20
    val max2 = (x > y) match {
      case true => x
      case false => y
    }
    println(max2)

    val status = 500
    val message = status match {
      case 200 =>
        "ok"
      case 400 => {
        println("ERROR - we called the service incorrectly")
        "error"
      }
      case 500 => {
        println("ERROR - the service encountered an error")
        "error"
      }
    }
    println(message)

    // 5. 와일드 카드
    // case _ => <하나 이상의 표현식>
    // 와일드 카드는 _ 로 표현한다. (모르는 값을 표현하고 싶을 때)
    val message2 = "Unauthorized"
    val status2 = message2 match {
      case "Ok" => 200
      case _ => {
        println("Couldn't parse $message2")
        -1
      }
    }
    println(status2)

    // 6. 루프
    // 숫자 범위 정하기 (반복자)
    //    <시작 정수 값> [to | until] <끝 정수값> [by increment]
    // 기본 for 루프로 반복하기
    //    for (<식별자> <- <반복자>) [yield] [<표현식>]
    for ( x <- 1 to 7)
      println("Day $x")

    // 중첩 반복자
    for ( x <- 1 to 7; y <- 1 to 8 )
      println("X and Y is $x , $y")

    // 7. while, do/while 루프
    // while(<부울식>) 표현식 (자바랑 똑같음)
    var x2 = 10
    while( x2 > 0)
      x2 -= 1
    println(x2)

    val x3 = 0;
    do
      println("x = $x")
    while(x3 > 0)

  }
}

