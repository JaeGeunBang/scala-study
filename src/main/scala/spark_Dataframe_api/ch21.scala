// 구조적 스트리밍 기초

object ch21
{
  def main(args: Array[String]) {
    // 정적인 방식의 데이터 셋을 읽기
    val static = spark.read.json("")
    val dataSchema = static.schema

    // 1. 스트리밍 방식의 데이터 셋을 읽기 (실제 운영할 땐 데이터가 바뀔 수도 있으니 스키마 추론 방식은 사용하지 않음.)
    // maxFilesPerTrigger는 폴더 내 전체 파일을 얼마나 빨리 읽을지 결정함. 이 값을 낮게잡으면 트리거당 하나의 파일만 익기 때문에 스트림의 흐름을 인위적으로 제한할 수 있음.
    // 실제 운영상황에서는 높게 설정해주는 것이 좋음.
    val streaming = spark.readStream.schema(dataSchema).option("maxFilesPerTrigger", 1).json("")
    val activityCounts = streaming.groupBy("gt").count()
    // 셔플 파티션은 기본 값이 200이기 때문에 이를 저성능 머신에서 동작하기 위해 파티션 수를 줄여야 함.
    spark.conf.set("spark.sql.shuffle.partitions", 5)
    
    // 싱크 설정 (메모리 싱크)
    // 싱크를 설정 후 출력 모드도 설정해야함.
    // 이를 실행하면 백그라운드에서 스트리밍 연산이 시작됨.
    val activityQuery = activityCounts.writeStream.queryName("activity_counts")
      .format("memory").outputMode("complete").start()
    
    // 실행 중인 스트리밍 쿼리를 제어하려면 쿼리 객체를 사용해야하며, 쿼리 종료 시까지 대기할 수 있도록 아래 명령을 반드시 지정해야함. (스트림처리에 꼭 필요함)
    // 이를 통해 쿼리 실행 도중 드라이브 프로세스가 종료 되는 상황을 막을 수 있음.
    activityQuery.awaitTermination()
    
    // 위 결과를 1초마다 출력하는 반복문
    for( i <- 1 to 5) {
      spark.sql("SELECT * FROM activity_counts").show()
      Thread.sleep(1000)
    }

    // 2. 스트림 트랜스포메이션
    // 선택, 필터링 (간단한 예제)
    import org.apache.spark.sql.functions.expr
    val simpleTransform = streaming.withColumn("stairs", expr("gt like '%stairs%'"))
      .where("stairs") // stairs가 true 일 경우만
      .where("gt is not null") // gt가 null이 아닐 경우만
      .select("gt", "model", "arrival_time", "creation_time") // gt, model, arrival_time, creation_time 필드 선택
      .writeStream
      .queryName("simple_transform")
      .format("memoty")
      .outputMode("append")
      .start()

    // 집계 (cube)
    val deviceModelStats = streaming.cube("gt", "model").avg()
      .drop("avg(Arrival_time)")
      .drop("avg(Creation_time)")
      .drop("avg(Index)")
      .writeStream
      .queryName("device_counts")
      .format("memory")
      .outputMode("complete")
      .start()

    // 조인 (join)
    // 정적 DataFrame과 스트리밍 DataFrame 사이 조인을 제공함.
    val historicalAgg = static.groupBy("gt", "model").avg()
    val deviceModelStats = streaming.drop("Arrival_Time", "Creation_Time", "Index")
      .cube("gt", "model").avg()
      .join(historicalAgg, Seq("gt", "model"))
      .writeStream
      .queryName("device_counts")
      .format("memory")
      .outputMode("complete")
      .start()
    
    // 3. 입력과 출력
    // 테스크용 소스와 싱크
    // 테스크 소스
    val socketDF = spark.readStream.format("socket").option("host", "localhost").option("port", 9999).load()
    // 테스크 싱크 (console, memory)
    activityCounts.writeStream.format("console").outputMode("complete").start()
    activityCounts.writeStream.format("memory").queryName("my_device_table").outputMode("complete").start()
    
    // 카프카 소스에서 메시지 읽기.
    // topic 1 수신
    val ds1 = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
      .option("subscribe", "topic1")
      .load()
    // 여러 topic 수신
    val ds1 = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
      .option("subscribe", "topic1,topic2")
      .load()
    // 토픽 패턴에 맞는 토픽 수신
    val ds1 = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
      .option("subscribePattern", "topic.*")
      .load()

    // 카프카 싱크에 메시지 쓰기.
    ds1.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .writeStream.format("kafka")
      .option("checkpointLocation", "/to/HDFS-compatible/dir")
      .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
      .option("topic", "topic1")
      .start()

    // 4. 트리거
    // 처리 시간 기반 트리거
    import org.apache.spark.sql.streaming.Trigger
    activityCounts.writeStream.trigger(Trigger.ProcessingTime("100 seconds"))
      .format("console").outputMode("complete").start()
    // 일회성 트리거
    // 일회성으로 실행해 테스트 용도로 사용함.
    import org.apache.spark.sql.streaming.Trigger
    activityCounts.writeStream.trigger(Trigger.Once())
      .format("console").outputMode("complete").start()
  }
}