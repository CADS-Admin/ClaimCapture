package monitoring

import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithBrowser



class ReportPagesSpec extends Specification with Tags {

  "Application" should {
    "should respond to ping" in new WithBrowser {
      browser goTo "/report/ping"
      browser.pageSource must contain("ping")
    }
    "should report health" in new WithBrowser {
      browser goTo "/report/health"
      browser.pageSource must contain("isHealthy")
    }
    "should report metrics" in new WithBrowser {
      browser goTo "/report/metrics"
      browser.pageSource must contain("MetricsFilter")
    }
  }

}