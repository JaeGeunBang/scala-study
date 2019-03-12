/*
그 외의 컬렉션
*/

object ch7 {
  def main(args: Array[String]) {
    // 1. 가변적인 컬렉션
    // 기본적으로 map, list, set은 불변의 컬렉션이므로 만들어진 후 바뀔 수 없다.
    val nums = collection.multable.Buffer(1) --> // Buffer는 collection.immutable.List와 대응됨.
    
    // 불변의 컬렉션 -> 가변적 컬렉션 생성
    val m = Map("APPL" -> 597, "MSFT" -> 40)
    val b = m.toBuffer
    b trimStart 1
    b += ("GOOD" -> 521)
    val n = b.toMap
    // 컬렉션 빌더 
    // buffer를 단순화한 형태이며, 추가 연산만 제공해주고 있음. (즉, 추가연산만 할 꺼면 Buffer 말고 builder를 사용할 것. buffer가 너무 광범위하게 쓰이기 때문에)
    val b = Set.newBuilder[Char]
    b += 'h'
    b ++= List('e', 'l', 'l', 'o')
    val helloSet = b.result
    
    // 2.배열
    // Array는 고정된 크기를 가지며, 내용 변경이 가능하며, 인덱스를 가지는 컬렉션.
    val colors = Array("red", "green", "blue")
    colors(0) = "purple"
    val files = new java.io.File(".").listFiles
    val scala = files map (_.getName) filter(_ endsWith "scala")

    // Seq와 시퀀스
    // Seq는 모든 시퀀스의 루트 타입 (list, Array의 루트 타입)
    val link = Seq('C', 'M', 'Y', 'K')

    // Try 컬렉션
    def loopAndFail(end: Int, failAt: Int): Int = {
      for ( i <- 1 to end) {
        println("$1")
        if( i == failAt) throw new Exception("Too many iterations")
      }
      end
    }
    val t1 = util.Try( loopAndFail(2,3)) // 성공 메시지를 가짐
    val t2 = util.Try( loopAndFail(4,2)) // 실패 메시지를 가짐
    // 아래 예제를 통해 성공, 실패 예제를 볼 수 있음.
    def nextError = util.Try { 1 / util.Random.nextInt(2) }
    // 랜덤 값이 0이 나오면 실패할 것.
    val x = nextError
    val y = nextError
    // Try를 이용한 에러 처리 메서드
    val input = " 234 "
    val result = util.Try(input.toInt) orElse util.Try(input.trim.toInt) // 성공 메시지를 가짐
    // 먼저 input.toInt를 수행해보고 실패했다면, 뒤에 input.trim.toInt를 시도하며, 성공했기 때문에 성공 메시지를 가짐 (flatMap의 반대 메서드)
    val x = result match {
      case util.Success(x) => Some(x)
      case util.Failure(x) => {
        println("not parsed")
        None
      }
    }
    // 만약 input 값에 "abc" 와 같은 일반적인 문자값이 들어가있었다면, 실패 메시지를 가질 것.
  }
}

