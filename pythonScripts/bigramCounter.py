#__author__ = 'Umberto'

#nltk.download()

import nltk
from nltk.util import ngrams
from nltk.collocations import *
import numpy as np
import pandas
#=======================import data
data = pandas.read_csv('./../data/plus/PS50007.csv')

#====================replace strings with spaces between
for i in range(0,len(data)):
    #print(data.iloc[[i]]['Sequence'][i])
    tmp = data['Sequence'][i]
    data['Sequence'][i] = tmp.replace(""," ")[1:-1]

#=====================create string to extract all possible amino acid bigrams
#aminoAcids = 'G P A V L I M C F Y W H K R Q N E D S T'
aminoAcids = ['G','P','A','V','L','I','M','C','F','Y','W','H','K','R','Q','N','E','D','S','T']
#consider all the possible amoniacids
allCombAmino = ''
for i in range(0,len(aminoAcids)):
    for j in range(0,len(aminoAcids)):
        allCombAmino += aminoAcids[i]+aminoAcids[j]
#put spaces between amino
allCombAmino= allCombAmino.replace(""," ")[1:-1]
#=========================create the keys for a dictionary of all possible bigram aminoacids
amino=nltk.word_tokenize(allCombAmino)
biAmino=ngrams(amino,2)
fAmino = nltk.FreqDist(biAmino)
myDict={}
for k,v in fAmino.items():
    myDict[k]=0

#==========================set up dataframe to store sequences encoding
# index = data['Entry']
# columns = myDict.keys()
# emptyData = np.zeros((len(data),len(myDict.keys())))
# df = pandas.DataFrame(emptyData,index=index, columns=columns)
df = pandas.DataFrame()
#===========================calculate relative frequencies for all sequnces and put them in dataframe
#copy dictonary for this string
for i in range(0,len(data)):
    tmpDict = myDict.copy()
    text = data['Sequence'][i]
    token=nltk.word_tokenize(text)
    bigrams=ngrams(token,2)
    fdist = nltk.FreqDist(bigrams)
    for k,v in fdist.items():
        #print (k,v)
        tmpDict[k]=v/data['Length'][i] * 100 #everything is mulitplied by 100 so that numbers are not too low
    df=df.append(pandas.Series(tmpDict),ignore_index=True)

#add class to dataframe
#save dataframe
#repeat for all dataframe