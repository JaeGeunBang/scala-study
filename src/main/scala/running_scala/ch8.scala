/*
클래스
- 데이터 구조와 함수의 조합. 객체 지향 언어의 핵심 구성 요소.
*/

object ch8 {
  def main(args: Array[String]) {
    // 1. 클래스 정의 및 인스턴스 생성
    class User
    val u = new User
    val isAnyRef = u.isInstanceOf[AnyRef]
    // 멤버 변수 및 메서드 추가
    class User {
      val name: String = "Yubaba"
      def greet: String = "Hello from $name"
      override def toString = "User($name)"
    }
    val u = new User
    println(u.greet)
    // 클래스 파라미터 추가 (생성자)
    class User(n: String) {
      val name: String = n
      def greet: String = "Hello from $name"
      override def toString = "User($name)" 
    }
    // 클래스 타입 매개변수 정의
    class Singular[A](element: A) extends Traversable[A] {
      def foreach[B](f: A => B) = f(element)
    }
    val p = new Singular[String]("Planes")

    // 상속
    // 모든 class는 기본적으로 Object 클래스를 상속받는다. (toString은 Object가 가지는 메서드)
    class A {
      def hi = "Hello from A"
      override def toString = getClass.getName // 상속 받은 함수를 재정의함 (override)
    }
    class B extends A
    class C extends B { 
      override def hi = "hi C ->" + super.hi // 상속 받은 함수를 재정의함 (override) 
    } 
    
    val hiA = new A().hi
    val hiB = new B().hi
    val hiC = new C().hi

    // 2. 그 외 클래스 유형
    // 추상 클래스
    // 추상 클래스는 독자적으로 인스턴스를 생성할 수 없다.
    abstract class Car {
      val year: Int
      val automatic: Boolean = true
      def color: String
    }
    // 추상 클래스를 상속 받는 하위 클래스는 메서드를 꼭 구현해주어야 한다. (override는 쓰지 않아도 된다.)
    // - Object를 상속받는 모든 클래스들은 Object가 추상 클래스가 아니기 때문에 toString은 구현해도 되고 안해도 상관 없다. (단 다시 구현하려면 override를 써야한다.)
    class RedMini(val year: Int) extends Car {
      def color = "red" // color 함수 구현
    }

    // 익명 클래스
    // 부모 클래서의 메서드를 구현하는 추상 클래스외에 다른 방법.
    // trigger는 재사용 할 수 없고 이름도 없는 익명 클래스를 사용하게 됨.
    abstract class Listener {
      def trigger
    }
    val myListener = new Listener {
      def trigger {println("test")}
    }

    // 3. 그 외 필드와 메서드 유형
    // 중복 메서드
    class Printer(msg: String) {
      def print(s: String): Unit = println("test")
      def print(l: Seq[String]): Unit = println("test2")
    }

    // apply 메서드
    // 근본적으로 메서드 이름 없이 괄호를 사용해 적용할 수 있는 기능을 제공한다.
    class Multiplier(factor: Int) {
      def apply(input: Int) = input * factor
    }
    val tripleMe = new Multiplier(3)
    tripleMe(10) // 메서드를 적지 않아도 알아서 apply가 수행된다.
  }
}

