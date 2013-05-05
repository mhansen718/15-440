import os
import random

# arguments
numStrands = 10000
numCentroids = 20
strandLen = 200
output = "DNAStrands.txt"

def writeFile(filename,text):
    fileHandler = open(filename, "w")
    fileHandler.write(text)
    fileHandler.close()

def stringDist(s1,s2):
    dist = 0
    for i in xrange(len(s1)):
        if s1[i] != s2[i]:
            dist += 1
    return dist
    
def generateDNA(n,c,s):
    text = ""
    diff = 15
    var = 3
    i = 0
    written = []
    centroids = []
    
    while i < c:
        strand = ""
        for j in xrange(s):
            strand += "ACGT"[random.randint(0,3)]
            
        okay = True
        for cent in centroids:
            if stringDist(strand,cent) < 30:
                okay = False
                break
                
        if okay:
            centroids.append(strand)
            i += 1
    
    i = 0
    while i < n:
        change = random.randint(diff-var,diff+var)
        cent = random.choice(centroids)
        strand = cent
        changed = []
        j = 0
        while j < change:
            p = random.randint(0,s-1)
            while p in changed:
                p = random.randint(0,s-1)
            
            pre = cent[p]
            letter = "ACGT"[random.randint(0,3)]
            while letter == pre:
                letter = "ACGT"[random.randint(0,3)]
            
            strand = strand[:p] + letter + strand[p+1:]
            changed.append(p)
            j += 1
        
        okay = True
        for dna in written:
            if strand == dna:
                okay = False
                break
        
        if okay:
            text += strand + "\n"
            written.append(strand)
            i += 1
    return text
    
writeFile(output,generateDNA(numStrands,numCentroids,strandLen))