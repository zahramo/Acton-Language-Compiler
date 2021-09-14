.class public Car_ready
.super Message

.field private receiver LCar;
.field private sender LActor;

.method public <init>(LCar;LActor;)V
.limit stack 16
.limit locals 16
aload_0
invokespecial Message/<init>()V
aload_0
aload_1
putfield Car_ready/receiver LCar;
aload_0
aload_2
putfield Car_ready/sender LActor;
return
.end method

.method public execute()V
.limit stack 16
.limit locals 16
aload_0
getfield Car_ready/receiver LCar;
aload_0
getfield Car_ready/sender LActor;
invokevirtual Car/ready(LActor;)V
return
.end method