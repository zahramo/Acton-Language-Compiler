.class public DefaultActor
.super java/lang/Thread

.method public <init>()V
.limit stack 16
.limit locals 16
aload_0
invokespecial java/lang/Thread/<init>()V
return
.end method

.method public send_foo(LActor;I)V
.limit stack 16
.limit locals 16
getstatic java/lang/System/out Ljava/io/PrintStream;
ldc "there is no msghandler named foo in sender"
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
return
.end method

