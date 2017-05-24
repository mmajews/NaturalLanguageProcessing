@Grab(group = 'com.google.guava', module = 'guava', version = '18.0')
import com.google.common.base.Preconditions

Preconditions.checkArgument(args.size() == 2, "Put two arguments! 1. lines range 2. word to search for")
def lines = args[0]
def toSearchFor = args[1]
println "Number of lines: " + lines
println "Search for: " + toSearchFor
def sout = new StringBuilder()
def serr = new StringBuilder()
def command = "bash execute.sh $lines $toSearchFor"
println command
def process = command.execute()
//def process = "ls -l".execute()
process.consumeProcessOutput(sout, serr)
process.waitFor()
println serr
println sout
println "Exit process value: " + process.exitValue()
