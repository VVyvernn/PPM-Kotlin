import java.io.File
import java.io.InputStream
import kotlin.math.*

class pixel {
    var pixel: UInt = 0u
    constructor(r: UByte, g: UByte, b: UByte) {
        this.pixel += r.toUInt() shl 8*0
        this.pixel += b.toUInt() shl 8*1
        this.pixel += g.toUInt() shl 8*2  
    }
}

class pixelIndex(X: Int, Y: Int) {
    val x = X
    val y = Y

}

fun createArrayAndFill(length: Int, width: Int, r: UByte, g: UByte, b: UByte): MutableList<MutableList<pixel>> {
    var list: MutableList<MutableList<pixel>> = MutableList(length) { MutableList(width) { pixel(r, g, b) } }
    return list
}



fun pixelListAsByteArray(list: MutableList<MutableList<pixel>>): ByteArray {
    var ba = ByteArray(list.size * list.get(0).size * 3)
    var row_size = list.get(0).size
    for (i in list.indices) {
        for (j in list.get(i).indices) {
            ba.set((i*row_size + j)* 3 + 0, (list.get(i).get(j).pixel shr 8*0 and 0xFFu).toByte())
            ba.set((i*row_size + j)* 3 + 1, (list.get(i).get(j).pixel shr 8*1 and 0xFFu).toByte())
            ba.set((i*row_size + j)* 3 + 2, (list.get(i).get(j).pixel shr 8*2 and 0xFFu).toByte())
        }
    }
    return ba
}



fun drawInPixelArray(list: MutableList<MutableList<pixel>>, index1: pixelIndex, index2: pixelIndex): MutableList<MutableList<pixel>>{
    for (i in index1.x..index2.x) {
        for (j in index1.y..index2.y) {
            list[i][j] = pixel(0.toUByte(), 255.toUByte(), 0.toUByte())
        }
    }
    return list
}

fun drawCircle (list: MutableList<MutableList<pixel>>, index: pixelIndex, radius: Int, colour: pixel): MutableList<MutableList<pixel>>{
    //d^2 <= r^2
    var x1: Int = 0
    var x2: Int = list.size-1
    var y1: Int = 0
    var y2: Int = list.get(0).size-1
    if (index.x-radius > 0) {
        x1 = index.x-radius
    }
    if (index.x+radius < list.size) {
        x2 = index.x+radius
    }
    if (index.y-radius > 0) {
        y1 = index.y-radius
    }
    if (index.y+radius < list.get(0).size) {
        y2 = index.y+radius
    }
    for (x in x1..x2) {
        for (y in y1..y2) {
            val dx = x - index.x
            val dy = y - index.y
            val distance = dx*dx + dy*dy
            if (distance <= radius*radius) {
                list[x][y] = colour
            }
        }
    }
    return list
}

fun drawFunction(list: MutableList<MutableList<pixel>>, colour: pixel): MutableList<MutableList<pixel>>{
    for (y in 0..list.size-1) {
        //val x = (400*sin(2*y.toDouble())-345*cos(y.toDouble())).toInt()
        val x = (450*sin(2*y.toDouble())).toInt()
        list[x+list.get(0).size/2][y] = colour
    }
    return list
}

fun drawGradient(list: MutableList<MutableList<pixel>>): MutableList<MutableList<pixel>> {
    //sin(x^2+y^2) upper left = red, lower left = green, upper right = blue, lower right ???
    val step: Int = (list.size/255)
    var counter: Int = 1
    for (y in 1..list.size-1) {
        for (x in 1..list.get(0).size-1) {
            //print(counter % step)
            if (counter % step == 0) {
                list[x][y] = pixel((255-y/step).toUByte(),0.toUByte(),(0+y/step).toUByte())
            } else {
                list[x][y] = list[x-1][y-1]
            }
            counter++
        } 
    }
    return list
}


fun writeToPpm(list: MutableList<MutableList<pixel>>, filename: String) {
    var f = File(filename)
    f.writeText("")
    f.appendText("P6\n")
    f.appendText(list.size.toString() + " " + list.get(0).size.toString() + "\n" + "255" + "\n")
    var ba = pixelListAsByteArray(list)
    f.appendBytes(ba)
}

fun main() {
    var ll = createArrayAndFill(3000, 3000, 255.toUByte(), 255.toUByte(), 255.toUByte())
    //drawInPixelArray(ll, pixelIndex(20, 20), pixelIndex(80, 80))
    //drawCircle(ll, pixelIndex(20, 50), 30, pixel(100.toUByte(), 100.toUByte(), 100.toUByte()))
    //drawFunction(ll, pixel(255.toUByte(), 0.toUByte(), 0.toUByte()))
    drawGradient(ll)
    writeToPpm(ll, "eo.ppm")
}