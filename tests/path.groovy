static List<String> splitter(String folder) {
    List<String> ret = new ArrayList<>()
    int lastIndex = 0
    while (lastIndex < folder.length()) {
        int currentIndex = folder.indexOf("/", lastIndex + 1)
        if (currentIndex == -1) {
            currentIndex = folder.length()
        }
        ret.add(folder.substring(0, currentIndex))
        lastIndex = currentIndex
    }
    return ret
}

// Tests
def jobFolder = ""
def expected = []
assert splitter(jobFolder) == expected


jobFolder = "a"
expected = ["a"]
assert splitter(jobFolder) == expected


jobFolder = "a/b"
expected = ["a", "a/b"]
assert splitter(jobFolder) == expected


jobFolder = "a/b/c"
expected = ["a", "a/b", "a/b/c"]
assert splitter(jobFolder) == expected
