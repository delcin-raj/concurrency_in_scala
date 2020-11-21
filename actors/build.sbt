name := "actors"
version := "1.0"
organization := "com"

val akkaVersion = "2.6.9"
libraryDependencies ++= Seq (
	"com.typesafe.akka" %% "akka-actor" % akkaVersion
)
