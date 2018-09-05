
import com.pharbers.ErrorCode
import org.scalatest.FunSuite

/**
  * Created by alfredyang on 07/07/2017.
  */
class errorcodeSuite extends FunSuite {

    test("pharbers error code test") {
        assert(!ErrorCode.xls.find(p => p.code == -9999 && p.name == "unknown error").isEmpty)
    }
}