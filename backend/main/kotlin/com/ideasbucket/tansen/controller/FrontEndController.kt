/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class FrontEndController {

    @GetMapping(
        value = [
            "/",
            "[a-zA-Z0-9\\_\\-]+/topics",
            "[a-zA-Z0-9\\_\\-]+/consumers-group",
            "[a-zA-Z0-9\\_\\-]+/topics/[a-zA-Z0-9\\_\\-]+",
            "[a-zA-Z0-9\\_\\-]+/topics/[a-zA-Z0-9\\_\\-]+/overview",
            "[a-zA-Z0-9\\_\\-]+/topics/[a-zA-Z0-9\\_\\-]+/schema/value",
            "[a-zA-Z0-9\\_\\-]+/topics/[a-zA-Z0-9\\_\\-]+/schema/key",
            "[a-zA-Z0-9\\_\\-]+/topics/[a-zA-Z0-9\\_\\-]+/schema/messages"
        ]
    )
    suspend fun index(): String = "index"
}
