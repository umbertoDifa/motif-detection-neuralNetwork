comp <- read.csv("C:/Users/Umberto/Desktop/projectNN/lamstar.csv")
d=comp
#convert exe time as numeric
#d$speed<-as.numeric(levels(d$speed))[d$speed]
d$success<- gsub(',','.',d$success)
d$success<-as.numeric(d$success)
#trim values < 95%
d= d[d$success>=0.95,]

#sort(d$EXE.TIME.ms.)
d <- d[order(d$speed),] 

library('ggplot2')
ggplot(data=d, aes(x= d$train , y=d$speed, group = d$kohonen.error, colour =  kohonen.error)) +
  geom_line() +
  geom_point( size=4, shape=21, fill="white")+
  xlab("Training %")+
  ylab("Execution time(ms)")
