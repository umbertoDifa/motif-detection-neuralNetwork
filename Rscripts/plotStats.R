comp <- read.csv("C:/Users/Umberto/Desktop/projectNN/comp.csv")
d=comp
#trim unkown values
d= d[d$EXE.TIME.ms.!='-',]

#convert exe time as numeric
d$EXE.TIME.ms.<-as.numeric(levels(d$EXE.TIME.ms.))[d$EXE.TIME.ms.]

#trim values > 1000ms
d= d[d$EXE.TIME.ms.<=1000,]

#sort(d$EXE.TIME.ms.)
d <- d[order(d$EXE.TIME.ms.),] 

library('ggplot2')
ggplot(data=d, aes(x= d$X.TRAINING , y=d$EXE.TIME.ms., group = d$LEARNING.RATE, colour =  LEARNING.RATE)) +
  geom_line() +
  geom_point( size=4, shape=21, fill="white")+
  xlab("Training %")+
  ylab("Execution time(ms)")