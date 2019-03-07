// 구조적 API 기본 연산

object ch5 
{
  def main(args: Array[String]) {
    // 1. DataFrame 생성
    val df = spark.read.format("json").load("./data.json")
    df.printSchema
    // 스키마
    spark.read.format("json").load("./data.json").schema
    // 직접 스키마 정의
    val myManualSchema = StructType(Array(
      StructField("DEST_COUNTRY_NAME", StringType, true),
      StructField("ORIGIN_COUNTRY_NAME", StringType, true),
      StructField("count", LongType, false)
    ))
    val df = spark.read.format("json").schema(myManualSchema).load("./data.json")

    // 2. 컬럼과 표현식
    // col, column을 통해 컬럼을 생성하고참조할 수 있음.
    import org.apache.spark.sql.functions.{col, column}
    col("someColumnName")
    column("someColumnName")
    // 명시적 컬럼 참조
    df.col("count")
    // 표현식
    // expr 함수를 통해 인수로 표현식을 사용하면, 표현식을 분석해 트랜스포메이션과 칼럼 참조를 알 수 있음.
    expr("(((someCol + 5) * 200) - 6) < otherCol")
    // DataFrame 컬럼에 접근
    spark.read.format("json").load("./data.json").columns

    // 3. 레코드와 로우
    // 첫번째 로우 확인
    df.first()

    // 로우 생성
    import org.apache.spark.sql.Row
    val myRow = Row("Hello", null, 1, false)

    // 4. DataFrame의 트렌스포메이션
    // 로우나 컬럼 추가 및 제거, 로우->컬럼, 컬럼->로우, 컬럼값을 기준으로 로우 순서 변경 등과 같은 트랜스포메이션을 제공함.
    
    // DataFrame 생성
    val df = spark.read.format("json").load("./data.json")
    df.createOrReplaceTempView("dfTable")

    // Seq를 활용해 DataFrame 생성
    import org.apache.spark.sql.Row
    import org.apache.spark.sql.types.{StructField, StructType, StringType, LongType}

    val myManualSchema = StructType(Array(
      StructField("DEST_COUNTRY_NAME", StringType, true),
      StructField("ORIGIN_COUNTRY_NAME", StringType, true),
      StructField("count", LongType, false)
    ))
    val myRows = Seq(Row("Hello", null, 1L))
    val myRDD = spark.sparkContext.parallelize(myRows)
    val myDF = spark.createDataFrame(myRDDm myManualSchema)

    myDF.show()

    // select, selectExpr
    // SQL을 실행하는 것 처럼 DataFrame에서도 SQL을 사용할 수 있음.
    df.select("DEST_COUNTRY_NAME").show(2)
    df.select("DEST_COUNTRY_NAME","ORIGIN_COUNTRY_NAME").show(2)

    // 다양한 방법으로 select 할 수 있음.
    import org.apache.spark.sql.functions.{expr, col, column}
    df.select(
      df.col("DEST_COUNTRY_NAME") ,
      col("DEST_COUNTRY_NAME") ,
      column("DEST_COUNTRY_NAME") ,
      'DEST_COUNTRY_NAME' ,
      $"DEST_COUNTRY_NAME" ,
      expr("DEST_COUNTRY_NAME")
    // expr은 가장 유연한 참조 방법
    df.select(expr("DEST_COUNTRY_NAME AS destination")).show(2)
    // select와 expr을 동시에 활용할 수 있음. (newColumnName과 DEST_COUNTRY_NAME이 출력됨)
    df.selectExpr("DEST_COUNTRY_NAME as newColumnName", "DEST_COUNTRY_NAME").show(2)
    // 두 나라의 이름이 같은지 withinCountry로 표현함 (모든 컬럼과 withinCountry(true, false)가 출력됨)
    df.selectExpr("*", "(DEST_COUNTRY_NAME = ORIGIN_COUNTRY_NAME) as withinCountry").show(2)
    // 카운트 평균.
    df.selectExpr("avg(count)", "count(distinct(DEST_COUNTRY_NAME))").show(2)

    // 스파크에 새로운 값 전달 방법.
    // One이라는 컬럼을 생성하고 값은 1을 넣는다.
    // lit(리터럴)은 어떤 상수를 통해 테이블의 데이터와 비교해 값이 큰지, 작은지 등 확인할 때 주로 사용함.
    df.select(expr("*"), lit(1).as("One")).show(2)
    
    // 컬럼 추가 방법
    df.withColumn("numberOne", lit(1)).show(2)
    df.withColumn("withinCountry", expr("DEST_COUNTRY_NAME = ORIGIN_COUNTRY_NAME")).show(2)
    
    // 컬럼명 변경 하기
    df.withColumnRenamed("DEST_COUNTRY_NAME", "dest").columns
    
    // 예약문자와 키워드
    // 예약 문자인 -, 공백 등은 기본적으로 컬럼명에 사용할 수 없음. 그래서 백틱(`)을 써야한다. 몇가지 상황을 보면.
    import org.apache.spark.sql.functions.expr
    // withColumn은 백틱(`)이 필요없음
    val dfWithLongColName = df.withColumn("This is Log Column-Name", expr("ORIGIN_COUNTRY_NAME"))
    // selectExpr은 백틱(`)이 필요함.
    dfWithLongColName.selectExpr("`This Long Column-Name`", "`This Long Column-Name` as `new col`")
    //select는 백틱(`)이 필요없음.
    dfWithLongColName.select(col("this Long Column-Name")).columns
    
    // 컬럼 제거하기
    df.drop("ORIGIN_COUNTRY_NAME").columns
    
    // 컬럼의 데이터 타입 변경하기 (int -> string)
    df.withColumn("count2", col("count").cast("string"))

    // 로우 필터링 하기
    df.filter(col("count") < 2).show(2)
    df.where("count < 2").show(2)
    // AND 필터링
    df.where(col("count") < 2).where(col("ORIGIN_COUNTRY_NAME") =!= "Croatia").show(2)

    // 고유한 로우 얻기
    df.select("ORIGIN_COUNTRY_NAME", "DEST_COUNTRY_NAME").distinct().count()

    // 무작위 샘플 만들기
    val seed = 5
    val withReplacement = false
    val fraction = 0.5
    df.sample(withReplacement, fraction, seed).count()

    // 임의 분할하기
    val dataFrames = df.randomSplit(Array(0.25, 0.75), seed)
    dataFrames(0).count() > dataFrames(1).count() // false

    // 로우 합치기와 추가하기
    import org.apache.spark.sql.Row
    val schema = df.schema
    val newRows = Seq(
      Row("New Country", "Other Country", 5L),
      Row("New Country 2", "Other Country 3", 1L)
    )
    val parallelizedRows = spark.sparkContext.parallelize(newRows)
    val newDF = spark.createDataFrame(parallelizedRows, schema) 
    df.union(newDF)
      .where("count = 1")
      .where($"ORIGIN_COUNTRY_NAME" =!= "United States")
      .show(2)
    )

    // 로우 정렬하기
    df.sort("count").show(5)
    df.orderBy("count", "DEST_COUNTRY_NAME").show(5)
    df.orderBy(col("count"), col("DEST_COUNTRY_NAME")).show(5)
    df.orderBy(expr("count desc")).show(2)
    df.orderBy(desc("count"), acs("DEST_COUNTRY_NAME")).show(2)

    // 로우 수 제한하기
    // 상위 x개만 보고 싶을 때.
    df.limit(5).show()
    // = SELECT * FROM dfTable LIMIT 6

    // repartition, coalesce
    // - 자주 필터링 하는 컬럼을 기준으로 데이터를 분할함.
    // repartition: 전체 데이터를 셔플함. 향후에 사용할 파티션 수가 지금보다 많거나 컬럼 기준으로 파티션을 만드는 경우에 사용함.
    // - 파티션 수 조정
    df.rdd.getNumPartitions // partition: 1
    df.repartition(5) // partition: 5
    // - 컬럼 기준으로 파티션을 만듦
    df.repartitions(col("DEST_COUNTRY_NAME"))
    df.repartitions(5, col("DEST_COUNTR_NAME"))
    // coalesce: 전체 데이터를 셔플하지 않고 파티션을 병합하는 경우에 사용함.
    df.repartition(5, col("DEST_COUNTR_NAME")).coalesce(2)
  }
}