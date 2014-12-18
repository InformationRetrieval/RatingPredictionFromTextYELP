import nltk
import sys
from nltk.collocations import *

def main():
    f1 = open(sys.argv[1], 'r')
    
    raw = f1.read()
    f = open("output.txt", "w")

    f2 = open("adjectives.txt", 'r')
    words = f2.readlines()

    vocabs = []
    for word in words:
        #print word
        vocabs.append(word.rstrip().lower())  
    print "$$$$$"
    tokens = nltk.word_tokenize(raw)
    #Create your bigrams
    bgs = nltk.bigrams(tokens)

    print "######"
    #compute frequency distribution for all the bigrams in the text
    fdist = nltk.FreqDist(bgs)
    for k,v in fdist.items():
        #print k
        bigram = str(k)
        bigram = bigram.lower()
        #print k[0]
        #print k[1]
        #print bigram
        #print vocabs
        for word in vocabs:
            #print word
            #if bigram.find(word) != -1:
            if str(k[0]).lower() == word.lower() or str(k[1]).lower() == word.lower():
                temp = (bigram + '\n')
                #print "-----------",temp
                f.write(temp)

    f.close()
    f1.close()
    f2.close()
    
if __name__ == "__main__":
    main()
