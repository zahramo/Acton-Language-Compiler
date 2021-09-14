.class public BufferManager
.super Actor

.field producer LProducer;
.field consumer LConsumer;
.field empty Z
.field full Z
.field producerWaiting Z
.field consumerWaiting Z
.field bufferLength I
.field nextProduce I
.field nextConsume I

.method public <init>(I)V
.limit stack 16
.limit locals 16
aload_0
ldc 0
putfield BufferManager/empty Z
aload_0
ldc 0
putfield BufferManager/full Z
aload_0
ldc 0
putfield BufferManager/producerWaiting Z
aload_0
ldc 0
putfield BufferManager/consumerWaiting Z
aload_0
ldc 0
putfield BufferManager/bufferLength I
aload_0
ldc 0
putfield BufferManager/nextProduce I
aload_0
ldc 0
putfield BufferManager/nextConsume I
aload_0
iload_1
invokespecial Actor/<init>(I)V
return
.end method

.method public initial()V
.limit stack 16
.limit locals 16
aload_0
ldc 5
putfield BufferManager/bufferLength I
aload_0
iconst_1
putfield BufferManager/empty Z
aload_0
iconst_0
putfield BufferManager/full Z
aload_0
iconst_0
putfield BufferManager/producerWaiting Z
aload_0
iconst_0
putfield BufferManager/consumerWaiting Z
aload_0
ldc 0
putfield BufferManager/nextProduce I
aload_0
ldc 0
putfield BufferManager/nextConsume I
return
.end method

.method public setKnownActors(LProducer;LConsumer;)V
.limit stack 16
.limit locals 16
aload_0
aload 1
putfield BufferManager/producer LProducer;
aload_0
aload 2
putfield BufferManager/consumer LConsumer;
return
.end method

.method public send_giveMeNextProduce(LActor;)V
.limit stack 16
.limit locals 16
aload_0
new BufferManager_giveMeNextProduce
dup
aload_0
aload_1
invokespecial BufferManager_giveMeNextProduce/<init>(LBufferManager;LActor;)V
invokevirtual BufferManager/send(LMessage;)V
return
.end method

.method public giveMeNextProduce(LActor;)V
.limit stack 16
.limit locals 16
aload_0 
getfield BufferManager/full Z
ifeq Label0
iconst_0
goto Label1
Label0:
iconst_1
Label1:
ifeq Label2
aload_0
getfield BufferManager/producer LProducer;
aload_0
invokevirtual Producer/send_produce(LActor;)V
goto Label3
Label2:
aload_0
iconst_1
putfield BufferManager/producerWaiting Z
Label3:
return
.end method

.method public send_giveMeNextConsume(LActor;)V
.limit stack 16
.limit locals 16
aload_0
new BufferManager_giveMeNextConsume
dup
aload_0
aload_1
invokespecial BufferManager_giveMeNextConsume/<init>(LBufferManager;LActor;)V
invokevirtual BufferManager/send(LMessage;)V
return
.end method

.method public giveMeNextConsume(LActor;)V
.limit stack 16
.limit locals 16
aload_0 
getfield BufferManager/empty Z
ifeq Label4
iconst_0
goto Label5
Label4:
iconst_1
Label5:
ifeq Label6
aload_0
getfield BufferManager/consumer LConsumer;
aload_0
invokevirtual Consumer/send_consume(LActor;)V
goto Label7
Label6:
aload_0
iconst_1
putfield BufferManager/consumerWaiting Z
Label7:
return
.end method

.method public send_ackProduce(LActor;)V
.limit stack 16
.limit locals 16
aload_0
new BufferManager_ackProduce
dup
aload_0
aload_1
invokespecial BufferManager_ackProduce/<init>(LBufferManager;LActor;)V
invokevirtual BufferManager/send(LMessage;)V
return
.end method

.method public ackProduce(LActor;)V
.limit stack 16
.limit locals 16
aload_0
aload_0 
getfield BufferManager/nextProduce I
ldc 1
iadd
aload_0 
getfield BufferManager/bufferLength I
irem
putfield BufferManager/nextProduce I
aload_0 
getfield BufferManager/nextProduce I
aload_0 
getfield BufferManager/nextConsume I
if_icmpne Label8
iconst_1
goto Label9
Label8:
iconst_0
Label9:
ifeq Label10
aload_0
iconst_1
putfield BufferManager/full Z
goto Label11
Label10:
Label11:
aload_0
iconst_0
putfield BufferManager/empty Z
aload_0 
getfield BufferManager/consumerWaiting Z
ifeq Label12
aload_0
getfield BufferManager/consumer LConsumer;
aload_0
invokevirtual Consumer/send_consume(LActor;)V
aload_0
iconst_0
putfield BufferManager/consumerWaiting Z
goto Label13
Label12:
Label13:
return
.end method

.method public send_ackConsume(LActor;)V
.limit stack 16
.limit locals 16
aload_0
new BufferManager_ackConsume
dup
aload_0
aload_1
invokespecial BufferManager_ackConsume/<init>(LBufferManager;LActor;)V
invokevirtual BufferManager/send(LMessage;)V
return
.end method

.method public ackConsume(LActor;)V
.limit stack 16
.limit locals 16
aload_0
aload_0 
getfield BufferManager/nextConsume I
ldc 1
iadd
aload_0 
getfield BufferManager/bufferLength I
irem
putfield BufferManager/nextConsume I
aload_0 
getfield BufferManager/nextConsume I
aload_0 
getfield BufferManager/nextProduce I
if_icmpne Label14
iconst_1
goto Label15
Label14:
iconst_0
Label15:
ifeq Label16
aload_0
iconst_1
putfield BufferManager/empty Z
goto Label17
Label16:
Label17:
aload_0
iconst_0
putfield BufferManager/full Z
aload_0 
getfield BufferManager/producerWaiting Z
ifeq Label18
aload_0
getfield BufferManager/producer LProducer;
aload_0
invokevirtual Producer/send_produce(LActor;)V
aload_0
iconst_0
putfield BufferManager/producerWaiting Z
goto Label19
Label18:
Label19:
return
.end method

