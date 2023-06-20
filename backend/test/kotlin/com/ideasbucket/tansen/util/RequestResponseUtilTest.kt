/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import com.ideasbucket.tansen.util.RequestResponseUtil.isAjaxRequest
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.Test
import org.springframework.http.HttpHeaders

class RequestResponseUtilTest {

    @Test
    fun `Can detect AJAX request`() {
        isAjaxRequest(HttpHeaders().apply { this.set("X-Requested-With", "XMLHttpRequest") }).shouldBeTrue()
        isAjaxRequest(HttpHeaders()).shouldBeFalse()
        isAjaxRequest(HttpHeaders().apply { this.set("X-Requested-With", "XLHttpRequest") }).shouldBeFalse()
    }
}
