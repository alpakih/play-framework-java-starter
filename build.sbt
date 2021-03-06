name := "play-framework-java-starter"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  javaWs,
  evolutions,
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "commons-io" % "commons-io" % "2.6",
  "org.xhtmlrenderer" % "flying-saucer-pdf" % "9.1.1",
  "nu.validator.htmlparser" % "htmlparser" % "1.4"
  
)
resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Apache Repository" at "https://repository.apache.org/content/repositories/releases/",
  Resolver.sonatypeRepo("public"),
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases")
)


