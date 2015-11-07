comp <- read.csv("C:/Users/Umberto/Desktop/projectNN/comp.csv")
d=comp
d= d[d$EXE.TIME.ms.!='-',]
sort(as.numeric(d$EXE.TIME.ms.))
as.numeric(d$EXE.TIME.ms.)

library('ggplot2')
ggplot(data=d, aes(x= d$X.TRAINING , y=d$EXE.TIME.ms., group = d$LEARNING.RATE, colour =  d$LEARNING.RATE)) +
  geom_line() +
  geom_point( size=4, shape=21, fill="white")

rain<-read.csv("cityrain.csv")
plot(d$EXE.TIME.ms.,type="b",lwd=2,
       xaxt="n",ylim=c(0,1),col="black",
     xlab="Training set %",ylab="Exe time (ms)",
     main="Execution time")

axis(1,at=1:length(d$X.TRAINING),labels=d$X.TRAINING)
lines(d$EXE.TIME.ms.,col="red",type="b",lwd=2)
#lines(rain$NewYork,col="orange",type="b",lwd=2)
#lines(rain$London,col="purple",type="b",lwd=2)

