
appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%-5level %-30logger{30} %msg%n"
  }
}

root(INFO, ["STDOUT"])
