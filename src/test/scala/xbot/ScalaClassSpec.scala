import org.scalatest.junit.JUnitRunner
import org.scalatest._
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ScalaClassSpec extends FlatSpec with Matchers {
    "ScalaClass" should "plusOne" in {
        val o = new ScalaClass(10)
        true should be(false)
        //o.plusOne() should be(12)
    }
}