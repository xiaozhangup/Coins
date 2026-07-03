plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.justeli.coins"
version = "1.16.2"
description = "Coins is a plugin that allows players to collect coins for killing mobs and mining precious blocks. It also comes with the ability to withdraw balance into physical coins."

val minecraftVersion = "1.21"
val foliaVersion = "1.21.4"
val adventurePlatformBukkitVersion = "4.4.1"
val adventureVersion = "4.20.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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
    compileOnly("org.spigotmc:spigot-api:$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
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
    filesMatching("plugin.yml") {
        expand(
            "project" to mapOf(
                "groupId" to project.group.toString(),
                "name" to project.name,
                "version" to project.version.toString(),
                "url" to "https://modrinth.com/plugin/coinsplugin",
                "description" to project.description,
            ),
            "versions" to mapOf(
                "minecraft" to minecraftVersion,
                "adventure-platform-bukkit" to adventurePlatformBukkitVersion,
                "adventure" to adventureVersion,
            ),
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
