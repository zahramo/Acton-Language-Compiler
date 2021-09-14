.class public Car_wash
.super Message

.field private deg I
.field private receiver LCar;
.field private sender LActor;

.method public <init>(LCar;LActor;I)V
.limit stack 16
.limit locals 16
aload_0
invokespecial Message/<init>()V
aload_0
aload_1
putfield Car_wash/receiver LCar;
aload_0
aload_2
putfield Car_wash/sender LActor;
aload_0
iload 3
putfield Car_wash/deg I
return
.end method

.method public execute()V
.limit stack 16
.limit locals 16
aload_0
getfield Car_wash/receiver LCar;
aload_0
getfield Car_wash/sender LActor;
aload_0
getfield Car_wash/deg I
invokevirtual Car/wash(LActor;I)V
return
.end method