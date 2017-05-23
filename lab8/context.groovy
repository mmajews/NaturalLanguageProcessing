@Grab(group = 'com.google.guava', module = 'guava', version = '18.0')
import com.google.common.base.Preconditions

Preconditions.checkArgument(args.size() == 2, "Put two arguments! 1. lines range 2. word to search for")
def lines = args[0]
def toSearchFor = args[1]
def process = "grep -n -A $lines -B $lines '$toSearchFor' in/potop.txt".execute()
println(process.in.text)