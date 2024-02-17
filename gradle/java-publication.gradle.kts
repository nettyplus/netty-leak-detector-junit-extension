group = "io.github.nettyplus"

plugins.withId("java") {
    //Sources/javadoc artifacts required by Maven module publications
    val licenseSpec = copySpec {
        from(project.rootDir) {
            include("LICENSE")
        }
    }

    tasks.register<Jar>("sourcesJar") {
        from(project.the<SourceSetContainer>()["main"].allJava)
        archiveClassifier.set("sources")
        with(licenseSpec)
    }

    tasks.register<Jar>("javadocJar") {
        from(tasks.named<Javadoc>("javadoc"))
        archiveClassifier.set("javadoc")
        with(licenseSpec)
    }

    tasks.named<Jar>("jar") {
        with(licenseSpec)
    }
}


tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

//Gradle Maven publishing plugin configuration (https://docs.gradle.org/current/userguide/publishing_maven.html)
apply(plugin="maven-publish")
apply(plugin="signing")

configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                plugins.withId("java") {
                    from(components["java"])
                    artifact(tasks.named("sourcesJar"))
                    artifact(tasks.named("javadocJar"))
                }
                plugins.withId("java-platform") {
                    from(components["java-platform"])
                }

                afterEvaluate {
                    artifactId = "netty-leak-detector-junit-extension" //  tasks.named<Jar>("jar").archiveBaseName.get()
                }

                pom {
                    name = artifactId
                    description = "netty-leak-detector-junit-extension"

                    plugins.withId("java") {
                        //Gradle does not write "jar" packaging to the pom (unlike other packaging types).
                        //This is OK because "jar" is implicit/default:
                        // https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#minimal-pom
                        packaging = "jar"
                    }

                    url = "https://github.com/nettyplus/netty-leak-detector-junit-extension"
                    licenses {
                        license {
                            name = "Apache License version 2.0"
                            url = "https://github.com/nettyplus/netty-leak-detector-junit-extension/blob/main/LICENSE"
                            distribution = "repo"
                        }
                    }
                    developers {
                            developer {
                                id.set("sullis")
                                name.set("Sean C. Sullivan")
                                url.set("https://github.com/sullis")
                            }
                    }
                    scm {
                        url = "https://github.com/nettyplus/netty-leak-detector-junit-extension.git"
                    }
                    issueManagement {
                        url = "https://github.com/nettyplus/netty-leak-detector-junit-extension/issues"
                        system = "GitHub issues"
                    }
                    ciManagement {
                        url = "https://github.com/nettyplus/netty-leak-detector-junit-extension/actions"
                        system = "GH Actions"
                    }
                }
            }
        }

}

plugins.withId("java") {
//fleshes out problems with Maven pom generation when building
//    tasks.build.dependsOn("publishJavaLibraryPublicationToMavenLocal")
}

configure<SigningExtension> {
    if (System.getenv("PGP_KEY") != null) {
        println("yes, pgp ok")
        useInMemoryPgpKeys(System.getenv("PGP_KEY"), System.getenv("PGP_PWD"))
        sign(the<PublishingExtension>().publications["mavenJava"])
    }
}
