// 다양한 데이터 타입 다루기

object ch6 
{
  def main(args: Array[String]) {
    // 2. 스파크 데이터 타입으로 변환
    import org.apache.spark.sql.functions.lit
    df.select(lit(5), lit("five"), lit(5.0))
    // = SELECT 5, "five", 5.0
    
    // 3. 불리언 데이터 타입 다루기
    // 불리언 데이터 타입은 and, or, true, false로 구성됨.
    import org.apache.spark.sql.functions.col
    df.where(col("InvoiceNo").equalTo(232314))
      .select("InvoiceNo", "Description")
      .show(5, false)

    // == , ===는?
    // === 동등 여부를 판단해 일치를 나타냄, =!= 불일치를 나타냄.
    df.where(col("InvoiceNo") === 532323)
      .select("InvoiceNo", "Description")
      .show(5, false)
    // 가장 명확한 방법은.
    df.where("InvoiceNo = 23123")
    df.where("InvoiceNo <> 23123")
    // 다양하게 표현이 가능함.
    val priceFilter = col("UnitPrice") > 600
    val descripFilter = col("Description").contains("POSTAGE")
    // AND, OR 모두 표현.
    df.where(col("StockCode").isin("DOT")).where(priceFilter.or(descripFilter))
    // 필터 표현은 DataFrame 방식보단 SQL 방식이 훨씬 이해하기 쉽다. 아래만 봐도 딱 그렇음.
    df.withColumn("isExpensive", not(col("UnitPrice").leq(250))).filter("isExpensive").select("Description", "UnitPrice").show(5)
    df.withColumn("isExpensive", expr("NOT UnitPrice <= 250")).filter("isExpensive").select("Description", "UnitPrice").show(5)

    // 4. 수치형 데이터 타입 다루기
    import org.apache.spark.sql.functions.{expr, pow}
    val fabricatedQuantity = pow(col("Quantity") * col("UnitPrice"), 2) + 5
    df.select(expr("CustomerId"), fabricatedQuantity.alias(realQuantity)).show()

    // SQL문을 통해 동일한 처리할 수 있음
    df.selectExpr("CustomerId", "(POWER((Quantity * UnitPrice), 2.0) + 5) as RealQuantity").show()

    // 반올림, 내림
    df.select(round(lit("2.5")), bround(lit("2.5"))).show(2)

    // 두 컬럼의 상관관계 계산
    df.select(corr("Quantity", "UnitPrice")).show()
    
    // describe 메서드는 컬럼에 대한 집계, 평균, 표준편차, 최솟값, 최댓값을 계산함
    df.describe().show()
    // 이외 다양한 방법들이 있음..

    // 5. 문자열 데이터 타입 다루기
    // initcap은 주어진 문자열에서 공백으로 나뉘고 모든 단어의 첫 글자를 대문자로 변경함.
    df.select(initcap(col("Description"))).show(2, false)
    // lower 전체 문자를 소문자로, upper 전체 문자를 대문자로
    df.select(col("Description"), lower(col("Description")), upper(col("Description"))).show(2)
    // 문자열 공백 추가, 제거
    // ltrim: 왼쪽 공백 제거, rtimr: 오른쪽 공백 제거, trim: 양쪽 공백 제거
    // lpad, rpad 얘넨 잘 모르겠다.
    import org.apache.spark.sql.functions.{lit, ltrim, rtrim, rpad, lpad, trim}
    df.select(
      ltrim(lit("   HELLO    ")).as("ltrim"),
      rtrim(lit("   HELLO    ")).as("rtrim"),
      trim(lit("   HELLO    ")).as("trim"),
      lpad(lit("HELLO"), 3, " ").as("lp"),
      rpad(lit("HELLO"), 10, " ").as("rp")).show(2)
    )

    // 정규표현식
    // - 문자열의 존재 여부 확인, 일치하는 모든 문자들을 치환할 때 사용함.
    // - 변환: regexp_replace, 추출: regexp_extract
    import org.apache.spark.sql.functions.regexp_replace
    val simpleColors = Seq("black", "white", "red", "green", "blue")
    val regexString = simpleColors.map(_.toUpperCase).mkString("|") //simpleColors에 있는 문자를 모두 대문자로 바꾸고 OR 체크함.
    df.select(
      regexp_replace(col("Description"), regexString, "COLOR").alias("color_clean"), col("Description")).show(2)
    ) // descript과 regexString을 비교해 맞는 문자가 있다면 "COLOR"로 replace함.
    
    // 정규 표현식 말고 다른 방법으로 치환 하는 방법
    // 각각 L--> 1, E-->3, T-->7 로 변환한다.
    df.select(translate(col("Description"), "LEET", "1337"), col("Description")).show(2)
    
    // 정규 표현식 추출 방법 regexp_extract
    import org.apache.spark.sql.functions.regexp_extract
    val regexString = simpleColors.map(_.toUpperCase).mkString("(", "|", ")")
    df.select(
      regexp_extract(col("Description"), regexString, 1).alias("color_clean"), col("Description")).show(2)
    ) //위 명령 수행 시 "WHITE" 하나만 추출됨

    // 값 추출을 하지 않고 존재 여부를 확인하고 싶을 때,
    val containsBlack = col("Description").contains("BLACK")
    val containsWhite = col("Description").contains("WHITE")
    df.withColumn("hasSimpleColor", containsBlack.or(containsWhite))
      .where("hasSimpleColor")
      .select("Description").show(3, false) // WHITE, BLACK이 있는지 체크해서 있다면 출력해줌. 

    // 6. 날짜와 타임스탬프 데이터 타입 다루기
    import org.apache.spark.sql.functions.{current_date, current_timestamp}

    val dateDF = spark.range(10)
      .withColumn("today", current_date())
      .withColumn("now", current_timestamp())
    dateDF.createOrReplaceTempView("dateTable")

    // 날짜 계산
    import org.apache.spark.sql.functions.{date_sub, date_add}
    dateDF.select(date_sub(col("today"), 5), date_add(col("today"), 5))

    // 두 날짜의 차이 비교
    // to_date는 문자열을 날짜 포맷으로 바꾸어 준다.
    import org.apache.spark.sql.functions.{datediff, months_between, to_date}
    dataDF.withColumn("week_age", date_sub(col("today"), 7))
      .select(datediff(col("week_age"), col("today"))).show(1)\
    dateDF.select(
      to_date(lit("2016-01-01")).alias("start"),
      to_date(lit("2016-05-22")).alias("end"))
      .select(months_between(col("start"), col("end"))).show(1)
    
    // date format
    import org.apache.spark.sql.functions.to_date
    val dateFormat = "yyyy-dd-MM"
    val cleanDateDF = spark.range(1).select(
      to_date(lit("2017-12-11"), dateFormat).alias("date") ,
      to_date(lit("2017-20-12"), dateFormat).alias("date2"))
    cleanDateDF.createOrReplaceTempView("dateTable2")
    // 항상 날짜 포맷을 지정하는 to_timestamp (얜 to_date와 다르게 시간,분,초도 나옴)
    import org.apache.spark.sql.functions.to_timestamp
    cleanDateDF.select(to_timestamp(col("date"), dateFormat)).show()
    
    // 필터를 통한 날짜 비교
    cleanDateDF.filter(col("date2") > lit("2017-12-12")).show()
    cleanDateDF.filter(col("date2") > "`2017-12-12`").show()

    // 7. NULL 다루기
    // 스파크에서는 빈 값에 대해 NULL로 처리하는 것이 좋음.
    // - 명시적으로 NULL값을 제거, 전역 단위로 NULL 값에 특정 값을 채우는 방법.
    
    // coalesce: 모든 컬럼이 null이 아닐 때 반환함.
    df.select(coalesce(col("Description"), col("CustomerId"))).show()
    // 이 외에도 NULL을 다루는 함수들. ifnull, nullif, nvl, nvl2
    // ifnull: null이 아니면 첫번째 값을 반환함.
    // nullif: 두 값이 같으면 null을 반환함.
    // nvl: 첫번째 값이 null이면 두번째 값을 반환함.
    // nvl2: 첫번째 값이 null이 아니면 두번째 값을 반환함.

    // DROP: NULL인 값을 가진 모든 로우를 삭제함.
    df.na.drop()
    df.na.drop("any") //로우 중 하나라도 null이면 해당 로우를 삭제함.
    df.na.drop("all") // 모든 컬럼의 값이 null or NAN 인 경우에만 로우를 제거함.
    df.na.drop("all", Seq("StockCode", "InvoiceNo")) //특정 컬럼에만 적용할 수 있음.

    // fill: 하나 이상의 컬럼을 특정 값으로 채울 수 있음.
    df.na.fill("All Null values becomd this string")
    df.na.fill(5, Seq("StockCode", "InvoiceNo")) //5를 특정 컬럼에 채움.
    // 특정 컬럼에 서로 다른 값을 채움.
    val fillColValues = Map("StockCode" -> 5, "Description" -> "No Value") 
    df.na.fill(fillColValues)
    
    // replace: 조건에 따라 다른 값으로 대체함.
    df.na.replace("Description", Map("" -> "UNKNOWN")) //NULL인 값을 UNKNOWN으로 채움
    
    // 9. 복합 데이터 타입 다루기
    // 구조체
    // 구조체는 DataFrame 내부의 DataFrame으로 생각하면 됨.
    import org.apache.spark.sql.functions.struct
    val complexDF = df.select(struct("Description", "InvoiceNo").alias("complex"))
    complexDF.createOrReplaceTempView("complexDF")
    complexDF.select("complex.Description")
    complexDF.select(col("complex").getField("Description"))

    // 배열
    // split: 구분자를 통해 split해 배열을 만들 수 있음.
    import org.apache.spark.sql.functions.split
    df.select(split(col("Description"), " ")).show(2)

    // size: 배열의 길이
    import org.apache.spark.sql.functions.size
    df.select(size(split(col("Description"), " "))).show(2)

    // array_contains: 배열에 특정 값이 존재하는지 확인
    import org.apache.spark.sql.functions.array_contains
    df.select(array_contains(split(col("Description"), " "))).show(2)

    // explode: 배열 타입의 컬럼을 입력받아 모든 값을 로우로 변환함.
    import org.apache.spark.sql.functions.{split, explode}
    df.withColumn("splitted", split(col("Description"), " "))
      .withColumn("exploded", explode(col("splitted"))).show(2)

    // 맵
    // WHITE METAL LENTERN 키를 가진 Map이 있다면 InvoiceNo를 출력함.
    df.select(map(col("Description"), col("InvoiceNo")).alias("complex_map"))
      .selectExpr("complex_map['WHITE METAL LENTERN']").show(2)

    // 10. JSON 다루기
    import org.apache.spark.sql.functions.{get_json_object, json_tuple}

    val jsonDF = spark.range(1).selectExpr("""'{"myJSONKey" : {"myJSONValue" : [1,2,3]}}' as jsonString""") 
    jsonDF.select(
      get_json_object(col("jsonString"), "$.myJSONKey.myJSONValue[1]") as "column", // 2 출력
      json_tuple(col("jsonString"), "myJSONKey")).show(2) // 전체 json 구조 출력
    )
    
    // mystruct를 json으로 변경함.
    import org.apache.spark.sql.functions.to_json
    df.selectExpr("(InvoiceNo, Description) as myStruct")
      .select(to_json(col("myuStruct")))
    
    import org.apache.spark.sql.functions.from_json
    import org.apache.spark.sql.types._

    val parseSchema = new StructType(Array(
      new StructField("InvoiceNo", StringType, true),
      new StructField("Description", StringType, true)))
    // Struct를 JSON으로, JSON을 다시 객체로 변환함.
    df.selectExpr("(InvoiceNo, Description) as MyStruct")
      .select(to_json(col("MyStruct")).alias("NewJSON"))
      .select(from_json(col("NewJSON"), parseSchema), col("newJSON")).show(2)

    // 11. 사용자 정의 함수
    // 사용자가 원하는 형태의 함수를 정의할 수 있음.
    val udfExampleDF = spark.range(5).toDF("num")
    def power3(number:Double):Double = number * number * number
    // 선언한 UDF 실행
    import org.apache.spark.sql.functions.udf
    val power3udf = udf(power3(_:Double):Double)
    udfExampleDF.select(power3udf(col("num"))).show()
    // 위 UDf는 dataframe에서만 사용가능. SQL에서도 사용하고 싶다면 추가 등록해야함.
    spark.udf.register("power3", power3(_:Double):Double)
    udfExampleDF.selectExpr("power3(num)").show(2)
  }
}