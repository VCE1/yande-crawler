import kotlinx.coroutines.runBlocking
import me.xiaocao.YandeAPI
import java.io.File
import kotlin.test.Test

class SampleTest {

    @Test
    fun test() {
        val savePath = File("C:\\Users\\XiaoCao\\Desktop\\xiaocao-yande")
        runBlocking {
            for (i in 1..5) {
                YandeAPI.getPosts(i).forEachIndexed { index, it ->

                    val saveFile = File(savePath.absolutePath, "${it.id}.${it.originalUrl.split(".").last()}")
                    if (saveFile.exists()) {
                        println("[${it.id}] - ${saveFile.absolutePath}已经存在")
                        return@forEachIndexed
                    }                    
                    if(it.originalUrl.isNotEmpty()) {
                        println("[${it.id}] - 开始下载第${1 + index}帐图片(${it.resolution}):${it.originalUrl}")
                        if (!saveFile.parentFile.exists()) {
                            saveFile.parentFile.mkdirs()
                        }
                        val bytes = YandeAPI.download(it.originalUrl)
                        saveFile.writeBytes(bytes)
                    }
                }
            }
        }
    }
}
