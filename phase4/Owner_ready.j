.class public Owner_ready
.super Message

.field private receiver LOwner;
.field private sender LActor;

.method public <init>(LOwner;LActor;)V
.limit stack 16
.limit locals 16
aload_0
invokespecial Message/<init>()V
aload_0
aload_1
putfield Owner_ready/receiver LOwner;
aload_0
aload_2
putfield Owner_ready/sender LActor;
return
.end method

.method public execute()V
.limit stack 16
.limit locals 16
aload_0
getfield Owner_ready/receiver LOwner;
aload_0
getfield Owner_ready/sender LActor;
invokevirtual Owner/ready(LActor;)V
return
.end method