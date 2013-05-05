Our DNA strand generator is written in python, and takes its arguments at the top of the source file.

It generates strands by first generating a number of centroids, and then it changes random bases in the centroids to create unique DNA strands.

Note that due to the anti-duplicate code, it is possible to have an infinite loop if your length/number of centroids is too small for the number of DNA strands you ask for.