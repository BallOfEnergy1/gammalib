
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

sourceSets {
    create("bench") {
        java.srcDir("src/bench/java")
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

configurations {
    getByName("benchImplementation").extendsFrom(getByName("implementation"))
    getByName("benchRuntimeOnly").extendsFrom(getByName("runtimeOnly"))
}

tasks.register<JavaExec>("runBenchmarks") {
    group = "verification"
    description = "Runs Spool micro-benchmarks."
    classpath = sourceSets["bench"].runtimeClasspath
    mainClass.set("com.gamma.spool.bench.BenchmarkMain")
}
