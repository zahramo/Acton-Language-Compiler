.class public A_foo
.super Message

.field private mo I
.field private receiver LA;
.field private sender LActor;

.method public <init>(LA;LActor;I)V
.limit stack 16
.limit locals 16
aload_0
invokespecial Message/<init>()V
aload_0
aload_1
putfield A_foo/receiver LA;
aload_0
aload_2
putfield A_foo/sender LActor;
aload_0
iload 3
putfield A_foo/mo I
return
.end method

.method public execute()V
.limit stack 16
.limit locals 16
aload_0
getfield A_foo/receiver LA;
aload_0
getfield A_foo/sender LActor;
aload_0
getfield A_foo/mo I
invokevirtual A/foo(LActor;I)V
return
.end method