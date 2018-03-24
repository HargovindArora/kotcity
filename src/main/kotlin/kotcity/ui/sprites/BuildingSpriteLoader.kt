package kotcity.ui.sprites

import javafx.scene.image.Image
import kotcity.data.*
import kotcity.memoization.CacheOptions
import kotcity.memoization.cache
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

object BuildingSpriteLoader {

    private val imageForFileCachePair = BuildingSpriteLoader::uncachedImageForFile.cache(
        CacheOptions(
            weakKeys = false,
            weakValues = false,
            maximumSize = 4096
        )
    )
    private val imageForFile = imageForFileCachePair.second

    fun spriteForBuildingType(building: Building, width: Double, height: Double) =
        imageForFile(filename(building), width, height)

    private fun uncachedImageForFile(filename: String, width: Double, height: Double): Image {
        return if (File(filename).isFile) {
            Image(filename, width, height, true, true)
        } else {
            // let's try to load from classpath now...
            val classPathFile = filename.replace("file:./assets", "assets")
            println("The sprite filename is: $classPathFile")
            Image(this::class.java.classLoader.getResource(classPathFile).toString(), width, height, true, true)
        }

    }


    fun filename(building: Building): String {
        return when (building::class) {
            PowerPlant::class -> powerPlantSprite(building)
            Commercial::class -> "file:./assets/commercial/${building.sprite}"
            Residential::class -> "file:./assets/residential/${building.sprite}"
            Industrial::class -> "file:./assets/industrial/${building.sprite}"
            PowerLine::class -> "file:./assets/utility/power_line.png"
            FireStation::class -> "file:./assets/utility/fire_station.png"
            PoliceStation::class -> "file:./assets/utility/police_station.png"
            Civic::class -> "file:./assets/civic/${building.sprite}"
            else -> throw RuntimeException("Unknown sprite for ${building::class}")
        }
    }

    private fun powerPlantSprite(building: Building): String {
        return when {
            building.variety == "coal" -> "file:./assets/utility/coal_power_plant.png"
            building.variety == "nuclear" -> "file:./assets/utility/nuclear_power_plant.png"
            else -> throw RuntimeException("Unknown power plant variety: ${building.variety}")
        }

    }
}
