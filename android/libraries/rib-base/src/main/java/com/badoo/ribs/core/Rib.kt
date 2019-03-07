package com.badoo.ribs.core

import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.core.directory.Directory

interface Rib {

    interface Dependency {
        fun ribCustomisation(): Directory
        fun activityStarter(): ActivityStarter
    }
}
