// Root: shared settings only. Problem code lives in subprojects under each folder.

allprojects {
    group = "com.saurav.lld"
    version = "unspecified"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(23)
    }
}
