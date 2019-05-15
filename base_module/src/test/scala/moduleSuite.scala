import com.pharbers.baseModules.PharbersModule
import org.scalatest.FunSuite

/**
  * Created by alfredyang on 07/07/2017.
  */
class moduleSuite extends FunSuite {
    test("pharbers module config path") {
        object cd_test extends PharbersModule {
            override val configPath: String = ???
            override val md: List[String] = ???
        }
        println(cd_test.configDir)
        assert(cd_test.configDir.length != 0)
    }
}
