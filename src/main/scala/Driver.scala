import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.mutable.ArrayBuffer
import skyline.SkylineOperator


object Driver {
  def main(args: Array[String]): Unit = {

      val conf = new SparkConf().setAppName("Dominance-Based Queries").setMaster("local[4]")
      val sc = new SparkContext(conf)

      val start = System.nanoTime()

      val inputFile = "./small.csv"

      val data = sc.textFile(inputFile)
                   .repartition(8)
                   .map(_.split(","))
                   .map(p => p.map(_.toDouble))

      println("Rdd partitions size: "+ data.partitions.length)

      println("Welcome!")
      println("Which task do you want to perform? ")

      println("Type: ")
      println("1 for Skyline Query")
      println("2 for Top-k Dominating Points")
      println("3 for Top-k Dominating Points from Skyline")

      val task = scala.io.StdIn.readLine().toInt
      val skylineObj = new SkylineOperator()

      task match {

        case 1 =>  // compute skyline of each partition

          val localSkylines = data.mapPartitions(skylineObj.SFS_Algorithm)
          val finalSkylineSet: ArrayBuffer[Array[Double]] = ArrayBuffer[Array[Double]]()
          // compute final skyline set
          localSkylines.collect.foreach(localSkyline => skylineObj.computeFinalSkyline(finalSkylineSet, ArrayBuffer(localSkyline)))
          println("Skyline Set consists of the following points")
          finalSkylineSet.foreach(p => println(p.mkString("{",",","}")))

        case 2 =>  // Compute the top-k points of the dataset
          println("Task 3 has not been implemented yet!")


        case 3 =>
          println("Task 3 has not been implemented yet!")
      }
      println("----------------------------------------------------")
      println("Total Execution Time: " + (System.nanoTime - start) / 1e9d + "seconds")
      println("----------------------------------------------------")
    }



}
