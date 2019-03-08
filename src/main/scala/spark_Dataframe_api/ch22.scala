// 이벤트 시간 처리의 기본

object ch22
{
  def main(args: Array[String]) {
    val static = spark.read.json("")
    val streaming = spark.readStream.schema(static.schema).option("maxFilesPerTrigger", 10).json("")

    // 이벤트 시간 윈도우
    // 1. 타임스탬프 컬럼을 적절한 스파크 SQL 타임스템프 데이터 타입으로 변환.
    val withEventTime = streaming.selectExpr(
      "*",
      "cast(cast(Creating_Time as double)/1000000000 as timestamp) as event_time)"
    )

    // 2. 텀블링 윈도우
    // 주어진 윈도우를 통해 이벤트가 발생한 횟수를 카운트 해보자.
    // - ch.21에 있는 Trigger.ProcessingTime("100 seconds") 는 처리 시점 기준으로의 트리거.
    // - 아래 예제는 이벤트 시간을 기준으로의 트리거.
    import org.apache.spark.sql.functions.{window, col}
    withEventTime.groupBy(window(col("event_time"), "10 minutes")).count()
      .writeStream
      .queryName("event_per_window")
      .format("memory")
      .outputMode("complete")
      .start()
    //결과 조회
    spark.sql("SELECT * FROM event_per_window").show()
    // 결과를 보면 window 객체를 볼 수 있으며, window는 시작시간, 종료시간 두 값을 가진 구조체 형식으로 되어있음.
    
    // 다른 예제, groupby할 때 window 뿐만 아니라, 다른 컬럼도 같이 집계할 수 있음.
    import org.apache.spark.sql.functions.{window, col}
    withEventTime.groupBy(window(col("event_time"), "10 minutes"), col("User")).count()
      .writeStream
      .queryName("event_per_window")
      .format("memory")
      .outputMode("complete")
      .start()

    // 3. 슬라이딩 윈도우
    // 텀블링 윈도우는 윈도우에 속한 이벤트 수를 단순히 카운트 했음.
    // 시간 증분을 하여, 지난 시간에 대한 데이터를 유지하면서 연속적으로 값을 갱신함.
    // 5분마다 시작하는 10분짜리 윈도우를 사용함.
    // 즉, 5분마다 아래 집계가 실행하는데 10분짜리 윈도우 크기만큼 카운팅을 함.
    // 예제)
    // 9:5 시작, 8:55~9:5
    // 9:10 시작, 9:00~9:10
    // 9:15 시작, 9:05~9:15
    import org.apache.spark.sql.functions.{window, col}
    withEventTime.groupBy(window(col("event_time"), "10 minutes", "5 minutes")).count()
      .writeStream
      .queryName("event_per_window")
      .format("memory")
      .outputMode("complete")
      .start()

    // 4. 워터파크 (지연 데이터 제어하기)
    // 기존 방법은 훌륭하지만 얼마나 늦게 도착한 데이터 까지 받아들일지 기준이 없음.
    // 워터파크, 데이터가 필요 없어지는 시간을 지정하지 않았기에 스파크는 중간 결과를 영원히 저장할 것. (꼭 설정해야함.)
    // 워터파크는 오래된 데이터를 제거할때 사용함. (특정 시간 이후 처리에서 제외할 이벤트, 이벤트 집합에 대한 시간 기준)
    
    // 5시간 지연 까지 데이터 상태를 저장하겠다는 의미.
    import org.apache.spark.sql.functions.{window, col}
    withEventTime
      .withWaterMark("event_time", "5 hours")
      .groupBy(window(col("event_time"), "10 minutes", "5 minutes")).count()
      .writeStream
      .queryName("event_per_window")
      .format("memory")
      .outputMode("complete")
      .start()
    
    // 5. 스트림에서 중복 데이터 제거하기
    import org.apache.spark.sql.functions.expr
    withEventTime
      .withWaterMark("event_time", "5 seconds")
      .dropDuplicates("User", "event_time")
      .groupBy("User")
      .count()
      .writeStream
      .queryName("deduplicated")
      .format("memory")
      .outputMode("complete")
      .start()
    
  }
}