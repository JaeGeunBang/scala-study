// 분산형 공유 변수

object ch14
{
  def main(args: Array[String]) {
    // 1. 브로드캐스트 변수
    val myCollection = "Spark The Definitive Guide: Big Data Processing Made Simple"
    val words = spark.SparkContext.parallelize(myCollection, 2)
    val supplementalData = Map("Spark" -> 1000, "Definitive" -> 200, "big" -> 300, "Simple" -> 100)
    val suppBroadcast = spark.SparkContext.broadcast(supplementalData)
    suppBroadcast.value

    // 브로드캐스트 변수 사용
    words.map(word => (word, suppBroadcast.value.getOrElse(word, 0)))

    // 2. 어큐물레이터 변수
    case class Flight(DEST_COUNTRY_NAME: String, ORIGIN_COUNTRY_NAME: String, count: BigInt)
    val flights = spark.read.parquet("").as[Flight]
    
    import org.apache.spark.util.LongAccumulator
    val accUnnamed = new LongAccumulator
    val acc = spark.sparkContext.register(accUnnamed)

    def accChinaFunc(flight_row: Flight) = {
      val destination = flight_row.DEST_COUNTRY_NAME
      val origin = flight_row.ORIGIN_COUNTRY_NAME

      if(destination == "China") 
        acc.add(flight_row.count.toLong)
      if(origin = "China")
        acc.add(flight_row.count.toLong)
    }

    flights.foreach(flight_row => accChinaFunc(flight_row))
    acc.value // 어큐물레이터 값을 조회함
  }
}