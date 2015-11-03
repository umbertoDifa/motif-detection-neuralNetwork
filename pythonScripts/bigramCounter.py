#__author__ = 'Umberto'

#nltk.download()

import nltk
from nltk.util import ngrams
from nltk.collocations import *

import csv

a=numpy.array([['mannia','utto'],['dio','can']])
import pandas
data_df = pandas.read_csv('./../data/plus/PS50007.csv')
print(data_df.columns)

with open('./../data/plus/PS50007.csv', 'rt') as f:
    reader = csv.reader(f)
    rownum = 0
    for row in reader:
        # Save header row.
        if rownum == 0:
            header = row
        else:
            #colnum = 0
            for col in row:
                #print '%-8s: %s' % (header[colnum], col)
                print (col)
            #colnum += 1

        rownum += 1
        #print (row)

text = "A C A C T G A C S A C T"
token=nltk.word_tokenize(text)
bigrams=ngrams(token,2)
# for a in bigrams:
#     print(a)
fdist = nltk.FreqDist(bigrams)
for k,v in fdist.items():
    print (k,v)

import numpy
a=numpy.loadtxt(open("./../data/plus/PS50007.csv","rt"),delimiter=",",skiprows=1)

reader=csv.reader(open("./../data/plus/PS50007.csv","rt"),delimiter=',')
x=list(reader)
result=numpy.array(x).astype('string')

import StringIO
numpy.genfromtxt(StringIO(data), delimiter=",", dtype="|S5")
data = numpy.genfromtxt('./../data/plus/PS50007.csv', dtype=None, delimiter=',', names=True)
