package com.uber.rib.core.requestcode

import java.lang.RuntimeException

class RequestCodeDoesntFitInMask(override val message: String?) : RuntimeException(message)
