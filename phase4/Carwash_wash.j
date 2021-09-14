.class public Carwash_wash
.super Message

.field private deg I
.field private start_deg I
.field private receiver LCarwash;
.field private sender LActor;

.method public <init>(LCarwash;LActor;II)V
.limit stack 16
.limit locals 16
aload_0
invokespecial Message/<init>()V
aload_0
aload_1
putfield Carwash_wash/receiver LCarwash;
aload_0
aload_2
putfield Carwash_wash/sender LActor;
aload_0
iload 3
putfield Carwash_wash/deg I
aload_0
iload 5
putfield Carwash_wash/start_deg I
return
.end method

.method public execute()V
.limit stack 16
.limit locals 16
aload_0
getfield Carwash_wash/receiver LCarwash;
aload_0
getfield Carwash_wash/sender LActor;
aload_0
getfield Carwash_wash/deg I
aload_0
getfield Carwash_wash/start_deg I
invokevirtual Carwash/wash(LActor;II)V
return
.end method