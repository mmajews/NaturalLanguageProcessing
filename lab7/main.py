import logging
import re
import string
from pathlib import Path

logging.basicConfig(format='%(levelname)s: %(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S', level=logging.INFO)
PERCENTAGE_OF_WORD_ELIMINATION = 0.7
NUMBER_OF_ALL_DOCUMENTS = 51572
ELIMINATION_THRESHOLD = NUMBER_OF_ALL_DOCUMENTS * PERCENTAGE_OF_WORD_ELIMINATION

logging.info("Program started!")
# Slicing input to documents
file_path = "pap.txt"
contents = Path(file_path).read_text(encoding="UTF-8")
allNotes = re.compile("#\d+").split(contents)
allNotes.pop(0)
for x in range(0, len(allNotes)):
    currentNote = allNotes[x]
    currentNote = currentNote.upper().strip()
    allNotes[x] = currentNote

# Creating dictionary to make words to default form
dictionary_path = "polimorfologik-2.1.txt"
dictionaryContent = Path(dictionary_path).read_text(encoding="UTF-8")
allEntries = re.compile("\n").split(dictionaryContent)
defaultForm = {}
for x in range(0, len(allEntries)):
    currentEntry = allEntries[x]
    slicedEntry = re.compile(";").split(currentEntry)
    mainForm = slicedEntry[0]
    deviation = slicedEntry[1]
    defaultForm[deviation.upper()] = mainForm.upper()
logging.info("Dictionary created!")

# Make all notes to default form
for x in range(0, len(allNotes)):
    currentNote = allNotes[x]
    slicedToWords = re.compile("\s+").split(currentNote)
    for i in range(0, len(slicedToWords)):
        oneWord = slicedToWords[i].translate(str.maketrans('', '', string.punctuation))
        toReplace = defaultForm.get(oneWord)
        if toReplace is None:
            toReplace = oneWord
        slicedToWords[i] = toReplace
    allNotes[x] = " ".join(slicedToWords)
logging.info("All notes converted to default form!")

# Eliminate words that appear in more than 70% documents
numberOfAllWords = 0
documentOccurrences = {}
for x in range(0, len(allNotes)):
    currentNote = allNotes[x]
    slicedToWords = re.compile("\s+").split(currentNote)
    setOfWords = set(slicedToWords)
    for i in range(0, len(setOfWords)):
        word = slicedToWords[i]
        numberOfOccurrences = documentOccurrences.get(word)
        if numberOfOccurrences is None:
            documentOccurrences[word] = 1
        else:
            documentOccurrences[word] = numberOfOccurrences + 1
wordsToBeEliminated = set()
for k, v in documentOccurrences.items():
    if v > ELIMINATION_THRESHOLD or v == 1:
        wordsToBeEliminated.add(k)
logging.info("List of words to be eliminated created!")

# Iterate through all notes and eliminate words
for x in range(0, len(allNotes)):
    currentNote = allNotes[x]
    slicedToWords = re.compile("\s+").split(currentNote)
    newSlicedWords = []
    for i in range(0, len(slicedToWords)):
        word = slicedToWords[i]
        if word not in wordsToBeEliminated:
            newSlicedWords.append(word)
    allNotes[x] = " ".join(newSlicedWords)
logging.info("All notes cleaned from elements to be eliminated!")
