.class public Producer_produce
.super Message

.field private receiver LProducer;
.field private sender LActor;

.method public <init>(LProducer;LActor;)V
.limit stack 16
.limit locals 16
aload_0
invokespecial Message/<init>()V
aload_0
aload_1
putfield Producer_produce/receiver LProducer;
aload_0
aload_2
putfield Producer_produce/sender LActor;
return
.end method

.method public execute()V
.limit stack 16
.limit locals 16
aload_0
getfield Producer_produce/receiver LProducer;
aload_0
getfield Producer_produce/sender LActor;
invokevirtual Producer/produce(LActor;)V
return
.end method