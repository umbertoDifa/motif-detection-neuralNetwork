#__author__ = 'Umberto'

#nltk.download()
from networkx.algorithms.traversal.depth_first_search import dfs_labeled_edges

import nltk
from nltk.util import ngrams
from nltk.collocations import *
import numpy as np
import pandas
import glob

#===============================find all the csv in the data/plus folder
csvFiles = []
for files in glob.glob("./../data/plus/*.csv"):
    csvFiles.append(files)
#==================================import the csv found in the folder
#data is the list of family for which the sequences where collelcted
data=[]
for file in csvFiles:
    tmpData = pandas.read_csv(file)
    #accept only files with more than 600 sequences
    if(len(tmpData)>=600):
        data.append(tmpData)

#====================replace strings with spaces between
#disable warning for cìmodifying dataframe with a copy
pandas.options.mode.chained_assignment = None  # default='warn'
for d in data:
    for i in range(0,len(d)):
        #print(data.iloc[[i]]['Sequence'][i])
        tmp = d['Sequence'][i]
        d['Sequence'][i] = tmp.replace(""," ")[1:-1]
#reenable the werning
pandas.options.mode.chained_assignment = 'warn'  # default='warn'


#=====================create string to extract all possible amino acid bigrams
#aminoAcids = 'G P A V L I M C F Y W H K R Q N E D S T'
#NB. adding 'X' for unknown amino acid and O,U,B,Z,J. B and Z are used when uncertain about aminoacides
aminoAcids = ['G','P','A','V','L','I','M','C','F','Y','W','H','K','R','Q','N','E','D','S','T',    'X','B','Z']
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


#==set up dataframe to store sequences encoding
df = pandas.DataFrame()

#===========================calculate relative frequencies for all sequnces and put them in dataframe
n=0
for d in data:
    print(n)
    n+=1
    for i in range(0,len(d)):
        #copy dictonary for this string
        tmpDict = myDict.copy()
        text = d['Sequence'][i]
        token=nltk.word_tokenize(text)
        bigrams=ngrams(token,2)
        fdist = nltk.FreqDist(bigrams)
        for k,v in fdist.items():
            #print (k,v)
            tmpDict[k]=v/d['Length'][i] * 100 #everything is mulitplied by 100 so that numbers are not too low
        df=df.append(pandas.Series(tmpDict),ignore_index=True)

#df.to_csv('trainingData.csv',header=False, index_label=False,index=False)

#==========create class labels
dfLabels = pandas.DataFrame();
for i in range(600):
    dfLabels=dfLabels.append(pandas.Series([0,0,0,1]),ignore_index=True)
for i in range(600):
    dfLabels=dfLabels.append(pandas.Series([0,0,1,0]),ignore_index=True)
for i in range(600):
    dfLabels=dfLabels.append(pandas.Series([0,1,0,0]),ignore_index=True)
for i in range(600):
    dfLabels=dfLabels.append(pandas.Series([1,0,0,0]),ignore_index=True)

#==================add target class su training data
dfPlusClasses = pandas.concat([df, dfLabels], axis=1)

#==================shuffle rows so that the data for a certain class is not sequential
shuffledDf = dfPlusClasses.iloc[np.random.permutation(len(dfPlusClasses))]

#=================slit the shuffled dataframe between trainin data and labels
shuffledData = shuffledDf.ix[:,0:529]
shuffledLabels = shuffledDf.ix[:,529:]
#reset indeces of the dataframes
shuffledData = shuffledData.reset_index(drop=True)
shuffledLabels = shuffledLabels.reset_index(drop=True)

#take out the test data
testData = shuffledData.ix[0:round(0.10 * len(shuffledData)),:]
testLabels = shuffledLabels.ix[0:round(0.10 * len(shuffledLabels)),:]
testData.to_csv("testData.csv",header=False, index_label=False,index=False)
testLabels.to_csv("testLabels.csv",header=False, index_label=False,index=False)

#remove test data from train data
shuffledData = shuffledData.ix[round(0.10 * len(shuffledData)):,:]
shuffledLabels = shuffledLabels.ix[round(0.10 * len(shuffledLabels)):,:]
#reset indeces of the dataframes
shuffledData = shuffledData.reset_index(drop=True)
shuffledLabels = shuffledLabels.reset_index(drop=True)

#create all division of dataset in %
for i in range(1,11):
    perc  =0.10*i * len(shuffledData)
    #partition
    tmpShuffledData = shuffledData.ix[0:round(perc),:]
    tmpShuffledLabels = shuffledLabels.ix[0:round(perc),:]
    #save partition
    fileNameData = 'trainingData%d.csv' % (i*10)
    fileNameLabels  = 'trainingLabels%d.csv' % (i*10)
    tmpShuffledData.to_csv(fileNameData,header=False, index_label=False,index=False)
    tmpShuffledLabels.to_csv(fileNameLabels,header=False, index_label=False,index=False)

#====================save files
shuffledData.to_csv('trainingData.csv',header=False, index_label=False,index=False)
shuffledLabels.to_csv('trainingLabels.csv',header=False, index_label=False,index=False)







#======================inutile per ora
#================================================add target classes to dataframe
classes=[]
count=1
for d in data:
    classes = classes + [count for x in range(len(d))]
    count +=1

classes=['0' for x in range(600)]
classes+=['0' for x in range(600)]
classes+=['0' for x in range(600)]
classes+=['1' for x in range(600)]

dfPlusClasses = pandas.concat([df, pandas.DataFrame(classes)], axis=1)
classes=['0' for x in range(600)]
classes+=['0' for x in range(600)]
classes+=['1' for x in range(600)]
classes+=['0' for x in range(600)]

dfPlusClasses = pandas.concat([dfPlusClasses, pandas.DataFrame(classes)], axis=1)


classes=['0' for x in range(600)]
classes+=['1' for x in range(600)]
classes+=['0' for x in range(600)]
classes+=['0' for x in range(600)]

dfPlusClasses = pandas.concat([dfPlusClasses, pandas.DataFrame(classes)], axis=1)


classes=['1' for x in range(600)]
classes+=['0' for x in range(600)]
classes+=['0' for x in range(600)]
classes+=['0' for x in range(600)]

dfPlusClasses = pandas.concat([dfPlusClasses, pandas.DataFrame(classes)], axis=1)


dfPlusClasses.to_csv('out.csv',header=False, index_label=False,index=False)

#add class to dataframe
#save dataframe
#repeat for all dataframedf