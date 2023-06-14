/*
 * This file was generated by the Gradle "init" task.
 *
 * This generated file contains a sample Java Library project to get you started.
 * For more details take a look at the Java Libraries chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.3/userguide/java_library_plugin.html
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

plugins {
	kotlin("jvm")
	application
	id("com.github.johnrengelman.shadow")
	id("com.github.gmazzo.buildconfig")
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}
tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "17"
}

// Set compiler to use UTF-8
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}
tasks.withType<Test> {
	systemProperty("file.encoding", "UTF-8")
}
tasks.withType<Javadoc> {
	options.encoding = "UTF-8"
}

tasks
	.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
	.configureEach {
		compilerOptions
			.languageVersion
			.set(
				org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9
			)
	}

allprojects {
	repositories {
		// Use jcenter for resolving dependencies.
		// You can declare any Maven/Ivy/file repository here.
		mavenCentral()
	}
}

dependencies {
	implementation(project(":server:core"))

	implementation("commons-cli:commons-cli:1.5.0")
	implementation("org.apache.commons:commons-lang3:3.12.0")
}


tasks.shadowJar {
	minimize {
		exclude(dependency("com.fazecast:jSerialComm:.*"))
		exclude(dependency("net.java.dev.jna:.*:.*"))
		exclude(dependency("com.google.flatbuffers:flatbuffers-java:.*"))

		exclude(project(":solarxr-protocol"))
	}
	archiveBaseName.set("slimevr")
	archiveClassifier.set("")
	archiveVersion.set("")
}
application {
	mainClass.set("dev.slimevr.desktop.Main")
}

fun String.runCommand(currentWorkingDir: File = file("./")): String {
	val byteOut = ByteArrayOutputStream()
	project.exec {
		workingDir = currentWorkingDir
		commandLine = this@runCommand.split("\\s".toRegex())
		standardOutput = byteOut
	}
	return String(byteOut.toByteArray()).trim()
}

buildConfig {
	val gitCommitHash = "git rev-parse --verify --short HEAD".runCommand().trim()
	val gitVersionTag = "git --no-pager tag --points-at HEAD".runCommand().trim()
	val gitClean = "git status --porcelain".runCommand().trim().isEmpty()
	useKotlinOutput { topLevelConstants = true }
	packageName("dev.slimevr.desktop")

	buildConfigField("String", "GIT_COMMIT_HASH", "\"${gitCommitHash}\"")
	buildConfigField("String", "GIT_VERSION_TAG", "\"${gitVersionTag}\"")
	buildConfigField("boolean", "GIT_CLEAN", gitClean.toString())
}

tasks.getByName("run", JavaExec::class) {
	standardInput = System.`in`
	args = listOf("run")
}
