plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.couponmanagementsystem.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
