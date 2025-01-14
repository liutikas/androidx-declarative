package org.example.utilities

import org.intellij.lang.annotations.Language

class KotlinLibrary {
    fun takeValue(@Language("kotlin") contents: String) {

    }

    fun differentMethod() {
        takeValue("""
            package foo
            class Bar
        """.trimIndent())
    }
}