package metrics

class LDCounter {
    private List<List<Double>> distances
    public static final int POLISH_METRIC = 3

    //swap with the weight 0.5
    //spelling mistake polish 0.25

    private static Map<String, List<String>> mistakesMap = new HashMap<>()

    static {
        List<String> aM = new ArrayList<>();
        aM.add("ą");
        mistakesMap.put("a", aM);
        List<String> a1M = new ArrayList<>();
        a1M.add("om");
        a1M.add("on");
        mistakesMap.put("ą", a1M);
        List<String> eM = new ArrayList<>();
        eM.add("ę");
        mistakesMap.put("e", eM);
        List<String> e1M = new ArrayList<>();
        e1M.add("en");
        mistakesMap.put("ę", e1M);
        List<String> hM = new ArrayList<>();
        hM.add("ch");
        mistakesMap.put("h", hM);
        List<String> uM = new ArrayList<>();
        uM.add("ó");
        mistakesMap.put("u", uM);
        List<String> oM = new ArrayList<>();
        oM.add("ó");
        mistakesMap.put("o", oM);
        List<String> zM = new ArrayList<>();
        zM.add("ż");
        zM.add("ź");
        zM.add("sz");
        zM.add("rz");
        mistakesMap.put("z", zM);
        List<String> z1M = new ArrayList<>();
        z1M.add("rz");
        z1M.add("ź");
        mistakesMap.put("ż", z1M);
        List<String> rzM = new ArrayList<>();
        rzM.add("sz");
        mistakesMap.put("rz", rzM);
        List<String> sM = new ArrayList<>();
        sM.add("ś");
        mistakesMap.put("s", sM);
        List<String> lM = new ArrayList<>();
        lM.add("ł");
        mistakesMap.put("l", lM);
        List<String> cM = new ArrayList<>();
        cM.add("ć");
        mistakesMap.put("c", cM);
        List<String> nM = new ArrayList<>();
        nM.add("ń");
        mistakesMap.put("n", nM);
        List<String> dzM = new ArrayList<>();
        dzM.add("dż");
        dzM.add("dź");
        mistakesMap.put("dz", dzM);
    }

    double getLD(String a, String b) {
        int metric = POLISH_METRIC
        if ((a == null && b == null))
            return 0;
        if (a == null || a.length() == 0)
            return b.length();
        if (b == null || b.length() == 0)
            return a.length();
        initDistances(metric, a.length());
        int layersDone = 2;

        for (int i = 0; i < b.length(); i++) {
            for (int j = 0; j < a.length(); j++) {
                double left = distances.get(layersDone - 1).get(j) + 1;
                double up = distances.get(layersDone - 2).get(j + 1) + 1;
                double symbolDiff = getDiff(a.substring(j, j + 1), b.substring(i, i + 1));
                double diag = distances.get(layersDone - 2).get(j) + symbolDiff;
                double min1 = Math.min(left, Math.min(up, diag));

                left = distances.get(layersDone - 1).get(Math.max(j - 1, 0)) + (j - 1 >= 0 ? 2 : 1);
                up = distances.get(Math.max(layersDone - 3, 0)).get(j + 1) + (i - 1 >= 0 ? 2 : 1);
                double leftUp =
                        distances.get(layersDone - 2).get(Math.max(j - 1, 0)) + getDiff(a.substring(Math.max(j - 1, 0), j + 1),
                                b.substring(i, i + 1));
                double upLeft = distances.get(Math.max(layersDone - 3, 0)).get(j) + getDiff(a.substring(j, j + 1),
                        b.substring(Math.max(i - 1, 0), i + 1));
                symbolDiff = getDiff(a.substring(Math.max(j - 1, 0), j + 1), b.substring(Math.max(i - 1, 0), i + 1));
                diag = distances.get(Math.max(layersDone - 3, 0)).get(Math.max(j - 1, 0)) + symbolDiff;
                double min2 = Math.min((Math.min(Math.min(leftUp, upLeft), Math.min(up, diag))), left);

                distances.get(layersDone - 1).set(j + 1, Math.min(min1, min2));
            }
            if (layersDone == metric)
                prepBeforeNextLayer(a.length(), i + 2);
            layersDone = metric;
        }

        return distances.get(1).get(a.length());
    }

    private void initDistances(int layers, int length) {
        distances = new LinkedList<>();
        for (int i = 0; i < layers; i++) {
            distances.add(new ArrayList<>(length + 1));
        }
        for (int i = 0; i <= length; i++) {
            distances.get(0).add((double) i);
        }
        for (int i = 1; i < layers; i++) {
            distances.get(i).add((double) i);
            for (int j = 1; j <= length; j++) {
                distances.get(i).add(0d);
            }
        }
    }


    private void prepBeforeNextLayer(int length, int layer) {
        distances.remove(0);
        List<Double> toAdd = new ArrayList<>(length);
        toAdd.add((double) layer);
        for (int i = 0; i < length; i++)
            toAdd.add(0d);
        distances.add(toAdd);
    }

    private double getDiff(String a, String b) {
        boolean len2 = (a.length() == 2 && b.length() == 2);
        if (a.equals(b))
            return 0d;
        if (len2 && isOrtMistakeBetween2TwoLetterWords(a, b))
            return 0.25d;
        if (isOrtMistakeBetween2Words(a, b) > 0)
            return len2 ? 1.25d : isOrtMistakeBetween2Words(a, b);
        if (isSwapMistake(a, b))
            return 0.5d;
        return len2 ? 2d : (a.charAt(a.length() - 1) == b.charAt(b.length() - 1) || a.length() * b.length() == 1) ? 1 : 2;
    }

    private double isOrtMistakeBetween2Words(String a, String b) {
        List<String> aList = mistakesMap.get(String.valueOf(a.charAt(a.length() - 1)));
        if (aList != null && aList.contains(b))
            return (a.length() < 2 || (mistakesMap.get(b) != null && mistakesMap.get(b).contains(a))) ? 0.25d : 1.25d;
        List<String> bList = mistakesMap.get(String.valueOf(b.charAt(b.length() - 1)));
        if (bList != null && bList.contains(a))
            return (b.length() < 2 || (mistakesMap.get(a) != null && mistakesMap.get(a).contains(b))) ? 0.25d : 1.25d;
        return -1d;
    }

    private boolean isOrtMistakeBetween2TwoLetterWords(String a, String b) {
        List<String> a1List = mistakesMap.get(a);
        if (a1List != null && a1List.contains(b))
            return true;
        List<String> b1List = mistakesMap.get(b);
        if (b1List != null && b1List.contains(a))
            return true;
        return false;
    }

    private boolean isSwapMistake(String a, String b) {
        return a.equals(new StringBuilder(b).reverse().toString());
    }

}
