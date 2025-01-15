package androidx.core.view

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi


class ViewCompat private constructor() {
    companion object {
        /**
         * Automatically determine whether a view is important for content capture.
         */
        const val IMPORTANT_FOR_CONTENT_CAPTURE_AUTO: Int = 0x0

        /**
         * The view is important for content capture, and its children (if any) will be traversed.
         */
        const val IMPORTANT_FOR_CONTENT_CAPTURE_YES: Int = 0x1

        /**
         * The view is not important for content capture, but its children (if any) will be traversed.
         */
        const val IMPORTANT_FOR_CONTENT_CAPTURE_NO: Int = 0x2

        /**
         * The view is important for content capture, but its children (if any) will not be traversed.
         */
        const val IMPORTANT_FOR_CONTENT_CAPTURE_YES_EXCLUDE_DESCENDANTS: Int = 0x4

        /**
         * The view is not important for content capture, and its children (if any) will not be
         * traversed.
         */
        const val IMPORTANT_FOR_CONTENT_CAPTURE_NO_EXCLUDE_DESCENDANTS: Int = 0x8

        @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        fun canScrollVertically(view: View, direction: Int): Boolean {
            return view.canScrollVertically(direction)
        }
    }
}
