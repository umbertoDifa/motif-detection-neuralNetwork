library('RCurl')
library('rjson')

#================================set wroking dir
setwd("C:/Users/Umberto/Desktop/projectNN/data")

#==========================================available API keys, #first is mine
apiKeys<-c('lj2wDSnKFryhnv5MDQBaizLRGoD4b3mW',
           'UAihH6xCMyX9SZnUsMMG9y3hw40hvdwI',
           'aMloVVFACa6T7e1sNnhZN1au9mOAolGp',
           'D2jJNdkHPa4aiXutyNhcXM0Pcdv3AknI',
           'tALZxK9D0J2CUGSMpy4CvBbZK6yEyLY6')
#==================================import data about proteins
#ADH_SHORT
PS00061 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS00061.xls", stringsAsFactors=FALSE)

#PIPLC_X_DOMAIN
PS50007 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS50007.xls", stringsAsFactors=FALSE)

#ABC_TRANSPORTER_2
PS50893 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS50893.xls", stringsAsFactors=FALSE)

#FN2_2
PS51092 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS51092.xls", stringsAsFactors=FALSE)

#G_TR_2
PS51722 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS51722.xls", stringsAsFactors=FALSE)

#globin
PS01033 <- read.delim("C:/Users/Umberto/Desktop/projectNN/data/PS01033.xls", stringsAsFactors=FALSE)

#=========================================remove useless columns
PS00061<-PS00061[c(-3,-4,-5,-6)]
PS50007<-PS50007[c(-3,-4,-5,-6)]
PS50893<-PS50893[c(-3,-4,-5,-6)]
PS51092<-PS51092[c(-3,-4,-5,-6)]
PS51722<-PS51722[c(-3,-4,-5,-6)]
PS01033<-PS01033[c(-3,-4,-5,-6)]


datasets<-c('PS00061','PS50007','PS50893','PS51092','PS51722','PS01033')

####===============collect the sequence for each protein name, version with loop until
#==================the sequence isn't collected, theoretically dangerous

err = 0

for(d in 1: length(datasets)){
  table<-get(datasets[d])
  #trim dataset to 600 proteins
  table<-table[1:min(600,nrow(table)),]
  table$Sequence<-''
  for(i in 1: nrow(table)){
    k=0 # this is to make the apikey slide if the call for a certain i fails
    while(nchar(table[i,'Sequence'])==0){
      k=k+1
     print(i)
      tryCatch({
        protein<-paste(table$Entry[i],'.fasta',sep='')
        apiK <- apiKeys[((i+k)%%5) + 1]         #mod gives number between 0-4, then plus 1 so i get between 1-5
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
        #print(sequence)
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
  }
  assign(datasets[d],table)
  write.table(table, file = paste("./plus/",datasets[d],".csv",sep=''),sep=",",row.names=FALSE)
}


#quick check
#which(nchar(PS51092 [,'Sequence'])==0)

