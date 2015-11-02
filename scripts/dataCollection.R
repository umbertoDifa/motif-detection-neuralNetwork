library('RCurl')
library('rjson')

# #================================extract list of proteins which belong to a certain family
# #set up matrix
# data = matrix(   c(1, 2, 3),     nrow=1,     ncol=3) 
# 
# #families PS00061,PS50893, PS51722
# family<-'PS50893'
# baseLink_family<-'https://www.kimonolabs.com/api/ondemand/42qi01ao?apikey=lj2wDSnKFryhnv5MDQBaizLRGoD4b3mW&psa='
# link_family<-paste(baseLink_family, family, sep = "")
# json_family <- getURL(link_family)
# obj_family <- fromJSON(json_family)
# print(obj_family)
# text<-obj_family$results$collection1[[1]]$text
# tokens_family<-strsplit(text,"\n")
# #delete first row
# tokens_family<-tokens_family[[1]][2:length(tokens_family[[1]])]
# #collect only the names
# for(i in 1:length(tokens_family)){
#   tokens_family[i]<-strsplit(tokens_family[i]," ")[[1]][1]
# }
#================================set wroking dir
setwd("C:/Users/Umberto/Desktop/projectNN/data")

#==========================================available API keys, #first is mine
apiKeys<-c('lj2wDSnKFryhnv5MDQBaizLRGoD4b3mW',
'UAihH6xCMyX9SZnUsMMG9y3hw40hvdwI',
'aMloVVFACa6T7e1sNnhZN1au9mOAolGp',
'D2jJNdkHPa4aiXutyNhcXM0Pcdv3AknI',
'tALZxK9D0J2CUGSMpy4CvBbZK6yEyLY6')
#==================================import data about proteins

#PS00061 <- read.table("C:/Users/Umberto/Desktop/projectNN/data/PS00061.txt", quote="\"", stringsAsFactors=FALSE)
#PS50007 <- read.table("C:/Users/Umberto/Desktop/projectNN/data/PS50007.txt", quote="\"", comment.char="", stringsAsFactors=FALSE)
#PS50893 <- read.table("C:/Users/Umberto/Desktop/projectNN/data/PS50893.txt", quote="\"", stringsAsFactors=FALSE)
#PS51092 <- read.table("C:/Users/Umberto/Desktop/projectNN/data/PS51092.txt", quote="\"", comment.char="", stringsAsFactors=FALSE)
#PS51722 <- read.table("C:/Users/Umberto/Desktop/projectNN/data/PS51722.txt", quote="\"", stringsAsFactors=FALSE)

PS00061 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS00061.xls", stringsAsFactors=FALSE)
PS50007 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS50007.xls", stringsAsFactors=FALSE)
PS50893 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS50893.xls", stringsAsFactors=FALSE)
PS51092 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS51092.xls", stringsAsFactors=FALSE)
PS51722 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS51722.xls", stringsAsFactors=FALSE)

#=========================================remove useless columns
PS00061<-PS00061[c(-3,-4,-5,-6)]
PS50007<-PS50007[c(-3,-4,-5,-6)]
PS50893<-PS50893[c(-3,-4,-5,-6)]
PS51092<-PS51092[c(-3,-4,-5,-6)]
PS51722<-PS51722[c(-3,-4,-5,-6)]

datasets<-c('PS00061','PS50007','PS50893','PS51092','PS51722')

####===============collect the sequence for each protein name
err = 0

for(d in 2: length(datasets)){
  table<-get(datasets[d])
  #trim dataset to 600 proteins
  table<-table[1:min(600,nrow(table)),]
  table$Sequence<-''
  for(i in 1: nrow(table)){
    tryCatch({
      protein<-paste(table$Entry[i],'.fasta',sep='')
      apiK <- apiKeys[(i%%5) + 1]         #mod gives number between 0-4, then plus 1 so i get between 1-5
      baseLink<-'https://www.kimonolabs.com/api/ondemand/bi56x3dg?apikey='
      VarProteinName<-'&kimpath2='
      link<-paste(baseLink,apiK, VarProteinName, protein, sep = "")
      json <- getURL(link)
      obj <- fromJSON(json)
      #print(obj)
      str<-obj$results$collection1[[1]]$stringa
      tokens<-strsplit(str,"\n")
      tokens[[1]][1]<-''
      sequence<-paste(tokens[[1]], collapse='')
      table$Sequence[i]<-sequence
    }, warning = function(w) {
      #i = i-1
      #'warn'
    }, error = function(e) {
      #if there is an error just retry
      i=i-1
      err = err+1
    }, finally = {
      #i=i-1
      #'final'
    })
    
  }
  assign(datasets[d],table)
  write.table(table, file = paste("./plus/",datasets[d],".csv",sep=''),sep=",",row.names=FALSE)
  paste('dataset ',d,' done',sep=' ')
}

#==================create data frame
dataset<-data.frame(data)
