package com.badoo.common.rib.requestcode

import java.lang.RuntimeException

class RequestCodeDoesntFitInMask(override val message: String?) : RuntimeException(message)
