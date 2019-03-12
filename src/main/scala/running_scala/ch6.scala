/*
보편적인 컬렉션
*/

object ch6 {
  def main(args: Array[String]) {
    // 1. 리스트, 집합, 맵
    // 리스트
    val numbers = List(32,95,24,21,17)
    val colors = List("red", "green", "blue")

    colors.head //red
    colors.tail //blue
    colors(1) //green
    // 집합
    val unique = Set(10,20,30,20,20,10)
    val sum = unique.reduce((a:Int, b:Int) => a + b)
    // 맵
    val colorMap = Map("red" -> 0xFF0000,"green" -> 0xFF00, "blue" -> 0xFF)
    val redRGB = colorMap("red")
    val hasWhite = colorMap.contains("white"
    
    // 2. 생성 연산자
    val numbers = 1 :: 2 :: 3 :: Nil
    // List (1,2,3) 생성. Nil은 List[Nothing]의 싱글턴 인스턴스로, List의 끝은 Nil로 표현함으로써 리스트의 마지막을 알 수 있다.

    // 리스트의 산술 연산자
    /*
    1 :: 2:: Nil | 리스트에 개별 요소를 덧붙임
    List(1,2) ::: List(2,3) | 이 리스트 앞에 다른 리스트를 추가함
    List(1,2) ++ Set(3,4,3) | 이 리스트에 다른 컬렉션을 붙임
    List(1,2) == List(1,2) | 두 컬렉션을 비교함
    List(3,4,5,4,3).distinct | 중복 요소가 없는 리스트 버전을 반환함
    List('a','b','c','d') drop 2 | 앞에서 n개의 요소를 뺌
    List(23, 8, 14, 21) filter (_>18) | 참/거짓 함수를 통과한 리스트 요소를 반환함.
    List(List(1,2), List(3,4)).flatten | 리스트의 리스트 구조를 그 요소들을 모두 포함하는 단일 리스트로 전환함.
    List(1,2,3,4,5) partition (_ < 3) | 리스트의 요소들을 참/거짓 함수의 결과에 따라 분류하여 두 개의 리스트를 포함하는 튜플로 만듦
    List(1,2,3).reverse | 리스트 요소들의 순서를 거꾸로 함
    List(2,3,5,7) slice (1,3) | 리스트 중 첫 번째 인덱스부터 두번째 인덱스까지에 부분을 반환함.
    List("apple", "to") sortBy (_.size) | 주어진 함수로부터 리스트 순서를 정렬함
    List("apple", "to").sorted | 자연값 기준으로 정렬함.
    List(2,3,5,7) splitAt 2 | List 요소들을 주어진 인덱스의 앞에 위치하는지 뒤에 위치하는지 두 리스트의 튜플로 분류함
    List(2,3,5,7,11,13) take 3 | 앞에서 n개의 요소를 추출함
    List(1,2) zip List("a", "b") | 튜플의 리스트로 결합함
    */

    // 리스트 매핑
    /*
    List(0,1,0) collect {case 1 => "ok"} | 각 요소를 부분함수를 사용하여 변환하고, 해당 함수를 적용할 수 있는 요소를 유지함
    List("milk,tea") flatMap (_.split(',')) | 주어진 함수를 이용해 각 요소를 변환하고, 그 결과 리스트를 리스트에 평면화함.
    List("milk", "tea") map (_.toUpperCase) | 주어진 함수를 이용하여 각 요소를 변환함.
    */

    // 리스트 축소하기
    /*
    List(41, 59, 26).max | 리스트의 최대값 구하기
    List(41, 59, 26).min | 리스트의 최소값 구하기
    List(41, 59, 26).product | 리스트의 숫자 곱하기
    List(41, 59, 26).sum | 리스트의 합산
    */

    // 부울 축소 연산
    /*
    List(34, 29, 18) contains 29 | 리스트에 해당 요소가 포함하는지 검사함
    List(34, 29, 18) startWith List(0) | 주어진 리스트로 시작하는지 검사함.
    List(34, 29, 18) endWith List(4,3) | 리스트가 주어진 리스트로 끝나는지 검사함.
    List(34, 29, 18) exists (_ < 18) | 리스트에서 최소 하나의 요소가 있는지 검사함.
    List(34, 29, 18) forall (_ < 18) | 리스트의 모든 요소가 성립하는지 검사함.
    */

    // 리스트 축소 연산
    /*
    대표적으로 fold, reduce, scan이 있음. 이 셋은 크게 다르지 않기에 아무거나 사용하면 될듯.
    Left, Right가 붙은 연산은 방향성을 가지며, 방향성이 없는 (ex. fold, reduce, scan) 연산보단 방향성이 있는 연산이 더 낫다.
    그 중 Left 를 선택하는 것이 더 낫고 더 적게 순회해도 된다(?)
    */
  }
}

