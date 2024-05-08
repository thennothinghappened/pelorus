package org.orca.pelorus.utils

import org.orca.pelorus.data.utils.Platform

private val os = System.getProperty("os.name").lowercase()

actual val platform: Platform =
    if (os.contains("win")) Platform.Desktop.Windows
    else if (os.contains("mac")) Platform.Desktop.MacOS
    else if (os.contains("nix") || os.contains("nux")) Platform.Desktop.Linux
    else Platform.Desktop.Other