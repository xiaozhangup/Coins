import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "9.4.3"
}

group = "me.justeli.coins"
version = "1.16.2"
description = "Coins is a plugin that allows players to collect coins for killing mobs and mining precious blocks. It also comes with the ability to withdraw balance into physical coins."

val minecraftVersion = "1.21"
val foliaVersion = "1.21.4"
val adventurePlatformBukkitVersion = "4.4.1"
val adventureVersion = "4.20.0"
val resourceTokens = mapOf(
    "project.groupId" to group.toString(),
    "project.name" to name,
    "project.version" to version.toString(),
    "project.url" to "https://modrinth.com/plugin/coinsplugin",
    "project.description" to description,
    "versions.minecraft" to minecraftVersion,
    "versions.adventure-platform-bukkit" to adventurePlatformBukkitVersion,
    "versions.adventure" to adventureVersion,
)

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("dev.folia:folia-api:$foliaVersion-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("io.lumine:Mythic-Dist:5.11.2")

    implementation("org.bstats:bstats-bukkit:3.2.1")

    compileOnly("net.kyori:adventure-platform-bukkit:$adventurePlatformBukkitVersion")
    compileOnly("net.kyori:adventure-text-minimessage:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-plain:$adventureVersion")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching(listOf("plugin.yml", "paper-plugin.yml", "config.yml")) {
        filter<ReplaceTokens>(
            "tokens" to resourceTokens,
            "beginToken" to "\${",
            "endToken" to "}",
        )
    }
}

tasks.shadowJar {
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
    relocate("org.bstats", "me.justeli.coins.org.bstats")
    exclude("META-INF/**")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
