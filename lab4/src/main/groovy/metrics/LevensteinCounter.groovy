package metrics

class LevensteinCounter {
    private List<List<Double>> distances
    public static final int POLISH_METRIC = 3

    //swap with the weight 0.5
    //spelling mistake polish 0.25

    private static Map<String, List<String>> mistakesMap = new HashMap<>()

    static {
        mistakesMap.put("a", ["ą"])
        mistakesMap.put("ą", ["om", "on"])
        mistakesMap.put("e", ["ę"])
        mistakesMap.put("ę", ["en"])
        mistakesMap.put("h", ["ch"])
        mistakesMap.put("u", ["ó"])
        mistakesMap.put("o", ["ó"])
        mistakesMap.put("z", ["ż", "ź", "sz", "rz"])
        mistakesMap.put("ż", ["rz", "ź"])
        mistakesMap.put("rz", ["sz"])
        mistakesMap.put("s", ["ś"])
        mistakesMap.put("l", ["ł"])
        mistakesMap.put("c", ["ć"])
        mistakesMap.put("n", ["ń"])
        mistakesMap.put("dz", ["dż", "dź"])
    }

    double getLD(String a, String b, double maximum) {
        def aLength = a.length()
        def bLength = b.length()
        if (Math.abs(aLength - bLength) > maximum) {
            return Double.MAX_VALUE
        }

        a = a.toLowerCase()
        b = b.toLowerCase()
        int metric = POLISH_METRIC
        if ((a == null && b == null))
            return 0
        if (a == null || aLength == 0)
            return bLength
        if (b == null || bLength == 0)
            return aLength
        initDistances(metric, aLength)
        int layersDone = 2


        for (def i = 0; i < bLength; i++) {
            for (def j = 0; j < aLength; j++) {
                def left = distances.get(layersDone - 1).get(j) + 1
                def up = distances.get(layersDone - 2).get(j + 1) + 1
                def symbolDiff = getDiff(a.substring(j, j + 1), b.substring(i, i + 1))
                def diag = distances.get(layersDone - 2).get(j) + symbolDiff
                def min1 = [left, up, diag].min()

                left = distances.get(layersDone - 1).get([j - 1, 0].max()) + (j - 1 >= 0 ? 2 : 1)
                up = distances.get([layersDone - 3, 0].max()).get(j + 1) + (i - 1 >= 0 ? 2 : 1)
                def leftUp = distances.get(layersDone - 2).get([j - 1, 0].max()) + getDiff(a.substring([j - 1, 0].max(), j + 1),
                        b.substring(i, i + 1))

                def upLeft = distances.get([layersDone - 3, 0].max()).get(j) + getDiff(a.substring(j, j + 1),
                        b.substring([i - 1, 0].max(), i + 1))

                symbolDiff = getDiff(a.substring([j - 1, 0].max(), j + 1), b.substring([i - 1, 0].max(), i + 1))
                diag = distances.get([layersDone - 3, 0].max()).get([j - 1, 0].max()) + symbolDiff

                def min2 = [leftUp, upLeft, up, diag, left].min()

                distances.get(layersDone - 1).set(j + 1, [min1, min2].min())
            }
            if (layersDone == metric)
                prepBeforeNextLayer(aLength, i + 2)
            layersDone = metric
        }

        return distances.get(1).get(aLength)
    }

    private void initDistances(int layers, int length) {
        distances = new LinkedList<>()
        for (def i = 0; i < layers; i++) {
            distances.add(new ArrayList<>(length + 1))
        }
        for (def i = 0; i <= length; i++) {
            distances.get(0).add((double) i)
        }
        for (def i = 1; i < layers; i++) {
            distances.get(i).add((double) i)
            for (int j = 1; j <= length; j++) {
                distances.get(i).add(0d)
            }
        }
    }


    private void prepBeforeNextLayer(int length, int layer) {
        //cutting first layer and adding new line under
        distances.remove(0)
        def toAdd = new ArrayList<>(length)
        toAdd.add((double) layer)
        for (int i = 0; i < length; i++)
            toAdd.add(0d)
        distances.add(toAdd)
    }

    private static double getDiff(String a, String b) {
        def len2 = (a.length() == 2 && b.length() == 2)
        if (a == b)
            return 0d
        if (len2 && isOrtMistakeBetween2TwoLetterWords(a, b))
            return 0.25d
        if (isOrtMistakeBetween2Words(a, b) > 0)
            return len2 ? 1.25d : isOrtMistakeBetween2Words(a, b)
        if (isSwapMistake(a, b))
            return 0.5d
        return len2 ? 2d : (a.charAt(a.length() - 1) == b.charAt(b.length() - 1) || a.length() * b.length() == 1) ? 1 : 2
    }

    private static double isOrtMistakeBetween2Words(String a, String b) {
        def aList = mistakesMap.get(String.valueOf(a.charAt(a.length() - 1)))
        if (aList != null && aList.contains(b))
            return (a.length() < 2 || (mistakesMap.get(b) != null && mistakesMap.get(b).contains(a))) ? 0.25d : 1.25d
        def bList = mistakesMap.get(String.valueOf(b.charAt(b.length() - 1)))
        if (bList != null && bList.contains(a))
            return (b.length() < 2 || (mistakesMap.get(a) != null && mistakesMap.get(a).contains(b))) ? 0.25d : 1.25d
        return -1d
    }

    private static boolean isOrtMistakeBetween2TwoLetterWords(String a, String b) {
        def a1List = mistakesMap.get(a)
        if (a1List != null && a1List.contains(b))
            return true
        def b1List = mistakesMap.get(b)
        if (b1List != null && b1List.contains(a))
            return true
        return false
    }

    private static boolean isSwapMistake(String a, String b) {
        return a == b.reverse()
    }

}
