import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
    id("org.jmailen.kotlinter")
    id("com.google.devtools.ksp")
    id("app.cash.licensee")
}

apply(plugin = "com.android.compose.screenshot")

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "ca.amandeep.nycairportsecuritylinewaits"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    flavorDimensions += "tier"
    productFlavors {
        create("full") {
            dimension = "tier"
        }
        create("slim") {
            dimension = "tier"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "ca.amandeep.nycairportsecuritylinewaits"
}

androidComponents {
    onVariants(selector().all()) { variant ->
        if (variant.productFlavors.any { it.second == "slim" }) {
            variant.androidResources.localeFilters.addAll(listOf("en"))
            variant.androidResources.aaptAdditionalParameters.addAll(
                listOf("--preferred-density", "xxxhdpi")
            )
        }
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        val composeCompilerDir = layout.buildDirectory.dir("compose_compiler").get().asFile.absolutePath
        jvmTarget = JvmTarget.JVM_21
        freeCompilerArgs.addAll(
            listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$composeCompilerDir",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$composeCompilerDir"
            )
        )
    }
}

dependencies {
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.constraintLayout.compose)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.material3.windowSizeClass)
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.core.splashscreen)
    implementation(AndroidX.lifecycle.runtime.compose)
    implementation(AndroidX.lifecycle.runtime.ktx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.navigation.compose)
    implementation(KotlinX.collections.immutable)
    implementation(AndroidX.compose.material.icons.extended)

    implementation(Square.moshi.adapters)
    implementation(Square.moshi)
    ksp(Square.moshi.kotlinCodegen)
    implementation(Square.retrofit2.converter.moshi)
    implementation(Square.retrofit2)

    implementation("com.github.ajalt:timberkt:_")
    implementation(JakeWharton.timber)

    implementation(AndroidX.compose.ui.tooling)
    debugImplementation(AndroidX.compose.ui.toolingPreview)
    add("screenshotTestImplementation", AndroidX.compose.ui.tooling)
    add("screenshotTestImplementation", "com.android.tools.screenshot:screenshot-validation-api:_")

    testImplementation("junit:junit:_")
}

licensee {
    allow("Apache-2.0")
    allowUrl("https://developer.android.com/studio/terms.html") {
        because("Android Software Development Kit License Agreement")
    }
}

tasks.withType<Test>().configureEach {
    android.sourceSets["main"].res.srcDir("src/test/res")
    if (name.contains("ScreenshotTest")) {
        setScanForTestClasses(true)
        doFirst {
            setScanForTestClasses(true)
        }
    }
}
