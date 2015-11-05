#use data already enhanced
PS00061 <- read.csv("C:/Users/Umberto/Desktop/projectNN/data/plus/PS00061.csv", stringsAsFactors=FALSE)
PS50007 <- read.csv("C:/Users/Umberto/Desktop/projectNN/data/plus/PS50007.csv", stringsAsFactors=FALSE)
PS50893 <- read.csv("C:/Users/Umberto/Desktop/projectNN/data/plus/PS50893.csv", stringsAsFactors=FALSE)
PS51092 <- read.csv("C:/Users/Umberto/Desktop/projectNN/data/plus/PS51092.csv", stringsAsFactors=FALSE)
PS51722 <- read.csv("C:/Users/Umberto/Desktop/projectNN/data/plus/PS51722.csv", stringsAsFactors=FALSE)

library(tau)
temp <- "A B C A B C"
textcnt(temp, method="ngram", n=2L, decreasing=TRUE)

library("RWeka")
library("tm")

data("crude")
BigramTokenizer <- function(x) NGramTokenizer(x, Weka_control(min = 2, max = 2))
tdm <- TermDocumentMatrix(crude, control = list(tokenize = BigramTokenizer))
inspect(tdm[340:445,1:10])

library('ngram')
 x <- "A B A C A B B"
 ng <- ngram (x , n =2)
ng
print(ng,full=TRUE)

library('biogram')
