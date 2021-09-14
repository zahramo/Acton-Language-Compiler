.class public Consumer_consume
.super Message

.field private receiver LConsumer;
.field private sender LActor;

.method public <init>(LConsumer;LActor;)V
.limit stack 16
.limit locals 16
aload_0
invokespecial Message/<init>()V
aload_0
aload_1
putfield Consumer_consume/receiver LConsumer;
aload_0
aload_2
putfield Consumer_consume/sender LActor;
return
.end method

.method public execute()V
.limit stack 16
.limit locals 16
aload_0
getfield Consumer_consume/receiver LConsumer;
aload_0
getfield Consumer_consume/sender LActor;
invokevirtual Consumer/consume(LActor;)V
return
.end method