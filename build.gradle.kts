// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:8.11.1" // ¡CORREGIDA LA VERSIÓN DE AGP!
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0"
    }
}

plugins {
    id("com.android.application") version "8.11.1" apply false // ¡CORREGIDA LA VERSIÓN DE AGP!
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
