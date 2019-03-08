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
      val rolledUpDF = dfNoNull.rollup("Date", "Country").agg(sum("QUantity"))
        .selectExpr("Date", "Country", "`sum(Quantity)` as total_quantity")
        .orderBy("Date")
      rolledUpDF.show()
  }
}