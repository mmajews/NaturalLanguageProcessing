import java.nio.charset.Charset
import java.util.stream.Collectors

def inputFile = "out/potop.txt.ccl" as File
String NEW_LINE = "\n"
def parser = new XmlParser()
parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
def parsedXML = parser.parse(inputFile)
println("XML parsed!")

peopleAttributes = ["person_first_nam", "person_last_nam"]
locationAttributes = ["country_nam", "city_nam", "road_nam"]
def peopleOutputFile = "out/people.index" as File
def locationsOutputFile = "out/locations.index" as File
peopleOutputFile.delete()
locationsOutputFile.delete()
peopleOutputFile.createNewFile()
locationsOutputFile.createNewFile()

def allTokens = parsedXML.depthFirst().findAll({ it.name() == "tok" })
println("All tokens found!")

def allPeopleNames = []
def allLocationNames = []
allTokens.forEach({
    token ->
        def name = getTokenName(token)
        def values = token.ann
        values.forEach({
            ann ->
                def value = (ann as Node).value()[0]
                if (value as Integer == 1) {
                    def chan = (ann as Node).attribute("chan") as String
                    def sentenceId = token.parent().attribute("id")
                    def appendable = "$name | $sentenceId | $chan"
                    if (chan in locationAttributes) {
                        allLocationNames.add(name)
                        locationsOutputFile.append(appendable)
                        locationsOutputFile.append(NEW_LINE)
                    }

                    if (chan in peopleAttributes) {
                        allPeopleNames.add(name)
                        peopleOutputFile.append(appendable)
                        peopleOutputFile.append(NEW_LINE)
                    }
                }
        })
})


println(NEW_LINE)
def locationCounts = getCounts(allLocationNames)
def peopleCounts = getCounts(allPeopleNames)

println("Locations:")
printOutTop(10, locationCounts)
print(NEW_LINE * 2)

println("People:")
printOutTop(10, peopleCounts)

private static void printOutTop(int top, Map<?, Integer> input) {
    input.entrySet().stream()
            .sorted(Map.Entry.<?, Integer> comparingByValue().reversed())
            .limit(top)
            .forEach({
        el ->
            def out = (el.getKey() as String) + " " + (el.getValue() as String)
            println out
    })
}

private static Map<String, Integer> getCounts(input) {
    return input.stream().collect(Collectors.groupingBy({ el -> el }, Collectors.counting()));
}

private static String getTokenName(token) {
    token
            .getDirectChildren()
            .findAll({ it.name() == "lex" })[0]
            .getDirectChildren()
            .findAll { it.name() == "base" }[0]
            .getDirectChildren()[0]
}
