package gatling.scenario

import gatling.base.phHeaders._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder

/**
  * @description:
  * @author: clock
  * @date: 2019-03-22 17:57
  */
object OAuthScenario {
    val getLoginPage: ChainBuilder = exec(http("loginPage")
            .get("/v0/Login")
            .headers(headers_base)
            .check(status.is(200))
    )

    val getAuthPage: ChainBuilder = exec(http("authPage")
            .get("/v0/Auth")
            .headers(headers_base)
            .check(status.is(200))
    )

    val accountValidation: (String, String) => ChainBuilder = { (user, pwd) =>
        exec(http("accountValidation")
                .post("/v0/AccountValidation")
                .headers(headers_base)
                .queryParam("scope", "NTM")
                .formParam("username", user)
                .formParam("password", pwd)
                .check(currentLocationRegex("/v0/Auth"))
//                .disableFollowRedirect
        )
    }

    val authToken: ChainBuilder = {
        exec(http("authToken")
                .get("/v0/Token")

        )
    }

    val passwordToken: (String, String) => ChainBuilder = { (user, pwd) =>
        exec(http("passwordToken")
                .post("/v0/Token")
                .headers(headers_base)
                .header("Authorization", "Basic NWM5MGRiNzFlZWVmY2MwODJjMDgyM2IyOjVjOTBkYjcxZWVlZmNjMDgyYzA4MjNiMg==")
                .queryParam("scope", "NTM")
                .queryParam("grant_type", "password")
                .queryParam("username", user)
                .queryParam("password", pwd)
                .check(status.is(200))
        )
    }

    val tokenValidation: String => ChainBuilder = { token =>
        exec(http("tokenValidation")
                .post("/v0/TokenValidation")
                .headers(headers_base)
                .header("Authorization", "Bearer " + token)
                .check(jsonPath("$.error").notExists)
        )
    }


}
