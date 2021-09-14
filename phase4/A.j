.class public A
.super Actor

.field i I
.field j Z
.field k Ljava/lang/String;
.field r [I

.method public <init>(I)V
.limit stack 16
.limit locals 16
aload_0
ldc 0
putfield A/i I
aload_0
ldc 0
putfield A/j Z
aload_0
ldc ""
putfield A/k Ljava/lang/String;
aload_0
ldc 5
newarray int
putfield A/r [I
aload_0
iload_1
invokespecial Actor/<init>(I)V
return
.end method

.method public initial(IZLjava/lang/String;I)V
.limit stack 16
.limit locals 16
aload_0
iload 1
ldc 1
iadd
putfield A/i I
aload_0
iload 2
ifeq Label2
iconst_1
ifeq Label2
iconst_1
goto Label3
Label2:
iconst_0
Label3:
ifne Label0
iconst_0
ifne Label0
iconst_0
goto Label1
Label0:
iconst_1
Label1:
putfield A/j Z
aload_0
aload 3
putfield A/k Ljava/lang/String;
aload_0
aload_0
iload 4
invokevirtual A/foo(LActor;I)V
return
.end method

.method public setKnownActors()V
.limit stack 16
.limit locals 16
return
.end method

.method public send_foo(LActor;I)V
.limit stack 16
.limit locals 16
aload_0
new A_foo
dup
aload_0
aload_1
iload 2
invokespecial A_foo/<init>(LA;LActor;I)V
invokevirtual A/send(LMessage;)V
return
.end method

.method public foo(LActor;I)V
.limit stack 16
.limit locals 16
ldc 0
istore 3
ldc 0
istore 4
ldc 0
istore 5
ldc 5
newarray int
astore 6
ldc ""
astore 7
getstatic java/lang/System/out Ljava/io/PrintStream;
aload_0 
getfield A/r [I
invokestatic java/util/Arrays/toString([I)Ljava/lang/String;
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 7
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload_0 
getfield A/j Z
invokevirtual java/io/PrintStream/println(Z)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload_0 
getfield A/k Ljava/lang/String;
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload_0 
getfield A/r [I
invokestatic java/util/Arrays/toString([I)Ljava/lang/String;
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
ldc 1
istore 4
getstatic java/lang/System/out Ljava/io/PrintStream;
ldc "--------------------------------"
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
iload 4
iinc 4 1
iload 4
aload_0
getfield A/r [I
ldc 1
aload_0
getfield A/r [I
ldc 1
dup2
iaload
iconst_1
iadd
dup_x2
iastore
iadd
iload 3
iadd
istore 3
aload 6
ldc 1
aload 6
ldc 1
iaload
ldc 4
iadd
iastore
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 4
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 6
ldc 1
iaload
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload_0
getfield A/r [I
ldc 2
iaload
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
ldc "--------------------------------"
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
iload 4
iinc 4 -1
iload 4
aload 6
ldc 1
aload 6
ldc 1
dup2
iaload
iconst_1
isub
dup_x2
iastore
iadd
iload 3
iadd
istore 3
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 4
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 6
ldc 1
iaload
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream;
ldc "--------------------------------"
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
aload_0
getfield A/r [I
ldc 2
dup2
iaload
iconst_1
iadd
dup_x2
iastore
istore 3
ldc 0
istore 3
Label4:
iload 3
ldc 12
if_icmpeq Label7
iconst_1
goto Label8
Label7:
iconst_0
Label8:
ifeq Label6
iload 3
ldc 4
if_icmpeq Label9
iconst_1
goto Label10
Label9:
iconst_0
Label10:
ifeq Label11
goto Label5
goto Label12
Label11:
Label12:
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 3
invokevirtual java/io/PrintStream/println(I)V
Label5:
iload 3
ldc 2
iadd
istore 3
goto Label4
Label6:
return
.end method

