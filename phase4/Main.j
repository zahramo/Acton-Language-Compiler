.class public Main
.super java/lang/Object

.method public <init>()V
.limit stack 16
.limit locals 16
0: aload_0
1: invokespecial java/lang/Object/<init>()V
4: return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 16
.limit locals 16 
new A
dup
ldc 10
invokespecial A/<init>(I)V
astore 1
aload 1
invokevirtual A/setKnownActors()V
aload 1
ldc 1
iconst_1
ldc "hi"
ldc 2
ldc 6
imul
invokevirtual A/initial(IZLjava/lang/String;I)V
aload 1
invokevirtual A/start()V
return
.end method