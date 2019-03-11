// 집계 연산
// 집계는 데이터를 모으는 행위.

object ch7
{
  def main(args: Array[String]) {
    // 1.집계 함수
    // count
    import org.apache.spark.sql.functions.count
    df.select(count("StockCode")).show()

    // countDistinct
    import org.apache.spark.sql.functions.countDistinct
    df.select(countDistinct("StockCode")).show()

    // approx_count_distinct: 근사치 count, countDistinct보다 훨씬 빠르다. 대규모 데이터셋에 사용함.
    import org.apache.spark.sql.functions.approx_count_distinct
    df.select(approx_count_distinct("StockCode", 0.1)).show()

    // first, last
    import org.apache.spark.sql.functions.{first, last}
    df.select(first("StockCode"), last("StockCode")).show()

    // min,max
    import org.apache.spark.sql.functions.{min, min}
    df.select(min("StockCode"), min("StockCode")).show()

    // sum
    import org.apache.spark.sql.function.sum
    df.select(sum("Quantity")).show()

    // sumDistinct: 중복된 값들만 합산함.
    import org.apache.spark.sql.function.sumDistinct
    df.select(sumDistinct("Quantity")).show()

    // avg
    import org.apache.spark.sql.function.avg
    df.select(avg("Quantity")).show()
    // 이외에도 분산, 표준편차 등 다양한 기능이 존재함.

    // 2. 그룹화
    // 그룹 기반의 집계 수행 (InvoiceNo, CustomerId)로 그룹화한 후 count함
    df.groupBy("InvoiceNo", "CustomerId").count().show()
    
    // 표현식을 이용한 그룹화
    // agg는 여러 집계 처리를 한 번에 지정할 수 있으며, 집계에 표현식(expr)도 사용할 수 있음.
    import org.apache.spark.sql.functions.count
    df.groupBy("InvoiceNo").agg(
      count("Quantity").alias("quan"),
      expr("count(Quantity)")
    )

    // 맵을 이용한 그룹화
    df.groupBy("InvoiceNo").agg("Quantity" -> "avg", "Quantity" -> "stddev_pop").show()

    // 3. 윈도우 함수
    // 윈도우 함수는 데이터의 특정 '윈도우' 를 대상으로 집계를 수행함.
    // - groupBy는 모든 로우 레코드가 단일 그룹으로만 이동함.
    // - 윈도우 함수는 프레임(로우 그룹 기반의 테이블)에 입력되는 모든 로우에 결과값을 계산함.
    // - 윈도우 함수는 랭크 함수, 분석 함수, 집계 함수를 제공함.
    import org.apache.spark.sql.functions.{col, to_date}
    val dfWithDate = df.withColumn("date", to_date(col("InvoiceDate"), "MM/d/yyyy H:mm"))
    dfWithDate.createOrReplaceTempView("dfWithDate")

    // 3.1.윈도우 명세를 만듦
    import org.apache.spark.sql.expressions.Window
    import org.apache.spark.sql.functions.col
    val windowSpec = Window
      .partitionBy("CustomerId", "date")
      .orderBy(col("Quantity").desc)
      .rowsBetween(Window.unboundedPreceding, Window.currentRow) // 프레임 명시 (unboundedPreceding ~ currentRow) 까지.
    
    // 3.2. 만든 윈도우 명세를 적용. 즉 윈도우 내 로우들 중 max 값을 가진 Quantity를 찾아줌.
    import org.apache.spark.sql.functions.max
    val maxPurchaseQuantity = max(col("Quantity")).over(windowSpec)

    // 3.3. 모든 고객에 대해 최대 구매수량을 가진 날짜를 구할 수 있음.
    import org.apache.spark.sql.functions.{dense_rank, rank}
    val purchaseDenseRank = dense_rank().over(windowSpec)
    val purchaseRank = rank().over(windowSpec) // 보통 dense_rank를 많이 사용함.

    // 3.4. 최종 결과
    import org.apache.spark.sql.functions.col
    dfWithDate.where("CustomerId IS NOT NULL").orderBy("CustomerId")
      .select(
        col("CustomerId") ,
        col("date") ,
        col("Quantity") ,
        purchaseRank.alias("quantityRank"),
        purchaseDenseRank.alias("quantityDenseRank"),
        maxPurchaseQuantity.alias("maxPurchaseQuantity"))
      .show()

      // 4. 그룹화 셋
      // 여러 그룹에 걸쳐 집계할 수 있음.
      val dfNoNull = dfWithDate.drop()
      dfNoNull.createOrReplaceTempView("dfNoNull")

      // 롤업: group-by 스타일의 다양한 연산을 수행할 수 있는 다차원 집계 기능
      // - roll-up을 하면 date, country가 모두 null, null로 나오면 전체 날짜, 전체 나라의 합계를 확인할 수 있음. 
      // - 단순히 group-by로는 날짜, 나라와 상관없이 전체 합계를 추가할 수 없음.
      val rolledUpDF = dfNoNull.rollup("Date", "Country").agg(sum("Quantity"))
        .selectExpr("Date", "Country", "`sum(Quantity)` as total_quantity")
        .orderBy("Date")
      rolledUpDF.show()
      /*
      roll-up 결과

      |Date, country, total_quantity|
      |null, null, 5176450| --> roll-up을 통해 나온 결과.
      |10.12.01, korea, 231|
      |10.12.02, US, 234|
      |10.12.03, France, 1211|
      ...
      */

      // cube: 롤업을 고차원적으로 사용할 수 있음.
      // - 요소들을 계층적으로 다루는 대신 모든 차원에 대해 동일한 작업을 수행함. (모든 날짜, 모든 나라에 합계, 모든 날짜에 합계, 모든 국가에 합께, 날짜별 국가별 합계)
      dfNoNull.cube("Date", "Country").agg(sum("Quantity"))
        .selectExpr("Date", "Country", "`sum(Quantity)` as total_quantity")
        .orderBy("Date")
      rolledUpDF.show()

      /*
      cube 결과

      |Date, country, total_quantity|
      |null, korea, 4421|
      |null, US, 2342|
      |null, France, 1234|
      ...
      --> Date로 정렬했기 때문에, Date에 null인 레코드먼저 노출되는 것. 그 아래에는 country가 null인 것도 있을 것이고, Date, country 둘 모두가 null인 레코드가 있을 것.
      */

    // 그룹화 메타데이터
    import org.apache.spark.sql.functions.{grouping_id, sum, expr}

    dfNoNull.cube("Date", "Country").agg(grouping_id(), sum("Quantity"))
      .selectExpr("Date", "Country", "`sum(Quantity)` as total_quantity")
      .orderBy(col("grouping_id()").desc)
    rolledUpDF.show()

    /*
    그룹화 ID = 0, Date와 Country 별 조합에 따라 총 수량 제공
    그룹화 ID = 1, Date는 null, Country를 기반으로 총 수량 제공
    그룹화 ID = 2, Country는 null, Date를 기반으로 총 수량 제공
    그룹화 ID = 3, Date, Country 모두 null을 기반으로 총 수량 제공
     */

    // 5. 사용자 정의 집계 함수
    /*
    UDAF를 직접 제작한 함수나 비즈니스 규칙에 기반을 둔 자체 집계 함수를 정의하는 방법이며, 다음 메서드들을 정의해야함.
    - inputSchema: UDAF 입력 파타미서를 스키마 StructType으로 정의
    - bufferSchema: UDAF 중간 결과의 스키마를 StructType으로 정의
    - dataType: 반환될 값의 DateType 정의
    - deterministric: UDAF가 동일한 입력값에 대해 항상 동일한 결과를 반환하는지 불리언값으로 정의
    - initialize: 집계용 버퍼의 값을 초기화 하는 로직을 정의
    - update: 입력받은 로우를 기반으로 내부 버퍼를 업데이트하는 로직 정의
    - merge: 두 개의 집계용 버퍼를 병합하는 로직을 정의
    - evaluate: 집계의 최종 결과를 생성하는 로직을 정의
    자세한 코드는 내용 참고.
     */

  }
}