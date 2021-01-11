import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.mutable.ArrayBuffer
import skyline.SkylineOperator
import topk.{DominanceScore, PointWithDomScore, STD_Algorithm}


object Driver {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Dominance-Based Queries").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val inputFile = "./small.csv"
    val data = sc.textFile(inputFile)
      .repartition(8).map(_.split(",")).map(p => p.map(_.toDouble))

    print("Welcome! \n")
    println("Which task do you want to perform? ")

    println("Type: ")
    println("1 for Skyline Query")
    println("2 for Top-k Dominating Points")
    println("3 for Top-k Dominating Points from Skyline")

    val task = scala.io.StdIn.readLine().toInt
    val start = System.nanoTime()
    val skylineObj = new SkylineOperator()
    task match {

      case 1 =>
        // compute skyline of each partition
        val localSkylines = data.mapPartitions(skylineObj.SFS_Algorithm)
        val finalSkylineSet: ArrayBuffer[Array[Double]] = ArrayBuffer[Array[Double]]()
        // compute final skyline set
        localSkylines.collect.foreach(localSkyline => skylineObj.computeFinalSkyline(finalSkylineSet, ArrayBuffer(localSkyline)))
        println("Skyline Set consists of the following points")
        finalSkylineSet.foreach(p => println(p.mkString("{",",","}")))

      case 2 =>  // Compute the top-k points of the dataset

        val dominance: DominanceScore = new DominanceScore(sc)
        val k = 10
        val std = new STD_Algorithm(sc, k)
        val top_k_Points: ArrayBuffer[Array[Double]] = std.compute(data, skylineObj, dominance)
        println(f"Top-$k%d Points are: ")
        top_k_Points.foreach(point => println(point.mkString("{",",","}")))

      case 3 => // Compute the top-k points that belong to the skyline
        val k = 10
        val localSkylines = data.mapPartitions(skylineObj.SFS_Algorithm)
        val finalSkylineSet: ArrayBuffer[Array[Double]] = ArrayBuffer[Array[Double]]()
        // compute final skyline set
        localSkylines.collect.foreach(localSkyline => skylineObj.computeFinalSkyline(finalSkylineSet, ArrayBuffer(localSkyline)))

        val dominanceScore = new DominanceScore(sc)
        var top_k_SkylinePoints: ArrayBuffer[PointWithDomScore] = dominanceScore.calculateScore(finalSkylineSet, data)
        top_k_SkylinePoints = top_k_SkylinePoints.sortWith(_.dominanceScore > _.dominanceScore)

        println(f"Top-$k%d Points of Skyline are: ")
        top_k_SkylinePoints.take(k).foreach(point =>
          println(point.p.mkString(","),"Dominates",",", point.dominanceScore,",","points"))
    }
    println("Total Execution Time: " + (System.nanoTime - start) / 1e9d + "seconds")
  }
}
