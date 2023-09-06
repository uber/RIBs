package com.uber.rib.core

/**
 * Configuration for XRay.
 * @property enabled `true` to enable XRay. By default it is disabled.
 * @property alphaEnabled `true` to enable alpha changes when XRay is enabled.
 */
public data class XRayConfig(
    val enabled: Boolean = false,
    val alphaEnabled: Boolean = true,
)
